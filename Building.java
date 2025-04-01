import java.awt.*;
import RenderingStuff.Mesh;
public class Building extends Canvas {
    float x, y;
    Catan.BuildingOption type = Catan.BuildingOption.Road;
    int resourcegain;
    Player owner;
    boolean inverted;// if Y is upsidedown;
    Road down,upleft,upright;
    Mesh mesh;

    public Building(Road d,Road ul,Road ur,boolean inverted){
        resourcegain=0;
        owner=null;
        down=d;
        upleft=ul;
        upright=ur;
        this.inverted=inverted;
    }
    public Building(){
        resourcegain=0;
        owner=null;
        down=null;
        upleft=null;
        upright=null;
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
        if (down!=null){ down=e.down;}
        if (upleft!=null){ upleft=e.upleft;}
        if (upright!=null){ upright=e.upright;}
        if (e.mesh != null)
            mesh = e.mesh;
    }
    public void paint(Graphics window, int x, int y, int size) {
        window.drawRect(x-size/2,y-size/2,size,size);
    }

}