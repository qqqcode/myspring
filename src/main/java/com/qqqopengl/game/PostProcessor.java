package com.qqqopengl.game;

import com.qqqopengl.graphic.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class PostProcessor {
    private FrameBufferObject MSFBO,FBO;
    private RenderBufferObject RBO;
    private VertexArrayObject VAO;


    public ShaderProgram postProcessingShader;
    public Texture texture;
    public int width, height;

    boolean confuse, chaos, shake;

    public PostProcessor(ShaderProgram shader, int width,int height) {
        this.confuse = false;
        this.chaos = false;
        this.shake = false;
        this.MSFBO = new FrameBufferObject();
        this.FBO = new FrameBufferObject();
        this.RBO = new RenderBufferObject();
        this.postProcessingShader = shader;

        MSFBO.bind();
        RBO.bind();
        RBO.storageMultisample(4,width,height);
        RBO.framebufferRenderbuffer(GL_COLOR_ATTACHMENT0);

        FBO.bind();
        this.texture = Texture.generateAttachmentTexture(false,false,width, height);
        FBO.framebufferTexture2D(texture,width,height);
        FBO.unbind();

        this.initRenderData();
        this.postProcessingShader.use();
        this.postProcessingShader.setUniform("scene", 0);
        float offset = 1.0f / 300.0f;

        float[] offsets = {
                -offset,offset,  // top-left
                0.0f,offset,  // top-center
                offset,offset,  // top-right
                -offset,0.0f,  // center-left
                0.0f,0.0f,  // center-center
                offset,0.0f,  // center - right
                -offset, -offset,  // bottom-left
                0.0f,   -offset,  // bottom-center
                offset,-offset   // bottom-right
        };
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(2*9);
            floatBuffer.put(offsets);
            floatBuffer.flip();
//            this.postProcessingShader.setUniform("offsets",floatBuffer);
            nglUniform2fv(this.postProcessingShader.getUniformLocation("offsets"),9, stack.getAddress());
        }
        int[] edge_kernel = {
            -1, -1, -1,
            -1,  8, -1,
            -1, -1, -1
        };
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuffer = stack.mallocInt(3*3);
            intBuffer.put(edge_kernel);
            intBuffer.flip();
            this.postProcessingShader.setUniform1iv("edge_kernel",intBuffer);
            //nglUniform1iv(this.postProcessingShader.getUniformLocation("edge_kernel"),9, stack.getAddress());
        }
        //glUniform1iv(this.postProcessingShader.getUniformLocation("edge_kernel"), edge_kernel);
        float[] blur_kernel = {
            1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f,
            2.0f / 16.0f, 4.0f / 16.0f, 2.0f / 16.0f,
            1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f
        };
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(3*3);
            floatBuffer.put(blur_kernel);
            floatBuffer.flip();
            nglUniform1fv(this.postProcessingShader.getUniformLocation("blur_kernel"),9, stack.getAddress());
        }
        //glUniform1fv(this.postProcessingShader.getUniformLocation("blur_kernel"), blur_kernel);
    }

    void beginRender() {
        this.MSFBO.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    void endRender() {
        this.MSFBO.bind();
        this.FBO.bind();
        glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, this.width, this.height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        this.FBO.unbind();
    }

    void render(float time) {
        this.postProcessingShader.use();
        this.postProcessingShader.setUniform("time", time);
        this.postProcessingShader.setUniform("confuse", this.confuse ? 1 : 0);
        this.postProcessingShader.setUniform("chaos", this.chaos ? 1 : 0);
        this.postProcessingShader.setUniform("shake", this.shake ? 1 : 0);

        glActiveTexture(GL_TEXTURE0);
        this.texture.bind();
        VAO.bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
        VAO.unbind();
    }

    void initRenderData() {
        VAO = new VertexArrayObject();
        VertexBufferObject vbo = new VertexBufferObject();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verBuf = stack.mallocFloat(4 * 6);
            verBuf.put(-1f).put(-1.0f).put(0f).put(0f);
            verBuf.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
            verBuf.put(-1f).put(1f).put(0f).put(1f);

            verBuf.put(-1f).put(-1f).put(0f).put(0f);
            verBuf.put(1.0f).put(-1.0f).put(1.0f).put(0f);
            verBuf.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
            verBuf.flip();
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadData(GL_ARRAY_BUFFER, verBuf, GL_STATIC_DRAW);
        }

        VAO.bind();
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        vbo.unbind();
        VAO.unbind();

    }

}
