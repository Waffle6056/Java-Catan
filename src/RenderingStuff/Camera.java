package RenderingStuff;

import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;
import static java.lang.Math.*;

public class Camera {

    public Vector3f camPos = new Vector3f(0,0,0), camDir = new Vector3f(0,0,1);
    public float epsilon = 0.01f;
    Vector3f up = new Vector3f(0,1,0);

    Vector3f x = new Vector3f(1,0,0), z = new Vector3f(0,0,1);
    Vector3f tmp = new Vector3f();
    Matrix4f projection;
    float w;

    double lastX = 400, lastY = 300;
    double yaw = 0, pitch = 0;
    float sensitivity = 0.005f;
    float scrollSensitivity = 0.1f;
    public Camera(){
        //System.out.println(view);
        //System.out.println(camDir);
    }
    void rotate(double yaw, double pitch){
        this.yaw += yaw;
        this.pitch += pitch;
        Vector3f direction = new Vector3f();
        direction.x = (float)( cos(toRadians(yaw)) * cos(toRadians(pitch)));
        direction.y = (float)( sin(toRadians(pitch)));
        direction.z = (float)( sin(toRadians(yaw)) * cos(toRadians(pitch)));
        camDir = direction.normalize();
    }
    public void setPerspectiveProjection(float aspectRatio){
        w = .1f;
        projection = new Matrix4f().perspective((float)java.lang.Math.toRadians(45), aspectRatio,w,100f);
    }
    public void setOrthoProjection(float aspectRatio){
        w = .1f;
        float height = 1f;
        projection = new Matrix4f().ortho(-aspectRatio * height,aspectRatio * height,-height,height,w,100f);
    }
    public void update(long window, Shader shader, double delta){
//        float cameraSpeed = 1f * (float)delta;
//
//        Vector3f camRight = camDir.cross(up, tmp).normalize();

//        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
//            cameraSpeed *= 5;
//
//        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
//            camPos.add(camDir.mul(cameraSpeed,tmp));
//        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
//            camPos.sub(camDir.mul(cameraSpeed,tmp));
//
//        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
//            camPos.sub(camRight.mul(cameraSpeed, tmp));
//        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
//            camPos.add(camRight.mul(cameraSpeed, tmp));

//        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)
//            camPos.add(up.mul(cameraSpeed, tmp));
//        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS)
//            camPos.sub(up.mul(cameraSpeed, tmp));
        //boundary();

        shader.setUniform("projection",projection);
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

    public Vector3f getMouseVector(CatanWindow window){
        // get screen vector
//        float xPercent = (float)(lastX-window.width/2.0f) / window.height * 2;
//        float yPercent = (float)(lastY-window.height/2.0f) / window.height * -2;
        double[] xBuffer = new double[1], yBuffer = new double[1];
        glfwGetCursorPos(window.window, xBuffer, yBuffer);
        float xPercent = (float)(xBuffer[0]-window.width/2.0f) / window.width * 2;
        float yPercent = (float)(yBuffer[0]-window.height/2.0f) / window.height * -2;
//        float worldScreenHeight = (float) tan(toRadians(45/2f)) * w;
        //System.out.println(xPercent+" "+yPercent);

//        Vector4f screenVector = new Vector4f(xPercent*worldScreenHeight, yPercent*worldScreenHeight, -w, 0);
        Vector4f homogenuousClipCoord = new Vector4f(xPercent,yPercent,-1.0f,1.0f);
        Vector4f screenVector = homogenuousClipCoord.mul(projection.invert(new Matrix4f()));
        screenVector = new Vector4f(screenVector.x, screenVector.y, -1.0f, 0.0f);
        //screenVector = screenVector.normalize();

        screenVector.mul(viewMatrix().invert());

        return new Vector3f(screenVector.x, screenVector.y, screenVector.z);
    }
    public void scrollMovement(long win, double xpos, double ypos){
        xpos *= scrollSensitivity;
        ypos *= scrollSensitivity;
        camPos = camPos.add(0,(float)-ypos,0);
    }
    public void cursorMovement(long win, double xpos, double ypos){
        float xoffset = (float)(xpos - lastX);
        float yoffset = (float)(ypos - lastY);

        xoffset *= sensitivity;
        yoffset *= sensitivity;

        if (glfwGetMouseButton(win, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS)
            camPos = camPos.add(-xoffset,0,-yoffset);
        //System.out.println(camPos);

        //System.out.println(yaw+" "+pitch);
//            yaw   += xoffset;
//            pitch += yoffset;
//
//            if(pitch > 89.0)
//                pitch = 89.0;
//            if(pitch < -89.0f)
//                pitch = -89.0;
//
//            Vector3f direction = new Vector3f();
//            direction.x = (float)( cos(toRadians(yaw)) * cos(toRadians(pitch)));
//            direction.y = (float)( sin(toRadians(pitch)));
//            direction.z = (float)( sin(toRadians(yaw)) * cos(toRadians(pitch)));
//            camDir = direction.normalize();
//            System.out.println(camDir +" "+yaw+" "+pitch);

        lastX = xpos;
        lastY = ypos;
    }
}
