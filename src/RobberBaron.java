import RenderingStuff.Mesh;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.*;

public class RobberBaron implements Renderable, Renderable2d{
    public Catan instance;
    public Mesh mesh;
    public Mesh meshNotifier;
    public Queue<Player> victims = new LinkedList<>();
    public RobberBaron(Catan i){
        instance = i;
        mesh = new Mesh("HexMeshes/Robber.fbx");
        mesh.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        mesh.position.add(0,-5,0);

        meshNotifier = new Mesh("HexMeshes/Robber.fbx");
        meshNotifier.rotation.rotateX((float)java.lang.Math.toRadians(-90));
        meshNotifier.position.add(0.8f,0f,-2f);
    }
    public void startRobbing(){
        instance.currentPhase = Catan.Phase.Rolling;
        try {
            //System.out.println("start build");
            new Thread( () -> {
                meshNotifier.position.add(0f,0f,4f);
                //Select New Hex
                instance.waitMouseRelease();

                Vector3f mouseClickPos = instance.waitMouseClick();
                Hex hex = instance.selectHex(mouseClickPos);
                while (Hex.isRobberBaroned == hex) {
                    System.out.println("ROBBER MUST MOVE TO A DIFFERENT HEX");
                    instance.waitMouseRelease();
                    mouseClickPos = instance.waitMouseClick();
                    hex = instance.selectHex(mouseClickPos);

                }
                meshNotifier.position.add(0f,0f,-4f);
                //assign is RobberBaron
                Hex.isRobberBaroned = hex;
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
                if (instance.currentPhase == Catan.Phase.Rolling)
                    instance.currentPhase = Catan.Phase.BuildingTrading;
            }).start();

        } catch (Exception e){}
    }
    public void robAllResource(Hex.resource r){
        for (Player victim : instance.players) {
            if (victim == instance.turnPlayer)
                continue;

            System.out.println("ROBBED "+victim.name);

            instance.turnPlayer.ResourceCards.deselectAll();
            victim.ResourceCards.deselectAll();
            if (victim.ResourceCards.Cards.isEmpty())
                return;


            if (!victim.ResourceCards.selectAllEqual(r))
                continue;

            System.out.println(victim.ResourceCards.current().data);

            instance.trade(victim.ResourceCards);
        }
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
        if (instance.turnPlayer.OpenTrade.owner == victim)
            instance.turnPlayer.OpenTrade.clear();

        if (victim.ResourceCards.Cards.isEmpty())
            return;
        victim.ResourceCards.scroll((int) (Math.random() * 200));
        victim.ResourceCards.select();
        System.out.println(victim.ResourceCards.current().data);

        instance.trade(victim.ResourceCards);
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
