import RenderingStuff.Mesh;
import org.joml.Vector3f;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Player {
    int[] resources =new int[]{7, 5, 3, 7, 5};
    String name;
    CardHolder<CardHolder> UIElements = new CardHolder<>();
    CardHolder<DevelopmentCard> DevelopmentCards = new CardHolder<>();
    CardHolder<NewHex.resource> ResourceCards = new CardHolder<>();
    CardHolder<CardHolder<NewHex.resource>> TradingCards = new CardHolder<>();
    CardHolder<NewHex.resource> OpenTrade = new CardHolder<>();
    int settlements = 5, cities = 4;
    int vpvisable=0,vphidden=0;
    public Player(){
        Playercreate("Tester");
    }
    public Player(String name){
        Playercreate(name);
    }
    public void Playercreate(String name){
        this.name=name;
        UIElements.add(DevelopmentCards);
        DevelopmentCards.position = new Vector3f(-0.2f,-0.2f,.5f);
        DevelopmentCards.len = 0.2f;
        UIElements.get(0).HighLight.position = DevelopmentCards.position;

        UIElements.add(ResourceCards);
        ResourceCards.position = new Vector3f(0.4f,-0.4f,1f);
        UIElements.get(1).HighLight.position = ResourceCards.position;

        UIElements.add(TradingCards);
        TradingCards.position = new Vector3f(0.4f,0.4f,1f);
        TradingCards.rotation = (float) Math.toRadians(180f);
        UIElements.get(2).HighLight.position = TradingCards.position;

        UIElements.add(OpenTrade);
        OpenTrade.position = new Vector3f(0,0,1);
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