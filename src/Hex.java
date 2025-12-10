import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import CardStructure.Card;
import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.Renderable;

public class Hex extends Canvas implements Renderable {
    @Override
    public List<Mesh> toMesh() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        if (mesh != null)
            meshList.add(mesh);
        if (numberMesh != null)
            meshList.add(numberMesh);
        for (Road r : roads)
            meshList.addAll(r.toMesh());
        for (Building b : buildings)
            meshList.addAll(b.toMesh());
        return meshList;
    }

    public enum resource{
        Brick(0, "HexMeshes/Hills.fbx","CatanCardMeshes/Resource/CardBrick.fbx"),
        Grain(1, "HexMeshes/Field.fbx","CatanCardMeshes/Resource/CardGrain.fbx"),
        Rock(2, "HexMeshes/Mountains.fbx","CatanCardMeshes/Resource/CardOre.fbx"),
        Wood(3, "HexMeshes/Forest.fbx","CatanCardMeshes/Resource/CardLumber.fbx"),
        Wool(4, "HexMeshes/Plains.fbx","CatanCardMeshes/Resource/CardWool.fbx"),
        Desert(-1, "HexMeshes/Desert.fbx", "CatanCardMeshes/Special/Arrow.fbx");
        public final int Index;
        public final String HexMesh;
        public final String ResourceMesh;
        resource(int Index, String HexMesh, String ResourceMesh) {
            this.Index = Index;
            this.HexMesh = HexMesh;
            this.ResourceMesh = ResourceMesh;
        }
        public static Card<resource> createResourceCard(Hex.resource r){
            return new Card<>(r, r.ResourceMesh);
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
    String tostring = "Desert";
    resource type;
    int dicenumber;
    float x, y;
    static Hex RobberBaronedHex;
    int q,r,s;
    //q + r + s = 0
    //Neighbors
    //(q + 1, r - 1, s)
    //(q + 1, r, s - 1)
    //(q, r + 1, s - 1)
    //(q - 1, r + 1, s)
    //(q - 1, r, s + 1)
    //(q, r - 1, s + 1)


    public Hex(int q, int r, int s, String type){
        this.type=resource.valueOf(type);
        mesh = new Mesh(this.type.HexMesh);

        setPositions(q,r,s);
        //System.out.println(mesh.position);
        makeVertexs();

        if (this.type.equals(resource.Desert)){
            // System.out.println("I own the Land");
            RobberBaronedHex = this;
        }
    }
    public Hex(String j){
        tostring=j;
    }
    void setPositions(int q, int r, int s){
        this.q=q;
        this.r=r;
        this.s=s;
        x=(-q+r)*44/45f;
        y=s*74/45f;

        mesh.position.add(x,0,y);
        mesh.rotation.rotateAxis((float)Math.toRadians(-90),1,0,0);
    }
    public void makeVertexs(){
        for (int i = 0; i < 6; i++) {
            buildings[i]=new Building();
            roads[i]=new Road();
        }
    }
    public int gather(){
        if (type!=resource.Desert || this.equals(RobberBaronedHex)){
            for (int i = 0; i < buildings.length; i++) {
                buildings[i].gather(type);
            }
        }
        return 0;
    }


    public boolean constructRoads(HexBuilding ver1, Hex hex2, HexBuilding ver2, Catan.BuildingOption option, Player owner, Road[] out){
        Building one=buildings[ver1.index],two=hex2.buildings[ver2.index];
        System.out.println("called build "+option);
        if (one == two)
            return false;

        for (int i = 0; i < 3; i++) {
            Road r = one.getRoads()[i];
            if (r != null && (two.equals(r.left) || two.equals(r.right))) {
                if ( r.owner != null )
                    return false;
                if ( !r.connectsWith(owner) )
                    return false;

                r.made(owner);
                r.setPos(this, ver1, hex2, ver2); // mesh pos
                out[0] = one.getRoads()[i];
                return true;
            }
        }
        return false;
    }

    static boolean roadRequirementOverride = false;
    boolean townConstructionValid(Building building, Player owner){
        //CHECK ADJACENT BUILDINGS
        boolean goodRoad = roadRequirementOverride;
        for (int i = 0; i < 3; i++) {
            if (building.getRoads()[i]==null){
                continue;
            }
            Building left=building.getRoads()[i].left, right=building.getRoads()[i].right;
            if (goodRoad||building.getRoads()[i].owner==owner){
                goodRoad=true;
            }
            if (left.type == Catan.BuildingOption.City || left.type == Catan.BuildingOption.Town)
                return false;
            if (right.type == Catan.BuildingOption.City || right.type == Catan.BuildingOption.Town)
                return false;

        }

        //System.out.println("passed check 2");
        return goodRoad;
    }
    public boolean constructBuilding(HexBuilding vertex, Catan.BuildingOption option, Player owner){
        // moved building conditions in here cuase i needed to check them for both road vertexs
        Building building=buildings[vertex.index];
        if (option != Catan.BuildingOption.Road)
            System.out.println("called build "+option);
        if (building.owner != owner && building.owner != null)
            return false;
        //System.out.println("passed check 1");

        //settlement
        if (option== Catan.BuildingOption.Town) {

            if (!townConstructionValid(building, owner))
                return false;
            //System.out.println("passed check 3");
            building.resourcegain = 1;
            building.type = option;
            building.owner=owner;
            return true;
        }
        //city
        if (option== Catan.BuildingOption.City){
            if (building.type != Catan.BuildingOption.Town)
                return false;
            building.resourcegain = 2;
            building.type = option;
            building.owner=owner;

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

    public void combineHex(Hex e, int sideIndex){
        int awaySideIndex=sideIndex-3;
        if (awaySideIndex<0){
            awaySideIndex+=6;
        }
        e.buildings[(awaySideIndex+1)%6] = buildings[sideIndex];
        e.buildings[awaySideIndex] = buildings[(sideIndex+1)%6];
        e.roads[awaySideIndex]=roads[sideIndex];
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
        numberMesh.position.add(mesh.position).add(0,-.25f,0);
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