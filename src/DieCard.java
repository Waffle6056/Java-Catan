import CardStructure.Card;
import CardStructure.RenderingStuff.Mesh;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public class DieCard<E> extends Card<E> {



    int roll = 1;
    boolean updateDice = true;

    public DieCard(E data) {
        super(data);
        mesh = new Mesh(dieMeshes[0]);
    }

    public int roll(){
        roll = (int)(Math.random()*6)+1;
        updateDice = true;
        System.out.println("ROLLED : "+roll);
        return roll;
    }
    String[] dieMeshes = {
            "Numbers/DieOne.fbx",
            "Numbers/DieTwo.fbx",
            "Numbers/DieThree.fbx",
            "Numbers/DieFour.fbx",
            "Numbers/DieFive.fbx",
            "Numbers/DieSix.fbx",
    };
    public void updateDiceVisual(){
        if (!updateDice)
            return;
        updateDice = false;

        Vector3f prevPos = mesh != null ? mesh.position : new Vector3f(0,0,0);

        mesh = new Mesh(dieMeshes[roll-1]);
        //mesh.rotation.rotateLocalX(Math.toRadians(90));
        mesh.position = prevPos;
        mesh.scale = new Vector3f(2,2,2);

    }

    @Override
    public List<Mesh> toMesh2d() {
        updateDiceVisual();
        return Arrays.asList(new Mesh[]{mesh});
    }
}
