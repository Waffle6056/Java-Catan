package RenderingStuff;

import RenderingStuff.Shader;
import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;
import static java.lang.Math.*;

public class Camera {

    public Vector3f camPos = new Vector3f(0,0,0), camDir = new Vector3f(0,0,1);
    public float epsilon = 0.01f;
    Vector3f up = new Vector3f(0,1,0);

    Vector3f x = new Vector3f(1,0,0), z = new Vector3f(0,0,1);
    Vector3f tmp = new Vector3f();
    public Camera(){
        //System.out.println(view);
    }
    public void update(long window, Shader shader, double delta){
        float cameraSpeed = 1f * (float)delta;

        Vector3f camRight = camDir.cross(up, tmp).normalize();

        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
            cameraSpeed *= 5;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            camPos.add(camDir.mul(cameraSpeed,tmp));
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            camPos.sub(camDir.mul(cameraSpeed,tmp));

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            camPos.sub(camRight.mul(cameraSpeed, tmp));
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            camPos.add(camRight.mul(cameraSpeed, tmp));

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)
            camPos.add(up.mul(cameraSpeed, tmp));
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS)
            camPos.sub(up.mul(cameraSpeed, tmp));
        boundary();

        shader.setUniform("view", viewMatrix());
        shader.setUniform("viewPos", camPos);

        //System.out.println(camPos);
    }
    public void boundary(){
        if (camPos.get(1)<1.162E+0){
            camPos.add(up.mul((float) (1.162E+0-camPos.get(1)),tmp));
        }
        if (camPos.get(1)>1.156E+1){
            camPos.add(up.mul((float) (1.156E+1-camPos.get(1)),tmp));
        }
        if (camPos.get(0)<-5.0E+0){
            camPos.add(x.mul((float) (-5.0E+0-camPos.get(0)),tmp));
        }
        if (camPos.get(0)>5.0E+0){
            camPos.add(x.mul((float) (5.0E+0-camPos.get(0)),tmp));
        }
        if (camPos.get(2)<-5.0E+0){
            camPos.add(z.mul((float) (-5.0E+0-camPos.get(2)),tmp));
        }
        if (camPos.get(2)>5.0E+0){
            camPos.add(z.mul((float) (5.0E+0-camPos.get(2)),tmp));
        }
    }
    public Matrix4f viewMatrix(){
        return new Matrix4f().identity().lookAt(camPos,camPos.add(camDir,new Vector3f()),up);
    }

    double lastX = 400, lastY = 300;
    double yaw = -90, pitch = 0;
    float sensitivity = 0.1f;
    public void createCallBacks(long window){
        glfwSetCursorPosCallback(window, (win, xpos, ypos)->{
            float xoffset = (float)(xpos - lastX);
            float yoffset = (float)(lastY - ypos);
            xoffset *= sensitivity;
            yoffset *= sensitivity;

            yaw   += xoffset;
            pitch += yoffset;

            if(pitch > 89.0)
                pitch = 89.0;
            if(pitch < -89.0f)
                pitch = -89.0;

            Vector3f direction = new Vector3f();
            direction.x = (float)( cos(toRadians(yaw)) * cos(toRadians(pitch)));
            direction.y = (float)( sin(toRadians(pitch)));
            direction.z = (float)( sin(toRadians(yaw)) * cos(toRadians(pitch)));
            camDir = direction.normalize();

            lastX = xpos;
            lastY = ypos;
        });
//        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
//            if ( key == GLFW_KEY_W )
//                camPos +=
//        });
    }
}
