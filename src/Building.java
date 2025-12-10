import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import CardStructure.Card;
import CardStructure.CardHolder;
import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.MeshUpdates;
import CardStructure.RenderingStuff.Renderable;
import org.joml.Math;

public class Building extends Canvas implements Renderable, MeshUpdates {
    float x, y;
    Catan.BuildingOption type = Catan.BuildingOption.Road;
    Card<CardHolder<Hex.resource, Card<Hex.resource>>> ConnectingPort = null;
    int resourcegain;
    Player owner;
    Mesh mesh;

    //boolean overrides = false;
    Road[] roads=new Road[3];

    public Building(){
        resourcegain=0;
        owner=null;
    }
    public void gather(Hex.resource type){
        //pre condition type!=desert
        if (owner!=null){
            owner.ResourceCards.add(Hex.resource.createResourceCard(type));
        }
    }

    public void setPos(Hex hex, Hex.HexBuilding ver){
        float radius = 1.2f;// arbitrary value temp
        float indexOffset = 3f;
        //float angleOffset = 30f;
        x = hex.x + Math.sin(Math.toRadians(60 * (-ver.index+indexOffset))) * radius;
        y = hex.y + Math.cos(Math.toRadians(60 * (-ver.index+indexOffset))) * radius;
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
        if (updateOnRender) {
            updateOnRender = false;
            updateMesh();
        }
        if (mesh != null)
            meshList.add(mesh);
        return meshList;
    }

    boolean updateOnRender = false;
    @Override
    public void updateOnRender() {
        updateOnRender = true;
    }

    @Override
    public void updateMesh() {
        if (owner != null){
            String file = "";
            //System.out.println(b.type);
            switch (type){
                case City -> file = owner.cityFile;
                case Town -> file = owner.settlementFile;
            }
            if (type == Catan.BuildingOption.Road)
                return;
            Mesh m = new Mesh(file);
            m.position.add(x,0,y);
            m.rotation.rotateX(Math.toRadians(-90));
            mesh = m;
        }
    }
}