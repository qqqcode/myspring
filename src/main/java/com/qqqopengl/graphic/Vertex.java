package com.qqqopengl.graphic;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;

public class Vertex {

    private Vector3f position;
    private Vector3f normal;
    private Vector2f texCoords;



    Vertex(Vector3f position, Vector3f normal, Vector2f texCoords) {
        this.position = position;
        this.normal = normal;
        this.texCoords = texCoords;
    }

    Vertex(float px, float py, float pz, float nx, float ny, float nz, float tx, float tz) {
        this.position = new Vector3f(px, py, pz);
        this.normal = new Vector3f(nx, ny, nz);
        this.texCoords = new Vector2f(tx, tz);
    }

    public static FloatBuffer getFloatBuffer(List<Vertex> vertices) {
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(4096);
        for (int i = 0; i < vertices.size(); i++) {
            floatBuffer.put(vertices.get(i).toFloats(), i * 8, 8);
        }
        return floatBuffer;
    }

    private float[] toFloats() {
        float[] floats = new float[8];
        floats[0] = this.position.x;
        floats[1] = this.position.y;
        floats[2] = this.position.z;
        floats[3] = this.normal.x;
        floats[4] = this.normal.y;
        floats[5] = this.normal.z;
        floats[6] = this.texCoords.x;
        floats[7] = this.texCoords.y;
        return floats;
    }


    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector2f getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(Vector2f texCoords) {
        this.texCoords = texCoords;
    }
}
