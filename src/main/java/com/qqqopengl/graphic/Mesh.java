package com.qqqopengl.graphic;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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

    List<Texture> textures = new ArrayList<>();

    Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        this.positions = positions;
        this.textCoords = textCoords;
        this.normals = normals;
        this.indices = indices;
        setupMesh();
    }

    void draw(ShaderProgram shaderProgram) {
        shaderProgram.use();
        int diffuseNr = 1;
        int specularNr = 1;
        for (int i = 0; i < this.textures.size(); i++) {
            Texture texture = textures.get(i);
            glActiveTexture(GL_TEXTURE0 + i);

            String type = texture.getType();
            if (type != null && type != "") {
                if (type == "texture_diffuse") {
                    type = type + diffuseNr;
                    diffuseNr++;
                }
                if (type == "texture_specular") {
                    type = type + specularNr;
                    specularNr++;
                }
            }
            shaderProgram.setUniform(type, i);
            texture.bind();
        }

        //shaderProgram.setUniform("material.shininess", 16.0f);


        glBindVertexArray(vao.getID());
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        for (int i = 0; i < this.textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
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
