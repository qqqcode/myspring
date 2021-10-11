package com.qqqopengl.test;

import com.qqqopengl.graphic.*;
import com.qqqopengl.listener.PollEvents;
import com.qqqopengl.util.Constant;
import com.qqqopengl.util.ShaderUtil;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class ModelTest {

    static int SCR_WIDTH = 800;
    static int SCR_HEIGHT = 600;
    static float deltaTime = 0.0f;
    static float lastFrame = 0.0f;

    static boolean firstMouse = true;

    public static void run() {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqqlight", SCR_WIDTH, SCR_HEIGHT, true);
        Camera camera = new Camera(0f, 0.0f, 3.0f);

        ShaderProgram shaderProgram = ShaderUtil.createShaderProgram(Constant.resources + "model.vert", Constant.resources + "model.frag");

        Model model = new Model(Constant.resources + "Lowpoly_tree_sample.obj");
        while (!qqq.isClosing()) {
            float currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            PollEvents.keyPress(qqq, camera, deltaTime);
            Matrix4f viewMatrix = camera.getViewMatrix();
            shaderProgram.setUniform("model", new Matrix4f().translate(0f, 0.0f, -2.0f));
            shaderProgram.setUniform("view", viewMatrix);
            shaderProgram.setUniform("projection", new Matrix4f().perspective((float) Math.toRadians(camera.getZoom()), qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));
            model.draw(shaderProgram);

            qqq.update();
        }
    }

    public static void main(String[] args) {
        run();
    }
}