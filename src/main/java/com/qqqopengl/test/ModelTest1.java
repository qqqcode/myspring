package com.qqqopengl.test;

import com.qqqopengl.graphic.*;
import com.qqqopengl.util.Constant;
import com.qqqopengl.util.ShaderUtil;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.io.IOException;
import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ModelTest1 {


    QqqWindow qqqWindow;
    int width = 800;
    int height = 600;
    int fbWidth = 800;
    int fbHeight = 600;
    float fov = 60;
    float rotation;

    ShaderProgram shaderProgram;
    int ambientColorUniform;
    int diffuseColorUniform;
    int specularColorUniform;
    Model model;

    Callback debugProc;

    void init() throws IOException {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        qqqWindow = new QqqWindow("qqq", width, height, true);
        qqqWindow.setFramebufferSizeCallback((window, width, height) -> {
            if (width > 0 && height > 0 && (fbWidth != width || fbHeight != height)) {
                fbWidth = width;
                fbHeight = height;
            }
        });
        qqqWindow.setWindowSizeCallback((window, width, height) -> {
            if (width > 0 && height > 0 && (this.width != width || this.height != height)) {
                this.width = width;
                this.height = height;
            }
        });
        qqqWindow.setKeyCallback((window, key, scancode, action, mods) -> {
            if (action != GLFW_RELEASE) {
                return;
            }
            if (key == GLFW_KEY_ESCAPE) {
                glfwSetWindowShouldClose(window, true);
            }
        });
        qqqWindow.setCursorPosCallback((window, x, y) -> {
            rotation = ((float) x / width - 0.5f) * 2f * (float) Math.PI;
        });
        qqqWindow.setScrollCallback((window, xoffset, yoffset) -> {
            if (yoffset < 0) {
                fov *= 1.05f;
            } else {
                fov *= 1f / 1.05f;
            }
            if (fov < 10.0f) {
                fov = 10.0f;
            } else if (fov > 120.0f) {
                fov = 120.0f;
            }
        });
        //debugProc = GLUtil.setupDebugMessageCallback();

        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);

        model = new Model("Lowpoly_tree_sample.obj");
        createProgram();
        qqqWindow.showWindow();
    }

    void createProgram() throws IOException {

        shaderProgram = ShaderUtil.createShaderProgram(Constant.resources + "vs/magnet.vs", Constant.resources + "frag/magnet.fs");

        shaderProgram.use();
        ambientColorUniform = shaderProgram.getUniformLocation("uAmbientColor");
        diffuseColorUniform = shaderProgram.getUniformLocation("uDiffuseColor");
        specularColorUniform = shaderProgram.getUniformLocation("uSpecularColor");
    }


    void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderProgram.use();
        Vector3f viewPosition = new Vector3f(50f * (float) Math.cos(rotation), 50f, 50f * (float) Math.sin(rotation));
        shaderProgram.setUniform("model",new Matrix4f());
        shaderProgram.setUniform("view",new Matrix4f().lookAt(viewPosition, new Vector3f(), new Vector3f(0f, 1.0f, 0f)));
        shaderProgram.setUniform("projection",new Matrix4f().setPerspective((float) Math.toRadians(fov), (float) width / height, 0.01f, 100.0f));

        shaderProgram.setUniform("uLightPosition", new Vector3f(-25f, 70f, 70f));
        shaderProgram.setUniform("uViewPosition", viewPosition);

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
            glViewport(0, 0, fbWidth, fbHeight);
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
        new ModelTest1().run();
    }

}
