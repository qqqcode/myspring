package com.qqqopengl.graphic;

import com.qqqopengl.util.Constant;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static com.qqqopengl.util.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Model {

    public AIScene scene;
    public List<Mesh> meshes;
    public List<Material> materials;

    Model(AIScene scene) {

        this.scene = scene;

        int meshCount = scene.mNumMeshes();
        PointerBuffer meshesBuffer = scene.mMeshes();
        meshes = new ArrayList<>();
        for (int i = 0; i < meshCount; ++i) {
            meshes.add(new Mesh());
        }

        int materialCount = scene.mNumMaterials();
        PointerBuffer materialsBuffer = scene.mMaterials();
        materials = new ArrayList<>();
        for (int i = 0; i < materialCount; ++i) {
            materials.add(new Material(AIMaterial.create(materialsBuffer.get(i))));
        }
    }

    public void free() {
        aiReleaseImport(scene);
        scene = null;
        meshes = null;
        materials = null;
    }


    public Model(String path) {
        loadModel(path);
    }

    public void draw(ShaderProgram shaderProgram) {
        for (int i = 0; i < meshes.size(); i++) {

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
        AIScene scene = aiImportFileEx(Constant.resources + "magnet.obj", aiProcess_JoinIdenticalVertices | aiProcess_Triangulate, fileIo);
        fileIo.OpenProc().free();
        fileIo.CloseProc().free();
        if (scene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }

        processNode(scene);
    }


    void processNode(AIScene scene) {
        AINode aiNode = scene.mRootNode();
        PointerBuffer meshBuffer = scene.mMeshes();
        for (int i = 0; i < aiNode.mNumMeshes(); i++) {
            AIMesh aiMesh = AIMesh.create(meshBuffer.get(i));
            meshes.add(processMesh(aiMesh,scene));
        }

//
//        for(unsigned int i = 0; i < node->mNumChildren; i++)
//        {
//            processNode(node->mChildren[i], scene);
//        }

    }
//
    Mesh processMesh(AIMesh mesh, AIScene scene) {
        // data to fill
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Texture> textures;

        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D vector = mesh.mVertices().get(i);
            Vector3f position = new Vector3f(vector.x(),vector.y(),vector.z());
            vector = mesh.mNormals().get(i);
            Vector3f normal = new Vector3f(vector.x(),vector.y(),vector.z());

            Vector2f texCoords = new Vector2f();
            if (mesh.mTextureCoords(0) != null) {
                vector = mesh.mTextureCoords(0).get(i);
                texCoords.set(vector.x(),vector.y());
            } else  {
                texCoords.set(0.0f, 0.0f);
            }
            Vertex vertex = new Vertex(position,normal,texCoords);
            vertices.add(vertex);
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }
        AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));

        List<Texture> diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE, "texture_diffuse");
        //textures.insert(textures.end(), diffuseMaps.begin(), diffuseMaps.end());

        List<Texture> specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR, "texture_specular");
        //textures.insert(textures.end(), specularMaps.begin(), specularMaps.end());

        List<Texture> normalMaps = loadMaterialTextures(material, aiTextureType_HEIGHT, "texture_normal");
        //textures.insert(textures.end(), normalMaps.begin(), normalMaps.end());

        List<Texture> heightMaps = loadMaterialTextures(material, aiTextureType_AMBIENT, "texture_height");
        //textures.insert(textures.end(), heightMaps.begin(), heightMaps.end());

        return null;//Mesh(vertices, indices, textures);
    }

    List<Texture> loadMaterialTextures(AIMaterial mat, int type, String typeName) {
        List<Texture> textures = new ArrayList<>();
        for (int i = 0; i < mat.sizeof(); i++) {
            boolean skip = false;

        }
//        for(unsigned int i = 0; i < mat->GetTextureCount(type); i++)
//        {
//            aiString str;
//            mat->GetTexture(type, i, &str);
//            // check if texture was loaded before and if so, continue to next iteration: skip loading a new texture
//            bool skip = false;
//            for(unsigned int j = 0; j < textures_loaded.size(); j++)
//            {
//                if(std::strcmp(textures_loaded[j].path.data(), str.C_Str()) == 0)
//                {
//                    textures.push_back(textures_loaded[j]);
//                    skip = true; // a texture with the same filepath has already been loaded, continue to next one. (optimization)
//                    break;
//                }
//            }
//            if(!skip)
//            {   // if texture hasn't been loaded already, load it
//                Texture texture = Texture.loadTexture("");
//                textures_loaded.push_back(texture);  // store it as texture loaded for entire model, to ensure we won't unnecesery load duplicate textures.
//            }
//        }
        return textures;
    }

}
