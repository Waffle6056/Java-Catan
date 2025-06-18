public class Monopoly extends DevelopmentCard{

    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardMonopoly.fbx";}
    @Override
    public void use(Catan instance)  {
        Player turnPlayer = instance.turnPlayer;
        PortHolder<Hex.resource> port = new PortHolder<>(null);
        for (Card<Hex.resource> c : PortHolder.defaultInventory(1))
            port.addPermanent(c);
        instance.openTradingInventory(port);
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

        instance.currentPhase = Catan.Phase.Rolling;

        Hex.resource r = instance.selectResource();
        instance.robber.robAllResource(r);
        turnPlayer.OpenTrade.clear();

        instance.currentPhase = Catan.Phase.BuildingTrading;
    }


}