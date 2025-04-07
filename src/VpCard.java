public class VpCard extends DevelopmentCard {

    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardVictoryPointOne.fbx";}
    @Override
    public void use(Catan instance) {//TODO TODO TODO
        instance.turnPlayer.vpvisable++;
    }
}