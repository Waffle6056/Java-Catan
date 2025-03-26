import java.awt.*;
import java.io.*;
import java.util.*;
import RenderingStuff.Mesh;

public class NewHex extends Canvas {
    enum resource{
        Brick(0),
        Grain(1),
        Rock(2),
        Wood(3),
        Wool(4),
        Desert(-1);
        final int index;
        resource(int l) {
            this.index =l;
        }
    }
    enum HexBuilding{// UpLeft then clockwise
        UpLeft(0),
        Up(1),
        UpRight(2),
        DownRight(3),
        Down(4),
        DownLeft(5);
        private final int index;
        HexBuilding(int l) {
            this.index =l;
        }
    }

    Mesh mesh;
    Building[] buildings=new Building[6];
    String tostring;
    resource type;
    int dicenumber;
    float x, y, size;
    int dupe = 2;
    int orginx=200,orginy=100;

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
        mesh = new Mesh("catan.fbx");
        x=(-q+r)*(21*dupe+2);
        y=s*(38*dupe-2);
        size=25*dupe;
        x/=50;
        y/=50;
        size/=100;



        mesh.position.add(x,0,y);
        mesh.rotation.rotateAxis((float)Math.toRadians(-90),1,0,0);
        System.out.println(mesh.position);
        makeVertexs();
        this.type=resource.valueOf(type);
        tostring="Desert";
    }
    public NewHex(String j){
        tostring=j;
    }
    public void makeVertexs(){
        for (int i = 0; i < 6; i++) {
            buildings[i]=new Building();
        }
    }
    public int gather(int rolled){
        if (type!=resource.Desert&& dicenumber==rolled){
            for (int i = 0; i < buildings.length; i++) {
                buildings[i].gather(type);
            }
        }
        return 0;
    }

    //hex vertex option player
    //Don't worry about resources
    //TODO change option to not int
    public void constructbuilding(HexBuilding vertex,int option, Player owner){
        buildings[vertex.index].owner=owner;
        //settlement
        if (option==1) {
            buildings[vertex.index].resourcegain = 1;
        }
        //city
        if (option==2){
            buildings[vertex.index].resourcegain = 2;
        }
    }


    //Sharedside is from the e's point of view
    public void combineHex(NewHex e,int toE){
        int awayE=toE-3;
        if (awayE<0){
            awayE+=6;
        }
        e.buildings[awayE].combine(buildings[toE]);
        e.buildings[(awayE+1)%6].combine(buildings[(toE+1)%6]);
    }
    public String toString(){
        return tostring;
    }
    public void setDicenumber(int number){
        dicenumber=number;
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
