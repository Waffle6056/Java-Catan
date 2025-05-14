import org.joml.Math;

public class RoadBuilding extends DevelopmentCard{

    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardRoadBuilding.fbx";}
    @Override
    public void use(Catan instance) {
        StartBuildThread(instance);
    }
    private void StartBuildThread(Catan instance){
        try {
            //System.out.println("start build");
            new Thread( () -> {
                Build(instance);
            }).start();

        } catch (Exception e){}
    }
    private void Build(Catan instance){

        instance.currentPhase = Catan.Phase.Rolling;

        Player turnPlayer = instance.turnPlayer;
        turnPlayer.resources[0] += 2;
        turnPlayer.resources[3] += 2;
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
