import RenderingStuff.Mesh;
import org.joml.Vector3f;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Player {
    int[] resources =new int[]{4, 2, 0, 4, 2};
    String name;
    CardHolder<CardHolder> UIElements = new CardHolder<>(this);
    CardHolder<DevelopmentCard> DevelopmentCards = new CardHolder<>(this);
    CardHolder<NewHex.resource> ResourceCards = new CardHolder<>(this);
    CardHolder<CardHolder<NewHex.resource>> TradingCards = new CardHolder<>(this);
    CardHolder<NewHex.resource> OpenTrade = new CardHolder<>(this);
    String markFile = "catan.fbx";
    String roadFile = "Buildings/Road.fbx";
    String settlementFile = "Buildings/Settlement.fbx";
    String cityFile = "Buildings/City.fbx";
    int settlements = 5, cities = 4;
    int vpvisable=0,vphidden=0;
    public Player(){
        Playercreate("Tester");
    }
    public Player(String name, String markFile){
        this.markFile = markFile; Playercreate(name);
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
    public int[] getResources(){
        return resources;
    }
    public void updateResourcesToCards(){
        ResourceCards.clear();
        String[] fileNames = {
                "CatanCardMeshes/Resource/CardBrick.fbx",
                "CatanCardMeshes/Resource/CardGrain.fbx",
                "CatanCardMeshes/Resource/CardOre.fbx",
                "CatanCardMeshes/Resource/CardLumber.fbx",
                "CatanCardMeshes/Resource/CardWool.fbx",
        };
        for (int i = 0; i < resources.length; i++){
            for (int j = 0; j < resources[i]; j++)
                ResourceCards.add(new Card<>(NewHex.resource.values()[i], fileNames[i]));
        }
    }
    public void updateCardsToResources(){
        Arrays.fill(resources,0);
        for (Card<NewHex.resource> card : ResourceCards.Cards)
            resources[card.data.index]++;
    }
    public boolean hasWon(){
        return 10<=(vpvisable+vphidden);
    }
}