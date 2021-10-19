package com.qqqopengl.graphic;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;

public class Mesh {

    public Material material;

    public VertexBufferObject vertexArrayBuffer;
    public VertexBufferObject normalArrayBuffer;
    public VertexBufferObject elementArrayBuffer;
    public int elementCount;


    public Mesh(AIMesh mesh,AIMaterial aiMaterial) {
        material = new Material(aiMaterial);

        vertexArrayBuffer = new VertexBufferObject();
        vertexArrayBuffer.bind(GL_ARRAY_BUFFER);
        AIVector3D.Buffer vertices = mesh.mVertices();
        nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * vertices.remaining(),vertices.address(), GL_STATIC_DRAW);

        normalArrayBuffer = new VertexBufferObject();
        normalArrayBuffer.bind(GL_ARRAY_BUFFER);
        AIVector3D.Buffer normals = mesh.mNormals();
        nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * normals.remaining(),normals.address(), GL_STATIC_DRAW);

        int faceCount = mesh.mNumFaces();
        elementCount = faceCount * 3;
        IntBuffer elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
        AIFace.Buffer facesBuffer = mesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndices() != 3");
            }
            elementArrayBufferData.put(face.mIndices());
        }
        elementArrayBufferData.flip();
        elementArrayBuffer = new VertexBufferObject();
        elementArrayBuffer.bind(GL_ELEMENT_ARRAY_BUFFER);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArrayBufferData, GL_STATIC_DRAW);
    }

    public void draw(ShaderProgram shaderProgram) {
        this.vertexArrayBuffer.bind(GL_ARRAY_BUFFER);
        shaderProgram.pointVertexAttribute("aVertex",3,0,0);
        this.normalArrayBuffer.bind(GL_ARRAY_BUFFER);
        shaderProgram.pointVertexAttribute("aNormal",3,0,0);
        this.elementArrayBuffer.bind(GL_ELEMENT_ARRAY_BUFFER);
        glDrawElements(GL_TRIANGLES, this.elementCount, GL_UNSIGNED_INT, 0);

    }

}
