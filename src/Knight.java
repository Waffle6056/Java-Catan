import java.util.HashMap;

public class Knight extends DevelopmentCard{

    static Player LargestArmy;
    static HashMap<Player, Integer> usedKnights = new HashMap<>();
    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardKnight.fbx";}
    @Override
    public void use(Catan instance) {
        instance.robberThread();
        usedKnights.put(instance.turnPlayer, usedKnights.getOrDefault(instance.turnPlayer,0)+1);
        if (usedKnights.get(instance.turnPlayer) > usedKnights.getOrDefault(LargestArmy, 2) ) {
            LargestArmy = instance.turnPlayer;
            System.out.println("NEW LARGEST ARMY : "+LargestArmy.name);
        }
    }
}
