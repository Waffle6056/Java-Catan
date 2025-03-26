package RenderingStuff;

import org.lwjgl.assimp.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

public class Material {
    public int diffuseBuffer, specularBuffer;
    public float shininess;

     //i cannot get this to import the material texture
    public Material(AIMaterial material){
        AIString out = AIString.create();
        System.out.println(aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, out, (IntBuffer) null, null, null, null, null, null));
        //System.out.println(naiGetMaterialTexture());
        for (int i = aiTextureType_NONE; i <= aiTextureType_MAYA_SPECULAR_ROUGHNESS; i++) {
            //System.out.println(i);
            if (aiGetMaterialTextureCount(material, i) != 0)
                System.out.println(aiGetMaterialTextureCount(material, i) + " " + i);
        }

        System.out.println(out.dataString()+" "+out+" "+out.data().toString());
        System.out.println();
        //processMaterial(material,"dir/");
        diffuseBuffer = createTexture(out.dataString());
    }
    int createTexture(String path){

        int[] width = new int[1], height = new int[1], nrChannels = new int[1];
        //System.out.println(path);
        ByteBuffer imageData = STBImage.stbi_load(path, width, height, nrChannels,0);

        //System.out.println(imageData+" "+nrChannels[0]);

        int Texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, Texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST_MIPMAP_NEAREST);

        glTexImage2D(GL_TEXTURE_2D,0,GL_RGB,width[0],height[0],0,GL_RGB,GL_UNSIGNED_BYTE,imageData);
        glGenerateMipmap(GL_TEXTURE_2D);

        return Texture;
    }
    void enable(){
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, diffuseBuffer);
    }
//    private static void processMaterial(AIMaterial aiMaterial, String modelDir) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            AIColor4D color = AIColor4D.create();
//
//            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0,
//                    color);
//            if (result == aiReturn_SUCCESS) {
//                //material.setAmbientColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
//                System.out.println(color);
//            }
//
//            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
//                    color);
//            if (result == aiReturn_SUCCESS) {
//                //material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
//                System.out.println(color);
//            }
//
//            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0,
//                    color);
//            if (result == aiReturn_SUCCESS) {
//                //material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
//                System.out.println(color);
//            }
//
//            float reflectance = 0.0f;
//            float[] shininessFactor = new float[]{0.0f};
//            int[] pMax = new int[]{1};
//            result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininessFactor, pMax);
//            if (result != aiReturn_SUCCESS) {
//                reflectance = shininessFactor[0];
//            }
//            //material.setReflectance(reflectance);
//
//            AIString aiTexturePath = AIString.calloc(stack);
//            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null,
//                    null, null, null, null, null);
//            String texturePath = aiTexturePath.dataString();
//            if (texturePath != null && texturePath.length() > 0) {
////                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
////                textureCache.createTexture(material.getTexturePath());
////                material.setDiffuseColor(Material.DEFAULT_COLOR);
//                  System.out.println(4);
//            }
//
//            AIString aiNormalMapPath = AIString.calloc(stack);
//            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiNormalMapPath, (IntBuffer) null,
//                    null, null, null, null, null);
//            String normalMapPath = aiNormalMapPath.dataString();
//            if (normalMapPath != null && normalMapPath.length() > 0) {
////                material.setNormalMapPath(modelDir + File.separator + new File(normalMapPath).getName());
////                textureCache.createTexture(material.getNormalMapPath());
//                System.out.println(5);
//            }
//            //return material;
//        }
//    }
}
