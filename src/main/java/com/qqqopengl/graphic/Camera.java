package com.qqqopengl.graphic;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    public enum CameraMovement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    Vector3f position;
    Vector3f up;
    Vector3f right;
    Vector3f worldUp;
    Vector3f front;

    float yaw = -90.0f;
    float pitch = 0.0f;

    float movementSpeed = 2.5f;
    float mouseSensitivity = 0.1f;
    float zoom = 45.0f;

    public Camera() {
        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        updateCameraVectors();
    }

    public Camera(float posX, float posY, float posZ) {
        this.position = new Vector3f(posX, posY, posZ);
        this.worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        updateCameraVectors();
    }

    public Camera(float yaw, float pitch) {
        this.position = new Vector3f(0.0f, 0.0f, 0.0f);
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        updateCameraVectors();
    }

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.position = position;
        this.up = up;
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        updateCameraVectors();
    }

    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        this.position = new Vector3f(posX, posY, posZ);
        this.worldUp = new Vector3f(upX, upY, upZ);
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(this.position, position.add(this.front.x, this.front.y, this.front.z, new Vector3f()), this.up);
    }

    public void processKeyboard(CameraMovement direction, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        if (direction == CameraMovement.FORWARD)
            position.add(this.front.mul(velocity, new Vector3f()));
        if (direction == CameraMovement.BACKWARD)
            position.sub(this.front.mul(velocity, new Vector3f()));
        if (direction == CameraMovement.LEFT)
            position.sub(this.right.mul(velocity, new Vector3f()));
        if (direction == CameraMovement.RIGHT)
            position.add(this.right.mul(velocity, new Vector3f()));
    }

    public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch) {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (constrainPitch) {
            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;
        }
        updateCameraVectors();
    }

    public void processMouseScroll(float yoffset) {
        zoom -= yoffset;
        if (zoom < 1.0f)
            zoom = 1.0f;
        if (zoom > 45.0f)
            zoom = 45.0f;
    }

    void updateCameraVectors() {
        Vector3f front = new Vector3f();
        front.x = (float) (Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
        front.y = (float) Math.sin(Math.toRadians(this.pitch));
        front.z = (float) (Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)));
        front.normalize(this.front);
        this.right = this.front.cross(this.worldUp, new Vector3f()).normalize(new Vector3f());
        this.up = this.right.cross(this.front, new Vector3f()).normalize(new Vector3f());
    }

    public float getZoom() {
        return zoom;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getFront() {
        return front;
    }
}
