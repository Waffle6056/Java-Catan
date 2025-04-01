import java.util.ArrayList;

public class DevDeck {
    enum Dev{
        Knight,
        RoadBuilding,
        YearOfPlenty,
        Monopoly,
        Vp;
    }
    ArrayList<Dev> deck;
    ArrayList<Dev> discard;
    public DevDeck(){
        deck=new ArrayList<>();
        discard=new ArrayList<>();
        NewDeck();
    }
    public void NewDeck(){
        deck.clear();
        discard.clear();
        for (int i = 0; i < 14; i++) {
            deck.add(Dev.Knight);
        }
        for (int i = 0; i < 5; i++) {
            deck.add(Dev.Vp);
        }
        for (int i = 0; i < 2; i++) {
            deck.add(Dev.RoadBuilding);
        }
        for (int i = 0; i < 2; i++) {
            deck.add(Dev.YearOfPlenty);
        }
        for (int i = 0; i < 2; i++) {
            deck.add(Dev.Monopoly);
        }
    }
    public Dev draw(){
        int random=(int)(Math.random()*deck.size());
        return deck.remove(random);
    }

}
