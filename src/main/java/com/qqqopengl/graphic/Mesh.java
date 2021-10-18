package com.qqqopengl.graphic;

import com.qqqopengl.test.ImportModelTest;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBShaderObjects.nglUniform3fvARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.ARBVertexProgram.glVertexAttribPointerARB;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh {

    VertexArrayObject vao;
    VertexBufferObject posVbo;
    VertexBufferObject norVbo;
    VertexBufferObject texVbo;
    VertexBufferObject ebo;

    float[] positions;
    float[] textCoords;
    float[] normals;
    int[] indices;


    Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        this.positions = positions;
        this.textCoords = textCoords;
        this.normals = normals;
        this.indices = indices;
        setupMesh();
    }

    void draw(ShaderProgram shaderProgram) {
        shaderProgram.use();
        int aVertex = shaderProgram.getAttributeLocation("aVertex");
        glEnableVertexAttribArray(aVertex);
        int aNormal = shaderProgram.getAttributeLocation("aNormal");
        glEnableVertexAttribArray(aNormal);

        posVbo.bind(GL_ARRAY_BUFFER);
        glVertexAttribPointer(aVertex, 3, GL_FLOAT, false, 0, 0);

        norVbo.bind(GL_ARRAY_BUFFER);
        glVertexAttribPointer(aNormal, 3, GL_FLOAT, false, 0, 0);

        shaderProgram.setUniform("uModelMatrix",new Matrix4f().rotateY(0.5f * (float) Math.PI).scale(1.5f, 1.5f, 1.5f));
        shaderProgram.setUniform("uViewProjectionMatrix",new Matrix4f());
        shaderProgram.setUniform("uNormalMatrix",new Matrix3f());
        shaderProgram.setUniform("uLightPosition",new Vector3f(-5f, 5f, 5f));
        shaderProgram.setUniform("uViewPosition",new Vector3f());


        vao.bind();
        ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

    }

    public void freeBuffer(Buffer buffer) {
        if (buffer != null) {
            MemoryUtil.memFree(buffer);
        }
    }

    private void setupMesh() {
        vao = new VertexArrayObject();
        posVbo = new VertexBufferObject();
        norVbo = new VertexBufferObject();
        texVbo = new VertexBufferObject();
        ebo = new VertexBufferObject();

        vao.bind();

        ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        ebo.uploadData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        freeBuffer(indicesBuffer);

        //position
        posVbo.bind(GL_ARRAY_BUFFER);
        FloatBuffer vecPositionsBuffer = MemoryUtil.memAllocFloat(positions.length);
        vecPositionsBuffer.put(positions).flip();
        posVbo.uploadData(GL_ARRAY_BUFFER, vecPositionsBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        freeBuffer(vecPositionsBuffer);

        //noraml
        norVbo.bind(GL_ARRAY_BUFFER);
        FloatBuffer vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
        vecNormalsBuffer.put(normals).flip();
        norVbo.uploadData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        freeBuffer(vecNormalsBuffer);

        //texCoords
        texVbo.bind(GL_ARRAY_BUFFER);
        FloatBuffer vecTexCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
        vecTexCoordsBuffer.put(textCoords).flip();
        texVbo.uploadData(GL_ARRAY_BUFFER, vecTexCoordsBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        freeBuffer(vecTexCoordsBuffer);

        glBindVertexArray(0);
    }

}
