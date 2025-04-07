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
    int playersCount = 2;
    CatanWindow Renderer;
    NewBoard Board;
    List<Player> players;
    Player turnPlayer;
    int turnInd;
    Mesh cursor, sky, ocean;

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
    String[] PlayerCards = {
            "CatanCardMeshes/PlayerThree.fbx",
            "CatanCardMeshes/PlayerFour.fbx",
            "CatanCardMeshes/PlayerTwo.fbx",
            "CatanCardMeshes/PlayerOne.fbx",
    };
    public void run(){
        Renderer = new CatanWindow();
        Renderer.run();
        Board = new NewBoard();

        cursor = new Mesh("CatanCardMeshes/PlayerOne.fbx");
        cursor.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        cursor.scale.mul(1f,1f,1f);
        Renderer.addMesh(cursor);

        sky = new Mesh("HexMeshes/SkySphere.fbx");
        sky.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        sky.position.add(1.9555556f,0f,3.288889f);
        Renderer.addMesh(sky);

        ocean = new Mesh("HexMeshes/Ocean.fbx");
        ocean.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        ocean.position.add(1.9555556f,-.3f,3.288889f);
        ocean.scale.mul(10,10,1);
        Renderer.addMesh(ocean);

        Renderer.addMeshes(Board.getMeshes());

        Mesh m = new Mesh("CatanCardMeshes/Resource/CardOre.fbx");
        m.position.add(0.5f,0,9);
        m.rotation.lookAlong(m.position,new Vector3f(0,1,0)).rotateLocalX((float) java.lang.Math.toRadians(90));
        //m.rotation.rotateAxis(Math.toRadians(90),1,0,0);
        Renderer.addMesh2d(m);
        bindKeys();
        currentPhase = Phase.SetUp;
        DevelopmentCard.createDeck();

        System.out.println("SELECT PLAYER COUNT INTERFACE HERE");
        System.out.println("temp player count = 2");

        players = new ArrayList<>();
        for (int i = 0; i < playersCount; i++)
            players.add(new Player());



//        Player Bank = new Player();
//        Bank.resources = new int[]{5,5,5,5,5};
        for (int i = 0; i < playersCount; i++){
            //p.updateResourcesToCards();
            Player p = players.get(i);
            for (int j = 0; j < playersCount; j++) {
                Player b = players.get(j);
                if (p != players.get(j))
                    p.TradingCards.add(b.ResourceCards, PlayerCards[j]);
            }
            //p.TradingCards.add(Bank.resources, )

            p.updateResourcesToCards();
        }


        StartSetUpThread();
//        turnPlayer = players.get(0);
//        turnInd = -1;
//        nextPlayerTurn();
        loop();
    }

    private void SetUp(){

        //overrides road building requirement because im too lazy to implement a better solution
        NewHex.ownerRequirementOverride = true;
        for (int i = 0; i < players.size()*2; i++) {
            int ind = Math.min(players.size()*2-1-i, i);
            turnPlayer = players.get(ind);
            turnInd = ind;
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
        //turnPlayer.updateCardsToResources();
        for (Card<CardHolder> cardElement : turnPlayer.UIElements.Cards) {
            CardHolder element = cardElement.data;
            element.toggleVisible(false);
            Renderer.removeMeshes2d(element.getMeshes());
        }

        turnInd = (turnInd+1)%players.size();
        turnPlayer = players.get(turnInd);

        roll();
        currentPhase = Phase.BuildingTrading;
    }
    public void robberThread(){
        try {
            //System.out.println("start build");
            new Thread( () -> {

                //Select New Hex
                waitMouseRelease();
                Vector3f mouseClickPos = waitMouseClick();
                NewHex hex = selectHex(mouseClickPos);

                //assign is RobberBaron
                NewHex.isRobberBaroned = hex;

                //Steal from near Player
                for (int i = 0; i < 6; i++)
                    if (hex.buildings[i].owner != null){
                        Player victim = hex.buildings[i].owner;

                        boolean a = turnPlayer.ResourceCards.visible, b = turnPlayer.OpenTrade.visible;
                        if (a)
                            toggleVisible(turnPlayer.ResourceCards);
                        if (b)
                            toggleVisible(turnPlayer.OpenTrade);


                        victim.ResourceCards.scroll((int)(Math.random()*200));
                        victim.ResourceCards.select();

                        turnPlayer.ResourceCards.trade(victim.ResourceCards);
                        turnPlayer.updateCardsToResources();

                        if (a)
                            toggleVisible(turnPlayer.ResourceCards);
                        if (b)
                            toggleVisible(turnPlayer.OpenTrade);
                    }

            }).start();

        } catch (Exception e){}
    }
    void roll(){
        int one= (int)(Math.random()*6)+1,two= (int)(Math.random()*6)+1;
        int random=one+two;
        System.out.println("ROLLED : "+random);
        Board.rolled(random);
        if (random==7){
            robberThread();
        }
    }

    boolean payCheck(int a, int b, int c, int d, int e){
        if (turnPlayer.resources[0] < a)
            return false;
        if (turnPlayer.resources[1] < b)
            return false;
        if (turnPlayer.resources[2] < c)
            return false;
        if (turnPlayer.resources[3] < d)
            return false;
        if (turnPlayer.resources[4] < e)
            return false;
        return true;
    }
    boolean checkAmt(BuildingOption Option){
        switch (Option){
            case Road: return payCheck(1,0,0,1,0);
            case Town: return payCheck(1,1,0,1,1);
            case City: return payCheck(0,2,3,0,0);
        }
        return false;
    }
    void pay(BuildingOption Option){
        switch (Option) {
            case Road -> pay(1,0,0,1,0);
            case Town -> pay(1,1,0,1,1);
            case City -> pay(0,2,3,0,0);
        }
    }
    void pay(int a, int b, int c, int d, int e){
        turnPlayer.resources[0] -= a;
        turnPlayer.resources[1] -= b;
        turnPlayer.resources[2] -= c;
        turnPlayer.resources[3] -= d;
        turnPlayer.resources[4] -= e;
    }
    boolean build(BuildingOption Option) { // TODO // returns if successful

        //check if required resource amount
        // return false if not enought
        if (!checkAmt(Option))
            return false;

        if (Option == BuildingOption.Town || Option == BuildingOption.City){
            if (Option == BuildingOption.Town && turnPlayer.settlements == 0)
                return false;
            if (Option == BuildingOption.City && turnPlayer.cities == 0)
                return false;

            waitMouseRelease();

            Vector3f mouseClickPos = waitMouseClick();
            NewHex hex = selectHex(mouseClickPos);
            NewHex.HexBuilding ver = selectVertex(hex, mouseClickPos);

            //System.out.println(hex.x + " " + hex.y+" "+ver+" "+hex.buildings[ver.index]);
            if (hex.constructbuilding(ver, Option, turnPlayer)) {
                pay(Option);
                if (Option == BuildingOption.City) {
                    turnPlayer.settlements++;
                    turnPlayer.cities--;
                }
                else
                    turnPlayer.settlements--;
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

            //System.out.println(hex1.x+" "+hex1.y+" "+ver1);
            //System.out.println(hex2.x+" "+hex2.y+" "+ver2);

            Road[] out = new Road[1];
            if (hex1.constructRoads(ver1, hex2, ver2, BuildingOption.Road, turnPlayer, out)){
                MeshQueueRoad.add(out[0]);
                pay(Option);
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
        if (c == null)
            return;
        System.out.println("toggle "+c);
        c.toggleVisible();
        if (c.visible) {
            Renderer.addMeshes2d(c.getMeshes());
        }
        else
            Renderer.removeMeshes2d(c.getMeshes());
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
                if (key == GLFW_KEY_4 && payCheck(0,1,1,0,1)) {
                    pay(0,1,1,0,1);
                    DevelopmentCard d = DevelopmentCard.createNew();
                    //System.out.println(d.meshFile);
                    turnPlayer.DevelopmentCards.add(d, d.meshFile());
                }

                if (key == GLFW_KEY_Z)
                    toggleVisible(turnPlayer.DevelopmentCards);
                if (key == GLFW_KEY_X) {
                    if (!turnPlayer.ResourceCards.visible)
                        turnPlayer.updateResourcesToCards();
                    toggleVisible(turnPlayer.ResourceCards);

                }
                if (key == GLFW_KEY_C)
                    toggleVisible(turnPlayer.TradingCards);
                if (key == GLFW_KEY_V)
                    toggleVisible(turnPlayer.OpenTrade);


                if (key == GLFW_KEY_TAB) {
                    if (turnPlayer.UIElements.visible)
                        toggleVisible(turnPlayer.UIElements);
                    turnPlayer.UIElements.select(false);
                    turnPlayer.UIElements.scroll(1); // scroll ui elements
                    turnPlayer.UIElements.select(true);
                    System.out.println(turnPlayer.UIElements.getMeshes().size());
                    toggleVisible(turnPlayer.UIElements);
//                    System.out.println(turnPlayer.UIElements.current().data.current().data);
                }
                if (key == GLFW_KEY_E)
                    turnPlayer.UIElements.current().data.scroll(1); // scroll current ui element
                if (key == GLFW_KEY_Q)
                    turnPlayer.UIElements.current().data.scroll(-1);
                if (key == GLFW_KEY_R) {
                    if (turnPlayer.UIElements.current() == null)
                        return;
                    CardHolder c = turnPlayer.UIElements.current().data;
                    toggleVisible(c);
                    if (c == turnPlayer.ResourceCards && turnPlayer.ResourceCards.current() != null)
                        selectCurrentResourceCard();
                    else if (c == turnPlayer.OpenTrade && turnPlayer.OpenTrade.current() != null)
                        selectCurrentTradeCard();
                    else if (c == turnPlayer.DevelopmentCards && turnPlayer.DevelopmentCards.current() != null)
                        useDevelopmentCard();
                    else if (c == turnPlayer.TradingCards && turnPlayer.TradingCards.current() != null)
                        openTradingInventory();
                    toggleVisible(c);
                }
                if (key == GLFW_KEY_F)
                    tradeCurrentSelectedCards();



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

            Mesh mark = new Mesh(PlayerCards[turnInd]);
            mark.position.add(b.x,2f,b.y);
            mark.rotation.rotateX(Math.toRadians(-90));
            Renderer.addMesh(mark);
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
    HashSet<Integer> pressed = new HashSet<>();
    boolean justPressed(int k){
        if (pressed.contains(k))
            return false;
        //pressed.clear();
        if (Renderer.getKey(k) == GLFW_PRESS)
            pressed.add(k);
        else
            return false;
        try {
            //System.out.println("start build");
            new Thread( () -> {
                while (Renderer.getKey(k) != GLFW_RELEASE);
                pressed.remove(k);
            }).start();

        } catch (Exception e){}
        return true;
    }
    void useDevelopmentCard(){
        turnPlayer.DevelopmentCards.current().data.use(this); // uses current development card;
        //Renderer.meshes2d.remove()
        toggleVisible(turnPlayer.DevelopmentCards);
        turnPlayer.DevelopmentCards.remove(turnPlayer.DevelopmentCards.current());
        toggleVisible(turnPlayer.DevelopmentCards);
    }
    void selectCurrentTradeCard(){
        turnPlayer.OpenTrade.select(); // selects current
    }
    void selectCurrentResourceCard(){
        turnPlayer.ResourceCards.select(); // selects current
    }
    void openTradingInventory(){
        ;//start trade or select or confirm idk im not done yet
        for (Player p : players) {
            boolean a = p.ResourceCards.visible;
            if (a)
                toggleVisible(p.ResourceCards);
            p.updateResourcesToCards();
            if (a)
                toggleVisible(p.ResourceCards);
        }
        toggleVisible(turnPlayer.OpenTrade);
        turnPlayer.OpenTrade.clear();
        turnPlayer.OpenTrade.addAll(turnPlayer.TradingCards.current().data);
        toggleVisible(turnPlayer.OpenTrade);
    }
    void tradeCurrentSelectedCards(){
        boolean a = turnPlayer.ResourceCards.visible, b = turnPlayer.OpenTrade.visible;
        if (a)
            toggleVisible(turnPlayer.ResourceCards);
        if (b)
            toggleVisible(turnPlayer.OpenTrade);

        turnPlayer.ResourceCards.trade(turnPlayer.OpenTrade);
        turnPlayer.updateCardsToResources();

        if (a)
            toggleVisible(turnPlayer.ResourceCards);
        if (b)
            toggleVisible(turnPlayer.OpenTrade);
    }
    private void StartSetUpThread(){
        try {
            //System.out.println("start build");
            new Thread( () -> {
                SetUp();
            }).start();

        } catch (Exception e){}
    }
    public static void main(String[] args) {
        Catan catan = new Catan();
        catan.run();
    }
}
