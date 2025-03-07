import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Board {
    //Graph of edges and vertexs
    Hex start;
    //Standard Board
    public Board(){
        makeDefaltBoard(3,3,3);
    }
    public void makeDefaltBoard(int Dimesion1,int Dimesion2,int Dimesion3){
        start=new Hex("Start");
        int numberOfHexs=1;
       // Hex wire=new Hex("eifesiojf");
       // start.combineHex(wire,1);
        Queue<Hex> building = new LinkedList<>();
        Hex left=start;
        for (int i = 1; i < Dimesion1; i++) {
            Hex temp=new Hex(""+numberOfHexs++);
            left.combineHex(temp,3);
            left=temp;
            System.out.println(left.toString());
            building.add(temp);
        }
        building.add(start);
        for (int i = building.size(); i < Dimesion2*building.size(); i++) {
            Hex pop=building.poll();
            Hex temp=new Hex(""+numberOfHexs++);
            pop.combineHex(temp,5);
            building.add(temp);
        }
        left=start;
    //    System.out.println(start.nearbyHex[5].toString());
        building.clear();
        for (int i = 1; i < Dimesion1; i++) {
            Hex cu=left;
            for (int j = 1; j < Dimesion2; j++) {
                if (i==Dimesion1-1){
                    building.add(cu);
                    continue;
                }
                if (j==Dimesion2-1){
                    building.add(cu);
                    continue;
                }
                System.out.println(cu.toString());
                Hex combining=cu.nearbyHex[3].nearbyHex[5];
                cu.combineHex(combining,4);
                if (j!=Dimesion2-1) {
                    Hex making = cu.nearbyHex[5];
                    making.combineHex(combining, 3);
                }

                cu=cu.nearbyHex[5];
            }
            left=left.nearbyHex[3];
        }
        // may need (dimesion3-1)*building.size();
        for (int i = building.size(); i < Dimesion3*building.size(); i++) {
            Hex pop=building.poll();
            Hex temp=new Hex();
            pop.combineHex(temp,4);
            building.add(temp);
        }

    }
    public void paint( Graphics window,double wrat,double hrat ) {
        start.paint(window,wrat,hrat);
    }

}
