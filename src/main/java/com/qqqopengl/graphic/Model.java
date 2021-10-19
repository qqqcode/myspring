package com.qqqopengl.graphic;


import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.qqqopengl.util.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Model {

    List<Mesh> meshes;

    public Model(String path) {
        meshes = new ArrayList<>();
        loadModel(path);
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public void draw(ShaderProgram shaderProgram) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        for (int i = 0; i < meshes.size(); i++) {
            meshes.get(i).draw(shaderProgram);
        }
    }

    void loadModel(String path) {
        AIFileIO fileIo = AIFileIO.create().OpenProc((pFileIO, fileName, openMode) -> {
                    ByteBuffer data;
                    String fileNameUtf8 = memUTF8(fileName);
                    try {
                        data = ioResourceToByteBuffer(fileNameUtf8, 8192);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not open file: " + fileNameUtf8);
                    }

                    return AIFile.create()
                            .ReadProc((pFile, pBuffer, size, count) -> {
                                long max = Math.min(data.remaining(), size * count);
                                memCopy(memAddress(data) + data.position(), pBuffer, max);
                                return max;
                            })
                            .SeekProc((pFile, offset, origin) -> {
                                if (origin == Assimp.aiOrigin_CUR) {
                                    data.position(data.position() + (int) offset);
                                } else if (origin == Assimp.aiOrigin_SET) {
                                    data.position((int) offset);
                                } else if (origin == Assimp.aiOrigin_END) {
                                    data.position(data.limit() + (int) offset);
                                }
                                return 0;
                            })
                            .FileSizeProc(pFile -> data.limit())
                            .address();
                })
                .CloseProc((pFileIO, pFile) -> {
                    AIFile aiFile = AIFile.create(pFile);
                    aiFile.ReadProc().free();
                    aiFile.SeekProc().free();
                    aiFile.FileSizeProc().free();
                });
        AIScene scene = aiImportFileEx(path, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate, fileIo);
        fileIo.OpenProc().free();
        fileIo.CloseProc().free();
        if (scene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }
        processNode(scene);
    }


    void processNode(AIScene scene) {
        PointerBuffer meshBuffer = scene.mMeshes();
        int meshCount = scene.mNumMeshes();
        for (int i = 0; i < meshCount; i++) {
            AIMesh aiMesh = AIMesh.create(meshBuffer.get(i));
            meshes.add(processMesh(aiMesh,scene));
        }

    }

    Mesh processMesh(AIMesh aiMesh, AIScene scene) {

        AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(aiMesh.mMaterialIndex()));
        Mesh mesh = new Mesh(aiMesh,aiMaterial);

        return mesh;
    }

    public void free() {}

}
