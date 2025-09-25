package RenderingStuff;

import org.joml.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL20C.glDeleteShader;

public class Shader {
    public final int shaderProgram;

    public Shader(){
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        String vertexCode = "#version 330 core\n"+
                "layout (location = 0) in vec3 aPos;\n"+
                "layout (location = 1) in vec3 aNorm;\n"+
                "layout (location = 2) in vec2 aTex;\n"+
                "uniform mat3 normalMat;\n"+
                "uniform mat4 model;\n"+
                "uniform mat4 view;\n"+
                "uniform mat4 projection;\n"+
                "out vec2 texCoord;\n"+
                "out vec3 fragPos;\n"+
                "out vec3 normal;\n"+
                "void main()\n"+
                "{\n"+
                "   fragPos = (model * vec4(aPos.xyz, 1.0)).xyz;\n"+
                "   gl_Position = projection * view * vec4(fragPos, 1.0);\n"+
                "   texCoord = aTex;\n"+
                "   normal = normalMat * aNorm;\n"+
                "}\0";
        glShaderSource(vertexShader,vertexCode);
        glCompileShader(vertexShader);

        int[] infoLog = new int[1];
        glGetShaderiv(vertexShader,GL_COMPILE_STATUS,infoLog);
        if (infoLog[0] == GL_FALSE) {
            //int[] errorLog = new int[512];

            System.out.println("vertex comp error : "+glGetShaderInfoLog(vertexShader, 512));
        }


        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        String fragmentCode ="" +
                "#version 330 core\n" +
                "struct Light{\n" +
                "   vec3 pos;\n" +
                "   vec3 ambient;\n" +
                "   vec3 diffuse;\n" +
                "   vec3 specular;\n" +
                "};\n" +

//                "struct Material{\n" +
//                "   sampler2D diffuse;\n" +
//                "   sampler2D specular;\n" +
//                "   float shininess;\n" +
//                "};\n" +

                "out vec4 FragColor;\n" +

                "in vec2 texCoord;\n"+
                "in vec3 fragPos;\n"+
                "in vec3 normal;\n"+

                "uniform Light light;\n"+

                "uniform sampler2D diffuseMapTest;\n"+

                //"uniform Material material;\n"+
                "uniform vec3 viewPos;\n"+

                "void main()\n" +
                "{\n" +
                "   vec3 viewDir = normalize(viewPos - fragPos);\n"+
                "   vec3 lightDir = normalize(light.pos - fragPos);\n"+
                "   vec3 reflectDir = reflect(-lightDir, normal);\n"+

                "   vec3 lightColor = texture(diffuseMapTest, texCoord).xyz;\n"+

                "   vec3 ambient = light.ambient * lightColor.rgb;\n"+

                "   float diffuseVal = max(dot(normalize(normal), lightDir), 0.0);\n"+
                "   vec3 diffuse = light.diffuse * lightColor.rgb * diffuseVal;\n"+

                "   float specularVal = pow(max(dot(viewDir, reflectDir), 0.0), 32);\n"+
                "   vec3 specular = light.specular * lightColor.rgb * specularVal;\n"+

                "   FragColor = vec4(ambient + diffuse + specular,1);\n" +
                //"   FragColor = vec4(lightColor,1);\n" +
                "} ";
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
