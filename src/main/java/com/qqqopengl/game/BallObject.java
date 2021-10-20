package com.qqqopengl.game;

import com.qqqopengl.graphic.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BallObject extends GameObject {

    public float radius;
    public boolean stuck;


    public BallObject(Vector2f pos, float radius, Vector2f velocity, Texture sprite) {
        super(pos, new Vector2f(radius * 2.0f), sprite, new Vector3f(1.0f), velocity, radius);
        this.radius = radius;
        this.stuck = true;
    }

    Vector2f move(float dt, int windowWidth) {
        if (this.stuck) {
            // move the ball
            this.position.add(this.velocity.mul(dt, new Vector2f()));
            // then check if outside window bounds and if so, reverse velocity and restore at correct position
            if (this.position.x <= 0.0f) {
                this.velocity.x = -this.velocity.x;
                this.position.x = 0.0f;
            } else if (this.position.x + this.size.x >= windowWidth) {
                this.velocity.x = -this.velocity.x;
                this.position.x = windowWidth - this.size.x;
            }
            if (this.position.y <= 0.0f) {
                this.velocity.y = -this.velocity.y;
                this.position.y = 0.0f;
            }
        }
        return this.position;
    }

    void reset(Vector2f position, Vector2f velocity) {
        this.position = position;
        this.velocity = velocity;
        this.stuck = true;
    }

    public BallObject(Vector2f pos, Vector2f size, Texture block) {
        super(pos, size, block);
    }

    public BallObject(Vector2f pos, Vector2f size, Texture block, Vector3f color) {
        super(pos, size, block, color);
    }

    public BallObject(Vector2f pos, Vector2f size, Texture sprite, Vector3f color, Vector2f velocity) {
        super(pos, size, sprite, color, velocity);
    }


}
