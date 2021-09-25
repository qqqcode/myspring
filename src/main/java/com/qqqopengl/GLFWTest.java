package com.qqqopengl;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWTest {

    public static void main(String[] args) {
        Long window;
        if (!glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = glfwCreateWindow(1080, 1920, "QQQ", NULL, NULL);

        if (window == null) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();
        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT ); // clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        glfwTerminate();

    }
}
