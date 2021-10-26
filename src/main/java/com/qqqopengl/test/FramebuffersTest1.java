package com.qqqopengl.test;

import com.qqqopengl.graphic.*;
import com.qqqopengl.util.Constant;
import com.qqqopengl.util.ShaderUtil;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class FramebuffersTest1 {

    public static int SCR_WIDTH = 800;
    public static int SCR_HEIGHT = 600;

    public static void fun() {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqqlight", SCR_WIDTH, SCR_HEIGHT, true);
        //glDepthFunc(GL_LESS);

        ShaderProgram program = ShaderUtil.createShaderProgram(Constant.resources + "vs/advanced1.vs",
                Constant.resources + "frag/advanced1.frag");

        ShaderProgram screenProgram = ShaderUtil.createShaderProgram(Constant.resources + "vs/screen.vs",
                Constant.resources + "frag/screen.frag");

        float[] floorVertices = {
                // Positions          // Texture Coords (note we set these higher than 1 that together with GL_REPEAT as texture wrapping mode will cause the floor texture to repeat)
                0.5f,  0.5f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                -0.5f,  0.5f, 0.0f, 0.0f, 1.0f,

                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
                -0.5f,  0.5f, 0.0f, 0.0f, 1.0f
        };
        float[] quadVertices = {
                // Positions   // TexCoords
                -1.0f, 1.0f, 0.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f, 0.0f,

                -1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };

        VertexArrayObject floorVAO = new VertexArrayObject();
        VertexBufferObject floorVBO = new VertexBufferObject();

        floorVAO.bind();
        floorVBO.bind(GL_ARRAY_BUFFER);
        try (MemoryStack stack = stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(5 * 6);
            floatBuffer.put(floorVertices);
            floatBuffer.flip();
            floorVBO.uploadData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        }
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        floorVBO.unbind();
        floorVAO.unbind();


        VertexArrayObject quadVAO = new VertexArrayObject();
        VertexBufferObject quadVBO = new VertexBufferObject();
        quadVAO.bind();
        quadVBO.bind(GL_ARRAY_BUFFER);
        try (MemoryStack stack = stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(4 * 6);
            floatBuffer.put(quadVertices);
            floatBuffer.flip();
            quadVBO.uploadData(GL_ARRAY_BUFFER,floatBuffer,GL_STATIC_DRAW);
        }
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
        quadVAO.unbind();

        Texture floorTexture = Texture.loadTexture(Constant.resources + "container.jpg");
//        glGenerateMipmap(GL_TEXTURE_2D);
//        glBindTexture(GL_TEXTURE_2D, 0);

        FrameBufferObject frameBuffer = new FrameBufferObject();
        frameBuffer.bind();
        Texture texture = Texture.generateAttachmentTexture(false, false, SCR_WIDTH, SCR_HEIGHT);
        frameBuffer.framebufferTexture2D(texture,SCR_WIDTH,SCR_HEIGHT);

        RenderBufferObject rbo = new RenderBufferObject();
        rbo.bind();
        rbo.storage(GL_DEPTH24_STENCIL8,SCR_WIDTH,SCR_HEIGHT);
        rbo.unbind();
        rbo.framebufferRenderbuffer(GL_DEPTH_STENCIL_ATTACHMENT);
        frameBuffer.unbind();

        while (!qqq.isClosing()) {
            glfwPollEvents();

            frameBuffer.bind();
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glActiveTexture(GL_TEXTURE0);
            floorTexture.bind();

            program.use();
            // Floor
            floorVAO.bind();
            glDrawArrays(GL_TRIANGLES, 0, 6);
            floorVAO.unbind();
            frameBuffer.unbind();

            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Draw Screen
            screenProgram.use();
            quadVAO.bind();
            texture.bind();
            glDrawArrays(GL_TRIANGLES, 0, 6);
            quadVAO.unbind();

            qqq.update();
        }
        floorVAO.delete();
        floorVBO.delete();
        glfwTerminate();
    }

    public static void main(String[] args) {
        fun();
    }
}
