package RenderingStuff;

import org.joml.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL20C.glDeleteShader;

public class Shader {
    public final int shaderProgram;

    public Shader(String vertexFile, String fragmentFile){
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);

        String vertexCode = "";
        try {
            vertexCode = new String(Files.readAllBytes(Paths.get(vertexFile)), StandardCharsets.UTF_8);
            System.out.println(vertexCode);
        }catch (Exception e){
            System.out.println("vertex code import failed");
        }
        glShaderSource(vertexShader,vertexCode);
        glCompileShader(vertexShader);

        int[] infoLog = new int[1];
        glGetShaderiv(vertexShader,GL_COMPILE_STATUS,infoLog);
        if (infoLog[0] == GL_FALSE) {
            //int[] errorLog = new int[512];

            System.out.println("vertex comp error : "+glGetShaderInfoLog(vertexShader, 512));
        }


        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);



        String fragmentCode = "";

        try {
            fragmentCode = new String(Files.readAllBytes(Paths.get(fragmentFile)), StandardCharsets.UTF_8);
            System.out.println(fragmentCode);
        }catch (Exception e){
            System.out.println("vertex code import failed");
        }
        glShaderSource(fragmentShader,fragmentCode);
        glCompileShader(fragmentShader);

        glGetShaderiv(fragmentShader,GL_COMPILE_STATUS,infoLog);
        if (infoLog[0] == GL_FALSE)
            System.out.println("frag comp error : "+glGetShaderInfoLog(fragmentShader, 512));


        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);


        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);


        setUniform("model",new Matrix4f().identity());
        glUniform3f(glGetUniformLocation(shaderProgram,"light.pos"),1.9555556f,2f,3.288889f);
        glUniform3f(glGetUniformLocation(shaderProgram,"light.ambient"),0.8f,0.8f,0.8f);
        glUniform3f(glGetUniformLocation(shaderProgram,"light.diffuse"),0.7f,0.7f,0.7f);
        glUniform3f(glGetUniformLocation(shaderProgram,"light.specular"),0.5f,0.5f,0.5f);
//
//        glUniform1f(glGetUniformLocation(shaderProgram,"ambientStrength"),.1f);
//        glUniform1f(glGetUniformLocation(shaderProgram,"specularStrength"),.5f);


    }

    public void setWorldSpace(Matrix4f modelMat){
        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(modelLoc,false,modelMat.get(new float[16]));

        int normLoc = glGetUniformLocation(shaderProgram, "normalMat");
        Matrix3f normMat = new Matrix3f(modelMat).invert().transpose();
        glUniformMatrix3fv(normLoc,false,normMat.get(new float[9]));
    }
    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            //System.out.println(glGetUniformLocation(shader.shaderProgram,uniformName));
            glUniformMatrix4fv(glGetUniformLocation(shaderProgram,uniformName), false, fb);
        }
    }
    public void setUniform(String uniformName, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(3);
            value.get(fb);
            //System.out.println(glGetUniformLocation(shader.shaderProgram,uniformName));
            glUniform3fv(glGetUniformLocation(shaderProgram,uniformName), fb);
        }
    }
    public void enable(){
        glUseProgram(shaderProgram);
    }

}
