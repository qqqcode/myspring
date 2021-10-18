package com.qqqopengl.test;

import com.qqqopengl.graphic.QqqWindow;
import com.qqqopengl.graphic.Shader;
import com.qqqopengl.graphic.ShaderProgram;
import com.qqqopengl.util.Constant;
import com.qqqopengl.util.ShaderUtil;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.qqqopengl.util.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.ARBVertexProgram.glEnableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexProgram.glVertexAttribPointerARB;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;
import static org.lwjgl.opengl.ARBVertexShader.glGetAttribLocationARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.system.MemoryUtil.*;

public class ModelTest1 {


    QqqWindow qqqWindow;
    //long window;
    int width = 1024;
    int height = 768;
    int fbWidth = 1024;
    int fbHeight = 768;
    float fov = 60;
    float rotation;

    int program;
    ShaderProgram shaderProgram;
    int modelMatrixUniform;
    int viewProjectionMatrixUniform;
    int normalMatrixUniform;
    int lightPositionUniform;
    int viewPositionUniform;
    int ambientColorUniform;
    int diffuseColorUniform;
    int specularColorUniform;

    Model model;

    Matrix4x3f modelMatrix = new Matrix4x3f().rotateY(0.5f * (float) Math.PI).scale(1.5f, 1.5f, 1.5f);
    Matrix4x3f viewMatrix = new Matrix4x3f();
    Matrix4f projectionMatrix = new Matrix4f();
    Matrix4f viewProjectionMatrix = new Matrix4f();
    Vector3f viewPosition = new Vector3f();
    Vector3f lightPosition = new Vector3f(-5f, 5f, 5f);

    private FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private FloatBuffer viewProjectionMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private Matrix3f normalMatrix = new Matrix3f();
    private FloatBuffer normalMatrixBuffer = BufferUtils.createFloatBuffer(3 * 3);
    private FloatBuffer lightPositionBuffer = BufferUtils.createFloatBuffer(3);
    private FloatBuffer viewPositionBuffer = BufferUtils.createFloatBuffer(3);

    Callback debugProc;

    void init() throws IOException {

        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        qqqWindow = new QqqWindow("qqq", width, height, true);
        qqqWindow.setFramebufferSizeCallback((window, width, height) -> {
            if (width > 0 && height > 0 && (fbWidth != width || fbHeight != height)) {
                fbWidth = width;
                fbHeight = height;
            }
        });
        qqqWindow.setWindowSizeCallback((window, width, height) -> {
            if (width > 0 && height > 0 && (this.width != width || this.height != height)) {
                this.width = width;
                this.height = height;
            }
        });
        qqqWindow.setKeyCallback((window, key, scancode, action, mods) -> {
            if (action != GLFW_RELEASE) {
                return;
            }
            if (key == GLFW_KEY_ESCAPE) {
                glfwSetWindowShouldClose(window, true);
            }
        });
        qqqWindow.setCursorPosCallback((window, x, y) -> {
            rotation = ((float)x / width - 0.5f) * 2f * (float)Math.PI;
        });
        qqqWindow.setScrollCallback((window, xoffset, yoffset) -> {
            if (yoffset < 0) {
                fov *= 1.05f;
            } else {
                fov *= 1f / 1.05f;
            }
            if (fov < 10.0f) {
                fov = 10.0f;
            } else if (fov > 120.0f) {
                fov = 120.0f;
            }
        });
        //debugProc = GLUtil.setupDebugMessageCallback();

        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);

        loadModel();
        createProgram();


        /* Show window */
        qqqWindow.showWindow();
    }

    void loadModel() {
        AIFileIO fileIo = AIFileIO.create()
                .OpenProc((pFileIO, fileName, openMode) -> {
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
        AIScene scene = aiImportFileEx("magnet.obj",
                aiProcess_JoinIdenticalVertices | aiProcess_Triangulate, fileIo);
        fileIo.OpenProc().free();
        fileIo.CloseProc().free();
        if (scene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }
        model = new Model(scene);
    }

    static int createShader(String resource, int type) throws IOException {
        int shader = glCreateShader(type);
        ByteBuffer source = ioResourceToByteBuffer(resource, 1024);
        PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        IntBuffer lengths = BufferUtils.createIntBuffer(1);
        strings.put(0, source);
        lengths.put(0, source.remaining());
        glShaderSource(shader, strings, lengths);
        glCompileShader(shader);
        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        String shaderLog = glGetShaderInfoLog(shader);
        if (shaderLog.trim().length() > 0) {
            System.err.println(shaderLog);
        }
        if (compiled == 0) {
            throw new AssertionError("Could not compile shader");
        }
        return shader;
    }

    void createProgram() throws IOException {

        shaderProgram =ShaderUtil.createShaderProgram(Constant.resources + "magnet.vs",Constant.resources + "magnet.fs");
        program = shaderProgram.getId();

        shaderProgram.use();


        modelMatrixUniform = shaderProgram.getUniformLocation("uModelMatrix");
        viewProjectionMatrixUniform = shaderProgram.getUniformLocation("uViewProjectionMatrix");
        normalMatrixUniform = shaderProgram.getUniformLocation("uNormalMatrix");
        lightPositionUniform = shaderProgram.getUniformLocation("uLightPosition");
        viewPositionUniform = shaderProgram.getUniformLocation("uViewPosition");
        ambientColorUniform = shaderProgram.getUniformLocation("uAmbientColor");
        diffuseColorUniform = shaderProgram.getUniformLocation("uDiffuseColor");
        specularColorUniform = shaderProgram.getUniformLocation("uSpecularColor");
    }

    void update() {
        projectionMatrix.setPerspective((float) Math.toRadians(fov), (float) width / height, 0.01f,
                100.0f);
        viewPosition.set(10f * (float) Math.cos(rotation), 10f, 10f * (float) Math.sin(rotation));
        viewMatrix.setLookAt(viewPosition.x, viewPosition.y, viewPosition.z, 0f, 0f, 0f, 0f, 1f,
                0f);
        projectionMatrix.mul(viewMatrix, viewProjectionMatrix);
    }

    void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(program);
        for (Model.Mesh mesh : model.meshes) {

            glBindBuffer(GL_ARRAY_BUFFER, mesh.vertexArrayBuffer);
            shaderProgram.pointVertexAttribute("aVertex",3,0,0);

            glBindBuffer(GL_ARRAY_BUFFER, mesh.normalArrayBuffer);
            shaderProgram.pointVertexAttribute("aNormal",3,0,0);

            shaderProgram.setUniformMatrix4f(modelMatrixUniform,modelMatrix.get4x4(modelMatrixBuffer));
            shaderProgram.setUniformMatrix4f(viewProjectionMatrixUniform,viewProjectionMatrix.get(viewProjectionMatrixBuffer));

            modelMatrix.normal(normalMatrix).invert().transpose();
            shaderProgram.setUniformMatrix3f(normalMatrixUniform, normalMatrix.get(normalMatrixBuffer));

            shaderProgram.setUniform3f(lightPositionUniform, lightPosition.get(lightPositionBuffer));
            shaderProgram.setUniform3f(viewPositionUniform, viewPosition.get(viewPositionBuffer));

            Model.Material material = model.materials.get(mesh.mesh.mMaterialIndex());
            shaderProgram.setNglUniform3fv(ambientColorUniform, 1, material.mAmbientColor.address());
            shaderProgram.setNglUniform3fv(diffuseColorUniform, 1, material.mDiffuseColor.address());
            shaderProgram.setNglUniform3fv(specularColorUniform, 1, material.mSpecularColor.address());

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.elementArrayBuffer);
            glDrawElements(GL_TRIANGLES, mesh.elementCount, GL_UNSIGNED_INT, 0);
        }
    }

    void loop() {
        while (!qqqWindow.isClosing()) {
            glfwPollEvents();
            glViewport(0, 0, fbWidth, fbHeight);
            update();
            render();
            qqqWindow.update();
        }
        GL.setCapabilities(null);
    }

    void run() {
        try {
            init();
            loop();
            model.free();
            if (debugProc != null) {
                debugProc.free();
            }
            qqqWindow.destroy();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            glfwTerminate();
        }
    }

    public static void main(String[] args) {
        new ModelTest1().run();
    }

    static class Model {

        public AIScene scene;
        public List<Mesh> meshes;
        public List<Material> materials;

        Model(AIScene scene) {

            this.scene = scene;

            int meshCount = scene.mNumMeshes();
            PointerBuffer meshesBuffer = scene.mMeshes();
            meshes = new ArrayList<>();
            for (int i = 0; i < meshCount; ++i) {
                meshes.add(new Mesh(AIMesh.create(meshesBuffer.get(i))));
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

        public static class Mesh {

            public AIMesh mesh;
            public int vertexArrayBuffer;
            public int normalArrayBuffer;
            public int elementArrayBuffer;
            public int elementCount;

            public Mesh(AIMesh mesh) {
                this.mesh = mesh;

                vertexArrayBuffer = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vertexArrayBuffer);
                AIVector3D.Buffer vertices = mesh.mVertices();
                nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * vertices.remaining(),
                        vertices.address(), GL_STATIC_DRAW);

                normalArrayBuffer = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, normalArrayBuffer);
                AIVector3D.Buffer normals = mesh.mNormals();
                nglBufferData(GL_ARRAY_BUFFER, AIVector3D.SIZEOF * normals.remaining(),
                        normals.address(), GL_STATIC_DRAW);

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
                elementArrayBuffer = glGenBuffers();
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementArrayBuffer);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementArrayBufferData,
                        GL_STATIC_DRAW);
            }
        }

        public static class Material {

            public AIMaterial mMaterial;
            public AIColor4D mAmbientColor;
            public AIColor4D mDiffuseColor;
            public AIColor4D mSpecularColor;

            public Material(AIMaterial material) {

                mMaterial = material;

                mAmbientColor = AIColor4D.create();
                if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_AMBIENT,
                        aiTextureType_NONE, 0, mAmbientColor) != 0) {
                    throw new IllegalStateException(aiGetErrorString());
                }
                mDiffuseColor = AIColor4D.create();
                if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_DIFFUSE,
                        aiTextureType_NONE, 0, mDiffuseColor) != 0) {
                    throw new IllegalStateException(aiGetErrorString());
                }
                mSpecularColor = AIColor4D.create();
                if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_SPECULAR,
                        aiTextureType_NONE, 0, mSpecularColor) != 0) {
                    throw new IllegalStateException(aiGetErrorString());
                }
            }
        }
    }
}
