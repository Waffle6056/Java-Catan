public class YearOfPlenty extends DevelopmentCard{

    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardYearOfPlenty.fbx";}
    @Override
    public void use(Catan instance) {
        System.out.println("TAKE 2 RESOURCE CARDS FROM TEMPORARY TRADE INVENTORY");
        PortHolder<Hex.resource> port = new PortHolder<>(null);
        for (Card<Hex.resource> c : PortHolder.defaultInventory(2))
            port.addPermanent(c);
        instance.openTradingInventory(port);
    }
}
