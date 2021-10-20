package com.qqqopengl.game;

import com.qqqopengl.graphic.ShaderProgram;
import com.qqqopengl.graphic.Texture;
import com.qqqopengl.graphic.VertexArrayObject;
import com.qqqopengl.graphic.VertexBufferObject;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ParticleGenerator {

    public ParticleGenerator(ShaderProgram shader, Texture texture, int amount) {
        this.shader = shader;
        this.texture = texture;
        this.amount = amount;
        this.init();
    }

    void update(float dt, GameObject object, int newParticles, Vector2f offset) {
        for (int i = 0; i < newParticles; i++) {
            int unusedParticle = this.firstUnusedParticle();
            this.respawnParticle(this.particles.get(unusedParticle), object, offset);
        }

        for (int i = 0; i < this.amount; i++) {
            Particle particle = this.particles.get(i);
            particle.life -= dt;
            if (particle.life > 0f) {
                particle.position.sub(particle.velocity.mul(dt, new Vector2f()));
                particle.color.w -= dt * 2.5f;
            }
        }
    }

    void update(float dt, GameObject object, int newParticles) {
        update(dt, object, newParticles, new Vector2f(0f));
    }

    void draw() {


        this.shader.use();
//        Matrix4f projection = new Matrix4f().ortho(0.0f, 800, 600, 0.0f, -1.0f, 1.0f);
//        this.shader.setUniform("sprite", 0);
//        this.shader.setUniform("projection", projection);


        glActiveTexture(GL_TEXTURE0);

        for (Particle particle : this.particles) {
            if (particle.life > 0.0f) {
                        glBlendFunc(GL_SRC_ALPHA,GL_ONE);
                this.shader.setUniform("offset", particle.position);
                this.shader.setUniform("color", particle.color);
                this.VAO.bind();
                texture.bind();
                glDrawArrays(GL_TRIANGLES, 0, 6);
                glBindVertexArray(0);
                        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }
        }


    }

    private List<Particle> particles;
    private int amount;
    private ShaderProgram shader;
    private Texture texture;
    private VertexArrayObject VAO;
    public int lastUsedParticle = 0;

    void init() {
        VAO = new VertexArrayObject();
        particles = new ArrayList<>();

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

        VAO.bind();
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        for (int i = 0; i < this.amount; ++i) {
            this.particles.add(new Particle());
        }
    }


    int firstUnusedParticle() {
        for (int i = lastUsedParticle; i < this.amount; ++i) {
            if (this.particles.get(i).life <= 0.0f) {
                lastUsedParticle = i;
                return i;
            }
        }

        for (int i = 0; i < lastUsedParticle; ++i) {
            if (this.particles.get(i).life <= 0.0f) {
                lastUsedParticle = i;
                return i;
            }
        }
        lastUsedParticle = 0;
        return 0;
    }

    void respawnParticle(Particle particle, GameObject object) {
        Vector2f offset = new Vector2f(0.0f);
        respawnParticle(particle, object, offset);
    }

    void respawnParticle(Particle particle, GameObject object, Vector2f offset) {
        float random = (float) (((Math.random() % 100) - 50) / 10.0f);
        float rColor = (float) (0.5f + ((Math.random() % 100) / 100.0f));
        particle.position = object.position.add(new Vector2f(random), new Vector2f()).add(new Vector2f(offset), new Vector2f());
        particle.color = new Vector4f(rColor, rColor, rColor, 1.0f);
        particle.life = 1.0f;
        particle.velocity = object.velocity.mul(0.1f, new Vector2f());
    }
}
