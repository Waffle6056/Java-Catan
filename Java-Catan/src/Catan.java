import RenderingStuff.CatanWindow;
import RenderingStuff.Mesh;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

public class Catan {
    CatanWindow Renderer;
    NewBoard Board;
    public void run(){
        Renderer = new CatanWindow();
        Renderer.run();
        Board = new NewBoard();
        loop();
    }
    private void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        double lastFrame = glfwGetTime();
//        Mesh test = new Mesh("catan.fbx");
//        test.scale = new Vector3f(1,1,1);
//        test.rotation.rotateAxis((float) Math.toRadians(-90),1,0,0);
        Renderer.addMeshes(Board.getMeshes());
        while (Renderer.shouldClose() ) {
            double currentFrame = glfwGetTime();
            double delta = currentFrame - lastFrame;
            //System.out.println(currentFrame);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
//            test.position = new Vector3f(0,(float) (Math.sin(currentFrame)),0);
//            //System.out.println(delta);
//            //stest.rotation.rotateAxis((float)(1*delta),.9f,.25f,.1f);
//            test.draw(Renderer.shader);
            Renderer.update(delta);

            lastFrame = currentFrame;
        }
        Renderer.terminate();
    }

    public static void main(String[] args) {
        Catan catan = new Catan();
        catan.run();
    }
}
