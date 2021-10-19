package com.qqqopengl.game;

import com.qqqopengl.graphic.Shader;
import com.qqqopengl.graphic.ShaderProgram;
import com.qqqopengl.graphic.Texture;
import com.qqqopengl.util.ShaderUtil;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    public static Map<String, ShaderProgram> shaders = new HashMap<>();
    public static Map<String, Texture> textures = new HashMap<>();

    public static ShaderProgram loadShader(String vertexShaderName, String fragmentShaderName, String name){
        ShaderProgram shaderProgram = ShaderUtil.createShaderProgram(vertexShaderName, fragmentShaderName);
        shaders.put(name,shaderProgram);
        return shaderProgram;
    }
    public static ShaderProgram getShader(String name){
        ShaderProgram shader = shaders.get(name);
        if (shader ==null) {
            throw new RuntimeException("不存在");
        }
        return shader;
    }

    public static Texture loadTexture(String path,String name){
        Texture texture = Texture.loadTexture(path);
        textures.put(name,texture);
        return texture;
    }
    public static Texture getTexture(String name) {
        Texture texture = textures.get(name);
        if (texture ==null) {
            throw new RuntimeException("不存在");
        }
        return texture;
    }

    public static void clear(){
        for (ShaderProgram value : shaders.values()) {
            value.delete();
        }

        for (Texture value : textures.values()) {
            value.delete();
        }

        shaders.clear();
        textures.clear();
    }
    private ResourceManager() {}
}
