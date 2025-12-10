package CardStructure.RenderingStuff;

import CardStructure.RenderingStuff.TextRenderer.Text;
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
    public long window;
    public Shader defaultShader;
    public Shader textShader;
    int width = 1600, height = 1000;
    public Camera camera;
    public Camera camera2d;
    public List<Mesh> meshes = new ArrayList<>();
    public List<Mesh> meshes2d = new ArrayList<>();
    public List<Mesh> textMeshes2d = new ArrayList<>();
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();


        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        defaultShader = new Shader("src/CardStructure/RenderingStuff/vertex.vs","src/CardStructure/RenderingStuff/fragment.vs");
        textShader = new Shader("src/CardStructure/RenderingStuff/vertex.vs","src/CardStructure/RenderingStuff/TextRenderer/textFragment.vs");
        glViewport(0,0,width,height);

        initializeCamera();
        glEnable(GL_DEPTH_TEST);

        // Set the clear color
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        Text.initialize("fonts/arial.ttf");
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
        camera.update(window, defaultShader, delta);
        for (Mesh mesh : meshes)
            mesh.draw(defaultShader);

        //System.out.println(camera2d.camPos);
        camera2d.update(window, defaultShader, delta);
        for (Mesh mesh : meshes2d)
            mesh.draw(defaultShader);

        camera2d.update(window, textShader, delta);
        for (Mesh mesh : textMeshes2d)
            mesh.draw(textShader);



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
        //projection = new Matrix4f().perspective((float)java.lang.Math.toRadians(45), (float) width/height,0.1f,100f);
        //projection = new Matrix4f().ortho(-width*camera.sensitivity/2, width*camera.sensitivity/2, -height*camera.sensitivity/2, height*camera.sensitivity/2, .1f, 100f);
        //
        camera = new Camera();
        camera.setPerspectiveProjection((float)width/height);
        camera.rotate(270,-89.99);
        camera.camPos.add(0,6,0);
        camera2d = new Camera();
        camera2d.setOrthoProjection((float)width/height);
        camera2d.sensitivity = 0;
        camera2d.scrollSensitivity = 0;
        glfwSetFramebufferSizeCallback(window,(window, width, height)-> {
            glViewport(0,0,width,height);
            this.width = width;
            this.height = height;
            camera.setPerspectiveProjection((float)width/height);
            camera2d.setOrthoProjection((float)width/height);
        });
        //glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    // the only collision in the scene will be the xz plane
    public Vector3f getMousePos(){
        Vector3f camPos = camera.camPos;
        Vector3f mousePos = camera.getMouseVector(this);
        Vector3f dir = mousePos.sub(camPos);
        //System.out.println(dir);


        float res = Intersectionf.intersectRayPlane(camPos, dir, new Vector3f(0,1,0), new Vector3f(0,1,0),camera.epsilon);
        Vector3f out = new Vector3f(camPos).add(dir.mul(res, new Vector3f()));
        //System.out.println(res+" "+out);
        return out;
    }
    public Vector3f getMousePos2d(){
        //Vector3f camPos = camera.camPos;
        //Vector3f dir =
        //System.out.println(camPos+" "+dir);
        //System.out.println(res+" "+out);
        //System.out.println(dir);
        Vector3f mousePos = camera2d.getMouseVector(this);
        //System.out.println(res);
        //System.out.println(mousePos.add(camera2d.camDir.mul(res, new Vector3f()), new Vector3f()));
        //System.out.println(mousePos);
        return mousePos;
    }
    public float hovered2D(Mesh m){
        //System.out.println(camera2d.camDir);
        return m.rayIntersects(getMousePos2d(), camera2d.camDir);
    }
//    public Vector3f getMousePos2d(){
//        Vector3f camPos = camera2d.camPos;
//        Vector3f dir = camera2d.getMouseVector(this);
//        //System.out.println(camPos+" "+dir);
//        //System.out.println(res+" "+out);
//        float out = Float.MAX_VALUE;
//        for (Mesh m : meshes2d) {
//            float res = m.rayIntersects(camPos, dir);
//            if (res >= 0)
//                out = Math.min(res, out);
//
//        }
//        System.out.println(camPos.add(dir.mul(out, new Vector3f())));
//        return camPos.add(dir.mul(out, new Vector3f()));
//    }


//    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
//        if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
//            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//    });
    public void bindCallback(GLFWKeyCallbackI cb){
        glfwSetKeyCallback(window, cb);
    }
    public void bindMouseCallback(GLFWMouseButtonCallbackI cb){
        glfwSetMouseButtonCallback(window, cb);
    }
    public void bindCursorPosCallback(GLFWCursorPosCallbackI cb){
        glfwSetCursorPosCallback(window, cb);
    }
    public void bindScrollCallback(GLFWScrollCallbackI cb){
        glfwSetScrollCallback(window, cb);
    }
    public int getKey(int key){
        return glfwGetKey(window, key);
    }

    public int getMouseButton(int button){return glfwGetMouseButton(window, button);}
//    public void removeMesh(Mesh mesh){meshes.remove(mesh);}
//    public void removeMeshes(List<Mesh> meshes){
//        this.meshes.removeAll(meshes);
//    }
//    public void addMesh(Mesh mesh){meshes.add(mesh);}
//    public void addMeshes(List<Mesh> meshes){
//        this.meshes.addAll(meshes);
//    }
//    public void addMesh2d(Mesh mesh){meshes2d.add(mesh);}
//    public void addMeshes2d(List<Mesh> meshes){
//        this.meshes2d.addAll(meshes);
//    }
//
//    public void removeMesh2d(Mesh mesh){meshes2d.remove(mesh);}
//    public void removeMeshes2d(List<Mesh> meshes){
//        this.meshes2d.removeAll(meshes);
//    }
}