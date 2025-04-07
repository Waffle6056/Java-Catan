import java.awt.*;
import java.io.*;
import java.util.*;
import RenderingStuff.Mesh;



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

        for (int i = 0; i < times; i++) {
            if (dir==0){
                if (!vist.contains(encoder(q-1,r+1,s))&& grid.containsKey(encoder(q-1,r+1,s))){
                    q--;r++;
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
                else {
                    dir++;i--;
                }
            }
            else if (dir==1){
                if (!vist.contains(encoder(q-1,r,s+1)) && grid.containsKey(encoder(q-1,r,s+1)   )){
                    q--;s++;
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
                else {
                    dir++;i--;
                }
            }
            else if (dir==2){
                if (!vist.contains(encoder(q,r-1,s+1)) &&grid.containsKey(encoder(q,r-1,s+1))){
                    r--;s++;
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
                else {
                    dir++;i--;
                }
            }
            else if (dir==3){
                if (!vist.contains(encoder(q+1,r-1,s)) &&grid.containsKey(encoder(q+1,r-1,s))){
                    q++;r--;
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
                else {
                    dir++;i--;
                }
            }else if (dir==4){
                if (!vist.contains(encoder(q+1,r,s-1)) &&grid.containsKey(encoder(q+1,r,s-1))){
                    q++;s--;
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
                else {
                    dir++;i--;
                }
            }
            else if (dir==5){
                if (!vist.contains(encoder(q,r+1,s-1)) &&grid.containsKey(encoder(q,r+1,s-1))){
                    r++;s--;
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
                else {
                    dir=0;i--;
                }
            }
        }
    }
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
        return meshList;
    }
    public void paint(Graphics window, double wrat, double hrat ) {
        for(String k:grid.keySet()){
            grid.get(k).paint(window,wrat,hrat);
        }
        // start.paint(window,wrat,hrat);
    }
    public void rolled(int rolled){
        System.out.println(numbers);
        for (NewHex hex:numbers.get(rolled)){
            hex.gather();
        }
    }

}