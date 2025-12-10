import CardStructure.BankHolder;
import CardStructure.Card;
import CardStructure.TradingRequirementFunction;

import java.util.*;

public abstract class DevelopmentCard {// NOT A CARD CHILD JUST SAME NAME
    public boolean enabled = false;
    public abstract void use(Catan instance);
    public String meshFile(){ return "unassigned";}
    public static Stack<DevelopmentCard> deck;

    // Knights, DevelopmentCard.Monopoly, DevelopmentCard.RoadBuilding, Year of Plenty, Victory points cards
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

    public static boolean empty() {
        return deck.empty();
    }
    public static DevelopmentCard createNew(){
        if (DevelopmentCard.empty())
            return null;
        return deck.pop();
    }

    public static class YearOfPlenty extends DevelopmentCard{

        @Override
        public String meshFile(){ return "CatanCardMeshes/Development/CardYearOfPlenty.fbx";}
        @Override
        public void use(Catan instance) {
            System.out.println("TAKE 2 RESOURCE CARDS FROM TEMPORARY TRADE INVENTORY");
            BankHolder<Hex.resource> port = new BankHolder<>(TradingRequirementFunction::SameTypeMatchingAmount);
            for (Card<Hex.resource> c : PortHolder.defaultInventory(2))
                port.addPermanent(c);
            instance.turnPlayer.openTradingInventory(port);
        }
    }

    public static class Monopoly extends DevelopmentCard{

        @Override
        public String meshFile(){ return "CatanCardMeshes/Development/CardMonopoly.fbx";}
        @Override
        public void use(Catan instance)  {
            Player turnPlayer = instance.turnPlayer;
            BankHolder<Hex.resource> port = new BankHolder<>(TradingRequirementFunction::SameTypeMatchingAmount);
            for (Card<Hex.resource> c : PortHolder.defaultInventory(1))
                port.addPermanent(c);
            instance.turnPlayer.openTradingInventory(port);
            StartUseThread(instance);
        }
        private void StartUseThread(Catan instance){
            try {
                //System.out.println("start build");
                new Thread( () -> {
                    Use(instance);
                }).start();

            } catch (Exception e){}
        }
        private void Use(Catan instance){
            Player turnPlayer = instance.turnPlayer;

            instance.currentPhase = Catan.Phase.SetUp;

            Hex.resource r = instance.selectResource();
            instance.robber.robAllResource(r);
            turnPlayer.OpenTrade.clear();

            instance.currentPhase = Catan.Phase.BuildingTrading;
        }


    }

    public static class Knight extends DevelopmentCard{


        @Override
        public String meshFile(){ return "CatanCardMeshes/Development/CardKnight.fbx";}
        @Override
        public void use(Catan instance) {
            instance.robber.moveRobberBaron();
            instance.turnPlayer.army++;
        }
    }

    public static class VpCard extends DevelopmentCard {

        @Override
        public String meshFile(){ return "CatanCardMeshes/Development/CardVictoryPointOne.fbx";}
        @Override
        public void use(Catan instance) {//TODO TODO TODO
            instance.turnPlayer.vp++;
        }
    }

    public static class RoadBuilding extends DevelopmentCard{

        @Override
        public String meshFile(){ return "CatanCardMeshes/Development/CardRoadBuilding.fbx";}
        @Override
        public void use(Catan instance) {
            StartBuildRoadsThread(instance);
        }
        private void StartBuildRoadsThread(Catan instance){
            try {
                //System.out.println("start build");
                new Thread( () -> {
                    BuildRoads(instance);
                }).start();

            } catch (Exception e){}
        }
        private void BuildRoads(Catan instance){

            instance.currentPhase = Catan.Phase.SetUp;

            Player turnPlayer = instance.turnPlayer;
            for (int i = 0; i < 2; i++) {
                turnPlayer.ResourceCards.add(new Card<>(Hex.resource.Brick));
                turnPlayer.ResourceCards.add(new Card<>(Hex.resource.Wood));
            }
            System.out.println("BUILD A ROAD");

                while (!instance.Board.build(Catan.BuildingOption.Road,turnPlayer,instance))
                    System.out.println("failed try again");

                instance.waitMouseRelease();
            System.out.println("BUILD A ROAD");

                while (!instance.Board.build(Catan.BuildingOption.Road,turnPlayer,instance))
                    System.out.println("failed try again");

            instance.currentPhase = Catan.Phase.BuildingTrading;
        }
    }
}
