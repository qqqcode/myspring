package com.qqqopengl.test;

import com.qqqopengl.ShaderProgram;
import com.qqqopengl.graphic.QqqWindow;
import com.qqqopengl.graphic.Shader;
import com.qqqopengl.graphic.VertexArrayObject;
import com.qqqopengl.graphic.VertexBufferObject;
import com.qqqopengl.util.Constant;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
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
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TexttureTest {
    public void init () {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqq", 800, 600, true);

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

        int floatSize = 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * floatSize, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * floatSize, 3*floatSize);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * floatSize, 6*floatSize);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        ByteBuffer image;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            image = stbi_load(Constant.resources + "container.jpg", w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!" + System.lineSeparator() + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width,height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);


        while (!qqq.isClosing()) {

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glBindTexture(GL_TEXTURE_2D,texture);

            shaderProgram.use();

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
