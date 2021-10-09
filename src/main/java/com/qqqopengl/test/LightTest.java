package com.qqqopengl.test;

import com.qqqopengl.graphic.*;
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

    static Vector3f lightPos = new Vector3f(2.2f, 1.0f, 2.0f);

    public static void fun() {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        QqqWindow qqq = new QqqWindow("qqqlight", SCR_WIDTH, SCR_HEIGHT, true);
        Camera camera = new Camera(0f, 0.0f, 3.0f);

        Texture texture = Texture.loadTexture(Constant.resources + "img_2.png");
        Texture texture1 = Texture.loadTexture(Constant.resources + "img_3.png");

        Renderer renderer = new Renderer();
        renderer.glEnbale(GL_DEPTH_TEST);

        renderer.init("/resources/light.vert", "/resources/light.frag");

        VertexArrayObject vao = renderer.createVAO();
        renderer.specifyVertexAttributes("position",vao, 3, 8, 0);
        renderer.specifyVertexAttributes("normal", vao,3, 8, 3);
        renderer.specifyVertexAttributes("texCoords",vao,2,8,6);

        VertexArrayObject lightVao = renderer.createVAO();
        renderer.specifyVertexAttributes("position",lightVao, 3, 6, 0);

        renderer.begin();
        renderer.useTexture(GL_TEXTURE0, texture, "material.diffuse", 0);
        renderer.useTexture(GL_TEXTURE1, texture1, "material.specular", 1);

        float[] ver = {
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,

                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,

                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f
        };
        while (!qqq.isClosing()) {

            float currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderer.clear();

            keyListen(qqq, camera);
            renderer.setUniform("material.shininess",32.0f);
            renderer.setUniform("viewPos",camera.getPosition());



            //renderer.setUniform("light.position", lightPos);
            renderer.setUniform("dirLight.direction", new Vector3f( -0.2f, -1.0f, -0.3f));
            renderer.setUniform("dirLight.ambient",new Vector3f(0.05f));
            renderer.setUniform("dirLight.diffuse",new Vector3f(0.4f));
            renderer.setUniform("dirLight.specular",new Vector3f(0.5f));

            renderer.setUniform("pointLights[0].direction", new Vector3f( 0.7f, 0.2f, 2.0f));
            renderer.setUniform("pointLights[0].ambient",new Vector3f(0.05f));
            renderer.setUniform("pointLights[0].diffuse",new Vector3f(0.8f));
            renderer.setUniform("pointLights[0].specular",new Vector3f(1.0f));
            renderer.setUniform("pointLights[0].constant",1.0f);
            renderer.setUniform("pointLights[0].linear",0.09f);
            renderer.setUniform("pointLights[0].quadratic",0.032f);

            renderer.setUniform("pointLights[1].direction", new Vector3f( 2.3f, -3.3f, -4.0f));
            renderer.setUniform("pointLights[1].ambient",new Vector3f(0.05f));
            renderer.setUniform("pointLights[1].diffuse",new Vector3f(0.8f));
            renderer.setUniform("pointLights[1].specular",new Vector3f(1.0f));
            renderer.setUniform("pointLights[1].constant",1.0f);
            renderer.setUniform("pointLights[1].linear",0.09f);
            renderer.setUniform("pointLights[1].quadratic",0.032f);

            renderer.setUniform("pointLights[2].direction", new Vector3f( -4.0f, 2.0f, -12.0f));
            renderer.setUniform("pointLights[2].ambient",new Vector3f(0.05f));
            renderer.setUniform("pointLights[2].diffuse",new Vector3f(0.8f));
            renderer.setUniform("pointLights[2].specular",new Vector3f(1.0f));
            renderer.setUniform("pointLights[2].constant",1.0f);
            renderer.setUniform("pointLights[2].linear",0.09f);
            renderer.setUniform("pointLights[2].quadratic",0.032f);

            renderer.setUniform("pointLights[3].direction", new Vector3f( 0f, 0f, -3.0f));
            renderer.setUniform("pointLights[3].ambient",new Vector3f(0.05f));
            renderer.setUniform("pointLights[3].diffuse",new Vector3f(0.8f));
            renderer.setUniform("pointLights[3].specular",new Vector3f(1.0f));
            renderer.setUniform("pointLights[3].constant",1.0f);
            renderer.setUniform("pointLights[3].linear",0.09f);
            renderer.setUniform("pointLights[3].quadratic",0.032f);

            renderer.setUniform("spotLight.position", camera.getPosition());
            renderer.setUniform("spotLight.position", camera.getFront());
            renderer.setUniform("spotLight.ambient",new Vector3f(0f));
            renderer.setUniform("spotLight.diffuse",new Vector3f(1.0f));
            renderer.setUniform("spotLight.specular",new Vector3f(1.0f));
            renderer.setUniform("spotLight.constant",1.0f);
            renderer.setUniform("spotLight.linear",0.09f);
            renderer.setUniform("spotLight.quadratic",0.032f);
            renderer.setUniform("spotLight.cutOff", (float) Math.cos(Math.toRadians(12.5)));
            renderer.setUniform("spotLight.outerCutOff",(float) Math.cos(Math.toRadians(15)));



            Matrix4f viewMatrix = camera.getViewMatrix();
            renderer.setUniform("model", new Matrix4f().translate(0f, 0.0f, -2.0f));
            renderer.setUniform("view", viewMatrix);
            renderer.setUniform("projection", new Matrix4f().perspective((float) Math.toRadians(camera.getZoom()), qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));
            renderer.render(ver, 36);

            renderer.setUniform("model", new Matrix4f().translate(1.2f, 1.0f, -5.0f));
            renderer.setUniform("view", viewMatrix);
            renderer.setUniform("projection", new Matrix4f().perspective((float) Math.toRadians(camera.getZoom()), qqq.getWidth() / qqq.getHeight(), 0.1f, 100.0f));
            renderer.render(ver, 36);

            qqq.update();
        }

        renderer.dispose();
    }

    public static void keyListen(QqqWindow qqq, Camera camera) {
        keyPress(qqq, camera);
        mousePress(qqq, camera);
        scrollCallback(camera);
    }

    private static void keyPress(QqqWindow window, Camera camera) {
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

    private static void mousePress(QqqWindow qqqWindow, Camera camera) {
        float xpos = MouseListener.getX();
        float ypos = MouseListener.getY();
        if (firstMouse) {
            lastX = qqqWindow.getWidth() / 2;
            lastY = qqqWindow.getHeight() / 2;
            firstMouse = false;
        }

        float xoffset = xpos - lastX;
        float yoffset = lastY - ypos;

        lastX = xpos;
        lastY = ypos;

        camera.processMouseMovement(xoffset, yoffset, true);
    }

    public static void scrollCallback(Camera camera) {
        float scrollX = MouseListener.getScrollX();
        float scrollY = MouseListener.getScrollY();
        camera.processMouseScroll(scrollY);
    }

    public static void main(String[] args) {
        fun();
    }
}
