import java.awt.*;
import java.io.*;
import java.util.*;
import RenderingStuff.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class NewBoard {
    HashMap<String ,NewHex> grid;
    HashMap<Integer, ArrayList<NewHex>> numbers;
    public NewBoard() {
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
    boolean build(Catan.BuildingOption Option, Player turnPlayer, Catan instance) {
        return build(Option, turnPlayer, instance, new Building[1]);
    }
    boolean build(Catan.BuildingOption Option, Player turnPlayer, Catan instance, Building[] out) {

        //check if required resource amount
        // return false if not enought
        if (!turnPlayer.checkAmt(Option))
            return false;

        if (Option == Catan.BuildingOption.Town || Option == Catan.BuildingOption.City){
            if (Option == Catan.BuildingOption.Town && turnPlayer.settlements == 0)
                return false;
            if (Option == Catan.BuildingOption.City && turnPlayer.cities == 0)
                return false;

            instance.waitMouseRelease();

            Vector3f mouseClickPos = instance.waitMouseClick();
            NewHex hex = instance.selectHex(mouseClickPos);
            NewHex.HexBuilding ver = instance.selectVertex(hex, mouseClickPos);

            //System.out.println(hex.x + " " + hex.y+" "+ver+" "+hex.buildings[ver.index]);
            if (hex.constructbuilding(ver, Option, turnPlayer)) {
                turnPlayer.pay(Option);
                if (Option == Catan.BuildingOption.City) {
                    instance.Renderer.removeMesh(hex.buildings[ver.index].mesh);
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
        else if (Option == Catan.BuildingOption.Road){

            instance.waitMouseRelease();

            Vector3f mouseClickPos1 = instance.waitMouseClick();
            NewHex hex1 = instance.selectHex(mouseClickPos1);
            NewHex.HexBuilding ver1 = instance.selectVertex(hex1, mouseClickPos1);

            instance.waitMouseRelease();

            Vector3f mouseClickPos2 = instance.waitMouseClick();
            NewHex hex2 = instance.selectHex(mouseClickPos2);
            NewHex.HexBuilding ver2 = instance.selectVertex(hex2, mouseClickPos2);

            //System.out.println(hex1.x+" "+hex1.y+" "+ver1);
            //System.out.println(hex2.x+" "+hex2.y+" "+ver2);

            Road[] t = new Road[1];
            if (hex1.constructRoads(ver1, hex2, ver2, Catan.BuildingOption.Road, turnPlayer, t)){
                instance.MeshQueueRoad.add(t[0]);
                turnPlayer.pay(Option);
//                hex1.constructbuilding(ver1, BuildingOption.Road, turnPlayer);
//                hex2.constructbuilding(ver2, BuildingOption.Road, turnPlayer);
                return true;
            }
            else
                return false;

        }
        return false;
    }
    public void makeDefaltBoard(int Dimesion1,int Dimesion2,int Dimesion3,String tileamounts,String DiceTiles) throws FileNotFoundException {
        Scanner Filetiles=new Scanner(new File(tileamounts));
        Scanner dice=new Scanner(new File(DiceTiles));
        int times=Filetiles.nextInt();
        Filetiles.nextLine();
        ArrayList<Integer> tiles=new ArrayList<>();
        ArrayList<String> names=new ArrayList<>();
        numbers=new HashMap<>();
        int d=0;
        while (times-->0){
            names.add(Filetiles.next()); Filetiles.next();
            tiles.add(Filetiles.nextInt());
            Filetiles.nextLine();
        }

        //System.out.println("step 1");
        for (int f = 0; f < Dimesion1; f++) {
            for (int i = 0; i < Dimesion3+2-f; i++) {

                int q=-f-i,r=f,s=i;
                int random=(int)(Math.random()*tiles.size());
                int temp=tiles.get(random);

                grid.putIfAbsent(encoder(q,r,s),new NewHex(q,r,s,names.get(random)));
                temp--;
                tiles.set(random,temp);
                if (temp==0){
                    tiles.remove(random);
                    names.remove(random);
                }
            }
            // System.out.println("step 2");
        }
        for (int f = 1; f < Dimesion2; f++) {
            for (int i = 0; i < Dimesion3 + 2 - f; i++) {
                int q = -i, r = -f, s = f + i;
                int random=(int)(Math.random()*tiles.size());
                int temp=tiles.get(random);

                grid.putIfAbsent(encoder(q,r,s),new NewHex(q,r,s,names.get(random)));
                temp--;
                tiles.set(random,temp);
                if (temp==0){
                    tiles.remove(random);
                    names.remove(random);
                }
            }
        }
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
        times=dice.nextInt();dice.nextLine();
        HashSet<String> vist=new HashSet<>();
        for (int i = 2; i <= 12; i++) {
            numbers.putIfAbsent(i, new ArrayList<>());
        }

        int[] deltaQ = new int[]{-1,-1,0,1,1,0};
        int[] deltaR = new int[]{1,0,-1,-1,0,1};
        int[] deltaS = new int[]{0,1,1,0,-1,-1};
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


            NewHex temp= grid.get(encoder(q,r,s));
            vist.add(encoder(q,r,s));
            if (temp.type== NewHex.resource.Desert){
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
            NewHex temp= grid.get(encoder(q,r,s));
            Building p1 = temp.buildings[ind1];
            Building p2 = temp.buildings[ind2];

            PortHolder<NewHex.resource> port = PortHolder.generatePort();
            int fileInd = port.TradeRequirements.get(0).data.index;
            String cardFile = "";
            if (fileInd == -1)
                cardFile = "CatanCardMeshes/Special/Arrow.fbx";
            else
                cardFile = NewHex.fileNames[fileInd];
            Card<CardHolder<NewHex.resource>> card = new Card<>(port, cardFile);



            p1.ConnectingPort = card;
            p2.ConnectingPort = card;

            p1.setPos(temp, NewHex.HexBuilding.values()[ind1]);
            p2.setPos(temp, NewHex.HexBuilding.values()[ind2]);

            Mesh m = new Mesh("Buildings/Port.fbx");
            m.position.add((p1.x+p2.x)/2,0,(p1.y+p2.y)/2);

            int ind = port.TradeRequirements.size() / 2;
            int j = 0;
            for (Card<NewHex.resource> c : port.TradeRequirements) {
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

                ports.add(req);
            }




            Vector3f v = new Vector3f(-temp.x + m.position.x, temp.y - m.position.z, 0).normalize();
            //System.out.println(new Vector3f(1,0,0).angleSigned(v,new Vector3f(0,0,1))+" "+v+" ("+temp.x+", "+temp.y+")"+m.position+" "+cardFile);
            m.rotation.rotateY(new Vector3f(1,0,0).angleSigned(v,new Vector3f(0,0,1)));
            m.rotation.rotateX((float) Math.toRadians(-90));

            //req.rotation = new Quaternionf(m.rotation);

            ports.add(m);



        }
    }
    java.util.List<Mesh> ports = new ArrayList<>();
    public String encoder(int q,int r,int s){
        return q+","+r+","+s;
    }
    public NewHex getHex(int q,int r,int s){
        return grid.get(encoder(q,r,s));
    }
    public java.util.List<Mesh> getMeshes(){
        java.util.List<Mesh> meshList = new ArrayList<>();
        //System.out.println("GET MESHES CALLED");
        for(String k:grid.keySet()){
            meshList.add(grid.get(k).mesh);
            if (grid.get(k).numberMesh != null) {
                //System.out.println("number here "+grid.get(k).numberMesh.position);
                meshList.add(grid.get(k).numberMesh);
            }
//            else
//                System.out.println("nope ");
        }
        meshList.addAll(ports);
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
        for (NewHex hex:numbers.get(rolled)){
            hex.gather();
        }
    }

}