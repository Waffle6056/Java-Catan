import java.lang.reflect.Array;
import java.util.Arrays;

public class Player {
    int[] resources =new int[5];
    String name;
    CardHolder<CardHolder> UIElements = new CardHolder<>();
    CardHolder<DevelopmentCard> DevelopmentCards = new CardHolder<>();
    CardHolder<NewHex.resource> ResourceCards = new CardHolder<>();
    CardHolder<CardHolder<NewHex.resource>> TradingCards = new CardHolder<>();
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
        UIElements.add(ResourceCards);
        UIElements.add(TradingCards);
    }
    public int[] getResources(){
        return resources;
    }
    public void updateResourcesToCards(){
        ResourceCards.clear();
        for (int i = 0; i < resources.length; i++){
            for (int j = 0; j < resources[i]; j++)
                ResourceCards.add(new Card<>(NewHex.resource.values()[i]));
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