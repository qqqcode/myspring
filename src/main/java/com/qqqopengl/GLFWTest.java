package com.qqqopengl;

import com.qqqopengl.util.TimeUtil;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWTest {
    // The window handle
    private long window;

    private float r= 1,g =1 ,b = 1,a = 1;

    private boolean faceback = false;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        int shader = createShader();
        loop(shader);

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetCursorPosCallback(window, MouseListener::moustPosCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(window, KeyListener::keyCallback);

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
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
        }

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN );

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        glfwShowWindow(window);
    }

    private void loop(int shaderProgram) {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(3 * 6);
            vertices.put(0.5f).put(-0.5f).put(0f);
            vertices.put(-0.5f).put(-0.5f).put(0f);
            vertices.put(0f).put(0.5f).put(0f);
            vertices.flip();

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }


//        int colAttrib = glGetAttribLocation(shaderProgram, "color");
//        glEnableVertexAttribArray(colAttrib);
        int floatSize = 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * floatSize, 3 * floatSize);
        glEnableVertexAttribArray(0);

        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        float beginTime = TimeUtil.getTime();
        float endTime = TimeUtil.getTime();
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glClearColor(r, g, b, a);

            if (faceback) {
                r = Math.max(r - 0.01f,0);
                g = Math.max(g - 0.01f,0);
                b = Math.max(b - 0.01f,0);
                a = Math.max(a - 0.01f,0);
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                faceback = true;
            } else {
                faceback = false;
            }

            glUseProgram(shaderProgram);
            float greenValue = (float)(Math.sin(endTime) /2 + 0.5);
            int ourColor = glGetUniformLocation(shaderProgram, "ourColor");
            glUniform4f(ourColor,0.0f,greenValue,0.0f,1.0f);
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES,0,3);

            glfwPollEvents();
            endTime = TimeUtil.getTime();
            float dt = endTime - beginTime;
            beginTime = endTime;
            glfwSwapBuffers(window);
        }
    }

    String vertexSource = "#version 330 core\n"+
            "layout (location = 0) in vec3 position;\n"+
            "layout (location = 1) in vec3 color;\n"+
            "out vec3 ourColor;\n"+
            "void main()\n"+
            "{\n"+
            "gl_Position = vec4(position, 1.0);\n"+
            "ourColor = color;\n"+
            "}\0";
    String fragmentSource = "#version 330 core\n"+
            "out vec4 color;\n"+
            "uniform vec4 ourColor;\n"+
            "void main()\n"+
            "{\n"+
            "color = ourColor;\n"+
            "}\n\0";

    int createShader () {

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSource);
        glCompileShader(vertexShader);
        int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = glCreateShader( GL_FRAGMENT_SHADER );
        glShaderSource(fragmentShader, fragmentSource);
        glCompileShader(fragmentShader);
        status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(fragmentShader));
        }

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glBindFragDataLocation(shaderProgram, 0 , " fragColor " );
        glLinkProgram(shaderProgram);

        status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);



        return shaderProgram;
    }

    public static void main(String[] args) {
        new GLFWTest().run();
    }
}
