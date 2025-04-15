import java.util.HashMap;

public class Knight extends DevelopmentCard{


    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardKnight.fbx";}
    @Override
    public void use(Catan instance) {
        instance.robber.startRobbing();
        instance.turnPlayer.army++;
    }
}
