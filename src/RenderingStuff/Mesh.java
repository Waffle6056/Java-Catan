package RenderingStuff;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import java.util.*;

public class Mesh {
    static HashMap<String, List<VertexCollection>> vcMap = new HashMap<>();
    static HashMap<String, List<Material>> matMap = new HashMap<>();
    List<VertexCollection> pieces = new ArrayList<>();
    public List<Material> materials = new ArrayList<>();
    public Vector3f position = new Vector3f(0,0,0);
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1,1,1);
    public Mesh(String filePath){
        importData(filePath);
    }
    public Mesh(String filePath, Vector3f position, Quaternionf rotation, Vector3f scale){
        importData(filePath);
    }

    void importData(String filePath){
        if (vcMap.containsKey(filePath)) {
            pieces = vcMap.get(filePath);
            materials = matMap.get(filePath);
            //System.out.println("working import skip");
            return;
        }

        AIScene scene = Assimp.aiImportFile(filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);

        if (scene == null) System.err.println("Couldn't load model at " + filePath +" "+Assimp.aiGetErrorString());

        for (int i = 0; i < scene.mNumMaterials(); i++)
            materials.add(new Material(AIMaterial.create(scene.mMaterials().get(i))));

        for (int i = 0; i < scene.mNumMeshes(); i++)
            pieces.add(new VertexCollection(AIMesh.create(scene.mMeshes().get(i))));

        vcMap.put(filePath, pieces);
        matMap.put(filePath, materials);

    }
    public void draw(Shader shader){
        shader.setWorldSpace(position, rotation, scale);
        for (VertexCollection p : pieces) {
            materials.get(p.MaterialIndex).enable();
            p.drawVertices(shader);
        }
    }
}
