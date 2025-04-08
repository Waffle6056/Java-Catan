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
    int playersCount = 4;
    CatanWindow Renderer;
    NewBoard Board;
    List<Player> players;
    Player turnPlayer;
    int turnInd;
    Mesh cursor, sky, ocean, robber;

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
            "CatanCardMeshes/PlayerOne.fbx",
            "CatanCardMeshes/PlayerTwo.fbx",
            "CatanCardMeshes/PlayerThree.fbx",
            "CatanCardMeshes/PlayerFour.fbx",
    };
    String[] RoadFiles = {
            "Buildings/RoadOne.fbx",
            "Buildings/RoadTwo.fbx",
            "Buildings/RoadThree.fbx",
            "Buildings/RoadFour.fbx",
    };
    String[] CityFiles = {
            "Buildings/CityOne.fbx",
            "Buildings/CityTwo.fbx",
            "Buildings/CityThree.fbx",
            "Buildings/CityFour.fbx",
    };

    String[] SettlementFiles = {
            "Buildings/SettlementOne.fbx",
            "Buildings/SettlementTwo.fbx",
            "Buildings/SettlementThree.fbx",
            "Buildings/SettlementFour.fbx",
    };
    CardHolder guideElement = new CardHolder(null);

    Queue<Building> MeshQueue = new LinkedList<>();
    Queue<Road> MeshQueueRoad = new LinkedList<>();

    Queue<Player> victims = new LinkedList<>();

    public void run(){
        Renderer = new CatanWindow();
        Renderer.run();
        Board = new NewBoard();

        cursor = new Mesh("CatanCardMeshes/Special/Arrow.fbx");
        //cursor.rotation.rotateX((float)java.lang.Math.toRadians(-90));
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

        Mesh m = new Mesh("HexMeshes/SkySphere.fbx");
        m.position.add(0,0,0.1f);
        m.scale.mul(0.00005f,0.00005f,0.00005f);
        //m.rotation.lookAlong(m.position,new Vector3f(0,1,0)).rotateLocalX((float) java.lang.Math.toRadians(90));
        //m.rotation.rotateAxis(Math.toRadians(90),1,0,0);
        Renderer.addMesh2d(m);

        robber = new Mesh("HexMeshes/Robber.fbx");
        robber.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        robber.position.add(0,-5,0);
        Renderer.addMesh(robber);

        guideElement.position = new Vector3f(0f,-0.2f,.5f);
        guideElement.len = 0.2f;
        guideElement.add(null, "CatanCardMeshes/Special/BuildingCost.fbx");
        guideElement.add(null, "CatanCardMeshes/Special/Controls.fbx");
        Renderer.addMesh2d(m);

        bindKeys();
        currentPhase = Phase.SetUp;
        DevelopmentCard.createDeck();

        System.out.println("SELECT PLAYER COUNT INTERFACE HERE");
        System.out.println("temp player count = "+playersCount);


        players = new ArrayList<>();
        for (int i = 0; i < playersCount; i++) {
            players.add(new Player("PLAYER "+i,PlayerCards[i]));
            players.get(i).roadFile = RoadFiles[i];
            players.get(i).cityFile = CityFiles[i];
            players.get(i).settlementFile = SettlementFiles[i];
        }


        Player Bank = new Player("BANK", "CatanCardMeshes/Special/Arrow.fbx");
        Bank.resources = new int[]{5,5,5,5,5};
        Bank.updateResourcesToCards();
        for (int i = 0; i < playersCount; i++){
            //p.updateResourcesToCards();
            Player p = players.get(i);
            for (int j = 0; j < playersCount; j++) {
                Player b = players.get(j);
                if (p != players.get(j))
                    p.TradingCards.add(b.ResourceCards, b.markFile);
            }
            p.TradingCards.add(Bank.ResourceCards, Bank.markFile);

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
            System.out.println("PLAYER "+ind);

            System.out.println("BUILD A ROAD");
            while (!build(BuildingOption.Road))
                System.out.println("failed try again");

            waitMouseRelease();

            Building[] out = new Building[1];
            System.out.println("BUILD A TOWN");
            while (!build(BuildingOption.Town, out))
                System.out.println("failed try again");

            if (i == players.size()-1) // adds resources when halfway
                for (int j = 2; j < 12; j++)
                    if (j != 7)
                        Board.rolled(j);

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
            rob();
            Renderer.update(delta);
            //Renderer.getMousePos();
            lastFrame = currentFrame;
        }
        Renderer.terminate();
    }

    void nextPlayerTurn(){
        //turnPlayer.updateCardsToResources();
        Renderer.removeMeshes2d(turnPlayer.UIElements.getMeshes());
        for (Card<CardHolder> cardElement : turnPlayer.UIElements.Cards) {
            CardHolder element = cardElement.data;
            toggleVisible(element, false);
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
                robber.position = new Vector3f(hex.mesh.position.x, 2.75f, hex.mesh.position.z);
                //Steal from near Player
                for (int i = 0; i < 6; i++)
                    if (hex.buildings[i].owner != null){
                        Player victim = hex.buildings[i].owner;
                        victims.add(victim);
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
    void bindKeys(){
        Renderer.bindCallback((window, key, scancode, action, mods) -> {
            if (currentPhase == Phase.BuildingTrading) {
                if (!justPressed(key))
                    return;

                if (key == GLFW_KEY_1) {
                    toggleVisible(turnPlayer.ResourceCards, false);
                    startBuildThread(BuildingOption.Road);
                }
                if (key == GLFW_KEY_2){
                    toggleVisible(turnPlayer.ResourceCards, false);
                    startBuildThread(BuildingOption.Town);
                }
                if (key == GLFW_KEY_3){
                    toggleVisible(turnPlayer.ResourceCards, false);
                    startBuildThread(BuildingOption.City);
                }
                if (key == GLFW_KEY_4 && payCheck(0,1,1,0,1)) {
                    pay(0,1,1,0,1);
                    toggleVisible(turnPlayer.ResourceCards, false);
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
                    int lazy = 0;
                    do {
                        turnPlayer.UIElements.select(false);
                        turnPlayer.UIElements.scroll(1); // scroll ui elements
                        turnPlayer.UIElements.select(true);
                        lazy++;
                    } while (!turnPlayer.UIElements.current().data.visible && lazy < 10);
                    //System.out.println(turnPlayer.UIElements.getMeshes().size());
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

                if (key == GLFW_KEY_GRAVE_ACCENT)
                    toggleVisible(guideElement);



                if (key == GLFW_KEY_ENTER)
                    nextPlayerTurn();
            }
        });
    }
    boolean build(BuildingOption Option) {
        return build(Option, new Building[1]);
    }
    boolean build(BuildingOption Option, Building[] out) {

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
                    Renderer.removeMesh(hex.buildings[ver.index].mesh);
                    turnPlayer.settlements++;
                    turnPlayer.cities--;
                }
                else
                    turnPlayer.settlements--;
                MeshQueue.add(hex.buildings[ver.index]);
                //System.out.println("COMPLETED BUILDING "+hex.buildings[ver.index].type);
                out[0] = hex.buildings[ver.index];
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

            Road[] t = new Road[1];
            if (hex1.constructRoads(ver1, hex2, ver2, BuildingOption.Road, turnPlayer, t)){
                MeshQueueRoad.add(t[0]);
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
    void toggleVisible(CardHolder c, boolean val){
        if (c == null)
            return;
        //System.out.println("toggle "+c);
        c.toggleVisible(val);
        if (c.visible && val) {
            Renderer.addMeshes2d(c.getMeshes());
        }
        else if (!c.visible && !val)
            Renderer.removeMeshes2d(c.getMeshes());
    }
    void toggleVisible(CardHolder c){
        if (c == null)
            return;
        //System.out.println("toggle "+c);
        c.toggleVisible();
        if (c.visible) {
            Renderer.addMeshes2d(c.getMeshes());
        }
        else
            Renderer.removeMeshes2d(c.getMeshes());
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
    void rob(){
        while (!victims.isEmpty()) {
            Player victim = victims.remove();
            System.out.println("ROBBED "+victim.name);

            toggleVisible(turnPlayer.ResourceCards, false);
            toggleVisible(victim.ResourceCards, false);

            victim.updateResourcesToCards();
            if (victim.ResourceCards.Cards.isEmpty())
                continue;
            victim.ResourceCards.scroll((int) (Math.random() * 200));
            victim.ResourceCards.select();
            System.out.println(victim.ResourceCards.current().data);

            trade(victim.ResourceCards);
        }
    }
    void InstantiateBuildingMeshes(){ // HAS TO RUN ON THE MAIN THREAD
        while (!MeshQueue.isEmpty()) {
            Building b = MeshQueue.remove();
            String file = "";
            //System.out.println(b.type);
            switch (b.type){
                case City -> file = b.owner.cityFile;
                case Town -> file = b.owner.settlementFile;
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
            String file = b.owner.roadFile;
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
        toggleVisible(turnPlayer.DevelopmentCards, false);
        turnPlayer.DevelopmentCards.remove(turnPlayer.DevelopmentCards.current());
        toggleVisible(turnPlayer.DevelopmentCards, true);
    }
    void selectCurrentTradeCard(){
        turnPlayer.OpenTrade.select(); // selects current
    }
    void selectCurrentResourceCard(){
        turnPlayer.ResourceCards.select(); // selects current
    }
    void openTradingInventory() {
        openTradingInventory(turnPlayer.TradingCards.current().data);
    }
    void openTradingInventory(CardHolder<NewHex.resource> inv){
        ;//start trade or select or confirm idk im not done yet
        toggleVisible(turnPlayer.OpenTrade, false);
        turnPlayer.OpenTrade.clear();
        inv.owner.updateResourcesToCards();
        turnPlayer.OpenTrade.addAll(inv);
        turnPlayer.OpenTrade.owner = inv.owner;
        toggleVisible(turnPlayer.OpenTrade, true);
    }
    void trade(CardHolder<NewHex.resource> inv){
        boolean a = turnPlayer.ResourceCards.visible, b = inv.visible;
        if (a)
            toggleVisible(turnPlayer.ResourceCards);
        if (b)
            toggleVisible(inv);

        turnPlayer.ResourceCards.trade(inv);
        turnPlayer.updateCardsToResources();

        Player other = inv.owner;
        if (other.ResourceCards != inv) {
            other.ResourceCards.clear();
            other.ResourceCards.addAll(inv);
        }
        other.updateCardsToResources();

        if (a)
            toggleVisible(turnPlayer.ResourceCards);
        if (b)
            toggleVisible(inv);
    }

    void tradeCurrentSelectedCards(){
        trade(turnPlayer.OpenTrade);
    }
    private void StartSetUpThread(){
        try {
            //System.out.println("start build");
            new Thread( () -> {
                SetUp();
            }).start();

        } catch (Exception e){}
    }
    void startBuildThread(BuildingOption Option){
        try {
            //System.out.println("start build");
            new Thread( () -> {
                build(Option);
            }).start();

        } catch (Exception e){}
    }
    public static void main(String[] args) {
        Catan catan = new Catan();
        catan.run();
    }
}
