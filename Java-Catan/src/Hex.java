import RenderingStuff.Mesh;

import java.awt.*;

public class Hex extends Canvas {
    enum resource{
        Brick(0),
        Grain(1),
        Rock(2),
        Wood(3),
        Wool(4);
        private final int index;
        resource(int l) {
            this.index =l;
        }
    }
    enum HexRoad{// leftmost then clockwise
         Left(0),
         UpLeft(1),
         UpRight(2),
         Right(3),
         DownRight(4),
         DownLeft(5);
        private final int index;
        HexRoad(int l) {
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
    Road[] roads=new Road[6];
    Hex[] nearbyHex=new Hex[6];//follows hex road
    Building[] buildings=new Building[6];
    String tostring;
    public Hex(){

        buildings[0]=new Building(roads[0],null,roads[1],false);
        buildings[1]=new Building(null,roads[1],roads[2],true);
        buildings[2]=new Building(roads[3],roads[2],null,false);
        buildings[3]=new Building(roads[3],roads[4],null,true);
        buildings[4]=new Building(null,roads[5],roads[4],false);
        buildings[5]=new Building(roads[0],null,roads[5],true);
    }
    public Hex(String j){
tostring=j;
        buildings[0]=new Building(roads[0],null,roads[1],false);
        buildings[1]=new Building(null,roads[1],roads[2],true);
        buildings[2]=new Building(roads[3],roads[2],null,false);
        buildings[3]=new Building(roads[3],roads[4],null,true);
        buildings[4]=new Building(null,roads[5],roads[4],false);
        buildings[5]=new Building(roads[0],null,roads[5],true);
    }
    //Sharedside is from the e's point of view
    public void combineHex(Hex e,int toE){
        int awayE=toE-3;
        if (awayE<0){
            awayE+=6;
        }
        e.nearbyHex[awayE]=this;
        nearbyHex[toE]=e;
        e.buildings[awayE].combine(buildings[toE]);
        e.buildings[(awayE+1)%6].combine(buildings[(toE+1)%6]);
    }
    public void makeRoads(){
        for (int i = 0; i < 6; i++) {
            roads[i]=new Road();
        }
        for (int i = 0; i < buildings.length; i++) {
            buildings[i].connect();
        }
    }
    public String toString(){
     return tostring;
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


        //find and image for your paddle and put it here
        Graphics2D g2 = (Graphics2D) window;
        Image img1 = Toolkit.getDefaultToolkit().getImage("Ball2.png"); //use .gif or .png, you can choose the image
        g2.drawImage(img1,(int)(10*wrat), (int)(10*hrat), (int)(10*wrat), (int)(10*hrat), this);

    }

}
