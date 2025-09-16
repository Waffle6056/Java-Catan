package RenderingStuff;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;

import java.nio.*;

import org.lwjgl.assimp.*;

import static org.lwjgl.opengl.GL33C.*;

// RenderingStuff.Shader code copied from https://learnopengl.com/Getting-started/Hello-Triangle
public class VertexCollection {

    public int MeshArray, MaterialIndex;
    int vertexBuffer, normalBuffer, textureUVBuffer, elementBuffer;
    float[] vertexData, normalData, textureUVData;
    int[] elementData;
    public VertexCollection(AIMesh mesh){
        loadData(mesh);
    }
    public VertexCollection(){

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
    int saveData(int index, int size, float[] data){
        int dataBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, dataBuffer);
        glBufferData(GL_ARRAY_BUFFER,data,GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index,size,GL_FLOAT,false,0,0);
        return dataBuffer;
    }
    void initializeArray(){
        MeshArray = glGenVertexArrays();
        glBindVertexArray(MeshArray);

        saveData(0,3,vertexData);
        saveData(1,3,normalData);
        saveData(2,2,textureUVData);


        int indexBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementData,GL_DYNAMIC_DRAW);

        glBindVertexArray(0);

    }
    void drawVertices(Shader shader){
        shader.enable();
        glBindVertexArray(MeshArray);
        glDrawElements(GL_TRIANGLES,elementData.length,GL_UNSIGNED_INT,0);
    }

    public boolean rayIntersects(Vector3f position, Vector3f ray){ // TODO
        return false;
    }


}
