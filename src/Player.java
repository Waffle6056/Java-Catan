import RenderingStuff.Mesh;
import org.joml.Vector3f;

import java.util.*;

public class Player implements Renderable2d {
    public java.util.List<Mesh> toMesh2d() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        meshList.addAll(UIElements.toMesh2d());
        meshList.addAll(DevelopmentCards.toMesh2d());
        meshList.addAll(ResourceCards.toMesh2d());
        meshList.addAll(TradingCards.toMesh2d());
        meshList.addAll(OpenTrade.toMesh2d());
        return meshList;
    }
    String name;
    CardHolder<CardHolder> UIElements = new CardHolder<>(this);
    CardHolder<DevelopmentCard> DevelopmentCards = new CardHolder<>(this);
    CardHolder<Hex.resource> ResourceCards = new CardHolder<>(this);
    CardHolder<CardHolder<Hex.resource>> TradingCards = new CardHolder<>(this);
    TradeHolder<Hex.resource> OpenTrade = new TradeHolder<>(this);
    String markFile = "catan.fbx";
    String roadFile = "Buildings/Road.fbx";
    String settlementFile = "Buildings/Settlement.fbx";
    String cityFile = "Buildings/City.fbx";
    int settlements = 5, cities = 4, developmentCardLimit = 1;
    int vp = 0;
    int longestRoad=0,army=0;
    static Player mason=new Player("Mason Of The World");
    static Player baron=new Player("Baron Of The World");
    public Player(){
        Playercreate("Tester");
    }
    public Player(String name){
        Playercreate(name);
    }
    public Player(String name, String markFile){
        this.markFile = markFile;
        Playercreate(name);
    }
    public void Playercreate(String name){
        this.name=name;
        UIElements.add(DevelopmentCards);
        DevelopmentCards.position = new Vector3f(-0.4f,-0.4f,1f);
        DevelopmentCards.len = 0.4f;

        UIElements.add(ResourceCards);
        ResourceCards.position = new Vector3f(0.8f,-0.8f,1f);

        UIElements.add(TradingCards);
        TradingCards.position = new Vector3f(0.8f,0.8f,1f);
        TradingCards.rotation = (float) Math.toRadians(180f);

        UIElements.add(OpenTrade);
        OpenTrade.position = new Vector3f(0,0,1);


        for (int i = 0; i < 4; i++){
            UIElements.get(i).HighLight = new Mesh(markFile);
            UIElements.get(i).HighLight.rotation.rotateX(90);
        }
        UIElements.get(0).HighLight.position = DevelopmentCards.position;
        UIElements.get(1).HighLight.position = ResourceCards.position;
        UIElements.get(2).HighLight.position = TradingCards.position;
        UIElements.get(3).HighLight.position = OpenTrade.position;

    }
    public boolean hasWon(){
        return 10<=(vp+(this == mason?2:0)+(this == baron?2:0));
    }


    //return null if no player has won;
    public static void redopoints(List<Player> players) {//TODO call at end turn
        for (int i = 0; i < players.size(); i++) {
            Player cu = players.get(i);
            if (cu.army >= 3 && cu.army > baron.army) {
                baron = cu;
            }
            if (cu.longestRoad >= 5 && cu.longestRoad > mason.longestRoad) {
                mason = cu;
            }

        }
    }
    boolean payCheck(int a, int b, int c, int d, int e){
        if (ResourceCards.count(Hex.resource.values()[0]) < a)
            return false;
        if (ResourceCards.count(Hex.resource.values()[1]) < b)
            return false;
        if (ResourceCards.count(Hex.resource.values()[2]) < c)
            return false;
        if (ResourceCards.count(Hex.resource.values()[3]) < d)
            return false;
        if (ResourceCards.count(Hex.resource.values()[4]) < e)
            return false;
        return true;
    }
    boolean checkAmt(Catan.BuildingOption Option){
        switch (Option){
            case Road: return payCheck(1,0,0,1,0);
            case Town: return payCheck(1,1,0,1,1);
            case City: return payCheck(0,2,3,0,0);
        }
        return false;
    }
    void pay(Catan.BuildingOption Option){
        switch (Option) {
            case Road -> pay(1,0,0,1,0);
            case Town -> pay(1,1,0,1,1);
            case City -> pay(0,2,3,0,0);
        }
    }
    void pay(int a, int b, int c, int d, int e){
        int[] ar = {a,b,c,d,e};
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < ar[i]; j++)
                ResourceCards.remove(Hex.resource.values()[i]);
    }
}