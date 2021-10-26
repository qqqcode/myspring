package com.qqqopengl.game;

import com.qqqopengl.graphic.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class PostProcessor {
    private FrameBufferObject FBO;
    private RenderBufferObject RBO;
    private VertexArrayObject VAO;


    public ShaderProgram postProcessingShader;
    public Texture texture;
    public int width, height;

    boolean confuse, chaos, shake;

    public PostProcessor(ShaderProgram shader, int width, int height) {
        this.confuse = false;
        this.chaos = false;
        this.shake = false;
        this.FBO = new FrameBufferObject();
        this.RBO = new RenderBufferObject();
        this.postProcessingShader = shader;

        FBO.bind();
        this.texture = Texture.generateAttachmentTexture(false, false, width, height);
        FBO.framebufferTexture2D(texture, width, height);

        RBO.bind();
        RBO.storage(GL_DEPTH24_STENCIL8,width, height);
        RBO.unbind();
        RBO.framebufferRenderbuffer(GL_DEPTH_STENCIL_ATTACHMENT);
        FBO.unbind();

        this.initRenderData();
        this.postProcessingShader.use();
        this.postProcessingShader.setUniform("scene", 0);
        float offset = 1.0f / 300.0f;

        float[] offsets = {
                -offset, offset,  // top-left
                0.0f, offset,  // top-center
                offset, offset,  // top-right
                -offset, 0.0f,  // center-left
                0.0f, 0.0f,  // center-center
                offset, 0.0f,  // center - right
                -offset, -offset,  // bottom-left
                0.0f, -offset,  // bottom-center
                offset, -offset   // bottom-right
        };
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(2 * 9);
            floatBuffer.put(offsets);
            floatBuffer.flip();
            this.postProcessingShader.setUniform2fv("offsets", floatBuffer);
        }
        int[] edge_kernel = {
                -1, -1, -1,
                -1, 8, -1,
                -1, -1, -1
        };
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuffer = stack.mallocInt(3 * 3);
            intBuffer.put(edge_kernel);
            intBuffer.flip();
            this.postProcessingShader.setUniform1iv("edge_kernel", intBuffer);
        }
        float[] blur_kernel = {
                1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f,
                2.0f / 16.0f, 4.0f / 16.0f, 2.0f / 16.0f,
                1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f
        };
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(3 * 3);
            floatBuffer.put(blur_kernel);
            floatBuffer.flip();
            this.postProcessingShader.setUniform1fv("blur_kernel", floatBuffer);
        }
    }

    void beginRender() {
        this.FBO.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    void endRender() {
        this.FBO.unbind();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    void render(float time) {
        this.postProcessingShader.use();
        this.postProcessingShader.setUniform("time", time);
        this.postProcessingShader.setUniform("confuse", this.confuse ? 1 : 0);
        this.postProcessingShader.setUniform("chaos", this.chaos ? 1 : 0);
        this.postProcessingShader.setUniform("shake", this.shake ? 1 : 0);
        if (shake) {
            System.out.println("shaking");
        }

        glActiveTexture(GL_TEXTURE0);
        this.texture.bind();
        VAO.bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
        VAO.unbind();
    }

    void initRenderData() {
        VAO = new VertexArrayObject();
        VertexBufferObject vbo = new VertexBufferObject();
        VAO.bind();
        vbo.bind(GL_ARRAY_BUFFER);
        float[] quadVertices = {
                // Positions   // TexCoords
                -1.0f, 1.0f, 0.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f, 0.0f,

                -1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verBuf = stack.mallocFloat(4 * 6);
            verBuf.put(quadVertices);
            verBuf.flip();
            vbo.uploadData(GL_ARRAY_BUFFER, verBuf, GL_STATIC_DRAW);
        }

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        vbo.unbind();
        VAO.unbind();

    }

}
