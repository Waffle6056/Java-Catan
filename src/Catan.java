import CardStructure.*;
import CardStructure.RenderingStuff.CatanWindow;
import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.Renderable;
import CardStructure.RenderingStuff.Renderable2d;
import org.joml.*;
import org.joml.Math;

import java.util.HashMap;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Catan implements Renderable, Renderable2d {
    int playersCount = 2;
    public CatanWindow Renderer;
    Board Board;
    CardHolder<Integer, DieCard<Integer>> dice = new CardHolder<>();
    List<Player> players;
    Player turnPlayer;
    BankHolder<Hex.resource> Bank;
    int turnInd;
    Mesh cursor, sky, ocean;
    RobberBaron robber;
    //GuideHolder guideElement = new GuideHolder(null);

    public java.util.List<Mesh> toMesh(){
        java.util.List<Mesh> meshList = new ArrayList<>();
        meshList.addAll(Board.toMesh());
        meshList.addAll(robber.toMesh());
        if (cursor != null)
            meshList.add(cursor);
//        if (sky != null)
//            meshList.add(sky);
        if (ocean != null)
            meshList.add(ocean);
        return meshList;
    }
    public java.util.List<Mesh> toMesh2d(){
        java.util.List<Mesh> meshList = new ArrayList<>();
        meshList.addAll(turnPlayer.toMesh2d());
        meshList.addAll(robber.toMesh2d());
        //meshList.addAll(guideElement.toMesh2d());
        meshList.addAll(dice.toMesh2d());
        return meshList;
    }


    enum BuildingOption{
        Road("CatanCardMeshes/BuildingCards/DevelopmentCard.fbx"),
        Town("CatanCardMeshes/BuildingCards/SettlementCard.fbx"),
        City("CatanCardMeshes/BuildingCards/CityCard.fbx"),
        DevelopmentCard("CatanCardMeshes/BuildingCards/DevelopmentCard.fbx");
        public final String CardFile;
        BuildingOption(String CardFile){
            this.CardFile = CardFile;
        }
    }

    Phase currentPhase = Phase.SetUp;
    enum Phase{
        SetUp,
        BuildingTrading,
        Nothing,
    }




    public void run(){
        Scanner in = new Scanner(System.in);
        System.out.println("enter player count(2-4) : ");
        playersCount = in.nextInt();


        Renderer = new CatanWindow();
        Renderer.run();

        Board = new Board();
        robber = new RobberBaron(this);
        for (int i = 0; i < 2; i++)
            dice.add(new DieCard(i));

        DevelopmentCard.createDeck();
        setupMiscVisuals();

        bindKeys();
        currentPhase = Phase.SetUp;

        setupBank();
        setupPlayers();


        StartSetUpPhaseThread();
//        turnPlayer = players.get(0);
//        turnInd = -1;
//        nextPlayerTurn();
        loop();
    }
    void setupBank(){
        Bank = new BankHolder<>(TradingRequirementFunction::AnyMatchingAmount);
        for (Card<Hex.resource> c : PortHolder.defaultInventory(5))
            Bank.addPermanent(c);
        for (int j = 0; j < 4; j++)
            Bank.TradeRequirements.add(new Card<>(Hex.resource.Desert, "CatanCardMeshes/Special/Arrow.fbx"));

    }
    void setupPlayers(){
        players = new ArrayList<>();
        // loads meshes
        for (int i = 0; i < playersCount; i++) {
            players.add(new Player("PLAYER "+i, Player.ModelSet.values()[i]));
        }

        for (int i = 0; i < playersCount; i++){
            Player p = players.get(i);

            //loads each player's inventory into the current player's trading options
            for (int j = 0; j < playersCount; j++) {
                Player b = players.get(j);
                if (p != players.get(j))
                    p.TradingCards.add(new Card<>(b.ResourceCards, b.markFile));
            }

            //loads resources for setup phase
            int[] ar = {4,2,0,4,2};
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < ar[j]; k++) {
                    p.ResourceCards.add(Hex.resource.createResourceCard(Hex.resource.values()[j]));
                }
            }
            p.TradingCards.add(new Card<>(Bank, "CatanCardMeshes/Special/Arrow.fbx"));

        }
    }
    void setupMiscVisuals(){
        cursor = new Mesh("CatanCardMeshes/Special/Arrow.fbx");
        //cursor.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        cursor.scale.mul(1f,1f,1f);

        sky = new Mesh("HexMeshes/SkySphere.fbx");
        sky.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        sky.position.add(1.9555556f,0f,3.288889f);

        ocean = new Mesh("HexMeshes/Ocean.fbx");
        ocean.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        ocean.position.add(1.9555556f,-.3f,3.288889f);
        ocean.scale.mul(10,10,1);


        Mesh m = new Mesh("HexMeshes/SkySphere.fbx");
        m.position.add(0,0,0.1f);
        m.scale.mul(0.00005f,0.00005f,0.00005f);
        //m.rotation.lookAlong(m.position,new Vector3f(0,1,0)).rotateLocalX((float) java.lang.Math.toRadians(90));
        //m.rotation.rotateAxis(Math.toRadians(90),1,0,0);
//        guideElement.build(this);
//        guideElement.position = new Vector3f(0f,-0.2f,1f);
//        guideElement.len = 0.2f;
//        guideElement.add(null, "CatanCardMeshes/Special/BuildingCost.fbx");
//        guideElement.add(null, "CatanCardMeshes/Special/Controls.fbx");

        dice.cardPositioner = CardPositioner::setLinearTransforms;
        dice.len = .3f;
        dice.dis = 0;
        dice.position = new Vector3f(dice.len*dice.Cards.size()/2f-dice.len/2.0f, .75f,5f);

        dice.toMesh2d();

        for (DieCard<Integer> dieCard : dice.Cards)
            System.out.println(dieCard.mesh.position);

        Renderer.camera.camPos.add(1.9555556f,3f,3.288889f);
    }
    private void SetUp(){

        Hex.roadRequirementOverride = true;
        ArrayList<Building> roundone =new ArrayList<>();
        int n = players.size()*2;
        for (int i = 0; i < n; i++) {
            int ind = Math.min(n-i-1, i);
            turnPlayer = players.get(ind);
            turnInd = ind;
            System.out.println("PLAYER "+ind);


            System.out.println("BUILD A TOWN");
            Building[] out = new Building[1];
            while (!Board.build(BuildingOption.Town, turnPlayer, this, out))
                System.out.println("failed try again");

            waitMouseRelease();

            System.out.println("BUILD A ROAD");
            while (!Board.build(BuildingOption.Road, turnPlayer, this))
                System.out.println("failed try again");

            if (i<playersCount){
                roundone.add(out[0]);
                out[0].resourcegain=0;
            }
        }
        for (int j = 2; j < 12; j++)
            if (j != 7)
                Board.rolled(j);
        for (int i = 0; i < roundone.size(); i++)
            roundone.get(i).resourcegain=1;

        Hex.roadRequirementOverride = false;
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
            robber.rob();
            Renderer.meshes = toMesh();
            Renderer.meshes2d = toMesh2d();
            Renderer.textMeshes2d = new ArrayList<>();
//            if (test == null) {
//                test = new Mesh("giasfelfebrehber",.005f);
//                test.position = new Vector3f(0,0,1);
//                test.scale = new Vector3f(.005f,.005f,.005f);
//            }
//            Renderer.textMeshes2d.add(test);

            Renderer.update(delta);
            //Renderer.getMousePos();
            lastFrame = currentFrame;
        }
        Renderer.terminate();
    }


    void nextPlayerTurn(){
        Player.redopoints(players);
        if (turnPlayer.hasWon()){
            System.out.println(turnPlayer.name+" HAS WON!");
            currentPhase = Phase.Nothing;
            return;
        }

        turnInd = (turnInd+1)%players.size();
        turnPlayer = players.get(turnInd);
        turnPlayer.developmentCardLimit = 1;
        turnPlayer.ActiveVisibilityLayer = CardHolder.VisibilityLayer.Building.bit;
        for (Card<DevelopmentCard> c : turnPlayer.DevelopmentCards.Cards)
            c.data.enabled = true;

        roll();
    }

    void roll(){
        int sum = 0;
        for (DieCard<Integer> dieCard : dice.Cards)
            sum += dieCard.roll();
        Board.rolled(sum);

        if (sum==7)
            robber.moveRobberBaron();
        else
            currentPhase = Catan.Phase.BuildingTrading;

    }



    double acuYPos = 0;
    void MouseWheelBinds(long win, double xpos, double ypos){
        //System.out.println(ypos);
        UIElementPair hovered = selectUIElement();
        if (hovered.container  != null) {
            acuYPos += ypos;
            hovered.container.scroll((int) (acuYPos) / 2);
            acuYPos %= 2;
        }
        else
            Renderer.camera.scrollMovement(win,xpos,ypos);
    }
    void CursorMovementBinds(long win, double xpos, double ypos){
        Renderer.camera.cursorMovement(win,xpos,ypos);
    }

    void selectAndInteract(){
        UIElementPair hoveredPair = selectUIElement();
        CardHolder container = hoveredPair.container;
        Card card = hoveredPair.card;
        if (container == null || card == null)
            return;
        int i = container.Cards.size();
        while (container.current() != card && i-- > 0)
            container.scroll(1);

        CardHolder<CardHolder, UICard> elems = turnPlayer.UIElements;
        elems.get(elems.indexOf(container)).use(this, turnPlayer);
    }


    void MouseButtonBind(long win, int button, int action, int mods){
        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS && currentPhase == Phase.BuildingTrading)
            selectAndInteract();
    }

    void RollingPhaseKeybinds(int key){
        switch (key) {
            case GLFW_KEY_Z:
                //turnPlayer.DevelopmentCards.toggleVisible();
                turnPlayer.ActiveVisibilityLayer = CardHolder.VisibilityLayer.DevelopmentCard.bit;
                break;

            case GLFW_KEY_X:
                //turnPlayer.ResourceCards.toggleVisible();
                turnPlayer.ActiveVisibilityLayer = CardHolder.VisibilityLayer.Building.bit;
                break;

            case GLFW_KEY_C:
                //turnPlayer.TradingCards.toggleVisible();
                turnPlayer.ActiveVisibilityLayer = CardHolder.VisibilityLayer.Trading.bit;
                break;

            case GLFW_KEY_R:
                selectAndInteract();
        }
    }


    void BuildingTradingPhaseKeybinds(int key) {
        if (key == GLFW_KEY_1) {
            startBuildThread(BuildingOption.Road);
        }
        if (key == GLFW_KEY_2) {
            startBuildThread(BuildingOption.Town);
        }
        if (key == GLFW_KEY_3) {
            startBuildThread(BuildingOption.City);
        }

//        if (key == GLFW_KEY_4) {
//            buyDevelopmentCard();
//        }
//
        if (key == GLFW_KEY_F)
            Player.tradeCurrentSelectedCards(turnPlayer.ResourceCards, this, turnPlayer);

        if (key == GLFW_KEY_ENTER) {
            nextPlayerTurn();
        }
    }

    void bindKeys(){
        Renderer.bindScrollCallback(this::MouseWheelBinds);
        Renderer.bindCursorPosCallback(this::CursorMovementBinds);
        Renderer.bindMouseCallback(this::MouseButtonBind);
        Renderer.bindCallback((window, key, scancode, action, mods) -> {
            if (!justPressed(key))
                return;

            if (currentPhase == Phase.SetUp || currentPhase == Phase.BuildingTrading)
                RollingPhaseKeybinds(key);


            if (currentPhase == Phase.BuildingTrading)
                BuildingTradingPhaseKeybinds(key);

        });
    }

    Hex.resource selectResource(){
        while (Renderer.getKey(GLFW_KEY_R) == GLFW_PRESS);
        while (Renderer.getKey(GLFW_KEY_R) != GLFW_PRESS && turnPlayer.OpenTrade.current().data != null);
        return ((Hex.resource)turnPlayer.OpenTrade.current().data.data);
    }




    record UIElementPair(CardHolder container, Card card){}
    UIElementPair selectUIElement() {
        float res = Float.MAX_VALUE;
        CardHolder hoveredContainer = null;
        Card hoveredCard = null;
        float cur = 0;
        for (CardHolder container : turnPlayer.UIElements.CardData()){
            if ((container.VisibilityLayers & turnPlayer.ActiveVisibilityLayer) == 0)
                continue;
            List<Card> listCards = container.Cards;
            for (Card m : listCards) {
                //Mesh m = meshes2d.get(0);
                //System.out.println(m.file);
                //System.out.println((cur = m.rayIntersects(mousePos, camera2d.camDir)) >= 0);
                if ((cur = Renderer.hovered2D(m.mesh)) < res && cur != -1) {
                    res = cur;
                    hoveredContainer = container;
                    hoveredCard = m;
                    //System.out.println(res + " " + m.file);
                }
            }
        }
//        if (hoveredContainer != null)
//            System.out.println(hoveredContainer+" "+hoveredCard.mesh.file);
        return new UIElementPair(hoveredContainer,hoveredCard);
    }
    Hex selectHex(Vector3f mousePos){ // selects closest hex
        HashMap<String , Hex> grid = Board.grid;
        Hex closest = grid.get(Board.encoder(0,0,0));
        float oDis = Float.MAX_VALUE;
        for (String k : grid.keySet()){
            Hex current = grid.get(k);
            Vector3f cPos = current.mesh.position;
            float cDis = cPos.distance(mousePos);
            if (cDis < oDis){
                closest = current;
                oDis = cDis;
            }
        }
        return closest;
    }
    Hex.HexBuilding selectVertex(Hex hex, Vector3f mousePos){
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

        return Hex.HexBuilding.values()[out];
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


    private void StartSetUpPhaseThread(){
        try {
            //System.out.println("start build");
            new Thread( () -> {
                SetUp();
            }).start();

        } catch (Exception e){}
    }
    void startBuildThread(BuildingOption Option){
        currentPhase = Phase.SetUp;
        turnPlayer.ActiveVisibilityLayer = 0;
        try {
            //System.out.println("start build");
            new Thread( () -> {
                Board.build(Option,turnPlayer,this);
                currentPhase = Phase.BuildingTrading;
                turnPlayer.ActiveVisibilityLayer = CardHolder.VisibilityLayer.Building.bit;
            }).start();

        } catch (Exception e){}
    }

    public static void main(String[] args) {
        Catan catan = new Catan();
        catan.run();
    }
}
