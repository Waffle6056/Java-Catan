package RenderingStuff;

import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.*;
// glfw template code from https://www.lwjgl.org/guide

public class CatanWindow {

    // The window handle
    private long window;
    public Shader shader;
    public Camera camera = new Camera();
    int width = 1600, height = 1200;
    List<Mesh> meshes = new ArrayList<>();
    public List<Mesh> meshes2d = new ArrayList<>();
    Matrix4f projection; Matrix4f view2d = new Matrix4f().identity().lookAt(new Vector3f(0,0,0),new Vector3f(0,0,1), new Vector3f(0,1,0));
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        shader = new Shader();
        glViewport(0,0,width,height);
        glfwSetFramebufferSizeCallback(window,(window, width, height)-> {
            glViewport(0,0,width,height);
            this.width = width;
            this.height = height;
        });

        initializeCamera();
        glEnable(GL_DEPTH_TEST);

        // Set the clear color
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    }

    public void terminate(){
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    public void update(double delta){

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
        camera.update(window, shader, delta);
        for (Mesh mesh : meshes)
            mesh.draw(shader);

        shader.setUniform("view", view2d);
        for (Mesh mesh : meshes2d)
            mesh.draw(shader);


        glfwSwapBuffers(window); // swap the color buffers

    }

    public boolean shouldClose(){
        return !glfwWindowShouldClose(window);
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Catan", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    void initializeCamera(){
        projection = new Matrix4f().perspective((float)java.lang.Math.toRadians(45), (float) width/height,0.1f,100f);
        shader.setUniform("projection",projection);
        Matrix4f model = new Matrix4f().identity();
        shader.setUniform("model",model);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        camera.createCallBacks(window);
    }

    // the only collision in the scene will be the xz plane
    public Vector3f getMousePos(){ //TODO
        float res = Intersectionf.intersectRayPlane(camera.camPos, camera.camDir, new Vector3f(0,0,0), new Vector3f(0,1,0),camera.epsilon);
        Vector3f out = new Vector3f(camera.camPos).add(camera.camDir.mul(res, new Vector3f()));
        //System.out.println(res+" "+out);
        return out;
    }


//    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
//        if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
//            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//    });
    public void bindCallback(GLFWKeyCallbackI cb){
        glfwSetKeyCallback(window, cb);
    }
    public int getKey(int key){
        return glfwGetKey(window, key);
    }

    public int getMouseButton(int button){return glfwGetMouseButton(window, button);}
    public void removeMesh(Mesh mesh){meshes.remove(mesh);}
    public void removeMeshes(List<Mesh> meshes){
        this.meshes.removeAll(meshes);
    }
    public void addMesh(Mesh mesh){meshes.add(mesh);}
    public void addMeshes(List<Mesh> meshes){
        this.meshes.addAll(meshes);
    }
    public void addMesh2d(Mesh mesh){meshes2d.add(mesh);}
    public void addMeshes2d(List<Mesh> meshes){
        this.meshes2d.addAll(meshes);
    }

    public void removeMesh2d(Mesh mesh){meshes2d.remove(mesh);}
    public void removeMeshes2d(List<Mesh> meshes){
        this.meshes2d.removeAll(meshes);
    }
}