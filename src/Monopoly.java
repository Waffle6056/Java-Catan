import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


import java.util.Scanner;

public class Monopoly extends DevelopmentCard{

    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardMonopoly.fbx";}
    @Override
    public void use(Catan instance)  {
        Player turnPlayer = instance.turnPlayer;
        Player bank = new Player();
        bank.resources = new int[]{1,1,1,1,1};
        bank.updateResourcesToCards();
        instance.openTradingInventory(bank.ResourceCards);
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
        NewHex.resource r = instance.selectResource();
        instance.robber.robAllResource(r);

        instance.toggleVisible(turnPlayer.OpenTrade, false);
        turnPlayer.OpenTrade.clear();

    }


}