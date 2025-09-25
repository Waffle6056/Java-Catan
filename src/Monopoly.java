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