package RenderingStuff;

import org.joml.*;
import org.lwjgl.stb.STBImage;

import java.lang.Math;
import java.nio.*;
import java.util.Arrays;

import org.lwjgl.assimp.*;

import static org.lwjgl.opengl.GL33C.*;

// RenderingStuff.Shader code copied from https://learnopengl.com/Getting-started/Hello-Triangle
public class VertexCollection {

    public int MeshArray, MaterialIndex;
    int vertexBuffer, normalBuffer, textureUVBuffer, elementBuffer;
    public float[] vertexData, normalData, textureUVData;
    int[] elementData;
    public VertexCollection(AIMesh mesh){
        loadData(mesh);
    }
    public VertexCollection() {

    }
    // builds a quad for the character
    public VertexCollection(CharacterTexture characterTexture, int ind, float xOffset){
        Vector2f origin = characterTexture.Bearing;
        Vector2f size = characterTexture.Size;
        float[] x = new float[]{0,1,0,1};
        float[] y = new float[]{1,1,0,0};
        vertexData = new float[4*3];
        normalData = new float[4*3];
        textureUVData = new float[4*2];

        for (int i = 0; i < 4; i++){
            vertexData[i*3+0] = (-origin.x - size.x * x[i]) + xOffset;
            vertexData[i*3+1] = (origin.y - size.y * y[i]);
            vertexData[i*3+2] = 0;

            normalData[i*3+0] = 0;
            normalData[i*3+1] = 0;
            normalData[i*3+2] = -1f; // potential issue'

            textureUVData[i*2+0] = x[i]; // potential issue
            textureUVData[i*2+1] = y[i]; // potential issue
        }

        //System.out.println(Arrays.toString(vertexData));
        elementData = new int[]{
                0,1,2,
                1,3,2
        };

        MaterialIndex = ind;
        initializeArray();
    }
    void loadData(AIMesh mesh){
        int numVertices = mesh.mNumVertices();
        vertexData = new float[numVertices*3];
        normalData = new float[numVertices*3];
        textureUVData = new float[numVertices*2];
        for (int i = 0; i < numVertices; i++) {

            AIVector3D v = mesh.mVertices().get(i);
            vertexData[i*3+0] = v.x();
            vertexData[i*3+1] = v.y();
            vertexData[i*3+2] = v.z();

            AIVector3D n = mesh.mNormals().get(i);
            normalData[i*3+0] = n.x();
            normalData[i*3+1] = n.y();
            normalData[i*3+2] = n.z();

            if (mesh.mNumUVComponents().get(0) != 0) {
                AIVector3D texture = mesh.mTextureCoords(0).get(i);
                textureUVData[i*2+0] = texture.x();
                textureUVData[i*2+1] = 1-texture.y();
            }
        }

        int numFaces = mesh.mNumFaces();
        AIFace.Buffer indices = mesh.mFaces();
        elementData = new int[numFaces * 3];

        for (int i = 0; i < numFaces; i++) {
            AIFace face = indices.get(i);
            elementData[i * 3 + 0] = face.mIndices().get(0);
            elementData[i * 3 + 1] = face.mIndices().get(1);
            elementData[i * 3 + 2] = face.mIndices().get(2);
        }

        MaterialIndex = mesh.mMaterialIndex();
        //normalData = new float[mesh.mNor]
        initializeArray();
    }
    int saveData(int dataBuffer, int index, int size, float[] data){
        glBindBuffer(GL_ARRAY_BUFFER, dataBuffer);
        glBufferData(GL_ARRAY_BUFFER,data,GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index,size,GL_FLOAT,false,0,0);
        return dataBuffer;
    }
    void initializeArray(){
        MeshArray = glGenVertexArrays();
        glBindVertexArray(MeshArray);

        vertexBuffer = saveData(glGenBuffers(),0,3,vertexData);
        normalBuffer = saveData(glGenBuffers(),1,3,normalData);
        textureUVBuffer = saveData(glGenBuffers(),2,2,textureUVData);


        elementBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,elementBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementData,GL_DYNAMIC_DRAW);

        glBindVertexArray(0);

    }
    void drawVertices(Shader shader){
        shader.enable();
        glBindVertexArray(MeshArray);
        glDrawElements(GL_TRIANGLES,elementData.length,GL_UNSIGNED_INT,0);
    }
    public float rayIntersects(Vector3f position, Vector3f ray, Matrix4f modelSpace){
        float out = Float.MAX_VALUE;
        boolean flag = true;
        for (int i = 0; i < elementData.length; i += 3){
            Vector3f[] verts = new Vector3f[3];
            for (int j = 0; j < 3; j++) {
                int ind = elementData[i + j] * 3;
                Vector4f v = new Vector4f(vertexData[ind + 0], vertexData[ind + 1], vertexData[ind + 2], 1f);
                v.mul(modelSpace);
                //System.out.println(v);
                verts[j] = new Vector3f(v.x,v.y,v.z);
            }

            float res = Intersectionf.intersectRayTriangle(position,ray,verts[0], verts[1], verts[2],0.01f);
//            System.out.println("ray:"+position+" "+ray+" ");
//            System.out.println("vertexs:"+verts[0]+" "+verts[1]+" "+verts[2]);
            if (res >= 0){
                out = Math.min(out, res);
                flag = false;
            }
        }
        if (flag)
            return -1;
        return out;
    }


}
