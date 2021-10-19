package com.qqqopengl.game;

import com.qqqopengl.graphic.ShaderProgram;
import com.qqqopengl.util.Constant;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class QqqGame {

    public enum GameState {
        GAME_ACTIVE,
        GAME_MENU,
        GAME_WIN
    }

    public GameState state;
    int width, height;
    SpriteRenderer renderer;
    GameObject player;
    List<GameLevel> levels;
    int level = 0;
    Vector2f PLAYER_SIZE = new Vector2f(100.0f, 20.0f);
    float PLAYER_VELOCITY = 500.0f;

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        state = state;
    }

    boolean[] keys = new boolean[1024];

    public QqqGame(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init() throws IOException {
        ResourceManager.loadShader(Constant.resources + "sprite.vert", Constant.resources + "sprite.frag", "sprite");
        renderer = new SpriteRenderer(ResourceManager.getShader("sprite"));

        ResourceManager.loadTexture(Constant.resources + "textures/background.jpg", "background");
        ResourceManager.loadTexture(Constant.resources + "textures/awesomeface.png", "face");
        ResourceManager.loadTexture(Constant.resources + "textures/block.png", "block");
        ResourceManager.loadTexture(Constant.resources + "textures/block_solid.png", "block_solid");
        ResourceManager.loadTexture(Constant.resources + "textures/paddle.png", "paddle");

        GameLevel gameLevelOne = new GameLevel();
        gameLevelOne.load(Constant.resources + "levels/one.lvl", this.width, this.height / 2);
        GameLevel gameLevelTwo = new GameLevel();
        gameLevelTwo.load(Constant.resources + "levels/two.lvl", this.width, this.height / 2);
        GameLevel gameLevelThree = new GameLevel();
        gameLevelThree.load(Constant.resources + "levels/three.lvl", this.width, this.height / 2);
        GameLevel gameLevelFour = new GameLevel();
        gameLevelFour.load(Constant.resources + "levels/four.lvl", this.width, this.height / 2);

        levels = new ArrayList<>();
        levels.add(gameLevelOne);
        levels.add(gameLevelTwo);
        levels.add(gameLevelThree);
        levels.add(gameLevelFour);

        Vector2f playerPos = new Vector2f(this.width / 2.0f - PLAYER_SIZE.x / 2.0f, this.height - PLAYER_SIZE.y);
        player = new GameObject(playerPos, PLAYER_SIZE, ResourceManager.getTexture("paddle"));
        this.state = GameState.GAME_ACTIVE;
    }

    public void ProcessInput(float dt) {
        if (this.state == GameState.GAME_ACTIVE) {
            float velocity = PLAYER_VELOCITY * dt;

            if (this.keys[GLFW_KEY_A]) {
                if (player.position.x >= 0.0f)
                    player.position.x -= velocity;
            }
            if (this.keys[GLFW_KEY_D]) {
                if (player.position.x <= this.width - player.size.x)
                    player.position.x += velocity;
            }
        }
    }

    public void Update(float dt) {
    }

    public void render() {
        if (this.state == GameState.GAME_ACTIVE) {

            renderer.drawSprite(ResourceManager.getTexture("background"), new Vector2f(0.0f, 0.0f), new Vector2f(this.width, this.height), 0.0f, new Vector3f(), width, height);
            //renderer.drawSprite(ResourceManager.getTexture("face"), new Vector2f(200, 200), new Vector2f(300, 400), 45.0f, new Vector3f(0.0f, 1.0f, 0.0f), width, height);
            // draw level
//            this.levels.get(this.level).draw(renderer);
            // draw player
//            player.draw(renderer);
        }
    }
}
