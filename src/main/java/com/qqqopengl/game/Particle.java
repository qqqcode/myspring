package com.qqqopengl.game;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Particle {
    public Vector2f position;
    public Vector2f velocity;
    public Vector4f color;
    public float life;

    public Particle() {
        this.position = new Vector2f(0.0f);
        this.velocity = new Vector2f(0.0f);
        this.color = new Vector4f(1.0f);
        this.life = 0f;
    }
}
