package com.qqqopengl.graphic;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh {

    VertexArrayObject vao;
    VertexBufferObject vbo;
    VertexBufferObject ebo;

    FloatBuffer vertices;
    IntBuffer indices;


    List<Texture> textures;

    Mesh(FloatBuffer vertices, IntBuffer indices, List<Texture> textures) {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;
        setupMesh();
    }

    void Draw(ShaderProgram shaderProgram) {
        // Bind appropriate textures
        int diffuseNr = 1;
        int specularNr = 1;
        for (int i = 0; i < this.textures.size(); i++) {
            Texture texture = textures.get(i);
            glActiveTexture(GL_TEXTURE0 + i);

            String type = texture.getType();
            if (type != null && type !=""){
                if (type == "texture_diffuse") {
                    type = type + diffuseNr;
                    diffuseNr ++;
                }
                if (type == "texture_specular") {
                    type = type + specularNr;
                    specularNr ++;
                }
            }
            shaderProgram.setUniform(type,i);
            texture.bind();
        }

        shaderProgram.setUniform("material.shininess",16.0f);

        glBindVertexArray(vao.getID());
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        for (int i = 0; i < this.textures.size();i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
    private void setupMesh() {
        vao = new VertexArrayObject();
        vbo = new VertexBufferObject();
        ebo = new VertexBufferObject();

        vao.bind();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
        ebo.uploadData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);

        glBindVertexArray(0);
    }

}
