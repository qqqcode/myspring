package com.qqqopengl.test;

import com.qqqopengl.graphic.ShaderProgram;
import com.qqqopengl.graphic.*;
import com.qqqopengl.util.Constant;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TexttureTest {
    public void init() {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqq", 1600, 1200, true);

        Shader vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "/resources/textture1.vert");
        Shader fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "/resources/textture1.frag");

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.link();

        VertexArrayObject vao = new VertexArrayObject();
        vao.bind();

        VertexBufferObject vbo = new VertexBufferObject();
        VertexBufferObject ebo = new VertexBufferObject();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(4 * 8);
            vertices.put(0.5f).put(0.5f).put(0f).put(1.0f).put(0.0f).put(0.0f).put(1.0f).put(1.0f);
            vertices.put(0.5f).put(-0.5f).put(0f).put(0f).put(1.0f).put(0.0f).put(1.0f).put(0f);
            vertices.put(-0.5f).put(-0.5f).put(0f).put(0f).put(0f).put(1f).put(0f).put(0f);
            vertices.put(-0.5f).put(0.5f).put(0f).put(1.0f).put(1.0f).put(0.0f).put(0.0f).put(1.0f);
            vertices.flip();


            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            IntBuffer indices = stack.mallocInt(2 * 3);
            indices.put(0).put(1).put(3);
            indices.put(1).put(2).put(3);
            indices.flip();

            ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
            ebo.uploadData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        int floatSize = Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * floatSize, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * floatSize, 3 * floatSize);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * floatSize, 6 * floatSize);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        Texture texture = Texture.loadTexture(Constant.resources + "container.jpg");
        Texture texture1 = Texture.loadTexture(Constant.resources + "img_1.png");
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);


        while (!qqq.isClosing()) {

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            shaderProgram.use();
            //texture
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
            int ourTexture1 = shaderProgram.getUniformLocation("ourTexture1");
            shaderProgram.setUniform(ourTexture1, 0);

            glActiveTexture(GL_TEXTURE1);
            texture1.bind();
            int ourTexture2 = shaderProgram.getUniformLocation("ourTexture2");
            shaderProgram.setUniform(ourTexture2, 1);

            //transform
            Matrix4f model = new Matrix4f();
            model.rotate(-45.0f, 1.0f, 0f, 0f);
            Matrix4f view = new Matrix4f();
            view.translate(0f, 0f, -3.0f);
            Matrix4f projection = new Matrix4f();
            projection.perspective(45.0f, qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f);

            shaderProgram.setUniform("model", model);
            shaderProgram.setUniform("view", view);
            shaderProgram.setUniform("projection", projection);


            //draw use vao
            glBindVertexArray(vao.getID());
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            //glDrawArrays(GL_TRIANGLES, 0, 3);
            glBindVertexArray(0);

            qqq.update();
        }

        vertexShader.delete();
        fragmentShader.delete();
        vao.delete();
        vbo.delete();
        ebo.delete();
        glfwTerminate();

    }

    public static void main(String[] args) {
        new TexttureTest().init();
    }

}
