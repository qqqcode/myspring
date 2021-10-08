package com.qqqopengl.test;

import com.qqqopengl.graphic.Camera;
import com.qqqopengl.graphic.QqqWindow;
import com.qqqopengl.graphic.Renderer;
import com.qqqopengl.graphic.Texture;
import com.qqqopengl.listener.KeyListener;
import com.qqqopengl.listener.MouseListener;
import com.qqqopengl.util.Constant;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;

public class LightTest {
    static int SCR_WIDTH = 800;
    static int SCR_HEIGHT = 600;
    static float lastX = SCR_WIDTH / 2.0f;
    static float lastY = SCR_HEIGHT / 2.0f;

    static float deltaTime = 0.0f;
    static float lastFrame = 0.0f;

    static boolean firstMouse = true;

    public static void fun() {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqqlight", SCR_WIDTH, SCR_HEIGHT, true);
        Camera camera = new Camera(0f,0.0f,3.0f);

        Renderer renderer = new Renderer();
        renderer.glEnbale(GL_DEPTH_TEST);

        renderer.init("/resources/light.vert","/resources/light.frag");
        renderer.specifyVertexAttributes("postion",3,8,0);
        renderer.specifyVertexAttributes("color",3,8,3);
        renderer.specifyVertexAttributes("texcoord",2,8,6);

        renderer.begin();
        renderer.flush();

        float[] ver = {
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,

                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f

        };
        while (!qqq.isClosing()) {

            float currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderer.clear();

            keyPress(qqq,camera);
            mousePress(camera);

            float sin = (float) Math.sin(glfwGetTime()) * 10.0f;
            float cos = (float) Math.cos(glfwGetTime()) * 10.0f;
            Matrix4f viewMatrix = camera.getViewMatrix();
            renderer.setUniform("model", new Matrix4f().rotate(20.0f, 0.0f, 1.0f, 0f));
            renderer.setUniform("view", viewMatrix);
            renderer.setUniform("projection", new Matrix4f().perspective((float) Math.toRadians(camera.getZoom()), qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));

            renderer.setUniform("objectColor", new Vector3f(1.0f, 0.5f, 0.31f));
            renderer.setUniform("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));

            renderer.render(ver, 36);

//            renderer.setUniform("model",new Matrix4f().translate(1.0f,-2.0f,-3.0f).rotate(2.5f * (float) glfwGetTime(), 0.5f, 1.0f, 0f));
//            renderer.setUniform("view",new Matrix4f().translate(0f, 0f, -3.0f));
//            renderer.setUniform("projection",new Matrix4f().perspective(45.0f, qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));
//
//            renderer.render(ver,36);

            qqq.update();
        }

        renderer.dispose();
    }

    private static void keyPress(QqqWindow window,Camera camera) {
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            window.close();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            camera.processKeyboard(Camera.CameraMovement.FORWARD, deltaTime);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            camera.processKeyboard(Camera.CameraMovement.BACKWARD, deltaTime);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            camera.processKeyboard(Camera.CameraMovement.LEFT, deltaTime);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            camera.processKeyboard(Camera.CameraMovement.RIGHT, deltaTime);
        }
    }

    private static void mousePress(Camera camera) {
        float xpos = MouseListener.getX();
        float ypos = MouseListener.getY();
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        float xoffset = xpos - lastX;
        float yoffset = lastY - ypos;

        lastX = xpos;
        lastY = ypos;

        camera.processMouseMovement(xoffset, yoffset, true);
    }

    public static void main(String[] args) {
        fun();
    }
}
