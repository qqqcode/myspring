package com.qqqopengl.util;

import com.qqqopengl.graphic.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private static TextureCache instance;

    private static Map<String, Texture> cache;
    public static TextureCache getInstance() {
        if (instance == null) {
            instance = new TextureCache();
            cache = new HashMap<>();
        }
        return instance;
    }

    public Texture getTexture(String path) {
        Map<String, Texture> cache = getInstance().cache;
        Texture texture = cache.get(path);
        if (texture == null) {
            texture = Texture.loadTexture(path);
            cache.put(path,texture);
        }
        return texture;
    }
}
