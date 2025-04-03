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
    Queue<Road> MeshQueueRoad = new LinkedList<>();
    enum BuildingOption{
        Road,
        Town,
        City,
        BuildingOption(){}
    }

    Phase currentPhase = Phase.SetUp;
    enum Phase{
        SetUp,
        Rolling,
        BuildingTrading,
        Phase(){}
    }
    public void run(){
        Renderer = new CatanWindow();
        Renderer.run();
        Board = new NewBoard();

        cursor = new Mesh("Buildings/Settlement.fbx");
        cursor.rotation.rotateX(-90);
        Renderer.addMesh(cursor);
        Renderer.addMeshes(Board.getMeshes());

        Mesh m = new Mesh("CatanCardMeshes/Resource/CardOre.fbx");
        m.position.add(0.5f,0,9);
        m.rotation.lookAlong(m.position,new Vector3f(0,1,0)).rotateLocalX((float) java.lang.Math.toRadians(90));
        //m.rotation.rotateAxis(Math.toRadians(90),1,0,0);
        Renderer.addMesh2d(m);
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


        //overrides road building requirement because im too lazy to implement a better solution
        NewHex.ownerRequirementOverride = true;
        for (int i = 0; i < playersCount*2; i++) {
            int ind = Math.min(playersCount*2-1-i, i);
            turnPlayer = players.get(ind);
            System.out.println("Player "+ind);

            System.out.println("Build Road");
            while (!build(BuildingOption.Road))
                System.out.println("failed try again");

            waitMouseRelease();

            System.out.println("Build Town");
            while (!build(BuildingOption.Town))
                System.out.println("failed try again");
        }
        NewHex.ownerRequirementOverride = false;

        turnPlayer = players.get(0);

        for (Player p : players){
            for (Player b : players)
                p.TradingCards.add(b.ResourceCards);
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

    void nextPlayerTurn(){
        turnPlayer.updateCardsToResources();
        for (Card<CardHolder> cardElement : turnPlayer.UIElements.Cards) {
            CardHolder element = cardElement.data;
            element.toggleVisible(false);
            Renderer.removeMeshes2d(element.getMeshes());
        }

        turnInd = (turnInd+1)%players.size();
        turnPlayer = players.get(turnInd);

        roll();
        turnPlayer.updateResourcesToCards();
        currentPhase = Phase.BuildingTrading;
    }

    void roll(){
        int one= (int)(Math.random()*6)+1,two= (int)(Math.random()*6)+1;
        int random=one+two;
        Board.rolled(random);
        if (random==7){
            //Select New Hex
            waitMouseRelease();
            Vector3f mouseClickPos = waitMouseClick();
            NewHex hex = selectHex(mouseClickPos);

            //assign is RobberBaron
            NewHex.isRobberBaroned = hex;

            //Steal from near Player
            //TODO im lazy

        }
    }

    boolean build(BuildingOption Option) { // TODO // returns if successful

        //check if required resource amount
        // return false if not enought

        if (Option == BuildingOption.Town || Option == BuildingOption.City){

            waitMouseRelease();

            Vector3f mouseClickPos = waitMouseClick();
            NewHex hex = selectHex(mouseClickPos);
            NewHex.HexBuilding ver = selectVertex(hex, mouseClickPos);

            if (hex.constructbuilding(ver, Option, turnPlayer)) {
                System.out.println(hex.buildings[ver.index].x + " " + hex.buildings[ver.index].y + " BUILDNG POSITION " + hex.x + " " + hex.y);
                MeshQueue.add(hex.buildings[ver.index]);
                //System.out.println("COMPLETED BUILDING "+hex.buildings[ver.index].type);
                return true;
                //System.out.println("built town/city "+hex.mesh.position+" "+ver);
            }
            return false;
        }
        else if (Option == BuildingOption.Road){

            waitMouseRelease();

            Vector3f mouseClickPos1 = waitMouseClick();
            NewHex hex1 = selectHex(mouseClickPos1);
            NewHex.HexBuilding ver1 = selectVertex(hex1, mouseClickPos1);

            waitMouseRelease();

            Vector3f mouseClickPos2 = waitMouseClick();
            NewHex hex2 = selectHex(mouseClickPos2);
            NewHex.HexBuilding ver2 = selectVertex(hex2, mouseClickPos2);
            Road[] out = new Road[1];
            if (hex1.constructRoads(ver1, hex2, ver2, BuildingOption.Road, turnPlayer, out)){
                MeshQueueRoad.add(out[0]);
                hex1.constructbuilding(ver1, BuildingOption.Road, turnPlayer);
                hex2.constructbuilding(ver2, BuildingOption.Road, turnPlayer);
                return true;
            }
            else
                return false;

        }
        return false;
    }
    void toggleVisible(CardHolder c){
        System.out.println("toggle "+c);
        c.toggleVisible();
        if (c.visible) {
            Renderer.addMeshes2d(c.getMeshes());
        }
        else
            Renderer.removeMeshes2d(c.getMeshes());
    }
    HashSet<Integer> pressed = new HashSet<>();
    boolean justPressed(int k){
        if (pressed.contains(k))
            return false;
        pressed.clear();
        pressed.add(k);
        return true;
    }
    void bindKeys(){
        Renderer.bindCallback((window, key, scancode, action, mods) -> {
            if (currentPhase == Phase.BuildingTrading) {
                if (!justPressed(key))
                    return;

                if (key == GLFW_KEY_1)
                    startBuildThread(BuildingOption.Town);
                if (key == GLFW_KEY_2)
                    startBuildThread(BuildingOption.City);
                if (key == GLFW_KEY_3)
                    startBuildThread(BuildingOption.Road);
                if (key == GLFW_KEY_4) //TODO
                    ;//create development card

                if (key == GLFW_KEY_Z)
                    toggleVisible(turnPlayer.DevelopmentCards);
                if (key == GLFW_KEY_X) {
                    if (!turnPlayer.ResourceCards.visible)
                        turnPlayer.updateResourcesToCards();
                    toggleVisible(turnPlayer.ResourceCards);

                }
                if (key == GLFW_KEY_C)
                    toggleVisible(turnPlayer.TradingCards);

                if (key == GLFW_KEY_TAB) {

                    turnPlayer.UIElements.scroll(1); // scroll ui elements
//                    System.out.println(turnPlayer.UIElements.current().data.current().data);
                }
                if (key == GLFW_KEY_E)
                    turnPlayer.UIElements.current().data.scroll(1); // scroll current ui element
                if (key == GLFW_KEY_Q)
                    turnPlayer.UIElements.current().data.scroll(-1);
                if (key == GLFW_KEY_R) {
                    CardHolder c = turnPlayer.UIElements.current().data;
                    if (c == turnPlayer.ResourceCards)
                        turnPlayer.ResourceCards.select(); // selects current
                    else if (c == turnPlayer.DevelopmentCards)
                        turnPlayer.DevelopmentCards.current().data.use(this); // uses current development card;
                    else if (c == turnPlayer.TradingCards)
                        ;//start trade or select or confirm idk im not done yet
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

    void InstantiateBuildingMeshes(){ // HAS TO RUN ON THE MAIN THREAD
        while (!MeshQueue.isEmpty()) {
            Building b = MeshQueue.remove();
            String file = "";
            System.out.println(b.type);
            switch (b.type){
                case City -> file = "Buildings/City.fbx";
                case Town -> file = "Buildings/Settlement.fbx";
                case Road -> file = "Buildings/Road.fbx";
            }
            if (b.type == BuildingOption.Road)
                continue;
            Mesh m = new Mesh(file);
            m.position.add(b.x,0,b.y);
            m.rotation.rotateX(Math.toRadians(-90));
            b.mesh = m;
            Renderer.addMesh(m);
        }
        while (!MeshQueueRoad.isEmpty()) {
            Road b = MeshQueueRoad.remove();
            String file = "Buildings/Road.fbx";
            Mesh m = new Mesh(file);
            m.position.add(b.x,0,b.y);
            m.rotation.rotateX(Math.toRadians(-90));
                m.rotation.rotateZ(Math.toRadians(b.angle+90));
            b.mesh = m;
            Renderer.addMesh(m);
        }
    }

    public static void main(String[] args) {
        Catan catan = new Catan();
        catan.run();
    }
}
