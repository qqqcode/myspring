package com.qqqopengl.graphic;

import static org.lwjgl.opengl.GL30.*;

public class RenderBufferObject {

    private final int id;

    public RenderBufferObject() {
        id = glGenRenderbuffers();
    }

    public void bind() {
        glBindRenderbuffer(GL_RENDERBUFFER, id);
    }

    public static void unbind() {
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    public void storage(int width, int height) {
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
    }

    public void storageMultisample(int sample, int width, int height) {
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, sample, GL_RGB, width, height);
    }

    public void framebufferRenderbuffer (int attachment) {
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, id);
        checkFramebufferStatus();
    }

    public void checkFramebufferStatus () {
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE ) {
            System.out.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
        }
    }
}
