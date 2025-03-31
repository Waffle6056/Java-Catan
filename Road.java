import java.util.BitSet;

public class Road {
    Player owner;
    Road[] connects=new Road[4];
    int connectionnumber=0;
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
        System.out.print(creation+" is connecting to "+e.creation+"  :");
        connects[connectionnumber++]=e;
        e.connects[e.connectionnumber++]=this;
        System.out.println("Sucess");

    }

}
