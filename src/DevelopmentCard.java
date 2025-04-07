import java.util.*;

public abstract class DevelopmentCard {// NOT A CARD CHILD JUST SAME NAME
    public abstract void use(Catan instance);
    public String meshFile(){ return "unassigned";}
    public static Stack<DevelopmentCard> deck;

    // Knights, Monopoly, RoadBuilding, Year of Plenty, Victory points cards
    public static int[] cardCounts = {14, 2, 2, 2, 5};

    public static void createDeck(){
        deck = new Stack<>();
        for (int i = 0; i < cardCounts[0]; i++)
            deck.add(new Knight());
        for (int i = 0; i < cardCounts[1]; i++)
            deck.add(new Monopoly());
        for (int i = 0; i < cardCounts[2]; i++)
            deck.add(new RoadBuilding());
        for (int i = 0; i < cardCounts[3]; i++)
            deck.add(new YearOfPlenty());
        for (int i = 0; i < cardCounts[4]; i++)
            deck.add(new VpCard());

        for (int i = 0; i < 1000; i++)
            deck.add((int)(Math.random()*deck.size()), deck.pop());
    }

    public static DevelopmentCard createNew(){
       return deck.pop();
    }
}
