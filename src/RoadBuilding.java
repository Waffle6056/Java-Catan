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

        Player turnPlayer = instance.turnPlayer;

             System.out.println("Build Road");
            while (!instance.build(Catan.BuildingOption.Road))
                System.out.println("failed try again");

            instance.waitMouseRelease();
            System.out.println("Build Road");
            while (!instance.build(Catan.BuildingOption.Road)) System.out.println("failed try again");

    }
}
