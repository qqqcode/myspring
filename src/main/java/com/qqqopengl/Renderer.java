package com.qqqopengl;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class Renderer {

    VertexArrayObject vao;
    VertexBufferObject vbo;

    private ShaderProgram program;

    private FloatBuffer vertices;
    private int numVertices;
    private boolean drawing;

    void init() {
        vao = new VertexArrayObject();
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

        vertices = MemoryUtil.memAllocFloat(4096);

        long size = vertices.capacity() * Float.BYTES;

        vbo.uploadData(GL_ARRAY_BUFFER,size,GL_DYNAMIC_DRAW);

        numVertices = 0;
        drawing = false;

        Shader vertexShader,fragmentShader;
        vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "resources/default.vert");
        fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "resources/default.frag");

        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);

        program.bindFragmentDataLocation(0,"fragColor");
        program.link();
        program.use();


    }



}
