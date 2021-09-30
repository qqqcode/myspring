package com.qqqopengl;

import com.qqqopengl.graphic.QqqWindow;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

public class QqqGameEngine implements Runnable{

    private final Thread gameLoopThread;

    private GLFWErrorCallback errorCallback;

    private QqqIGameLogic gameLogic;

    protected QqqWindow window;

    public QqqGameEngine(String windowTitle, int width, int height, boolean vsSync, QqqIGameLogic gameLogic) {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.gameLogic = gameLogic;
    }


    public void start() {
        gameLoopThread.start();
    }

    public void init() {

        errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = new QqqWindow("qqq",1920,1080,true);
    }

    @Override
    public void run() {
        init();


    }
}
