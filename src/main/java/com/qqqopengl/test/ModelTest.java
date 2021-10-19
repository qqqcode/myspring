package com.qqqopengl.test;

import com.qqqopengl.graphic.*;
import com.qqqopengl.util.Constant;
import com.qqqopengl.util.ShaderUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class ModelTest {

    QqqWindow qqqWindow;
    Camera camera;
    int width = 1024;
    int height = 768;

    ShaderProgram shaderProgram;
    Model model;

    Callback debugProc;

    int lightPositionUniform;
    int viewPositionUniform;
    int ambientColorUniform;
    int diffuseColorUniform;
    int specularColorUniform;

    void init() throws IOException {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        qqqWindow = new QqqWindow("qqq", width, height, true);
        //debugProc = GLUtil.setupDebugMessageCallback();

        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);

        camera = new Camera(0f,0.0f,3.0f);

        model = new Model("magnet.obj");
        createProgram();
        qqqWindow.showWindow();
    }

    void createProgram() throws IOException {

        shaderProgram =ShaderUtil.createShaderProgram(Constant.resources + "magnet.vs",Constant.resources + "magnet.fs");

        shaderProgram.use();

        lightPositionUniform = shaderProgram.getUniformLocation("uLightPosition");
        viewPositionUniform = shaderProgram.getUniformLocation("uViewPosition");
        ambientColorUniform = shaderProgram.getUniformLocation("uAmbientColor");
        diffuseColorUniform = shaderProgram.getUniformLocation("uDiffuseColor");
        specularColorUniform = shaderProgram.getUniformLocation("uSpecularColor");
    }

    void update() {
        Matrix4f viewMatrix = camera.getViewMatrix();
        shaderProgram.setUniform("model", new Matrix4f().translate(0f, 0.0f, -2.0f));
        shaderProgram.setUniform("view", viewMatrix);
        shaderProgram.setUniform("projection", new Matrix4f().perspective((float) Math.toRadians(camera.getZoom()), qqqWindow.getWidth() / qqqWindow.getHeight(), 0.1f, 100.0f));
    }

    void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderProgram.use();

        List<Mesh> meshes = model.getMeshes();
        for (Mesh mesh : meshes) {
            Material material = mesh.material;
            shaderProgram.setNglUniform3fv(ambientColorUniform, 1, material.mAmbientColor.address());
            shaderProgram.setNglUniform3fv(diffuseColorUniform, 1, material.mDiffuseColor.address());
            shaderProgram.setNglUniform3fv(specularColorUniform, 1, material.mSpecularColor.address());
            mesh.draw(shaderProgram);
        }
    }

    void loop() {
        while (!qqqWindow.isClosing()) {
            glfwPollEvents();
            update();
            render();
            qqqWindow.update();
        }
        GL.setCapabilities(null);
    }

    void run() {
        try {
            init();
            loop();
            model.free();
            if (debugProc != null) {
                debugProc.free();
            }
            qqqWindow.destroy();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            glfwTerminate();
        }
    }

    public static void main(String[] args) {
        new ModelTest().run();
    }

}
