package com.qqqopengl.test;

import com.qqqopengl.graphic.QqqWindow;
import com.qqqopengl.graphic.Renderer;
import com.qqqopengl.graphic.ShaderProgram;
import com.qqqopengl.graphic.Texture;
import com.qqqopengl.util.Constant;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;

public class RendererTest {


    public static void fun () {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqq", 1600, 1200, true);

        Renderer renderer = new Renderer();
        renderer.glEnbale(GL_DEPTH_TEST);

        renderer.init();

        Texture texture = Texture.loadTexture(Constant.resources + "container.jpg");
        Texture texture1 = Texture.loadTexture(Constant.resources + "img_1.png");

        renderer.begin();
        renderer.flush();

        float[] ver = {

                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f, 0.0f,1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

                0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f,  0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f,1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,1.0f, 0.0f, 0.0f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f,0.0f, 1.0f
        };


        renderer.useTexture(GL_TEXTURE0,texture,"ourTexture",0);
        renderer.useTexture(GL_TEXTURE1,texture1,"ourTexture2",1);

//        renderer.setUniform("model",new Matrix4f().rotate(-55.0f, 1.0f, 0f, 0f));
//        renderer.setUniform("view",new Matrix4f().translate(0f, 0f, -3.0f));
//        renderer.setUniform("projection",new Matrix4f().perspective(45.0f, qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));

        while (!qqq.isClosing()) {

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderer.clear();

            float sin = (float) Math.sin(glfwGetTime()) * 10.0f;
            float cos = (float) Math.cos(glfwGetTime()) * 10.0f;
            renderer.setUniform("model",new Matrix4f().rotate(20.0f, 0.0f, 1.0f, 0f));
            renderer.setUniform("view",new Matrix4f().lookAt(new Vector3f(sin,0f,cos),new Vector3f(0f,0f,0f),new Vector3f(0f,1.0f,0f)));
            renderer.setUniform("projection",new Matrix4f().perspective(45.0f, qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));

            renderer.render(ver,36);

//            renderer.setUniform("model",new Matrix4f().translate(1.0f,-2.0f,-3.0f).rotate(2.5f * (float) glfwGetTime(), 0.5f, 1.0f, 0f));
//            renderer.setUniform("view",new Matrix4f().translate(0f, 0f, -3.0f));
//            renderer.setUniform("projection",new Matrix4f().perspective(45.0f, qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));
//
//            renderer.render(ver,36);

            qqq.update();
        }

        renderer.dispose();
    }

    public static void main(String[] args) {
        fun();
    }
}
