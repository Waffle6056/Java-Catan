import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class NewBoard {
    HashMap<String ,NewHex> grid;
    public NewBoard(){
        grid=new HashMap<>();
        makeDefaltBoard(3,3,3);
    }
    //q + r + s = 0
    //Neighbors
    //(q + 1, r - 1, s)
    //(q + 1, r, s - 1)
    //(q, r + 1, s - 1)
    //(q - 1, r + 1, s)
    //(q - 1, r, s + 1)
    //(q, r - 1, s + 1)
    public void makeDefaltBoard(int Dimesion1,int Dimesion2,int Dimesion3){
        
        for (int f = 0; f < Dimesion1; f++) {
            for (int i = 0; i < Dimesion3+2-f; i++) {
                int q=-f-i,r=f,s=i;
                grid.putIfAbsent(encoder(q,r,s),new NewHex(q,r,s));
            }
        }
        for (int f = 1; f < Dimesion2; f++) {
            for (int i = 0; i < Dimesion3 + 2 - f; i++) {
                int q = -i, r = -f, s = f + i;
                grid.putIfAbsent(encoder(q, r, s), new NewHex(q, r, s));
            }
        }
        for(String k:grid.keySet()){
            String[] line=k.split(",");
            int q=Integer.parseInt(line[0]), r=Integer.parseInt(line[1]), s=Integer.parseInt(line[2]);
            if (grid.containsKey(encoder(q-1,r+1,s))){//Right
                grid.get(k).combineHex(getHex(q-1,r+1,s),3);
            }
            if (grid.containsKey(encoder(q,r-1,s+1))){//DownLeft
                grid.get(k).combineHex(getHex(q,r-1,s+1),5);
            }
            if (grid.containsKey(encoder(q-1,r,s+1))){//DownRight
                grid.get(k).combineHex(getHex(q-1,r,s+1),4);
            }
            System.out.println(k);
        }
    }
    public String encoder(int q,int r,int s){
        return q+","+r+","+s;
    }
    public NewHex getHex(int q,int r,int s){
        return grid.get(encoder(q,r,s));
    }
    public void paint(Graphics window, double wrat, double hrat ) {
        for(String k:grid.keySet()){
            grid.get(k).paint(window,wrat,hrat);
        }
       // start.paint(window,wrat,hrat);
    }
}
