package com.qqqopengl;

import com.qqqopengl.math.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class QQQWindow {

    private int width, heigth;
    private String title;
    private long glfwWindow;
    public float r, g, b, a;
    private boolean fadeToBlack = false;

    String vsoure = "#version 330 core\n" +
            "\n" +
            "in vec3 position;\n" +
            "in vec3 color;\n" +
            "\n" +
            "out vec3 vertexColor;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "void main() {\n" +
            "    vertexColor = color;\n" +
            "    mat4 mvp = projection * view * model;\n" +
            "    gl_Position = mvp * vec4(position, 1.0);\n" +
            "}\n";
    String ssource = "#version 330 core\n" +
            "\n" +
            "in vec3 vertexColor;\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = vec4(vertexColor, 1.0);\n" +
            "}\n";

    private static QQQWindow window;

    public static QQQWindow get() {
        if (QQQWindow.window == null) {
            QQQWindow.window = new QQQWindow();
        }
        return QQQWindow.window;
    }

    private QQQWindow() {
        this.width = 1920;
        this.heigth = 1080;
        this.title = "qqq";
        this.r = 1;
        this.b = 1;
        this.g = 1;
        this.a = 1;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        glfwWindow = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (glfwWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetCursorPosCallback(glfwWindow, MouseListener::moustPosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);


    }

    private void loop() {

        GL.createCapabilities();

        int vbo;
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(3 * 6);
            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
            vertices.flip();

            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }


        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vsoure);
        glCompileShader(vertexShader);
        int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShader));
        }


        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader,ssource);
        glCompileShader(fragmentShader);
        status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(fragmentShader));
        }

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
        }

        glBindFragDataLocation(shaderProgram, 0, "fragColor");

        int floatSize = 4;

        int posAttrib = glGetAttribLocation(shaderProgram, "position");
        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6 * floatSize, 0);

        int colAttrib = glGetAttribLocation(shaderProgram, "color");
        glEnableVertexAttribArray(colAttrib);
        glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6 * floatSize, 3 * floatSize);


        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        float beginTime = TimeUtil.getTime();
        float endTime = TimeUtil.getTime();
        while (!glfwWindowShouldClose(glfwWindow)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glClearColor(r, g, b, a);

            if (fadeToBlack) {
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
                a = Math.max(a - 0.01f, 0);
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                fadeToBlack = true;
            } else {
                fadeToBlack = false;
            }

            glfwSwapBuffers(glfwWindow); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            endTime = TimeUtil.getTime();
            float dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static void main(String[] args) {
        QQQWindow.get().run();
    }


}
