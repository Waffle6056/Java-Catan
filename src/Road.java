import RenderingStuff.Mesh;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Road implements Renderable {
    @Override
    public List<Mesh> toMesh() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        if (mesh != null)
            meshList.add(mesh);
        return meshList;
    }
    Player owner;
    Road[] connects=new Road[4];
    Building left, right;
    Mesh mesh;
    int creation=a;
    static int a=0;
    float x, y, angle;
    public Road(){
        owner=null;
        a++;
    }

    public Road[] getConnects() {
        return connects;
    }
    public Player getOwner() {
        return owner;
    }
    public void connectRoads(Road e){
        // System.out.print(creation+" is connecting to "+e.creation+"  :");
        for (int i = 0; i < 4; i++) {
            if (connects[i]==null){
                connects[i]=e;
                break;
            }
            if(connects[i].creation == e.creation){
                //  System.out.print("AllreadyDone");
                return;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (e.connects[i]==null){
                e.connects[i]=this;
                break;
            }
        }
        //  System.out.println("Sucess");
    }
    public void connectBuildings(Building one){
        one.connectRoad(this);
        if (left==null){
            left=one;
            return;
        }
        else if (left.equals(one)){
            return;
        }
        if (right==null){
            right=one;
        }

    }
    public void setPos(Hex hex1, Hex.HexBuilding ver1, Hex hex2, Hex.HexBuilding ver2){
        float radius = 1.2f;// arbitrary value temp
        float indexOffset = 3f;

        //float angleOffset = -30f;
        float x1 = hex1.x + Math.sin(Math.toRadians(60 * (-ver1.index+indexOffset))) * radius;
        float y1 = hex1.y + Math.cos(Math.toRadians(60 * (-ver1.index+indexOffset))) * radius;

        float x2 = hex2.x + Math.sin(Math.toRadians(60 * (-ver2.index+indexOffset))) * radius;
        float y2 = hex2.y + Math.cos(Math.toRadians(60 * (-ver2.index+indexOffset))) * radius;

        x = (x1 + x2) / 2;
        y = (y1 + y2) / 2;

        if (x1 > x2){
            float swap = x1;
            x1 = x2;
            x2 = swap;

            swap = y1;
            y1 = y2;
            y2 = swap;
        }

        if (y2 > y1)
            angle = -Math.toDegrees(Math.atan2(Math.abs(y2-y1),Math.abs(x2-x1)));
        else
            angle = Math.toDegrees(Math.atan2(Math.abs(y2-y1),Math.abs(x2-x1)));
        //System.out.println(angle + " "+Math.abs(x2-x1)+" "+Math.abs(y2-y1));
    }
    public boolean connectsWith(Player p){
        for (Road r : connects)
            if (r != null && r.owner == p)
                return true;
        return left != null && left.owner == p || right != null && right.owner == p;
    }
    public void made(Player owner){
        this.owner=owner;
    }

    public static int RoadPath(Player owner, Road cu, Set<Integer> visited){//TODO check if work
        if (cu==null || visited.contains(cu.creation) || cu.owner!=owner){
            return 0;
        }
        visited.add(cu.creation);
        return 1+Math.max(Math.max(Math.max(RoadPath(owner, cu.connects[0], visited),RoadPath(owner, cu.connects[1], visited)),RoadPath(owner, cu.connects[2], visited)),RoadPath(owner, cu.connects[3], visited));
    }
}