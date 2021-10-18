package com.qqqopengl.graphic;

import com.qqqopengl.listener.KeyListener;
import com.qqqopengl.listener.MouseListener;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class QqqWindow {

    private final long id;
    //private final GLFWKeyCallback keyCallback;

    int width;
    int height;
    boolean vsync;

    public QqqWindow(CharSequence title, int width, int height, boolean vsync) {
        this.width = width;
        this.height = height;
        this.vsync = vsync;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        long temp = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (temp == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        glfwMakeContextCurrent(temp);
        GL.createCapabilities();
        GLCapabilities caps = GL.getCapabilities();
        glfwDestroyWindow(temp);
        glfwDefaultWindowHints();
//        if (caps.OpenGL32) {
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
//            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
//        } else if (caps.OpenGL21) {
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
//            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
//        } else {
//            throw new RuntimeException("Neither OpenGL 3.2 nor OpenGL 2.1 is "
//                    + "supported, you may want to update your graphics driver.");
//        }
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (id == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window!");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(id,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        glfwMakeContextCurrent(id);
        glfwSwapInterval(0);
        glfwSetCursorPos(id, width / 2, height / 2);

        GL.createCapabilities();

        if (vsync) {
            glfwSwapInterval(1);
        }

        setKeyCallback(KeyListener::keyCallback);
        setCursorPosCallback(MouseListener::moustPosCallback);
        setMouseButtonCallback(MouseListener::mouseButtonCallback);
        setScrollCallback(MouseListener::mouseScrollCallback);
    }

    public void setFramebufferSizeCallback(GLFWFramebufferSizeCallbackI cbfun) {
        glfwSetFramebufferSizeCallback(id,cbfun);
    }

    public void showWindow() {
        glfwShowWindow(id);
    }

    public void setWindowSizeCallback(GLFWWindowSizeCallbackI cbfun) {
        glfwSetWindowSizeCallback(id,cbfun);
    }

    public void setKeyCallback(GLFWKeyCallbackI cbfun) {
        glfwSetKeyCallback(id, cbfun);
    }

    public void setCursorPosCallback(GLFWCursorPosCallbackI cbfun) {
        glfwSetCursorPosCallback(id, cbfun);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI cbfun) {
        glfwSetMouseButtonCallback(id, cbfun);
    }
    public void setScrollCallback(GLFWScrollCallbackI cbfun) {
        glfwSetScrollCallback(id, cbfun);
    }

    public boolean isClosing() {
        return glfwWindowShouldClose(id);
    }

    public void close() {
        glfwSetWindowShouldClose(id, true);
    }

    public void setTitle(CharSequence title) {
        glfwSetWindowTitle(id, title);
    }

    public void update() {
        glfwSwapBuffers(id);
        glfwPollEvents();
    }

    public void destroy() {
        glfwFreeCallbacks(id);
        glfwDestroyWindow(id);
    }

    public void setVSync(boolean vsync) {
        this.vsync = vsync;
        if (vsync) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }

    public boolean isVSyncEnabled() {
        return this.vsync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getId() {
        return id;
    }
}
