import java.util.Set;

public class Road {
    Player owner;
    Road[] connects=new Road[4];
    Building left, right;
    int creation=a;
    static int a=0;
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

    public static int RoadPath(Player owner, Road cu, Set<Integer> visited){//TODO check if work
        if (cu==null || visited.contains(cu.creation) || cu.owner!=owner){
            return 0;
        }
        visited.add(cu.creation);
        return 1+Math.max(Math.max(Math.max(RoadPath(owner, cu.connects[0], visited),RoadPath(owner, cu.connects[1], visited)),RoadPath(owner, cu.connects[2], visited)),RoadPath(owner, cu.connects[3], visited));
    }
}