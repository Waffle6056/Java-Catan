import RenderingStuff.CatanWindow;
import RenderingStuff.Mesh;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Catan {
    CatanWindow Renderer;
    NewBoard Board;
    List<Player> players;
    Player turnPlayer;
    int turnInd;
    Mesh cursor;

    Queue<Building> MeshQueue = new LinkedList<>();
    enum BuildingOption{
        Road,
        Town,
        City,
        BuildingOption(){}
    }

    Phase currentPhase = Phase.SetUp;
    enum Phase{
        SetUp,
        BuildingTrading,
        Phase(){}
    }
    public void run(){
        Renderer = new CatanWindow();
        Renderer.run();
        Board = new NewBoard();

        cursor = new Mesh("catan.fbx");
        //Renderer.addMesh(cursor);
        Renderer.addMeshes(Board.getMeshes());

        Mesh test2d = new Mesh("catan.fbx");
        //test2d.scale.mul(0.1f,0.1f,0.1f);
        //Renderer.addMesh2d(test2d);

        bindKeys();
        currentPhase = Phase.SetUp;
        StartSetUpThread();
        loop();
    }
    private void StartSetUpThread(){
        try {
            //System.out.println("start build");
            new Thread( () -> {
                SetUp();
            }).start();

        } catch (Exception e){}
    }
    private void SetUp(){
        System.out.println("SELECT PLAYER COUNT INTERFACE HERE");
        System.out.println("temp player count = 2");
        int playersCount = 2;
        players = new ArrayList<>();
        for (int i = 0; i < playersCount; i++)
            players.add(new Player());

        for (int i = 0; i < playersCount; i++) {
            turnPlayer = players.get(i);
            System.out.println("Player "+i);
            System.out.println("Build Road");
            while (!build(BuildingOption.Road));
            waitMouseRelease();
            System.out.println("Build Town");
            while (!build(BuildingOption.Town));
        }

        for (int i = playersCount-1; i >= 0; i--) {
            turnPlayer = players.get(i);
            System.out.println("Player "+i);
            System.out.println("Build Road");
            while (!build(BuildingOption.Road));
            waitMouseRelease();
            System.out.println("Build Town");
            while (!build(BuildingOption.Town));
        }

        turnInd = -1;
        nextPlayerTurn();
    }
    private void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        double lastFrame = glfwGetTime();
//        Mesh test = new Mesh("catan.fbx");
//        test.scale = new Vector3f(1,1,1);
//        test.rotation.rotateAxis((float) Math.toRadians(-90),1,0,0);


        while (Renderer.shouldClose() ) {
            double currentFrame = glfwGetTime();
            double delta = currentFrame - lastFrame;
            //System.out.println(currentFrame);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
//            test.position = new Vector3f(0,(float) (Math.sin(currentFrame)),0);
//            //System.out.println(delta);
//            //stest.rotation.rotateAxis((float)(1*delta),.9f,.25f,.1f);
//            test.draw(Renderer.shader);
            InstantiateBuildingMeshes();
            Renderer.update(delta);
            //Renderer.getMousePos();
            lastFrame = currentFrame;
        }
        Renderer.terminate();
    }
    void InstantiateBuildingMeshes(){ // HAS TO RUN ON THE MAIN THREAD
        while (!MeshQueue.isEmpty()) {
            Building b = MeshQueue.remove();
            String file = "";
            switch (b.type){
                case City -> file = "city.fbx";
                case Town -> file = "town.fbx";
            }
            Mesh m = new Mesh(file);
            m.position.add(b.x,1,b.y);
            b.mesh = m;
//            Renderer.addMeshes(m);
        }
    }
    void nextPlayerTurn(){
        turnInd = (turnInd+1)%players.size();
        turnPlayer = players.get(turnInd);

        roll();

        currentPhase = Phase.BuildingTrading;
    }
    void roll(){
        int one= (int)(Math.random()*6)+1,two= (int)(Math.random()*6)+1;
        int random=one+two;
        Board.rolled(random);
        if (random==7){
            //TODO Move RobberBaron
            //Select New Hex
            //assign is RobberBaron
            //Steal from near Player
        }
    }
    void bindKeys(){
        Renderer.bindCallback((window, key, scancode, action, mods) -> {
            if (currentPhase == Phase.BuildingTrading) {
                if (key == GLFW_KEY_1)
                    startBuildThread(BuildingOption.Town);
                if (key == GLFW_KEY_2)
                    startBuildThread(BuildingOption.City);
                if (key == GLFW_KEY_3)
                    startBuildThread(BuildingOption.Road);
                if (key == GLFW_KEY_4) //TODO
                    ;//create development card

                if (key == GLFW_KEY_Z) //TODO
                    turnPlayer.DevelopmentCards.toggleVisible();
                if (key == GLFW_KEY_X)
                    turnPlayer.ResourceCards.toggleVisible();
                if (key == GLFW_KEY_C)
                    turnPlayer.TradingCards.toggleVisible();

                if (key == GLFW_KEY_Q)
                    turnPlayer.UIElements.scroll(-1); // scroll ui elements
                if (key == GLFW_KEY_E)
                    turnPlayer.UIElements.scroll(1);

                if (key == GLFW_KEY_A)
                    turnPlayer.UIElements.current().data.scroll(-1); // scroll current ui element
                if (key == GLFW_KEY_D)
                    turnPlayer.UIElements.current().data.scroll(1);
                if (key == GLFW_KEY_S)
                    turnPlayer.UIElements.current().data.select(); // selects current

                if (key == GLFW_KEY_F)
                    turnPlayer.DevelopmentCards.current().data.use(this); // uses current development card;
                if (key == GLFW_KEY_R){
                    CardHolder<NewHex.resource> other = turnPlayer.TradingCards.current().data;
                    turnPlayer.ResourceCards.trade(other);
                }


                if (key == GLFW_KEY_ENTER)
                    nextPlayerTurn();
            }
        });
    }
    NewHex selectHex(Vector3f mousePos){ // selects closest hex
        HashMap<String , NewHex> grid = Board.grid;
        NewHex closest = grid.get(Board.encoder(0,0,0));
        float oDis = Float.MAX_VALUE;
        for (String k : grid.keySet()){
            NewHex current = grid.get(k);
            Vector3f cPos = current.mesh.position;
            float cDis = cPos.distance(mousePos);
            if (cDis < oDis){
                closest = current;
                oDis = cDis;
            }
        }
        return closest;
    }
    NewHex.HexBuilding selectVertex(NewHex hex, Vector3f mousePos){
        Vector3f base = new Vector3f(0,0,-1);
        Vector3f axis = base.cross(new Vector3f(1,0,0),new Vector3f());
        Vector3f pos = hex.mesh.position;
        Vector3f mAngle = mousePos.add(-pos.x,-pos.y,-pos.z, new Vector3f());

        float minAngle = Float.MAX_VALUE;
        int out = -1;

        int verts = 6;
        for (int i = 0; i < verts; i++){

            float cAngle = base.angle(mAngle);
            if (cAngle < minAngle) {
                out = i;
                minAngle = cAngle;
            }
            base.rotateAxis(Math.toRadians(360/verts),axis.x,axis.y,axis.z);
        }

        return NewHex.HexBuilding.values()[out];
    }
    Vector3f waitMouseClick(){
        while (Renderer.getMouseButton(GLFW_MOUSE_BUTTON_LEFT) != GLFW_PRESS);
        Vector3f pos = Renderer.getMousePos().add(0,1,0, new Vector3f());
        cursor.position = pos;
        return pos;
    }
    void waitMouseRelease(){
        while (Renderer.getMouseButton(GLFW_MOUSE_BUTTON_LEFT) != GLFW_RELEASE);
    }
    void startBuildThread(BuildingOption Option){
        try {
            //System.out.println("start build");
            new Thread( () -> {
                build(Option);
            }).start();

        } catch (Exception e){}
    }
    boolean build(BuildingOption Option) { // TODO // returns if successful

        //check if required resource amount
        // return false if not enought

        if (Option == BuildingOption.Town || Option == BuildingOption.City){
            Vector3f mouseClickPos = waitMouseClick();
            NewHex hex = selectHex(mouseClickPos);
            NewHex.HexBuilding ver = selectVertex(hex, mouseClickPos);

            hex.constructbuilding(ver, Option, turnPlayer); //TODO BUILDING REQUIREMENTS OF SETTLEMENTS & BUILDINGS Done

            float radius = 1;// arbitrary value temp
            hex.buildings[ver.index].x = hex.x + Math.sin(Math.toRadians(60 * ver.index)) * radius;
            hex.buildings[ver.index].y = hex.y + Math.sin(Math.toRadians(60 * ver.index)) * radius;
            MeshQueue.add(hex.buildings[ver.index]);
            return true;
            //System.out.println("built town/city "+hex.mesh.position+" "+ver);

        }
        else if (Option == BuildingOption.Road){
            Vector3f mouseClickPos1 = waitMouseClick();
            NewHex hex1 = selectHex(mouseClickPos1);
            NewHex.HexBuilding ver1 = selectVertex(hex1, mouseClickPos1);

            waitMouseRelease();

            Vector3f mouseClickPos2 = waitMouseClick();
            NewHex hex2 = selectHex(mouseClickPos2);
            NewHex.HexBuilding ver2 = selectVertex(hex2, mouseClickPos2);

            hex1.constructRoads(ver1,ver2, BuildingOption.Road, turnPlayer); //TODO BUILDING REQUIREMENTS OF ROADS Done
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        Catan catan = new Catan();
        catan.run();
    }
}