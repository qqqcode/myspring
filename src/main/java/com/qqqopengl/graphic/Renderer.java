package com.qqqopengl.graphic;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {

    VertexArrayObject vao;
    VertexBufferObject vbo;

    Map<Integer,VertexArrayObject> vaoMap;

    private ShaderProgram program;

    private FloatBuffer vertices;
    private int numVertices;
    private boolean drawing;

    public void init() {
        //init()
        vaoMap = new HashMap<>();
        numVertices = 0;
        drawing = false;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    }

    public void init(String vertexShaderName, String fragmentShaderName) {

        vaoMap = new HashMap<>();

        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

        vertices = MemoryUtil.memAllocFloat(4096);
        long size = vertices.capacity() * Float.BYTES;
        vbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        numVertices = 0;
        drawing = false;

        initShaderProgram(vertexShaderName, fragmentShaderName);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * @param vertexShaderName   "/resources/textture1.vert"
     * @param fragmentShaderName "/resources/textture1.frag"
     */
    public void initShaderProgram(String vertexShaderName, String fragmentShaderName) {
        Shader vertexShader = Shader.loadShader(GL_VERTEX_SHADER, vertexShaderName);
        Shader fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, fragmentShaderName);
        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.link();
        program.use();
    }

    public void glEnbale(int target) {
        glEnable(target);
    }

    public void uploadVBO(float[] vertice, int count) {
        vertices.put(vertice);
        vertices.flip();

        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        numVertices = count;
    }

    public void draw(int mode, int first, int count) {
        this.vao.bind();
        glEnableVertexAttribArray(0);
        glDrawArrays(mode, first, count);
        glBindVertexArray(0);
    }

    public void draw(VertexArrayObject vao,int mode, int first, int count) {
        vao.bind();
        glEnableVertexAttribArray(0);
        glDrawArrays(mode, first, count);
        glBindVertexArray(0);
    }

    public void render(float[] vertice, int count) {
        uploadVBO(vertice, count);
        program.use();
        draw(GL_TRIANGLES, 0, numVertices);
    }

    public void render(VertexArrayObject vao ,float[] vertice, int count) {
        uploadVBO(vertice, count);
        program.use();
        draw(vao,GL_TRIANGLES, 0, numVertices);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vbo.delete();

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

    public void dispose() {
        MemoryUtil.memFree(vertices);

        if (vao != null) {
            vao.delete();
        }
        vbo.delete();
        program.delete();
    }

    public void useTexture(int activePos, Texture texture, CharSequence uniformLocation, int uniform) {
        glActiveTexture(activePos);
        texture.bind();
        setUniform(uniformLocation, uniform);
    }

    public void setUniform(CharSequence name, float value) {
        int ourTexture = program.getUniformLocation(name);
        program.setUniform(ourTexture, value);
    }

    public void setUniform(CharSequence name, int value) {
        int ourTexture = program.getUniformLocation(name);
        program.setUniform(ourTexture, value);
    }

    public void setUniform(CharSequence name, Vector3f value) {
        int ourTexture = program.getUniformLocation(name);
        program.setUniform(ourTexture, value);
    }

    public void setUniform(CharSequence name, Matrix4f value) {
        int uniformLocation = program.getUniformLocation(name);
        program.setUniform(uniformLocation, value);
    }

    private void setupSahderProgram() {

        vaoMap = new HashMap<>();
        vao = new VertexArrayObject();
        vao.bind();
        vaoMap.put(vao.getID(), vao);

        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

        vertices = MemoryUtil.memAllocFloat(4096);
        long size = vertices.capacity() * Float.BYTES;
        vbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        numVertices = 0;
        drawing = false;

//        long window = GLFW.glfwGetCurrentContext();
//        int width, height;
//
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer widthBuffer = stack.mallocInt(1);
//            IntBuffer heightBuffer = stack.mallocInt(1);
//
//            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
//            width = widthBuffer.get();
//            height = heightBuffer.get();
//        }
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

    public void specifyVertexAttributes(CharSequence name, int size, int stride, int offset) {
        int v = program.getAttributeLocation(name);
        program.enableVertexAttribute(v);
        program.pointVertexAttribute(v, size, stride * Float.BYTES, offset * Float.BYTES);
    }

    public void specifyVertexAttributes(CharSequence name, VertexArrayObject vao, int size, int stride, int offset) {
        vao.bind();
        int v = program.getAttributeLocation(name);
        program.enableVertexAttribute(v);
        program.pointVertexAttribute(v, size, stride * Float.BYTES, offset * Float.BYTES);
    }

    public VertexArrayObject createVAO() {
        VertexArrayObject vertexArrayObject = new VertexArrayObject();
        if (this.vaoMap == null) {
            this.vaoMap = new HashMap<>();
        }
        if (vao == null) {
            vao = vertexArrayObject;
        }
        this.vaoMap.put(vertexArrayObject.getID(),vertexArrayObject);
        return vertexArrayObject;
    }

    public VertexArrayObject getVAO(int id) {
        if (this.vaoMap == null) {
            this.vaoMap = new HashMap<>();
        }
        return this.vaoMap.get(id);
    }

}
