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
        for (int i = 0; i < 2; i++) {
            turnPlayer.ResourceCards.add(Hex.resource.Brick);
            turnPlayer.ResourceCards.add(Hex.resource.Wood);
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
