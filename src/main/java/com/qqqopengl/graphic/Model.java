package com.qqqopengl.graphic;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class Model {

    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;

        public int idxTextCoord;

        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }

    protected static class Face {

        private List<IdxGroup> idxGroups;

        public Face(String... v) {
            idxGroups = new ArrayList<>();
            for (String s : v) {
                IdxGroup idxGroup = parseLine(s);
                idxGroups.add(idxGroup);
            }
        }

        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public List<IdxGroup> getFaceVertexIndices() {
            return idxGroups;
        }
    }

    List<Texture> textures;
    List<Mesh> meshes = new ArrayList<>();

    public Model(String path) {
        loadModel(path);
    }

    public void draw(ShaderProgram shaderProgram) {
        for (int i = 0; i < meshes.size(); i++) {
            meshes.get(i).draw(shaderProgram);
        }
    }

    void loadModel(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String line = "";
            List<Vector3f> vertices = new ArrayList<>();
            List<Vector2f> textures = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Face> faces = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "v":
                        Vector3f vec3f = new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3]));
                        vertices.add(vec3f);
                        break;
                    case "vt":
                        // Texture coordinate
                        Vector2f vec2f = new Vector2f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]));
                        textures.add(vec2f);
                        break;
                    case "vn":
                        Vector3f vec3fNorm = new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3]));
                        normals.add(vec3fNorm);
                        break;
                    case "f":
                        Face face = new Face(tokens[1], tokens[2], tokens[3]);
                        faces.add(face);
                        break;
                    default:
                        break;
                }
            }
            meshes.add(reorderLists(vertices,textures,normals,faces));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Mesh reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList, List<Vector3f> normList, List<Face> facesList) {

        List<Integer> indices = new ArrayList();
        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textCoordArr = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];

        for (Face face : facesList) {
            List<IdxGroup> faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList,
                        indices, textCoordArr, normArr);
            }
        }
        int[] indicesArr = new int[indices.size()];
        indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        Mesh mesh = new Mesh(posArr, textCoordArr, normArr, indicesArr);
        return mesh;
    }

    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,List<Vector3f> normList, List<Integer> indicesList,float[] texCoordArr, float[] normArr) {
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }

//    // processes a node in a recursive fashion. Processes each individual mesh located at the node and repeats this process on its children nodes (if any).
//    void processNode(aiNode *node, const aiScene *scene)
//    {
//        // process each mesh located at the current node
//        for(unsigned int i = 0; i < node->mNumMeshes; i++)
//        {
//            // the node object only contains indices to index the actual objects in the scene.
//            // the scene contains all the data, node is just to keep stuff organized (like relations between nodes).
//            aiMesh* mesh = scene->mMeshes[node->mMeshes[i]];
//            meshes.push_back(processMesh(mesh, scene));
//        }
//        // after we've processed all of the meshes (if any) we then recursively process each of the children nodes
//        for(unsigned int i = 0; i < node->mNumChildren; i++)
//        {
//            processNode(node->mChildren[i], scene);
//        }
//
//    }
//
//    Mesh processMesh(aiMesh *mesh, const aiScene *scene)
//    {
//        // data to fill
//        vector<Vertex> vertices;
//        vector<unsigned int> indices;
//        vector<Texture> textures;
//
//        // walk through each of the mesh's vertices
//        for(unsigned int i = 0; i < mesh->mNumVertices; i++)
//        {
//            Vertex vertex;
//            glm::vec3 vector; // we declare a placeholder vector since assimp uses its own vector class that doesn't directly convert to glm's vec3 class so we transfer the data to this placeholder glm::vec3 first.
//            // positions
//            vector.x = mesh->mVertices[i].x;
//            vector.y = mesh->mVertices[i].y;
//            vector.z = mesh->mVertices[i].z;
//            vertex.Position = vector;
//            // normals
//            if (mesh->HasNormals())
//            {
//                vector.x = mesh->mNormals[i].x;
//                vector.y = mesh->mNormals[i].y;
//                vector.z = mesh->mNormals[i].z;
//                vertex.Normal = vector;
//            }
//            // texture coordinates
//            if(mesh->mTextureCoords[0]) // does the mesh contain texture coordinates?
//            {
//                glm::vec2 vec;
//                // a vertex can contain up to 8 different texture coordinates. We thus make the assumption that we won't
//                // use models where a vertex can have multiple texture coordinates so we always take the first set (0).
//                vec.x = mesh->mTextureCoords[0][i].x;
//                vec.y = mesh->mTextureCoords[0][i].y;
//                vertex.TexCoords = vec;
//                // tangent
//                vector.x = mesh->mTangents[i].x;
//                vector.y = mesh->mTangents[i].y;
//                vector.z = mesh->mTangents[i].z;
//                vertex.Tangent = vector;
//                // bitangent
//                vector.x = mesh->mBitangents[i].x;
//                vector.y = mesh->mBitangents[i].y;
//                vector.z = mesh->mBitangents[i].z;
//                vertex.Bitangent = vector;
//            }
//            else
//                vertex.TexCoords = glm::vec2(0.0f, 0.0f);
//
//            vertices.push_back(vertex);
//        }
//        // now wak through each of the mesh's faces (a face is a mesh its triangle) and retrieve the corresponding vertex indices.
//        for(unsigned int i = 0; i < mesh->mNumFaces; i++)
//        {
//            aiFace face = mesh->mFaces[i];
//            // retrieve all indices of the face and store them in the indices vector
//            for(unsigned int j = 0; j < face.mNumIndices; j++)
//            indices.push_back(face.mIndices[j]);
//        }
//        // process materials
//        aiMaterial* material = scene->mMaterials[mesh->mMaterialIndex];
//        // we assume a convention for sampler names in the shaders. Each diffuse texture should be named
//        // as 'texture_diffuseN' where N is a sequential number ranging from 1 to MAX_SAMPLER_NUMBER.
//        // Same applies to other texture as the following list summarizes:
//        // diffuse: texture_diffuseN
//        // specular: texture_specularN
//        // normal: texture_normalN
//
//        // 1. diffuse maps
//        vector<Texture> diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE, "texture_diffuse");
//        textures.insert(textures.end(), diffuseMaps.begin(), diffuseMaps.end());
//        // 2. specular maps
//        vector<Texture> specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR, "texture_specular");
//        textures.insert(textures.end(), specularMaps.begin(), specularMaps.end());
//        // 3. normal maps
//        std::vector<Texture> normalMaps = loadMaterialTextures(material, aiTextureType_HEIGHT, "texture_normal");
//        textures.insert(textures.end(), normalMaps.begin(), normalMaps.end());
//        // 4. height maps
//        std::vector<Texture> heightMaps = loadMaterialTextures(material, aiTextureType_AMBIENT, "texture_height");
//        textures.insert(textures.end(), heightMaps.begin(), heightMaps.end());
//
//        // return a mesh object created from the extracted mesh data
//        return Mesh(vertices, indices, textures);
//    }
//
//    // checks all material textures of a given type and loads the textures if they're not loaded yet.
//    // the required info is returned as a Texture struct.
//    vector<Texture> loadMaterialTextures(aiMaterial *mat, aiTextureType type, string typeName)
//    {
//        vector<Texture> textures;
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
//                Texture texture;
//                texture.id = TextureFromFile(str.C_Str(), this->directory);
//                texture.type = typeName;
//                texture.path = str.C_Str();
//                textures.push_back(texture);
//                textures_loaded.push_back(texture);  // store it as texture loaded for entire model, to ensure we won't unnecesery load duplicate textures.
//            }
//        }
//        return textures;
}
