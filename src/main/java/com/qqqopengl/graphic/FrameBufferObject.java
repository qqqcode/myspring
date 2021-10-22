package com.qqqopengl.graphic;

import static org.lwjgl.opengl.GL30.*;

public class FrameBufferObject {

    private final int id;

    private Texture texture;

    public FrameBufferObject() {
        id = glGenFramebuffers();
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public static void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void framebufferTexture2D (int width,int height) {
        this.texture = new Texture();
        this.texture.bind();
        this.texture.uploadData(GL_RGB,width,height,GL_RGB,null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId(), 0);
    }

    public void framebufferTexture2D (Texture texture,int width,int height) {
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId(), 0);
        checkFramebufferStatus();
    }

    public void checkFramebufferStatus () {
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE ) {
            System.out.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
        }
    }



    public void delete() {
        glDeleteFramebuffers(id);
    }
}
