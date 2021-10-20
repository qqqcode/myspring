package com.qqqopengl.game;

import com.qqqopengl.graphic.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SpriteRenderer {

    ShaderProgram shaderProgram;
    VertexArrayObject quadVAO;

    public SpriteRenderer(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        initRenderData();
    }

    public VertexArrayObject getQuadVAO() {
        return quadVAO;
    }

    public void drawSprite(Texture texture, Vector2f position, Vector2f size, float rotate, Vector3f color, int width, int height) {
        shaderProgram.use();

        Matrix4f projection = new Matrix4f().ortho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
        shaderProgram.setUniform("image", 0);
        shaderProgram.setUniform("projection", projection);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        Matrix4f model = new Matrix4f();
        model.translate(new Vector3f(position, 0.0f));
        model.translate(new Vector3f(0.5f * size.x, 0.5f * size.y, 0.0f));
        model.rotate(rotate, new Vector3f(0.0f, 0.0f, 1.0f));
        model.translate(new Vector3f(-0.5f * size.x, -0.5f * size.y, 0.0f));
        model.scale(new Vector3f(size, 1.0f));
        shaderProgram.setUniform("model", model);
        shaderProgram.setUniform("spriteColor", color);

        glBindVertexArray(quadVAO.getID());
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);

    }

    public void drawSprite(Texture texture, Vector2f position, Vector2f size, float rotate, Vector3f color) {
        shaderProgram.use();

        Matrix4f projection = new Matrix4f().ortho(0.0f, 800, 600, 0.0f, -1.0f, 1.0f);
        shaderProgram.setUniform("image", 0);
        shaderProgram.setUniform("projection", projection);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        Matrix4f model = new Matrix4f();
        model.translate(new Vector3f(position, 0.0f));
        model.translate(new Vector3f(0.5f * size.x, 0.5f * size.y, 0.0f));
        model.rotate(rotate, new Vector3f(0.0f, 0.0f, 1.0f));
        model.translate(new Vector3f(-0.5f * size.x, -0.5f * size.y, 0.0f));
        model.scale(new Vector3f(size, 1.0f));
        shaderProgram.setUniform("model", model);
        shaderProgram.setUniform("spriteColor", color);

        glBindVertexArray(quadVAO.getID());
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);

    }

    void initRenderData() {
        quadVAO = new VertexArrayObject();
        VertexBufferObject vbo = new VertexBufferObject();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer verBuf = stack.mallocFloat(4 * 6);
            verBuf.put(0f).put(1.0f).put(0f).put(1.0f);
            verBuf.put(1.0f).put(0f).put(1.0f).put(0f);
            verBuf.put(0f).put(0f).put(0f).put(0f);
            verBuf.put(0f).put(1.0f).put(0f).put(1.0f);
            verBuf.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
            verBuf.put(1.0f).put(0f).put(1.0f).put(0f);
            verBuf.flip();
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadData(GL_ARRAY_BUFFER, verBuf, GL_STATIC_DRAW);
        }

        quadVAO.bind();
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
}
