package com.qqqopengl.graphic;

import com.qqqopengl.math.Matrix2f;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

public class ShaderProgram {
    /**
     * Stores the handle of the program.
     */
    private final int id;

    private final Map<String, Integer> uniforms;

    public int getId() {
        return id;
    }

    public ShaderProgram() {
        id = glCreateProgram();
        uniforms = new HashMap<>();
    }

    public void attachShader(Shader shader) {
        glAttachShader(id, shader.getID());
    }

    public void bindFragmentDataLocation(int number, CharSequence name) {
        glBindFragDataLocation(id, number, name);
    }

    public void link() {
        glLinkProgram(id);

        checkStatus();
    }

    public int getAttributeLocation(CharSequence name) {
        return glGetAttribLocation(id, name);
    }

    public void enableVertexAttribute(int location) {
        glEnableVertexAttribArray(location);
    }

    public void disableVertexAttribute(int location) {
        glDisableVertexAttribArray(location);
    }

    public void pointVertexAttribute(int location, int size, int stride, int offset) {
        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
    }

    public void pointVertexAttribute(CharSequence name, int size, int stride, int offset) {
        int location = getAttributeLocation(name);
        enableVertexAttribute(location);
        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
    }

    public int getUniformLocation(CharSequence name) {
        int uniformLocation = glGetUniformLocation(id, name);
        uniforms.put(name.toString(),uniformLocation);
        return uniformLocation;
    }

    public void setUniform(int location, int value) {
        glUniform1i(location, value);
    }

    public void setUniform(int location, float value) {
        glUniform1f(location, value);
    }

    public void setUniform(int location, Vector2f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            value.get(buffer);
            glUniform2fv(location, buffer);
        }
    }

    public void setUniform(int location, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            value.get(buffer);
            glUniform3fv(location, buffer);
        }
    }

    public void setNglUniform3fv(int location, int count, long value) {
        nglUniform3fv(location, count, value);
    }

    public void setUniform3f(int location, FloatBuffer value) {
        glUniform3fv(location, value);
    }

    public void setUniform2f(int location, FloatBuffer value) {
        glUniform2fv(location, value);
    }

    public void setUniform1f(int location, FloatBuffer value) {
        glUniform1fv(location, value);
    }

    public void setUniform(int location, Vector4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            value.get(buffer);
            glUniform4fv(location, buffer);
        }
    }

    public void setUniform(int location, Matrix2f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2 * 2);
            value.toBuffer(buffer);
            glUniformMatrix2fv(location, false, buffer);
        }
    }

    public void setUniform(int location, Matrix3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 3);
            value.get(buffer);
            glUniformMatrix3fv(location, false, buffer);
        }
    }

    public void setUniform(int location, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.get(buffer);
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setUniformMatrix4f(int location,FloatBuffer value) {
        glUniformMatrix4fv(location, false, value);
    }

    public void setUniformMatrix3f(int location,FloatBuffer value) {
        glUniformMatrix3fv(location, false, value);
    }

    public void setUniform(CharSequence name, Matrix4f value) {
        int uniformLocation = getUniformLocation(name);
        setUniform(uniformLocation,value);
    }

    public void setUniform(CharSequence name, Matrix3f value) {
        int uniformLocation = getUniformLocation(name);
        setUniform(uniformLocation,value);
    }

    public void setUniform(CharSequence name, Vector4f value) {
        int uniformLocation = getUniformLocation(name);
        setUniform(uniformLocation,value);
    }

    public void setUniform(CharSequence name, Vector3f value) {
        int uniformLocation = getUniformLocation(name);
        setUniform(uniformLocation,value);
    }

    public void setUniform(CharSequence name, Vector2f value) {
        int uniformLocation = getUniformLocation(name);
        setUniform(uniformLocation,value);
    }

    public void setUniform(CharSequence name, int value) {
        int uniformLocation = getUniformLocation(name);
        setUniform(uniformLocation,value);
    }

    public void setUniform(CharSequence name, float value) {
        int uniformLocation = getUniformLocation(name);
        setUniform(uniformLocation,value);
    }

    public void setUniform4f(CharSequence name, FloatBuffer value) {
        int location = getUniformLocation(name);
        glUniformMatrix4fv(location, false, value);
    }

    public void setUniform3f(CharSequence name, FloatBuffer value) {
        int location = getUniformLocation(name);
        glUniformMatrix3fv(location, false, value);
    }
    /**
     * Use this shader program.
     */
    public void use() {
        glUseProgram(id);
    }

    /**
     * Checks if the program was linked successfully.
     */
    public void checkStatus() {
        int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(id));
        }
    }

    /**
     * Deletes the shader program.
     */
    public void delete() {
        glDeleteProgram(id);
    }
}
