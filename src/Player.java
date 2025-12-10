import CardStructure.Card;
import CardStructure.CardHolder;
import CardStructure.CardHolderDisplayOnly;
import CardStructure.CardPositioner;
import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.Renderable2d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class Player implements Renderable2d {
    public java.util.List<Mesh> toMesh2d() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        if (playerIndicator != null)
            meshList.add(playerIndicator);

        if (OpenTrade.Data != null)
            TradingCards.VisibilityLayers = 0;
        else
            TradingCards.VisibilityLayers = CardHolder.VisibilityLayer.Trading.bit;

        for (CardHolder c : UIElements.CardData()) {
            if ((c.VisibilityLayers & ActiveVisibilityLayer) > 0)
                meshList.addAll(c.toMesh2d());

        }

        return meshList;
    }

    enum ModelSet {
        Red("CatanCardMeshes/PlayerOne.fbx", "Buildings/RoadOne.fbx", "Buildings/CityOne.fbx", "Buildings/SettlementOne.fbx"),
        White("CatanCardMeshes/PlayerTwo.fbx", "Buildings/RoadTwo.fbx", "Buildings/CityTwo.fbx", "Buildings/SettlementTwo.fbx"),
        Blue("CatanCardMeshes/PlayerThree.fbx", "Buildings/RoadThree.fbx", "Buildings/CityThree.fbx", "Buildings/SettlementThree.fbx"),
        Orange("CatanCardMeshes/PlayerFour.fbx", "Buildings/RoadFour.fbx", "Buildings/CityFour.fbx", "Buildings/SettlementFour.fbx");
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
    CardHolder<CardHolder, UICard> UIElements = new CardHolder<>();
    CardHolder<CardHolder.VisibilityLayer, Card<CardHolder.VisibilityLayer>> VisibilityCards = new CardHolder<>( CardHolder.VisibilityLayer.DevelopmentCard.bit | CardHolder.VisibilityLayer.Trading.bit | CardHolder.VisibilityLayer.Building.bit);
    CardHolder<Catan.BuildingOption, Card<Catan.BuildingOption>> BuildingCards = new CardHolder<>( CardHolder.VisibilityLayer.Building.bit);
    CardHolder<DevelopmentCard, Card<DevelopmentCard>> DevelopmentCards = new CardHolder<>(CardHolder.VisibilityLayer.DevelopmentCard.bit);
    CardHolder<Hex.resource, Card<Hex.resource>> ResourceCards = new CardHolder<>( CardHolder.VisibilityLayer.DevelopmentCard.bit | CardHolder.VisibilityLayer.Trading.bit | CardHolder.VisibilityLayer.Building.bit);
    CardHolder<CardHolder<Hex.resource, Card<Hex.resource>>, Card<CardHolder<Hex.resource, Card<Hex.resource>>>> TradingCards = new CardHolder<>( CardHolder.VisibilityLayer.Trading.bit);
    CardHolderDisplayOnly OpenTrade = new CardHolderDisplayOnly( CardHolder.VisibilityLayer.Trading.bit);
    CardHolder<CardHolder,UICard> BuildingButtons = new CardHolder<>(CardHolder.VisibilityLayer.Building.bit);
    CardHolder<CardHolder,UICard> TradeButtons = new CardHolder<>(CardHolder.VisibilityLayer.Trading.bit);
    CardHolder<CardHolder,UICard> DevelopmentCardButtons = new CardHolder<>(CardHolder.VisibilityLayer.DevelopmentCard.bit);
    Mesh playerIndicator;
    int ActiveVisibilityLayer = 0;

    String markFile = "catan.fbx";
    String roadFile = "Buildings/Road.fbx";
    String settlementFile = "Buildings/Settlement.fbx";
    String cityFile = "Buildings/City.fbx";
    int settlements = 5, cities = 4, developmentCardLimit = 1;
    int vp = 0;
    int longestRoad = 0, army = 0;
    static Player mason = new Player("Mason Of The World");
    static Player baron = new Player("Baron Of The World");

    public Player() {
        Playercreate("Tester");
    }

    public Player(String name) {
        Playercreate(name);
    }

    public Player(String name, ModelSet set) {

        roadFile = set.RoadMesh;
        cityFile = set.CityMesh;
        settlementFile = set.SettlementMesh;
        markFile = set.ColorIndicator;
        Playercreate(name);
    }

    public void Playercreate(String name) {
        this.name = name;
        VisibilityCards.cardPositioner = CardPositioner::setLinearTransforms;
        for (CardHolder.VisibilityLayer layer : CardHolder.VisibilityLayer.values())
            VisibilityCards.add(new Card<>(layer, layer.TabMeshFile));
        //System.out.println(VisibilityCards.Cards.size());
        VisibilityCards.len = .24f;
        VisibilityCards.dis = VisibilityCards.len * 2;
        VisibilityCards.rotation = (float) (Math.PI / 2);
        VisibilityCards.position = new Vector3f(1.5f, VisibilityCards.len * VisibilityCards.Cards.size() / 2f - VisibilityCards.len / 2.0f, 5f);
        UIElements.add(new UICard(VisibilityCards, Player::setVisibilityLayer));


        for (Catan.BuildingOption building : Catan.BuildingOption.values()) {
            BuildingCards.add(new Card<>(building, building.CardFile));
            //System.out.println(building);
        }
        BuildingCards.position = new Vector3f(-0.5f, -1f, 2f);
        UIElements.add(new UICard(BuildingCards, Player::doNothing));

        DevelopmentCards.position = new Vector3f(-0.5f, -1f, 2f);
        UIElements.add(new UICard(DevelopmentCards, Player::doNothing));

        ResourceCards.position = new Vector3f(0.5f, -1f, 2f);
        UIElements.add(new UICard(ResourceCards, Player::selectCurrentCard));

        TradingCards.position = new Vector3f(-0.5f, -1f, 2f);
        UIElements.add(new UICard(TradingCards, Player::openTradingInventory));

        OpenTrade.position = TradingCards.position;
        UIElements.add(new UICard(OpenTrade, Player::selectCurrentTradeInvCard));

        BuildingButtons.position = new Vector3f(0,-1,5);
        BuildingButtons.add(new UICard(BuildingCards,"Buttons/TradeButton.fbx",Player::buildBuilding));
        UIElements.add(new UICard(BuildingButtons, Player::recursiveUse));

        TradeButtons.position = new Vector3f(0,-1,5);
        TradeButtons.add(new UICard(ResourceCards,"Buttons/TradeButton.fbx",Player::tradeCurrentSelectedCards));
        UIElements.add(new UICard(TradeButtons, Player::recursiveUse));

        DevelopmentCardButtons.position = new Vector3f(0,-1,5);
        DevelopmentCardButtons.add(new UICard(DevelopmentCards,"Buttons/TradeButton.fbx",Player::useCurrentDevelopmentCard));
        UIElements.add(new UICard(DevelopmentCardButtons, Player::recursiveUse));

        playerIndicator = new Mesh(markFile);
        playerIndicator.position = new Vector3f(1.2f,0.8f,5);
        playerIndicator.rotation = new Quaternionf();
        playerIndicator.rotation.rotateLocalY((float) Math.toRadians(180)).rotateLocalX((float) Math.toRadians(90));
        playerIndicator.scale = new Vector3f(2,2,2);
//        Integer a = -1;
//        Object b = a;
//        String c = b;

//        CardStructure.CardStructure.Card<Integer> a = new CardStructure.CardStructure.Card<>(null);
//        CardStructure.CardStructure.Card b = a;
//        CardStructure.CardStructure.Card<String> c = b;

//        CardStructure.CardStructure.Card<Integer> a = new CardStructure.CardStructure.Card<>(5);
//        CardStructure.CardStructure.Card b = a;
//        test(b);
//    }
//
//    void test(CardStructure.CardStructure.Card<String> d) {
//        System.out.println(d);
//        System.out.println(d.data.length());
//    }

    }
    static void recursiveUse(CardHolder<CardHolder,UICard> buttons, Catan instance, Player owner){
        buttons.current().effect.use(buttons.current().data,instance,owner);
    }
    static void buyDevelopmentCard(CardHolder<DevelopmentCard,Card<DevelopmentCard>> DevelopmentCards, Player owner){
        if (owner != null && owner.payCheck(0, 1, 1, 0, 1) && !DevelopmentCard.empty()) {
            owner.pay(0, 1, 1, 0, 1);
            DevelopmentCard d = DevelopmentCard.createNew();
            //System.out.println(d.meshFile);
            DevelopmentCards.add(new Card<DevelopmentCard>(d, d.meshFile()));
        }
    }
    static void doNothing(CardHolder DevelopmentCards, Catan instance, Player owner) {
        return;
    }
    static void useCurrentDevelopmentCard(CardHolder<DevelopmentCard,Card<DevelopmentCard>> DevelopmentCards, Catan instance, Player owner){
        if (owner == null || owner.developmentCardLimit <= 0 || DevelopmentCards.current() == null || !DevelopmentCards.current().data.enabled)
            return;
        owner.developmentCardLimit--;
        DevelopmentCards.current().data.use(instance); // uses current development card;

        DevelopmentCards.remove(DevelopmentCards.current());
    }
    static void selectCurrentTradeInvCard(CardHolderDisplayOnly OpenTrade, Catan instance, Player owner){
        OpenTrade.select();
    }
    static void selectCurrentCard(CardHolder Cards, Catan instance, Player owner){
        Cards.select();
    }
    static void openTradingInventory(CardHolder<CardHolder<Hex.resource, Card<Hex.resource>>, Card<CardHolder<Hex.resource, Card<Hex.resource>>>> TradingCards, Catan instance, Player owner) {
        if (owner == null)
            return;
        owner.openTradingInventory(TradingCards.current().data);
    }
    static void setVisibilityLayer(CardHolder<CardHolder.VisibilityLayer, Card<CardHolder.VisibilityLayer>> VisibilityCards, Catan instance, Player owner){
        owner.ActiveVisibilityLayer = VisibilityCards.current().data.bit; // selects current
    }
    static void buildBuilding(CardHolder<Catan.BuildingOption, Card<Catan.BuildingOption>> BuildingCards, Catan instance, Player owner){
        if (owner == null)
            return;
        if (BuildingCards.current().data == Catan.BuildingOption.DevelopmentCard)
            buyDevelopmentCard(owner.DevelopmentCards, owner);
        else
            instance.startBuildThread(owner.BuildingCards.current().data);
    }
    void openTradingInventory(CardHolder<Hex.resource, Card<Hex.resource>> inv){

        OpenTrade.clear();
        if (OpenTrade.Data == inv) {
            OpenTrade.Data = null;
            return;
        }

        if (OpenTrade.Data != null)
            OpenTrade.Data.deselectAll();
        OpenTrade.update(inv);
    }


    static void tradeCurrentSelectedCards(CardHolder<Hex.resource, Card<Hex.resource>> ResourceCards, Catan instance, Player owner){
        if (owner == null)
            return;
        if (owner.OpenTrade.Data == null)
            return;
        owner.OpenTrade.Data.trade(ResourceCards);
        owner.OpenTrade.update(null);
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
                ResourceCards.removeFirst(Hex.resource.values()[i]);
    }
}