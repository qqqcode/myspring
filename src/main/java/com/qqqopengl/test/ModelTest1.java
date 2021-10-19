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
    int width = 1024;
    int height = 768;
    int fbWidth = 1024;
    int fbHeight = 768;
    float fov = 60;
    float rotation;

    ShaderProgram shaderProgram;
    int modelMatrixUniform;
    int viewProjectionMatrixUniform;
    int normalMatrixUniform;
    int lightPositionUniform;
    int viewPositionUniform;
    int ambientColorUniform;
    int diffuseColorUniform;
    int specularColorUniform;

    Model model;

    Matrix4x3f modelMatrix = new Matrix4x3f().rotateY(0.5f * (float) Math.PI).scale(1.5f, 1.5f, 1.5f);
    Matrix4x3f viewMatrix = new Matrix4x3f();
    Matrix4f projectionMatrix = new Matrix4f();
    Matrix4f viewProjectionMatrix = new Matrix4f();
    Vector3f viewPosition = new Vector3f();
    Vector3f lightPosition = new Vector3f(-5f, 5f, 5f);

    private FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private FloatBuffer viewProjectionMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private Matrix3f normalMatrix = new Matrix3f();
    private FloatBuffer normalMatrixBuffer = BufferUtils.createFloatBuffer(3 * 3);
    private FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(3);
    private FloatBuffer viewPositionBuffer = BufferUtils.createFloatBuffer(3);

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
            rotation = ((float)x / width - 0.5f) * 2f * (float)Math.PI;
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

        model = new Model("magnet.obj");
        createProgram();
        qqqWindow.showWindow();
    }

    void createProgram() throws IOException {

        shaderProgram =ShaderUtil.createShaderProgram(Constant.resources + "magnet.vs",Constant.resources + "magnet.fs");

        shaderProgram.use();

        modelMatrixUniform = shaderProgram.getUniformLocation("uModelMatrix");
        viewProjectionMatrixUniform = shaderProgram.getUniformLocation("uViewProjectionMatrix");
        normalMatrixUniform = shaderProgram.getUniformLocation("uNormalMatrix");
        lightPositionUniform = shaderProgram.getUniformLocation("uLightPosition");
        viewPositionUniform = shaderProgram.getUniformLocation("uViewPosition");
        ambientColorUniform = shaderProgram.getUniformLocation("uAmbientColor");
        diffuseColorUniform = shaderProgram.getUniformLocation("uDiffuseColor");
        specularColorUniform = shaderProgram.getUniformLocation("uSpecularColor");
    }

    void update() {
        projectionMatrix.setPerspective((float) Math.toRadians(fov), (float) width / height, 0.01f,
                100.0f);
        viewPosition.set(10f * (float) Math.cos(rotation), 10f, 10f * (float) Math.sin(rotation));
        viewMatrix.setLookAt(viewPosition.x, viewPosition.y, viewPosition.z, 0f, 0f, 0f, 0f, 1f,
                0f);
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderProgram.use();
        shaderProgram.setUniformMatrix4f(modelMatrixUniform,modelMatrix.get4x4(modelMatrixBuffer));
        shaderProgram.setUniformMatrix4f(viewProjectionMatrixUniform,viewProjectionMatrix.get(viewProjectionMatrixBuffer));

        modelMatrix.normal(normalMatrix).invert().transpose();
        shaderProgram.setUniformMatrix3f(normalMatrixUniform, normalMatrix.get(normalMatrixBuffer));

        shaderProgram.setUniform3f(lightPositionUniform, lightPosition.get(lightPositionBuffer));
        shaderProgram.setUniform3f(viewPositionUniform, viewPosition.get(viewPositionBuffer));

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
        new ModelTest1().run();
    }

}
