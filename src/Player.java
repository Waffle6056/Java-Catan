import RenderingStuff.Mesh;
import org.joml.Vector3f;

import java.util.*;

public class Player {
    int[] resources =new int[]{4, 2, 0, 4, 2};
    String name;
    CardHolder<CardHolder> UIElements = new CardHolder<>(this);
    CardHolder<DevelopmentCard> DevelopmentCards = new CardHolder<>(this);
    CardHolder<NewHex.resource> ResourceCards = new CardHolder<>(this);
    CardHolder<CardHolder<NewHex.resource>> TradingCards = new CardHolder<>(this);
    TradeHolder<NewHex.resource> OpenTrade = new TradeHolder<>(this);
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
        DevelopmentCards.position = new Vector3f(-0.2f,-0.2f,.5f);
        DevelopmentCards.len = 0.2f;

        UIElements.add(ResourceCards);
        ResourceCards.position = new Vector3f(0.4f,-0.4f,1f);

        UIElements.add(TradingCards);
        TradingCards.position = new Vector3f(0.4f,0.4f,1f);
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

    public int[] getResources(){
        return resources;
    }
    public void updateResourcesToCards(){
        ResourceCards.clear();
        //System.out.println(name+" CARDS UPDATED "+Arrays.toString(resources));
        for (int i = 0; i < resources.length; i++){
            for (int j = 0; j < resources[i]; j++)
                ResourceCards.add(NewHex.resource.values()[i], NewHex.fileNames[i]);
        }
    }
    public void updateCardsToResources(){
        Arrays.fill(resources,0);
        for (Card<NewHex.resource> card : ResourceCards.Cards)
            resources[card.data.index]++;
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
        if (resources[0] < a)
            return false;
        if (resources[1] < b)
            return false;
        if (resources[2] < c)
            return false;
        if (resources[3] < d)
            return false;
        if (resources[4] < e)
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
        resources[0] -= a;
        resources[1] -= b;
        resources[2] -= c;
        resources[3] -= d;
        resources[4] -= e;

    }
}