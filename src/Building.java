import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import RenderingStuff.Mesh;
import org.joml.Math;

public class Building extends Canvas implements Renderable {
    float x, y;
    Catan.BuildingOption type = Catan.BuildingOption.Road;
    Card<CardHolder<Hex.resource>> ConnectingPort = null;
    int resourcegain;
    Player owner;
    boolean inverted;// if Y is upsidedown;
    Mesh mesh;
    //boolean overrides = false;
    Road[] roads=new Road[3];

    public Building(){
        resourcegain=0;
        owner=null;
        this.inverted=false;
    }
    public void gather(Hex.resource type){
        //pre condition type!=desert
        if (owner!=null){
            owner.ResourceCards.add(Card.createResourceCard(type));
        }
        //check resource and owner exists

    }
    //    public void connect(){
//        if (down!=null) down.connect(this);
//        if (upleft!=null) upleft.connect(this);
//        if (upright!=null) upright.connect(this);
//    }
    public void setPos(Hex hex, Hex.HexBuilding ver){
        float radius = 1.2f;// arbitrary value temp
        float indexOffset = 3f;
        //float angleOffset = 30f;
        x = hex.x + Math.sin(Math.toRadians(60 * (-ver.index+indexOffset))) * radius;
        y = hex.y + Math.cos(Math.toRadians(60 * (-ver.index+indexOffset))) * radius;
    }
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
                //System.out.println("Doublework");
                return;
            }
        }
    }
    public Road[] getRoads(){
        return roads;
    }


    public void paint(Graphics window, int x, int y, int size) {
        window.drawRect(x-size/2,y-size/2,size,size);
    }
    @Override
    public List<Mesh> toMesh() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        if (mesh != null)
            meshList.add(mesh);
        return meshList;
    }

}