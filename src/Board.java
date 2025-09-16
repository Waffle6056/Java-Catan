import java.awt.*;
import java.io.*;
import java.util.*;
import RenderingStuff.Mesh;
import org.joml.Vector3f;


public class Board implements Renderable{
    HashMap<String , Hex> grid;
    HashMap<Integer, ArrayList<Hex>> numbers;
    java.util.List<Mesh> ports = new ArrayList<>();
    java.util.List<Mesh> reqSig = new ArrayList<>();

    public Board() {
        grid=new HashMap<>();
        try {
            makeDefaltBoard(3, 3, 3, "TileAmounts.dat", "DiceTiles.dat");
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
    static int[] deltaQ = new int[]{-1,-1,0,1,1,0};
    static int[] deltaR = new int[]{1,0,-1,-1,0,1};
    static int[] deltaS = new int[]{0,1,1,0,-1,-1};

    boolean build(Catan.BuildingOption Option, Player turnPlayer, Catan instance) {
        return build(Option, turnPlayer, instance, new Building[1]);
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
            return constructRoad(option, turnPlayer, instance, out);
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


        //System.out.println(hex.x + " " + hex.y+" "+ver+" "+hex.buildings[ver.index]);
        if (hex.constructbuilding(ver, option, turnPlayer)) {
            turnPlayer.pay(option);
            if (option == Catan.BuildingOption.City) {
                turnPlayer.settlements++;
                turnPlayer.cities--;
            }
            else
                turnPlayer.settlements--;
            instance.MeshQueue.add(hex.buildings[ver.index]);
            //System.out.println("COMPLETED BUILDING "+hex.buildings[ver.index].type);
            out[0] = hex.buildings[ver.index];
            if (out[0].ConnectingPort != null)
                turnPlayer.TradingCards.add(out[0].ConnectingPort);
            return true;
            //System.out.println("built town/city "+hex.mesh.position+" "+ver);
        }
        return false;
    }

    boolean constructRoad(Catan.BuildingOption option, Player turnPlayer, Catan instance, Building[] out){

        buildingSlot slot1 = selectSlot(instance);
        Hex hex1 = slot1.hex;
        Hex.HexBuilding ver1 = slot1.ver;

        buildingSlot slot2 = selectSlot(instance);
        Hex hex2 = slot2.hex;
        Hex.HexBuilding ver2 = slot2.ver;

        //System.out.println(hex1.x+" "+hex1.y+" "+ver1);
        //System.out.println(hex2.x+" "+hex2.y+" "+ver2);

        Road[] t = new Road[1];
        if (hex1.constructRoads(ver1, hex2, ver2, Catan.BuildingOption.Road, turnPlayer, t)){
            instance.MeshQueueRoad.add(t[0]);
            turnPlayer.pay(option);
//                hex1.constructbuilding(ver1, BuildingOption.Road, turnPlayer);
//                hex2.constructbuilding(ver2, BuildingOption.Road, turnPlayer);
            return true;
        }
        else
            return false;
    }

    void loadTileCounts(String tileamounts, ArrayList<Integer> tiles, ArrayList<String> names)  throws FileNotFoundException {
        Scanner Filetiles=new Scanner(new File(tileamounts));
        int times=Filetiles.nextInt();
        Filetiles.nextLine();
        numbers=new HashMap<>();
        int d=0;
        while (times-->0){
            names.add(Filetiles.next()); Filetiles.next();
            tiles.add(Filetiles.nextInt());
            Filetiles.nextLine();
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

    void fillGrid(int Dimesion1,int Dimesion2,int Dimesion3, String tileamounts) throws FileNotFoundException{
        ArrayList<Integer> tiles=new ArrayList<>();
        ArrayList<String> names=new ArrayList<>();
        loadTileCounts(tileamounts, tiles, names);
        //System.out.println("step 1");
        for (int f = 0; f < Dimesion1; f++) {
            for (int i = 0; i < Dimesion3+2-f; i++) {
                int q=-f-i,r=f,s=i;
                placeRandomTile(q,r,s,tiles,names);
            }
            // System.out.println("step 2");
        }
        for (int f = 1; f < Dimesion2; f++) {
            for (int i = 0; i < Dimesion3 + 2 - f; i++) {
                int q = -i, r = -f, s = f + i;
                placeRandomTile(q,r,s,tiles,names);
            }
        }
    }

    public void makeDefaltBoard(int Dimesion1,int Dimesion2,int Dimesion3,String tileamounts,String DiceTiles)  throws FileNotFoundException {

        fillGrid(Dimesion1,Dimesion2,Dimesion3,tileamounts);

        for(String k:grid.keySet()){
            String[] line=k.split(",");
            int q=Integer.parseInt(line[0]), r=Integer.parseInt(line[1]), s=Integer.parseInt(line[2]);

            if (grid.containsKey(encoder(q-1,r+1,s))){//Right
                //System.out.println(getHex(q-1,r+1,s).x+" "+getHex(q-1,r+1,s).y+" |currently index 1| "+grid.get(k).x+" "+grid.get(k).y);
                grid.get(k).combineHex(getHex(q-1,r+1,s),1);
            }
            if (grid.containsKey(encoder(q,r-1,s+1))){//DownLeft
                grid.get(k).combineHex(getHex(q,r-1,s+1),3);
            }
            if (grid.containsKey(encoder(q-1,r,s+1))){//DownRight
                grid.get(k).combineHex(getHex(q-1,r,s+1),2);
            }

            if (grid.containsKey(encoder(q+1,r-1,s))){//Left
                grid.get(k).combineHex(getHex(q+1,r-1,s),4);
            }
            if (grid.containsKey(encoder(q,r+1,s-1))){//UpRight
                grid.get(k).combineHex(getHex(q,r+1,s-1),0);
            }
            if (grid.containsKey(encoder(q+1,r,s-1))){//UpLeft
                grid.get(k).combineHex(getHex(q+1,r,s-1),5);
            }
        }
        for(String k:grid.keySet()){
            grid.get(k).connectRoads();
        }

        int q = 1, r = -1, s = 0, dir=0;
        Scanner dice=new Scanner(new File(DiceTiles));
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


            Hex temp= grid.get(encoder(q,r,s));
            vist.add(encoder(q,r,s));
            if (temp.type== Hex.resource.Desert){
                i--;continue;
            }
            String tes=dice.nextLine();
            temp.tostring=tes;
            int l= Integer.parseInt(tes.split(" ")[2]);
            temp.setDicenumber(l);
            numbers.get(l).add(temp);
        }

        q = 1; r = -1; s = 0; dir=0;
        int[] PortSide = new int[]{0,1,-1,1,2,3,-1,3,4,5,-1,5};
        //int[] PortSide = new int[]{-1,2,-1,1,1,1,1,1,1,1,1,1};
        for (int i = 0; i < PortSide.length; i++) {
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

            if (PortSide[i] == -1)
                continue;
            int ind1 = PortSide[i];
            int ind2 = Math.floorMod(PortSide[i]-1,6);
            Hex temp= grid.get(encoder(q,r,s));
            Building p1 = temp.buildings[ind1];
            Building p2 = temp.buildings[ind2];

            PortHolder<Hex.resource> port = PortHolder.generatePort();
            int fileInd = port.TradeRequirements.get(0).data.index;
            String cardFile = "";
            if (fileInd == -1)
                cardFile = "CatanCardMeshes/Special/Arrow.fbx";
            else
                cardFile = Hex.resourceFileNames[fileInd];
            Card<CardHolder<Hex.resource>> card = new Card<>(port, cardFile);



            p1.ConnectingPort = card;
            p2.ConnectingPort = card;

            p1.setPos(temp, Hex.HexBuilding.values()[ind1]);
            p2.setPos(temp, Hex.HexBuilding.values()[ind2]);

            Mesh m = new Mesh("Buildings/Port.fbx");
            m.position.add((p1.x+p2.x)/2,0,(p1.y+p2.y)/2);

            int ind = port.TradeRequirements.size() / 2;
            int j = 0;
            for (Card<Hex.resource> c : port.TradeRequirements) {
                Mesh req = new Mesh(c.file);
                req.position.add(m.position).add(0, 2+j*0.05f, 0);
                req.scale.mul(3);
                int midDiff = j++-ind;
                float angle = (float)Math.toRadians(Math.min(180f/port.TradeRequirements.size(), 180f/7f)) * midDiff;
                req.rotation.rotateY(angle+ (float)Math.toRadians(180f));
                float len = 0.3f;
                Vector3f rotated = new Vector3f(0,0,-len).rotateY(angle);
                req.position.add(rotated);
                //req.rotation.rotateX((float) Math.toRadians(-90));

                reqSig.add(req);
            }




            Vector3f v = new Vector3f(-temp.x + m.position.x, temp.y - m.position.z, 0).normalize();
            //System.out.println(new Vector3f(1,0,0).angleSigned(v,new Vector3f(0,0,1))+" "+v+" ("+temp.x+", "+temp.y+")"+m.position+" "+cardFile);
            m.rotation.rotateY(new Vector3f(1,0,0).angleSigned(v,new Vector3f(0,0,1)));
            m.rotation.rotateX((float) Math.toRadians(-90));

            //req.rotation = new Quaternionf(m.rotation);

            ports.add(m);



        }
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