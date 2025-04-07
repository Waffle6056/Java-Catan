import java.awt.*;
import java.io.*;
import java.util.*;
import RenderingStuff.Mesh;

import javax.sound.midi.Soundbank;

public class NewHex extends Canvas {
    enum resource{
        Brick(0),
        Grain(1),
        Rock(2),
        Wood(3),
        Wool(4),
        Desert(-1),
        Sea(-2);
        public final int index;
        resource(int l) {
            this.index =l;
        }
    }
    enum HexBuilding{// Up then clockwise
        Up(0),
        UpRight(1),
        DownRight(2),
        Down(3),
        DownLeft(4),
        UpLeft(5);
        public final int index;
        HexBuilding(int l) {
            this.index =l;
        }
    }

    Mesh mesh, numberMesh;
    Building[] buildings=new Building[6];
    Road[] roads=new Road[6];
    String tostring;
    resource type;
    int dicenumber;
    float x, y, size;
    int dupe = 2;
    int orginx=200,orginy=100;
    static NewHex isRobberBaroned;
    int q,r,s;
    //q + r + s = 0
    //Neighbors
    //(q + 1, r - 1, s)
    //(q + 1, r, s - 1)
    //(q, r + 1, s - 1)
    //(q - 1, r + 1, s)
    //(q - 1, r, s + 1)
    //(q, r - 1, s + 1)

    public NewHex(int q,int r,int s,String type){
        this.q=q;
        this.r=r;
        this.s=s;
        this.type=resource.valueOf(type);
        switch (this.type)
        {
            case Brick -> mesh = new Mesh("HexMeshes/Hills.fbx");
            case Wool -> mesh = new Mesh("HexMeshes/Plains.fbx");
            case Desert -> mesh = new Mesh("HexMeshes/Desert.fbx");
            case Rock -> mesh = new Mesh("HexMeshes/Mountains.fbx");
            case Grain -> mesh = new Mesh("HexMeshes/Field.fbx");
            case Wood -> mesh = new Mesh("HexMeshes/Forest.fbx");
            default -> mesh = new Mesh("catan.fbx");
        }

        x=(-q+r)*(21*dupe+2);
        y=s*(38*dupe-2);
        size=25*dupe;
        x/=45;
        y/=45;
        size/=100;



        mesh.position.add(x,0,y);
        mesh.rotation.rotateAxis((float)Math.toRadians(-90),1,0,0);
        //System.out.println(mesh.position);
        makeVertexs();
        tostring="Desert";
        if (this.type.equals(resource.Desert)){
            // System.out.println("I own the Land");
            isRobberBaroned=this;
        }
    }
    public NewHex(String j){
        tostring=j;
    }
    public void makeVertexs(){
        for (int i = 0; i < 6; i++) {
            buildings[i]=new Building();
            roads[i]=new Road();
        }
    }
    public int gather(){
        if (type!=resource.Desert || this.equals(isRobberBaroned)){
            for (int i = 0; i < buildings.length; i++) {
                buildings[i].gather(type);
            }
        }
        return 0;
    }

//    String buildingToString(Building b){
//        return b.x+" "+b.y;
//    }
    static boolean ownerRequirementOverride = false;
    public boolean constructRoads(HexBuilding ver1, NewHex hex2, HexBuilding ver2, Catan.BuildingOption option, Player owner, Road[] out){
        Building one=buildings[ver1.index],two=hex2.buildings[ver2.index];
        System.out.println("started ");
        if (ownerRequirementOverride)
            ;
        else if (!(one.owner==owner||two.owner==owner)){
            return false;
        }
        if (one == two)
            return false;
        System.out.println("passed check 2 "+two);
        for (int i = 0; i < 3; i++) {
            if (one.getRoads()[i]==null){
                continue;
            }
            System.out.println(one.getRoads()[i].left+" "+one.getRoads()[i].right);
            if (one.getRoads()[i].left.equals(two)||one.getRoads()[i].right.equals(two)){
                System.out.println("passed check 3");
                one.getRoads()[i].made(owner);
                one.getRoads()[i].setPos(this, ver1, hex2, ver2);
                out[0] = one.getRoads()[i];
                return true;
            }
        }
        return false;
    }
    //hex vertex option player
    //Don't worry about resources
    public boolean constructbuilding(HexBuilding vertex, Catan.BuildingOption option, Player owner){
        // moved building conditions in here cuase i needed to check them for both road vertexs
        Building temp=buildings[vertex.index];

        System.out.println("called");
        if (temp.owner != owner && temp.owner != null)
            return false;
        System.out.println("passed check 1");

        //settlement
        if (option== Catan.BuildingOption.Town) {

            //CHECK ADJACENT BUILDINGS
            boolean goodRoad=false;
            for (int i = 0; i < 3; i++) {
                if (temp.getRoads()[i]==null){
                    continue;
                }
                Building left=temp.getRoads()[i].left,right=temp.getRoads()[i].right;
                if (goodRoad||temp.getRoads()[i].owner==owner){
                    goodRoad=true;
                }
                if (left.type == Catan.BuildingOption.City || left.type == Catan.BuildingOption.Town)
                    if (left.owner!=null&&left.owner!=owner || right.owner!=null&&right.owner!=owner){
                        return false;
                    }
            }

            System.out.println("passed check 2");
            if (!goodRoad ||temp.type != Catan.BuildingOption.Road)
                return false;

            System.out.println("passed check 3");
            temp.resourcegain = 1;
            temp.type = option;
            temp.owner=owner;
            owner.vpvisable++;
            temp.setPos(this,vertex);
            return true;
        }
        //city
        if (option== Catan.BuildingOption.City){
            if (temp.type != Catan.BuildingOption.Town)
                return false;
            temp.resourcegain = 2;
            temp.type = option;
            temp.owner=owner;
            owner.vpvisable++;
            return true;
        }

        if (option== Catan.BuildingOption.Road){

            temp.type = option;
            temp.owner=owner;
            owner.vpvisable++;
            return true;
        }
        return false;
    }

    public void connectRoads(){
        // System.out.println("Hex connecting");
        for (int i = 0; i < 6; i++) {
            roads[i].connectRoads(roads[(i+1)%6]);
            roads[i].connectBuildings(buildings[i]);
            roads[i].connectBuildings(buildings[(i+1)%6]);
        }
    }

    //Sharedside is from the e's point of view
    public void combineHex(NewHex e,int toE){
        int awayE=toE-3;
        if (awayE<0){
            awayE+=6;
        }
        e.buildings[(awayE+1)%6].combine(buildings[toE]);
        e.buildings[awayE].combine(buildings[(toE+1)%6]);
        e.buildings[(awayE+1)%6] = buildings[toE];
        e.buildings[awayE] = buildings[(toE+1)%6];
        e.roads[awayE]=roads[toE];
    }
    public String toString(){
        return tostring;
    }
    public void setDicenumber(int number){
        dicenumber=number;
        String file = "";
        //System.out.println("DICE SET TO "+number);
        switch (number){
            case 2 -> file = "Numbers/Two.fbx";
            case 3 -> file = "Numbers/Three.fbx";
            case 4 -> file = "Numbers/Four.fbx";
            case 5 -> file = "Numbers/Five.fbx";
            case 6 -> file = "Numbers/Six.fbx";
            case 8 -> file = "Numbers/Eight.fbx";
            case 9 -> file = "Numbers/Nine.fbx";
            case 10 -> file = "Numbers/Ten.fbx";
            case 11 -> file = "Numbers/Eleven.fbx";
            case 12 -> file = "Numbers/Twelve.fbx";
        }
        numberMesh = new Mesh(file);

        //System.out.println(file);
        numberMesh.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        numberMesh.position.add(mesh.position);
    }
    public void paint( Graphics window,double wrat,double hrat )
    {
        //window.drawString("Brick Class ", 50, 150 );

        // drawing methods for Java:
        // go to the Graphics Intro Folder

        // window.setColor(Color.YELLOW);
        //  window.fillRect(getX(), getY(), getW(), getH());
        //  window.setColor(Color.BLACK);
        // window.drawRect(getX(), getY(), getW(), getH());
        // q r s
        int dupe=3;
        int orginx=200,orginy=100;
        int right=-q+r,down=Math.abs(s),shift=21*dupe+2;
        int x=orginx+right*shift,y=orginy+down*(38*dupe-2),size=25*dupe;
        Polygon poly = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_deg = (60 * i) -30;
            double angle_rad = Math.PI / 180 * angle_deg;
            poly.addPoint((int)(wrat*(x + size * Math.cos(angle_rad))), (int)(hrat*(y + size * Math.sin(angle_rad))));
            int building=3-i;
            if (building<0){
                building+=6;
            }
            buildings[building].paint(window,(int)(wrat*(x + size * Math.cos(angle_rad))),(int)(hrat*(y + size * Math.sin(angle_rad))),(int)(size*(wrat+hrat)/10));
        }
        /*
        Polygon poly = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_deg = (60 * i) -30;
            double angle_rad = Math.PI / 180 * angle_deg;
            poly.addPoint((int)((x + size * Math.cos(angle_rad))), (int)((y + size * Math.sin(angle_rad))));
        }
         */
        //find and image for your paddle and put it here
        Graphics2D g2 = (Graphics2D) window;
        Image img1 = Toolkit.getDefaultToolkit().getImage("Ball2.png"); //use .gif or .png, you can choose the image
        // g2.drawImage(img1,(int)(10*wrat), (int)(10*hrat), (int)(10*wrat), (int)(10*hrat), this);
        window.drawPolygon(poly);
        Font steal=  window.getFont();
        steal= steal.deriveFont((float)(5.1*(wrat+hrat)));
        window.setFont(steal);
        window.drawString(q+","+r+","+s,(int) (wrat*(x- (double) size /4)),(int) (hrat*y));
        window.drawString(tostring,(int) (wrat*(x- (double) size /4)),(int) (hrat*y*34/32));
        window.drawString(type.name(),(int) (wrat*(x- (double) size /4)),(int) (hrat*y*36/32));
    }

}