import RenderingStuff.Mesh;
import org.joml.Vector3f;
import java.util.*;

public class Player implements Renderable2d {
    public java.util.List<Mesh> toMesh2d() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        for (CardHolder c : UIElements){
            if ((c.VisibilityLayers & ActiveVisibilityLayer) > 0)
                meshList.addAll(c.toMesh2d());

        }
        return meshList;
    }
    enum ModelSet{
        Red("CatanCardMeshes/PlayerOne.fbx","Buildings/RoadOne.fbx","Buildings/CityOne.fbx","Buildings/SettlementOne.fbx"),
        White("CatanCardMeshes/PlayerTwo.fbx","Buildings/RoadTwo.fbx","Buildings/CityTwo.fbx","Buildings/SettlementTwo.fbx"),
        Blue("CatanCardMeshes/PlayerThree.fbx","Buildings/RoadThree.fbx","Buildings/CityThree.fbx","Buildings/SettlementThree.fbx"),
        Orange("CatanCardMeshes/PlayerFour.fbx","Buildings/RoadFour.fbx","Buildings/CityFour.fbx","Buildings/SettlementFour.fbx");
        public final String ColorIndicator;
        public final String RoadMesh;
        public final String CityMesh;
        public final String SettlementMesh;
        ModelSet(String ColorIndicator, String RoadMesh, String CityMesh, String SettlementMesh) {
            this.ColorIndicator = ColorIndicator;
            this.RoadMesh = RoadMesh;
            this.CityMesh = CityMesh;
            this.SettlementMesh = SettlementMesh;
        }
    }
    String name;
    List<CardHolder> UIElements = new ArrayList<>();
    CardHolder<CardHolder.VisibilityLayer> VisibilityCards = new CardHolder<>(this, CardHolder.VisibilityLayer.DevelopmentCard.bit | CardHolder.VisibilityLayer.Trading.bit | CardHolder.VisibilityLayer.Building.bit);
    CardHolder<Catan.BuildingOption> BuildingCards = new CardHolder<>(this, CardHolder.VisibilityLayer.Building.bit);
    CardHolder<DevelopmentCard> DevelopmentCards = new CardHolder<>(this, CardHolder.VisibilityLayer.DevelopmentCard.bit);
    CardHolder<Hex.resource> ResourceCards = new CardHolder<>(this, CardHolder.VisibilityLayer.DevelopmentCard.bit | CardHolder.VisibilityLayer.Trading.bit | CardHolder.VisibilityLayer.Building.bit);
    CardHolder<CardHolder<Hex.resource>> TradingCards = new CardHolder<>(this, CardHolder.VisibilityLayer.Trading.bit);
    TradeHolder<Hex.resource> OpenTrade = new TradeHolder<>(this, CardHolder.VisibilityLayer.Trading.bit);
    int ActiveVisibilityLayer = 0;

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
        VisibilityCards.cardPositioner = CardHolder::setLinearTransforms;
        for (CardHolder.VisibilityLayer layer : CardHolder.VisibilityLayer.values())
            VisibilityCards.add(layer,markFile);
        //System.out.println(VisibilityCards.Cards.size());
        VisibilityCards.position = new Vector3f(VisibilityCards.len*VisibilityCards.Cards.size()/2f-VisibilityCards.len/2.0f,-1.2f,2f);
        UIElements.add(VisibilityCards);


        for (Catan.BuildingOption building : Catan.BuildingOption.values()) {
            BuildingCards.add(building, markFile);
            //System.out.println(building);
        }
        BuildingCards.position = new Vector3f(-0.8f,-1f,2f);
        UIElements.add(BuildingCards);

        DevelopmentCards.position = new Vector3f(-0.8f,-1f,2f);
        UIElements.add(DevelopmentCards);

        ResourceCards.position = new Vector3f(0.8f,-1f,2f);
        UIElements.add(ResourceCards);

        TradingCards.position = new Vector3f(-0.8f,-1f,2f);
        UIElements.add(TradingCards);

        OpenTrade.position = new Vector3f(0,0,2);
        UIElements.add(OpenTrade);



    }
    public boolean hasWon(){
        return 10<=(vp+(this == mason?2:0)+(this == baron?2:0));
    }


    //return null if no player has won;
    public static void redopoints(List<Player> players) {
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
    boolean payCheck(int Brick, int Grain, int Rock, int Wood, int Wool){
        if (ResourceCards.count(Hex.resource.values()[0]) < Brick)
            return false;
        if (ResourceCards.count(Hex.resource.values()[1]) < Grain)
            return false;
        if (ResourceCards.count(Hex.resource.values()[2]) < Rock)
            return false;
        if (ResourceCards.count(Hex.resource.values()[3]) < Wood)
            return false;
        if (ResourceCards.count(Hex.resource.values()[4]) < Wool)
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
    void pay(int Brick, int Grain, int Rock, int Wood, int Wool){
        int[] ar = {Brick, Grain, Rock, Wood, Wool};
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < ar[i]; j++)
                ResourceCards.remove(Hex.resource.values()[i]);
    }
}