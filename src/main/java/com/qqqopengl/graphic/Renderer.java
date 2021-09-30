package com.qqqopengl.graphic;

import com.qqqopengl.ShaderProgram;
import com.qqqopengl.graphic.Shader;
import com.qqqopengl.graphic.VertexArrayObject;
import com.qqqopengl.graphic.VertexBufferObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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

        vbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        numVertices = 0;
        drawing = false;

        Shader vertexShader, fragmentShader;
        vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "resources/default.vert");
        fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "resources/default.frag");

        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);

        program.bindFragmentDataLocation(0, "fragColor");
        program.link();
        program.use();


    }

    private void setupSahderProgram() {
        vao = new VertexArrayObject();
        vao.bind();

        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

        vertices = MemoryUtil.memAllocFloat(4096);
        long size = vertices.capacity() * Float.BYTES;
        vbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);


        //shader
        Shader vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "/resources/rainrowtriange.vert");
        Shader fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "/resources/rainrowtriange.frag");

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.link();
        shaderProgram.use();

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

        int uniTex = program.getUniformLocation("texImage");
        program.setUniform(uniTex,0);

    }

    private void specifyVertexAttributes() {
        int postion = program.getAttributeLocation("postion");
        program.enableVertexAttribute(postion);
        program.pointVertexAttribute(postion, 2, 8 * Float.BYTES, 0);

        int color = program.getAttributeLocation("color");
        program.enableVertexAttribute(color);
        program.pointVertexAttribute(color, 4, 8 * Float.BYTES, 2 * Float.BYTES);

        int texcoord = program.getAttributeLocation("texcoord");
        program.enableVertexAttribute(texcoord);
        program.pointVertexAttribute(texcoord, 2, 8 * Float.BYTES, 6 * Float.BYTES);
    }

}
