package CardStructure.RenderingStuff;

import CardStructure.RenderingStuff.TextRenderer.CharacterTexture;
import CardStructure.RenderingStuff.TextRenderer.Text;
import org.joml.Matrix4f;
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
    public List<VertexCollection> pieces = new ArrayList<>();
    public List<Material> materials = new ArrayList<>();
    public Vector3f position = new Vector3f(0,0,0);
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1,1,1);

    public String file="";
    public Mesh(String filePath){
        importData(filePath);
    }
    public Mesh(String filePath, Vector3f position, Quaternionf rotation, Vector3f scale){
        importData(filePath);
    }
    public Mesh(String text, float spacingScale){
        if (vcMap.containsKey(text)){
            pieces = vcMap.get(text);
            materials = matMap.get(text);
            return;
        }
        //System.out.println(1);
        int textLen = text.length();
        float xOffset = 0;
        for (int cInd = 0; cInd < textLen; cInd++) {
            char c = text.charAt(cInd);
            CharacterTexture characterTexture = Text.Map.get(c);
            //System.out.println(2);
            VertexCollection current = new VertexCollection(characterTexture,cInd,xOffset);
            //System.out.println(3);
            //System.out.println(xOffset);
            xOffset -= (characterTexture.Advance >> 6);
            pieces.add(current);
            materials.add(new Material(characterTexture));
        }
        //System.out.println(4);
        vcMap.put(text, pieces);
        matMap.put(text, materials);
    }
    void importData(String filePath){
        file = filePath;
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
        shader.enable();
        shader.setWorldSpace(modelMatrix());
        for (VertexCollection p : pieces) {
            materials.get(p.MaterialIndex).enable();
            p.drawVertices(shader);
        }
    }
    public Matrix4f modelMatrix(){
        return new Matrix4f().translationRotateScale(position,rotation,scale);
    }
    public float rayIntersects(Vector3f rayPosition, Vector3f ray){
        float out = Float.MAX_VALUE;
        boolean flag = true;
        //System.out.println(file);
        for (VertexCollection piece : pieces) {
            float res = piece.rayIntersects(rayPosition, ray, modelMatrix());
            if (res >= 0) {
                out = Math.min(res, out);
                flag = false;
            }
        }
        if (flag)
            return -1;
        return out;
    }
}
