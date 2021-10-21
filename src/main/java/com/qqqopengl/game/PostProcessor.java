package com.qqqopengl.game;

import com.qqqopengl.graphic.ShaderProgram;
import com.qqqopengl.graphic.Texture;
import com.qqqopengl.graphic.VertexArrayObject;
import com.qqqopengl.graphic.VertexBufferObject;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

public class PostProcessor {
    private int MSFBO, FBO;
    private int RBO;
    private VertexArrayObject VAO;


    public ShaderProgram postProcessingShader;
    public Texture texture;
    public int width, height;

    boolean confuse, chaos, shake;

    public PostProcessor(ShaderProgram shader, int width,int height) {
        this.confuse = false;
        this.chaos = false;
        this.shake = false;
        this.MSFBO = glGenFramebuffers();
        this.FBO = glGenFramebuffers();
        this.RBO = glGenRenderbuffers();
        this.postProcessingShader = shader;

        glBindFramebuffer(GL_FRAMEBUFFER, this.MSFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, this.RBO);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, 4, GL_RGB, width, height); // allocate storage for render buffer object
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, this.RBO); // attach MS render buffer object to framebuffer
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("ERROR::POSTPROCESSOR: Failed to initialize MSFBO");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, this.FBO);
        this.texture = Texture.createTexture(width, height, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0); // attach texture to framebuffer as its color attachment
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("ERROR::POSTPROCESSOR: Failed to initialize FBO");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        this.initRenderData();
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
            nglUniform1iv(this.postProcessingShader.getUniformLocation("edge_kernel"),9, stack.getAddress());
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
        glBindFramebuffer(GL_FRAMEBUFFER, this.MSFBO);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    void endRender() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.MSFBO);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.FBO);
        glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, this.width, this.height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
        glBindVertexArray(0);
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

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }

}
