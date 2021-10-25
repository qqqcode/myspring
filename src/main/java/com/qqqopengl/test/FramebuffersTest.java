package com.qqqopengl.test;

import com.qqqopengl.graphic.*;
import com.qqqopengl.util.Constant;
import com.qqqopengl.util.ShaderUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class FramebuffersTest {

    public static int SCR_WIDTH = 800;
    public static int SCR_HEIGHT = 600;

    public static void fun() {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqqlight", SCR_WIDTH, SCR_HEIGHT, true);
//        glDepthFunc(GL_LESS);
//        glEnable(GL_MULTISAMPLE);


        ShaderProgram program = ShaderUtil.createShaderProgram(Constant.resources + "vs/advanced.vs",
                Constant.resources + "frag/advanced.frag");

        ShaderProgram screenProgram = ShaderUtil.createShaderProgram(Constant.resources + "vs/screen.vs",
                Constant.resources + "frag/screen.frag");

        float[] cubeVertices = {
                // Positions          // Texture Coords
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
        };
        float[] floorVertices = {
                // Positions          // Texture Coords (note we set these higher than 1 that together with GL_REPEAT as texture wrapping mode will cause the floor texture to repeat)
                5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                -5.0f, -0.5f, 5.0f, 0.0f, 0.0f,
                -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,

                5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
                -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
                5.0f, -0.5f, -5.0f, 2.0f, 2.0f
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

        VertexArrayObject cubeVAO = new VertexArrayObject();
        VertexBufferObject cubeVBO = new VertexBufferObject();

        cubeVAO.bind();
        try (MemoryStack stack = stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(5 * 6 * 6);
            floatBuffer.put(cubeVertices);
            floatBuffer.flip();
            cubeVBO.bind(GL_ARRAY_BUFFER);
            cubeVBO.uploadData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        }
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        cubeVAO.unbind();

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

        Texture cubeTexture = Texture.loadTexture(Constant.resources + "container.jpg");
        Texture floorTexture = Texture.loadTexture(Constant.resources + "img.png", true);

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
            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // We're not using stencil buffer so why bother with clearing?
            glEnable(GL_DEPTH_TEST);
            program.use();

            Matrix4f view = new Matrix4f().translate(new Vector3f(0.0f, -2.0f, -6.0f));
            Matrix4f projection = new Matrix4f().perspective(45.0f, qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f);

            // Set uniforms
            program.setUniform("view", view);
            program.setUniform("projection", projection);

            // Floor
            floorVAO.bind();
            program.setUniform("ourTexture", 0);
            floorTexture.bind();
            program.setUniform("model", new Matrix4f());
            glDrawArrays(GL_TRIANGLES, 0, 6);
            floorVAO.unbind();
            // Cubes
            cubeVAO.bind();
            cubeTexture.bind();
            program.setUniform("model", new Matrix4f().translate(new Vector3f(2.0f, 0.0f, 0.0f)));
            glDrawArrays(GL_TRIANGLES, 0, 36);

            program.setUniform("model", new Matrix4f().translate(new Vector3f(-1.0f, 0.0f, -1.0f)));
            glDrawArrays(GL_TRIANGLES, 0, 36);
            cubeVAO.unbind();

            /////////////////////////////////////////////////////
            // Bind to default framebuffer again and draw the
            // quad plane with attched screen texture.
            // //////////////////////////////////////////////////
            frameBuffer.unbind();
            // Clear all relevant buffers
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glDisable(GL_DEPTH_TEST);

            // Draw Screen
            screenProgram.use();
            quadVAO.bind();
            texture.bind();
            glDrawArrays(GL_TRIANGLES, 0, 6);
            quadVAO.unbind();

            qqq.update();
        }

        glfwTerminate();
    }

    public static void main(String[] args) {
        fun();
    }
}
