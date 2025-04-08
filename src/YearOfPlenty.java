public class YearOfPlenty extends DevelopmentCard{

    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardYearOfPlenty.fbx";}
    @Override
    public void use(Catan instance) {
        System.out.println("TAKE 2 RESOURCE CARDS FROM TEMPORARY TRADE INVENTORY");
        Player bank = new Player();
        bank.resources = new int[]{2,2,2,2,2};
        bank.updateResourcesToCards();
        instance.openTradingInventory(bank.ResourceCards);
    }
}
