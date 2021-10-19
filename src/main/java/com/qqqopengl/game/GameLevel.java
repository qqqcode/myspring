package com.qqqopengl.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameLevel {
    public List<GameObject> bricks = new ArrayList<>();

    public GameLevel() {
    }

    public void load(String file, int levelWidth, int levelHeight) throws IOException {

        bricks.clear();

        List<List<Integer>> tileData = new ArrayList<>();
        BufferedReader buffer = new BufferedReader(new FileReader(file));
        String s = "";
        while ((s=buffer.readLine()) != null) {
            String[] nums = s.split(" ");
            List<Integer> lineData = new ArrayList<>();
            for (String num : nums) {
                Integer integer = Integer.valueOf(num);
                lineData.add(integer);
            }
            tileData.add(lineData);
        }

        if (tileData == null || tileData.isEmpty() || tileData.size() == 0) {
            return;
        }

        init(tileData,levelWidth,levelHeight);
    }

    void draw(SpriteRenderer renderer) {
        for (GameObject brick : this.bricks) {
            if (brick.destroyed) continue;
            brick.draw(renderer);
        }
    }

    boolean isCompleted() {
        for (GameObject brick : this.bricks) {
            if (!brick.isSolid&&!brick.destroyed) return false;
        }
        return true;
    }

    private void init(List<List<Integer>> tileData, int levelWidth, int levelHeight) {
        int height = tileData.size();
        int width = tileData.get(0).size();
        float unitWidth = levelWidth / width;
        float unitHeight = levelHeight / height;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Integer data = tileData.get(i).get(j);
                if (data.equals(1)) {
                    Vector2f pos = new Vector2f(unitWidth*j,unitHeight*i);
                    Vector2f size = new Vector2f(unitWidth,unitHeight);
                    this.bricks.add(new GameObject(pos, size, ResourceManager.getTexture("block_solid"), new Vector3f(0.8f, 0.8f, 0.7f)));
                } else if (data.compareTo(1) > 0) {
                    Vector3f color=  new Vector3f(1.0f);
                    if (data == 2)
                        color = new Vector3f(0.2f, 0.6f, 1.0f);
                    else if (data == 3)
                        color = new Vector3f(0.0f, 0.7f, 0.0f);
                    else if (data == 4)
                        color = new Vector3f(0.8f, 0.8f, 0.4f);
                    else if (data == 5)
                        color = new Vector3f(1.0f, 0.5f, 0.0f);

                    Vector2f pos = new Vector2f(unitWidth * i, unitHeight * j);
                    Vector2f size = new Vector2f(unitWidth, unitHeight);
                    this.bricks.add(new GameObject(pos, size, ResourceManager.getTexture("block"), color));
                }
            }
        }
    }
}
