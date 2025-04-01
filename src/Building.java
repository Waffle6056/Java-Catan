import java.awt.*;
import RenderingStuff.Mesh;
public class Building extends Canvas {
    float x, y;
    Catan.BuildingOption type = Catan.BuildingOption.Road;
    int resourcegain;
    Player owner;
    boolean inverted;// if Y is upsidedown;
    Mesh mesh;
    Road[] roads=new Road[3];

    public Building(){
        resourcegain=0;
        owner=null;
        this.inverted=false;
    }
    public void gather(NewHex.resource type){
        //pre condition type!=desert
        if (owner!=null){
            owner.resources[type.index]+=resourcegain;
        }
        //check resource and owner exists

    }
    //    public void connect(){
//        if (down!=null) down.connect(this);
//        if (upleft!=null) upleft.connect(this);
//        if (upright!=null) upright.connect(this);
//    }
    public void combine(Building e){
        if (e.mesh != null)
            mesh = e.mesh;
    }
    public void connectRoad(Road e){
        for (int i = 0; i < 3; i++) {
            if (roads[i]==null){
                roads[i]=e;

                return;
            }
            if (roads[i].creation==e.creation){
                System.out.println("Doublework");
                return;
            }
        }
    }

    public void paint(Graphics window, int x, int y, int size) {
        window.drawRect(x-size/2,y-size/2,size,size);
    }

}