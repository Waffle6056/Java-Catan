import java.util.BitSet;

public class Road {
    Player owner;
    Building one,two;
    public Road(){
        owner=null;
    }
    public void connect(Building a){
        if (one==null){
            one=a;
        }
        else if (two==null){
            two=a;
        }
        else {
            System.out.println("Connection code broke");
        }
    }

}
