import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.Renderable;
import CardStructure.RenderingStuff.Renderable2d;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.*;

public class RobberBaron implements Renderable, Renderable2d {
    public Catan instance;
    public Mesh mesh;
    public Mesh meshNotifier;
    public Queue<Player> victims = new LinkedList<>();
    public RobberBaron(Catan i){
        instance = i;
        mesh = new Mesh("HexMeshes/Robber.fbx");
        mesh.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        mesh.position.add(0,-5,0);

        Hex hex = Hex.RobberBaronedHex;
        mesh.position = new Vector3f(hex.mesh.position.x, 2.75f, hex.mesh.position.z);
        meshNotifier = new Mesh("HexMeshes/Robber.fbx");
        meshNotifier.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        meshNotifier.position.add(1f,0f,-1f);
    }
    public void moveRobberBaron(){
        instance.currentPhase = Catan.Phase.SetUp;
        meshNotifier.position.add(0f,0f,2f);
        try {
            //System.out.println("start build");
            new Thread( () -> {
                //Select New Hex
                instance.waitMouseRelease();

                Vector3f mouseClickPos = instance.waitMouseClick();
                Hex hex = instance.selectHex(mouseClickPos);
                while (Hex.RobberBaronedHex == hex) {
                    System.out.println("ROBBER MUST MOVE TO A DIFFERENT HEX");
                    instance.waitMouseRelease();
                    mouseClickPos = instance.waitMouseClick();
                    hex = instance.selectHex(mouseClickPos);
                }

                meshNotifier.position.add(0f,0f,-2f);
                //assign is RobberBaron
                Hex.RobberBaronedHex = hex;
                mesh.position = new Vector3f(hex.mesh.position.x, 2.75f, hex.mesh.position.z);
                HashSet<Player> visited = new HashSet<>();
                //Steal from near Player
                for (int i = 0; i < 6; i++)
                    if (hex.buildings[i].owner != null){
                        Player victim = hex.buildings[i].owner;
                        if (visited.contains(victim))
                            continue;
                        visited.add(victim);
                        victims.add(victim);
                    }
                instance.turnPlayer.OpenTrade.clear();
                if (instance.currentPhase == Catan.Phase.SetUp)
                    instance.currentPhase = Catan.Phase.BuildingTrading;
            }).start();

        } catch (Exception e){}
    }
    public void robAllResource(Hex.resource r){
        for (Player victim : instance.players) {
            if (victim == instance.turnPlayer)
                continue;

            System.out.println("ROBBED "+victim.name);

            victim.ResourceCards.deselectAll();
            if (victim.ResourceCards.Cards.isEmpty())
                return;


            if (!victim.ResourceCards.selectAllEqual(r))
                continue;

            System.out.println(victim.ResourceCards.current().data);

            instance.turnPlayer.ResourceCards.deselectAll();
            instance.turnPlayer.ResourceCards.trade(victim.ResourceCards);
        }
        instance.turnPlayer.OpenTrade.update();
    }
    void rob(){
        while (!victims.isEmpty()) {
            rob(victims.remove());
        }
    }
    void rob(Player victim){
        System.out.println("ROBBED "+victim.name);
        if (instance.turnPlayer == victim)
            return;


        if (victim.ResourceCards.Cards.isEmpty())
            return;
        victim.ResourceCards.scroll((int) (Math.random() * 200));
        victim.ResourceCards.select();
        System.out.println(victim.ResourceCards.current().data);

        instance.turnPlayer.ResourceCards.deselectAll();
        instance.turnPlayer.ResourceCards.trade(victim.ResourceCards);
        instance.turnPlayer.OpenTrade.update();
    }

    @Override
    public List<Mesh> toMesh() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        if (mesh != null)
            meshList.add(mesh);
        return meshList;
    }
    @Override
    public java.util.List<Mesh> toMesh2d() {
        java.util.List<Mesh> meshList = new ArrayList<>();
        if (meshNotifier != null)
            meshList.add(meshNotifier);
        return meshList;
    }
}
