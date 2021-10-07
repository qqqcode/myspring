package com.qqqopengl.graphic;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {

    VertexArrayObject vao;
    VertexBufferObject vbo;

    private ShaderProgram program;

    private FloatBuffer vertices;
    private int numVertices;
    private boolean drawing;

    public void init() {
        setupSahderProgram();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void glEnbale(int target) {
        glEnable(target);
    }

    public void uploadVBO(float[] vertice,int count) {
        vertices.put(vertice);
        vertices.flip();

        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        numVertices = count;
    }

    public void draw(int mode,int first,int count){
        vao.bind();
        glEnableVertexAttribArray(0);
        glDrawArrays(mode, first, count);
        glBindVertexArray(0);
    }

    public void render(float[] vertice,int count) {

        uploadVBO(vertice,count);
        program.use();
        draw(GL_TRIANGLES,0,numVertices);
    }

    public void cleanup () {
        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vbo.delete();;

        // Delete the VAO
        glBindVertexArray(0);
        vao.delete();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void begin() {
        if (drawing) {
            throw new IllegalStateException("Renderer is already drawing!");
        }
        drawing = true;
        numVertices = 0;
    }

    public void end() {
        if (!drawing) {
            throw new IllegalStateException("Renderer isn't drawing!");
        }
        drawing = false;
        flush();
    }

    public void flush() {
        if (numVertices > 0) {
            vertices.flip();

            if (vao != null) {
                vao.bind();
            } else {
                vbo.bind(GL_ARRAY_BUFFER);
                specifyVertexAttributes();
            }
            program.use();

            /* Upload the new vertex data */
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadSubData(GL_ARRAY_BUFFER, 0, vertices);

            /* Draw batch */
            glDrawArrays(GL_TRIANGLES, 0, numVertices);

            /* Clear vertex data for next batch */
            vertices.clear();
            numVertices = 0;
        }
    }

    public void dispose() {
        MemoryUtil.memFree(vertices);

        if (vao != null) {
            vao.delete();
        }
        vbo.delete();
        program.delete();
    }

    public void useTexture(int activePos,Texture texture,CharSequence uniformLocation,int uniform) {
        glActiveTexture(activePos);
        texture.bind();
        setUniform(uniformLocation,uniform);
    }

    public void setUniform(CharSequence name, int value) {
        int ourTexture = program.getUniformLocation(name);
        program.setUniform(ourTexture, value);
    }

    public void setUniform(CharSequence name, Matrix4f value) {
        int uniformLocation = program.getUniformLocation(name);
        program.setUniform(uniformLocation,value);
    }

    private void setupSahderProgram() {
        vao = new VertexArrayObject();
        vao.bind();

        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

        vertices = MemoryUtil.memAllocFloat(4096);
        long size = vertices.capacity() * Float.BYTES;
        vbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        numVertices = 0;
        drawing = false;

        Shader vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "/resources/textture1.vert");
        Shader fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "/resources/textture1.frag");

        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.link();
        program.use();

        vertexShader.delete();
        fragmentShader.delete();

        long window = GLFW.glfwGetCurrentContext();
        int width, height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);

            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        specifyVertexAttributes();

//        glActiveTexture(GL_TEXTURE0);
//        int ourTexture = program.getUniformLocation("ourTexture");
//        program.setUniform(ourTexture, 0);


        Matrix4f model = new Matrix4f();
        program.setUniform("model", model);

        Matrix4f view = new Matrix4f();
        program.setUniform("view", view);

        Matrix4f projection = new Matrix4f();
        program.setUniform("projection", projection);

    }

    private void specifyVertexAttributes() {
        int postion = program.getAttributeLocation("postion");
        program.enableVertexAttribute(postion);
        program.pointVertexAttribute(postion, 3, 8 * Float.BYTES, 0);

        int color = program.getAttributeLocation("color");
        program.enableVertexAttribute(color);
        program.pointVertexAttribute(color, 3, 8 * Float.BYTES, 3 * Float.BYTES);

        int texcoord = program.getAttributeLocation("texcoord");
        program.enableVertexAttribute(texcoord);
        program.pointVertexAttribute(texcoord, 2, 8 * Float.BYTES, 6 * Float.BYTES);
    }

}
