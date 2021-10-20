package com.qqqopengl.test;

import com.qqqopengl.game.QqqGame;
import com.qqqopengl.graphic.*;
import com.qqqopengl.listener.KeyListener;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GameTest {

    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 600;

    public static void run() throws IOException {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        QqqWindow qqqWindow = new QqqWindow("qqq", SCREEN_WIDTH, SCREEN_HEIGHT, true);
        QqqGame breakout = new QqqGame(SCREEN_WIDTH, SCREEN_HEIGHT);
        breakout.setState(QqqGame.GameState.GAME_ACTIVE);
        breakout.init();

        float deltaTime = 0.0f;
        float lastFrame = 0.0f;
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);

        while (!qqqWindow.isClosing()) {
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            float currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;
            glfwPollEvents();

            breakout.processInput(deltaTime,qqqWindow);

            breakout.update(deltaTime);
            breakout.render();

            qqqWindow.update();
        }

        glfwTerminate();
    }
    public static void main(String[] args) throws IOException {
        run();
    }
}
