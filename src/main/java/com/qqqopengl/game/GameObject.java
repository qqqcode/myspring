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

    public void draw(SpriteRenderer renderer) {
        renderer.drawSprite(this.sprite, this.position, this.size, this.rotation, this.color);
    }
}
