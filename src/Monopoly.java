import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


import java.util.Scanner;

public class Monopoly extends DevelopmentCard{

    @Override
    public String meshFile(){ return "CatanCardMeshes/Development/CardMonopoly.fbx";}
    @Override
    public void use(Catan instance) {
        System.out.println("not implemented just trade everyone manually lol");
        Player bank = new Player();
        bank.resources = new int[]{2,2,2,2,2};
        bank.updateResourcesToCards();
        instance.openTradingInventory(bank.ResourceCards);
        new Thread(){
            NewHex.resource r = instance.selectResource();
        }.run();
    }
}