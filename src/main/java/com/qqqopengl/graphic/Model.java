package com.qqqopengl.graphic;

import com.qqqopengl.util.TextureCache;
import com.qqqopengl.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.qqqopengl.util.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Model {

    List<Mesh> meshes;
    public List<Material> materials;

    public Model(String path) {
        meshes = new ArrayList<>();
        materials = new ArrayList<>();
        loadModel(path);
    }

    public void draw(ShaderProgram shaderProgram) {
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

        int numMaterials = scene.mNumMaterials();
        PointerBuffer aiMaterials = scene.mMaterials();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            Material material = new Material(aiMaterial);
            materials.add(material);
        }

//
//        for(unsigned int i = 0; i < node->mNumChildren; i++)
//        {
//            processNode(node->mChildren[i], scene);
//        }

    }
//
    Mesh processMesh(AIMesh aiMesh, AIScene scene) {

        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList();

        for (int i = 0; i < aiMesh.mNumVertices(); i++) {
            AIVector3D vector = aiMesh.mVertices().get(i);
            vertices.add(vector.x());
            vertices.add(vector.y());
            vertices.add(vector.z());

            vector = aiMesh.mNormals().get(i);
            normals.add(vector.x());
            normals.add(vector.y());
            normals.add(vector.z());

            if (aiMesh.mTextureCoords(0) != null) {
                vector = aiMesh.mTextureCoords(0).get(i);
                textures.add(vector.x());
                textures.add(vector.y());
            } else  {
                textures.add(0.0f);
                textures.add(0.0f);
            }
        }

        for (int i = 0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiMesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }


        Mesh mesh = new Mesh(Util.listToFloatArray(vertices),Util.listToFloatArray(textures),Util.listToFloatArray(normals),Util.listToIntArray(indices));

        AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(aiMesh.mMaterialIndex()));

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String textPath = path.dataString();
        Texture texture = null;
        if (textPath != null && textPath.length() > 0) {
            TextureCache textCache = TextureCache.getInstance();
            texture = textCache.getTexture(textPath);
        }

        return mesh;
    }

}
