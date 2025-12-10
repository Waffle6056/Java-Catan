import java.awt.*;
import java.io.*;
import java.util.*;

import CardStructure.BankHolder;
import CardStructure.Card;
import CardStructure.CardHolder;
import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.Renderable;
import org.joml.Vector3f;


public class Board implements Renderable {
    HashMap<String , Hex> grid;
    HashMap<Integer, ArrayList<Hex>> numbers;
    java.util.List<Mesh> ports = new ArrayList<>();
    java.util.List<Mesh> reqSig = new ArrayList<>();

    public Board() {
        grid=new HashMap<>();
        try {
            makeDefaultBoard(3, 3, 3, "TileAmounts.dat", "DiceTiles.dat");
        }
        catch (Exception e){
            System.out.println("New Board failed  :"+e);
        }
    }
    //q + r + s = 0
    //Neighbors
    //(q + 1, r - 1, s)
    //(q + 1, r, s - 1)
    //(q, r + 1, s - 1)
    //(q - 1, r + 1, s)
    //(q - 1, r, s + 1)
    //(q, r - 1, s + 1)


    // edge traversal directions
    // right, down right, down left, left, up left, up right
    static int[] deltaQ = new int[]{-1,-1,0,1,1,0};
    static int[] deltaR = new int[]{1,0,-1,-1,0,1};
    static int[] deltaS = new int[]{0,1,1,0,-1,-1};

    boolean build(Catan.BuildingOption option, Player turnPlayer, Catan instance) {
        return build(option, turnPlayer, instance, new Building[1]);
    }

    boolean build(Catan.BuildingOption option, Player turnPlayer, Catan instance, Building[] out) {

        //check if required resource amount
        // return false if not enought
        if (!turnPlayer.checkAmt(option))
            return false;

        if (option == Catan.BuildingOption.Town || option == Catan.BuildingOption.City){
            return constructTownCity(option, turnPlayer, instance, out);
        }
        else if (option == Catan.BuildingOption.Road){
            return constructRoad(option, turnPlayer, instance);
        }
        return false;
    }

    record buildingSlot(Hex hex, Hex.HexBuilding ver){}

    buildingSlot selectSlot(Catan instance){
        instance.waitMouseRelease();

        Vector3f mouseClickPos = instance.waitMouseClick();
        Hex hex = instance.selectHex(mouseClickPos);
        Hex.HexBuilding ver = instance.selectVertex(hex, mouseClickPos);
        return new buildingSlot(hex, ver);
    }

    boolean constructTownCity(Catan.BuildingOption option, Player turnPlayer, Catan instance, Building[] out){
        if (option == Catan.BuildingOption.Town && turnPlayer.settlements == 0)
            return false;
        if (option == Catan.BuildingOption.City && turnPlayer.cities == 0)
            return false;

        buildingSlot slot = selectSlot(instance);
        Hex hex = slot.hex;
        Hex.HexBuilding ver = slot.ver;


        if (hex.constructBuilding(ver, option, turnPlayer)) {
            turnPlayer.pay(option);
            if (option == Catan.BuildingOption.City) {
                turnPlayer.settlements++;
                turnPlayer.cities--;
            }
            else
                turnPlayer.settlements--;

            turnPlayer.vp++;
            out[0] = hex.buildings[ver.index];
            out[0].updateOnRender();
            if (out[0].ConnectingPort != null)
                turnPlayer.TradingCards.add(out[0].ConnectingPort);
            return true;
        }
        return false;
    }

    boolean constructRoad(Catan.BuildingOption option, Player turnPlayer, Catan instance){

        buildingSlot slot1 = selectSlot(instance);
        Hex hex1 = slot1.hex;
        Hex.HexBuilding ver1 = slot1.ver;

        buildingSlot slot2 = selectSlot(instance);
        Hex hex2 = slot2.hex;
        Hex.HexBuilding ver2 = slot2.ver;


        Road[] t = new Road[1];
        if (hex1.constructRoads(ver1, hex2, ver2, Catan.BuildingOption.Road, turnPlayer, t)){
            t[0].updateOnRender();
            turnPlayer.pay(option);
            return true;
        }
        else
            return false;
    }

    void loadTileCounts(String tileAmounts, ArrayList<Integer> tiles, ArrayList<String> names)  throws FileNotFoundException {
        Scanner fileTiles=new Scanner(new File(tileAmounts));
        int times=fileTiles.nextInt();
        fileTiles.nextLine();
        numbers=new HashMap<>();
        int d=0;
        while (times-->0){
            names.add(fileTiles.next()); fileTiles.next();
            tiles.add(fileTiles.nextInt());
            fileTiles.nextLine();
        }
    }

    void placeRandomTile(int q, int r, int s, ArrayList<Integer> tiles, ArrayList<String> names){
        int random=(int)(Math.random()*tiles.size());
        int temp=tiles.get(random);

        grid.putIfAbsent(encoder(q,r,s),new Hex(q,r,s,names.get(random)));
        temp--;
        tiles.set(random,temp);
        if (temp==0){
            tiles.remove(random);
            names.remove(random);
        }
    }

    void fillGrid(int dimension1,int dimension2,int dimension3, String tileAmounts, String diceTiles) throws FileNotFoundException{
        ArrayList<Integer> tiles=new ArrayList<>();
        ArrayList<String> names=new ArrayList<>();
        loadTileCounts(tileAmounts, tiles, names);
        //System.out.println("step 1");
        for (int f = 0; f < dimension1; f++) {
            for (int i = 0; i < dimension3+2-f; i++) {
                int q=-f-i,r=f,s=i;
                placeRandomTile(q,r,s,tiles,names);
            }
            // System.out.println("step 2");
        }
        for (int f = 1; f < dimension2; f++) {
            for (int i = 0; i < dimension3 + 2 - f; i++) {
                int q = -i, r = -f, s = f + i;
                placeRandomTile(q,r,s,tiles,names);
            }
        }
        fillDiceNumbers(diceTiles);
    }
    void combineHexReferences(){
        for(String k:grid.keySet()){
            String[] line=k.split(",");
            int q=Integer.parseInt(line[0]), r=Integer.parseInt(line[1]), s=Integer.parseInt(line[2]);

            for (int i = 0; i < 6; i++){
                int nq = q+deltaQ[i],nr = r+deltaR[i], ns = s+deltaS[i];
                // side index starts at top right edge but this traversal starts right edge for number generation so its offset by one
                int sideIndex = (i+1)%6;
                if (grid.containsKey(encoder(nq,nr,ns))){
                    grid.get(k).combineHex(getHex(nq,nr,ns),sideIndex);
                }
            }
        }
    }
    void connectRoads(){
        for(String k:grid.keySet()){
            grid.get(k).connectRoads();
        }
    }
    void setBuildingPositions(){
        for(String k:grid.keySet()){
            Hex hex = grid.get(k);
            for (Hex.HexBuilding vertex : Hex.HexBuilding.values())
                hex.buildings[vertex.index].setPos(hex,vertex);
        }
    }
    void fillDiceNumbers(String diceTiles) throws FileNotFoundException{
        int q = 1, r = -1, s = 0, dir=0;
        Scanner dice=new Scanner(new File(diceTiles));
        int times=dice.nextInt();dice.nextLine();
        HashSet<String> vist=new HashSet<>();
        for (int i = 2; i <= 12; i++) {
            numbers.putIfAbsent(i, new ArrayList<>());
        }

        for (int i = 0; i < times; i++) {
            int nQ = q+deltaQ[dir];
            int nR = r+deltaR[dir];
            int nS = s+deltaS[dir];
            while (vist.contains(encoder(nQ,nR,nS)) || !grid.containsKey(encoder(nQ,nR,nS))){
                dir = (dir+1)%6;
                nQ = q+deltaQ[dir];
                nR = r+deltaR[dir];
                nS = s+deltaS[dir];
            }
            q = nQ;
            r = nR;
            s = nS;
            vist.add(encoder(q,r,s));


            Hex hex= grid.get(encoder(q,r,s));
            if (hex.type== Hex.resource.Desert){ // desert tile isnt assigned a number
                i--;continue;
            }

            String HexAssignment=dice.nextLine();
            int number = Integer.parseInt(HexAssignment.split(" ")[2]);
            hex.tostring=HexAssignment;
            hex.setDicenumber(number);
            numbers.get(number).add(hex);
        }
    }
    void initializePorts(){
        int q = 1, r = -1, s = 0, dir=0;
        //port locations dont change
        int[] PortSideIndexes = new int[]{5,0,-1,0,1,2,-1,2,3,4,-1,4};
        for (int i = 0; i < PortSideIndexes.length; i++) {
            int nQ = q + deltaQ[dir];
            int nR = r + deltaR[dir];
            int nS = s + deltaS[dir];
            while (!grid.containsKey(encoder(nQ, nR, nS))) {
                dir = (dir + 1) % 6;
                nQ = q + deltaQ[dir];
                nR = r + deltaR[dir];
                nS = s + deltaS[dir];
            }
            q = nQ;
            r = nR;
            s = nS;

            if (PortSideIndexes[i] == -1)
                continue;
            Hex hex= grid.get(encoder(q,r,s));
            addPort(hex, PortSideIndexes[i]);
        }
    }
    void addPort(Hex hex, int sideIndex){
        int ind1 = sideIndex;
        int ind2 = Math.floorMod(sideIndex+1,6);

        Building p1 = hex.buildings[ind1];
        Building p2 = hex.buildings[ind2];

        BankHolder<Hex.resource> port = PortHolder.generatePort();
        Card<CardHolder<Hex.resource, Card<Hex.resource>>> card = PortHolder.generatePortCard(port);
        p1.ConnectingPort = card;
        p2.ConnectingPort = card;

        Vector3f position = new Vector3f((p1.x+p2.x)/2,0,(p1.y+p2.y)/2);
        addPortMesh(port, hex, position);
        addRequirementSignatures(port, position);
    }
    Mesh generateRequirementSignature(BankHolder<Hex.resource> port, Card<Hex.resource> c, int cardPosition){
        Mesh req = new Mesh(c.file);

        req.scale.mul(3);

        float angle = (float)Math.toRadians(Math.min(180f/port.TradeRequirements.size(), 180f/7f)) * cardPosition;
        req.rotation.rotateY(angle+ (float)Math.toRadians(180f));

        req.position.add(0, 2+cardPosition*0.05f, 0);
        float len = 0.3f;
        Vector3f rotated = new Vector3f(0,0,-len).rotateY(angle);
        req.position.add(rotated);
        //req.rotation.rotateX((float) Math.toRadians(-90));

        return req;
    }
    void addRequirementSignatures(BankHolder<Hex.resource> port, Vector3f position){
        int midInd = port.TradeRequirements.size() / 2;
        int j = 0;
        for (Card<Hex.resource> reqResource : port.TradeRequirements) {
            int cardPosition = j - midInd;
            Mesh cardMesh = generateRequirementSignature(port, reqResource, cardPosition);
            cardMesh.position.add(position);
            reqSig.add(cardMesh);
            j++;
        }
        //System.out.println(new Vector3f(1,0,0).angleSigned(v,new Vector3f(0,0,1))+" "+v+" ("+temp.x+", "+temp.y+")"+m.position+" "+cardFile);
    }
    void addPortMesh(BankHolder<Hex.resource> port, Hex hex, Vector3f position){
        Mesh m = new Mesh("Buildings/Port.fbx");
        m.position.add(position);
        Vector3f v = new Vector3f(-hex.x + position.x, hex.y - position.z, 0).normalize();
        m.rotation.rotateY(new Vector3f(1,0,0).angleSigned(v,new Vector3f(0,0,1)));
        m.rotation.rotateX((float) Math.toRadians(-90));


        //req.rotation = new Quaternionf(m.rotation);

        ports.add(m);
    }

    public void makeDefaultBoard(int dimension1,int dimension2,int dimension3,String tileAmounts,String diceTiles)  throws FileNotFoundException {

        fillGrid(dimension1,dimension2,dimension3,tileAmounts,diceTiles);

        combineHexReferences();
        connectRoads();
        setBuildingPositions();

        initializePorts();
    }
    public String encoder(int q,int r,int s){
        return q+","+r+","+s;
    }
    public Hex getHex(int q, int r, int s){
        return grid.get(encoder(q,r,s));
    }
    @Override
    public java.util.List<Mesh> toMesh(){
        java.util.List<Mesh> meshList = new ArrayList<>();
        //System.out.println("GET MESHES CALLED");
        for(String k:grid.keySet()){
            Hex c = grid.get(k);
            meshList.addAll(c.toMesh());

//            else
//                System.out.println("nope ");
        }
        meshList.addAll(ports);
        meshList.addAll(reqSig);
        return meshList;
    }
    public void paint(Graphics window, double wrat, double hrat ) {
        for(String k:grid.keySet()){
            grid.get(k).paint(window,wrat,hrat);
        }
        // start.paint(window,wrat,hrat);
    }
    public void rolled(int rolled){
        //System.out.println(numbers);
        for (Hex hex:numbers.get(rolled)){
            hex.gather();
        }
    }

}