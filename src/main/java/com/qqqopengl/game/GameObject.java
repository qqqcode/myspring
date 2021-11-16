package com.qqqopengl.game;

import com.qqqopengl.graphic.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GameObject {
    public Vector2f position, size, velocity;
    public Vector3f color;
    public float rotation;
    public boolean isSolid;
    public boolean destroyed;
    public Texture sprite;

    public static class Builder {
        private Vector2f position;
        private Vector2f size = new Vector2f(10.0f, 10.0f);
        private Vector2f velocity;
        private Vector3f color;
        private float rotation;
        private boolean isSolid;
        private boolean destroyed;
        private Texture sprite;

        public Builder (Vector2f pos, Texture sprite) {
            this.position = pos;
            this.sprite = sprite;
        }

        public Builder size(Vector2f size) {
            this.size = size;
            return this;
        }

        public Builder velocity(Vector2f velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder color(Vector3f color) {
            this.color = color;
            return this;
        }

        public Builder rotation(float rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder isSolid(boolean isSolid) {
            this.isSolid = isSolid;
            return this;
        }

        public Builder destroyed(boolean destroyed){
            this.destroyed = destroyed;
            return this;
        }


        public GameObject build() {
            return new GameObject(this);
        }
    }

    public GameObject(Vector2f pos, Texture block) {
        this.position = pos;
        this.sprite = block;
        this.size = new Vector2f(10.0f, 10.0f);
        this.color = new Vector3f(1.0f);
        this.rotation = 0;
        this.isSolid = false;
        this.destroyed = false;
        this.velocity = new Vector2f(0f);
    }

    public GameObject(Vector2f pos, Vector2f size, Texture block) {
        this.position = pos;
        this.size = size;
        this.sprite = block;
        this.color = new Vector3f(1.0f);
        this.rotation = 0;
        this.isSolid = false;
        this.destroyed = false;
        this.velocity = new Vector2f(0f);
    }

    public GameObject(Vector2f pos, float rotation, Vector2f velocity, Texture sprite) {
        this.position = pos;
        this.size = new Vector2f(10.0f, 10.0f);
        this.sprite = sprite;
        this.color = new Vector3f(1.0f);
        this.rotation = rotation;
        this.isSolid = false;
        this.destroyed = false;
        this.velocity = velocity;
    }

    public GameObject(Vector2f pos, Vector2f size, Texture block, Vector3f color) {
        this.position = pos;
        this.size = size;
        this.sprite = block;
        this.color = color;
        this.rotation = 0;
        this.isSolid = false;
        this.destroyed = false;
        this.velocity = new Vector2f(0f);
    }

    public GameObject(Vector2f pos, Vector2f size, Texture sprite, Vector3f color, Vector2f velocity) {
        this.position = pos;
        this.size = size;
        this.velocity = velocity;
        this.color = color;
        this.rotation = 0;
        this.isSolid = false;
        this.destroyed = false;
        this.sprite = sprite;
    }

    public GameObject(Vector2f pos, Vector2f size, Texture sprite, Vector3f color, Vector2f velocity,float rotation) {
        this.position = pos;
        this.size = size;
        this.velocity = velocity;
        this.color = color;
        this.rotation = rotation;
        this.isSolid = false;
        this.destroyed = false;
        this.sprite = sprite;
    }

    private GameObject(Builder builder) {
        this.position = builder.position;
        this.size = builder.size;
        this.velocity = builder.velocity;
        this.color = builder.color;
        this.rotation = builder.rotation;
        this.isSolid = builder.isSolid;
        this.destroyed = builder.destroyed;
        this.sprite = builder.sprite;
    }

    public void draw(SpriteRenderer renderer) {
        renderer.drawSprite(this.sprite, this.position, this.size, this.rotation, this.color);
    }
}
