package com.qqqopengl.test;

import com.qqqopengl.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class CreateWindow {

    private static CreateWindow instance;

    private long glfwWindow;

    private int width, height;

    public CreateWindow() {
        this.width = 800;
        this.height = 600;
    }

    public static CreateWindow get() {
        if (CreateWindow.instance == null) {
            CreateWindow.instance = new CreateWindow();
        }
        return CreateWindow.instance;
    }

    public void init() {

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(this.width, this.height, "Hello World!", NULL, NULL);
        if (glfwWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(glfwWindow, (window, key, scancode, action, mode) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
        });

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
        }

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);
        GL.createCapabilities();

        Shader vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "/resources/rainrowtriange.vert");
        Shader fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "/resources/rainrowtriange.frag");

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.link();

        VertexArrayObject vao = new VertexArrayObject();
        vao.bind();

        VertexBufferObject vbo = new VertexBufferObject();
        VertexBufferObject ebo = new VertexBufferObject();
        try (MemoryStack stack = MemoryStack.stackPush()) {
//            FloatBuffer vertices = stack.mallocFloat(4 * 3);
//            vertices.put(0.5f).put(0.5f).put(0f);
//            vertices.put(0.5f).put(-0.5f).put(0f);
//            vertices.put(-0.5f).put(-0.5f).put(0f);
//            vertices.put(-0.5f).put(0.5f).put(0f);
//            vertices.flip();

            FloatBuffer vertices = stack.mallocFloat(3*6);
            vertices.put(0.5f).put(-0.5f).put(0.0f).put(1.0f).put(0.0f).put(0.0f);
            vertices.put(-0.5f).put(-0.5f).put(0.0f).put(0.0f).put(1.0f).put(0.0f);
            vertices.put(0.0f).put(0.5f).put(0.0f).put(0.0f).put(0.0f).put(1.0f);
            vertices.flip();

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

//            IntBuffer indices = stack.mallocInt(2 * 3);
//            indices.put(0).put(1).put(3);
//            indices.put(1).put(2).put(3);
//            indices.flip();
//
//            ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
//            ebo.uploadData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        int floatSize = 4;
//        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * floatSize, 0);
//        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * floatSize, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * floatSize, 3* floatSize);
        glEnableVertexAttribArray(1);

        //glBindBuffer(GL_ARRAY_BUFFER, 0);
        //glBindVertexArray(0);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        while (!glfwWindowShouldClose(glfwWindow)) {
            double timeValue = glfwGetTime();
            double greenValue = (Math.sin(timeValue) / 2.0) + 0.5d;

            glfwPollEvents();
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            //int vertexColorLocation = shaderProgram.getUniformLocation("ourColor");
            shaderProgram.use();
            //glUniform4f(vertexColorLocation,0.0f,(float)greenValue,0.0f,1.0f);

            glBindVertexArray(vao.getID());
            //glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            //glBindVertexArray(0);

            glfwSwapBuffers(glfwWindow);
        }

        vertexShader.delete();
        fragmentShader.delete();
        vao.delete();
        vbo.delete();
        ebo.delete();
        glfwTerminate();

    }

    public static void main(String[] args) {
        CreateWindow.get().init();
    }

}
