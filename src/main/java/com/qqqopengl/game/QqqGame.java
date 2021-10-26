package com.qqqopengl.game;

import com.qqqopengl.graphic.*;
import com.qqqopengl.listener.KeyListener;
import com.qqqopengl.util.Constant;
import com.qqqopengl.util.ShaderUtil;
import org.javatuples.Triplet;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.qqqopengl.game.QqqGame.Direction.UP;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class QqqGame {

    public enum GameState {
        GAME_ACTIVE,
        GAME_MENU,
        GAME_WIN
    }

    public enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }

    public GameState state;
    int width, height;
    SpriteRenderer renderer;
    List<GameLevel> levels;
    int level;
    Vector2f PLAYER_SIZE = new Vector2f(100.0f, 20.0f);
    float PLAYER_VELOCITY = 500.0f;

    Vector2f INITIAL_BALL_VELOCITY = new Vector2f(100.0f, -200.0f);
    float BALL_RADIUS = 12.5f;

    float shakeTime = 0.0f;

    GameObject player;
    BallObject ball;
    ParticleGenerator particle;
    PostProcessor effects;

    VertexArrayObject quadVAO;

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        state = state;
    }

    public QqqGame(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init() throws IOException {
        ResourceManager.loadShader(Constant.resources + "sprite.vert", Constant.resources + "sprite.frag", "sprite");

        ResourceManager.loadShader(Constant.resources + "particle.vert", Constant.resources + "particle.frag", "particle");

        ResourceManager.loadShader(Constant.resources + "post_processing.vert", Constant.resources + "post_processing.frag", "postprocessing");

        ResourceManager.loadShader(Constant.resources + "vs/screen.vs", Constant.resources + "frag/screen.frag","screen");

        Matrix4f projection = new Matrix4f().ortho(0.0f, width, height, 0.0f, -1.0f, 1.0f);

        ResourceManager.getShader("sprite").use();
        ResourceManager.getShader("sprite").setUniform("image", 0);
        ResourceManager.getShader("sprite").setUniform("projection", projection);
        ResourceManager.getShader("particle").use();
        ResourceManager.getShader("particle").setUniform("sprite", 0);
        ResourceManager.getShader("particle").setUniform("projection", projection);


        ResourceManager.loadTexture(Constant.resources + "textures/particle.png", "particle");
        ResourceManager.loadTexture(Constant.resources + "textures/background.jpg", "background");
        ResourceManager.loadTexture(Constant.resources + "textures/awesomeface.png", "face");
        ResourceManager.loadTexture(Constant.resources + "textures/block.png", "block");
        ResourceManager.loadTexture(Constant.resources + "textures/block_solid.png", "block_solid");
        ResourceManager.loadTexture(Constant.resources + "textures/paddle.png", "paddle");

        renderer = new SpriteRenderer(ResourceManager.getShader("sprite"));
        particle = new ParticleGenerator(ResourceManager.getShader("particle"), ResourceManager.getTexture("particle"), 500);
        effects = new PostProcessor(ResourceManager.getShader("postprocessing"), this.width, this.height);

        GameLevel gameLevelOne = new GameLevel();
        gameLevelOne.load(Constant.resources + "levels/one.lvl", this.width, this.height / 2);
        GameLevel gameLevelTwo = new GameLevel();
        gameLevelTwo.load(Constant.resources + "levels/two.lvl", this.width, this.height / 2);
        GameLevel gameLevelThree = new GameLevel();
        gameLevelThree.load(Constant.resources + "levels/three.lvl", this.width, this.height / 2);
        GameLevel gameLevelFour = new GameLevel();
        gameLevelFour.load(Constant.resources + "levels/four.lvl", this.width, this.height / 2);

        this.levels = new ArrayList<>();
        this.levels.add(gameLevelOne);
        this.levels.add(gameLevelTwo);
        this.levels.add(gameLevelThree);
        this.levels.add(gameLevelFour);
        this.level = 0;
        Vector2f playerPos = new Vector2f(this.width / 2.0f - PLAYER_SIZE.x / 2.0f, this.height - PLAYER_SIZE.y);
        player = new GameObject(playerPos, PLAYER_SIZE, ResourceManager.getTexture("paddle"));
        this.state = GameState.GAME_ACTIVE;

        Vector2f ballPos = playerPos.add(new Vector2f(PLAYER_SIZE.x / 2.0f - BALL_RADIUS, -BALL_RADIUS * 2.0f), new Vector2f());
        ball = new BallObject(ballPos, BALL_RADIUS, INITIAL_BALL_VELOCITY, ResourceManager.getTexture("face"));
    }

    public void processInput(float dt, QqqWindow qqqWindow) {
        if (this.state == GameState.GAME_ACTIVE) {
            float velocity = PLAYER_VELOCITY * dt;
            if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                if (player.position.x >= 0.0f) {
                    player.position.x -= velocity;
                    if (ball.stuck) {
                        ball.position.x -= velocity;
                    }
                }
            }
            if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                if (player.position.x <= this.width - player.size.x) {
                    player.position.x += velocity;
                    if (ball.stuck) {
                        ball.position.x += velocity;
                    }
                }
            }
            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                ball.stuck = false;
            }
            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                qqqWindow.close();
            }
        }
    }

    public void update(float dt) throws IOException {
        ball.move(dt, this.width);
        this.doCollisions();
        if (ball.position.y >= this.height) {
            this.resetLevel();
            this.resetPlayer();
        }
        particle.update(dt, ball, 2, new Vector2f(ball.radius / 2.0f));
        if (shakeTime > 0.0f) {
            shakeTime -= dt;
            if (shakeTime <= 0.0f)
                effects.shake = false;
        }
    }

    void resetLevel() throws IOException {
        if (this.level == 0) {
            this.levels.get(0).load(Constant.resources + "levels/one.lvl", this.width, this.height / 2);
        } else if (this.level == 1) {
            this.levels.get(1).load(Constant.resources + "levels/two.lvl", this.width, this.height / 2);
        } else if (this.level == 2) {
            this.levels.get(2).load(Constant.resources + "levels/three.lvl", this.width, this.height / 2);
        } else if (this.level == 3) {
            this.levels.get(3).load(Constant.resources + "levels/four.lvl", this.width, this.height / 2);
        }
    }

    void resetPlayer() {
        player.size = PLAYER_SIZE;
        player.position = new Vector2f(this.width / 2.0f - PLAYER_SIZE.x / 2.0f, this.height - PLAYER_SIZE.y);
        ball.reset(player.position.add(new Vector2f(PLAYER_SIZE.x / 2.0f - BALL_RADIUS, -(BALL_RADIUS * 2.0f)), new Vector2f()),
                INITIAL_BALL_VELOCITY);
    }

    public void doCollisions() {
        List<GameObject> bricks = this.levels.get(this.level).bricks;
        for (GameObject brick : bricks) {
            if (!brick.destroyed) {
                Triplet<Boolean, Direction, Vector2f> collision = checkCollision(ball, brick);
                if (collision.getValue0()) {
                    if (!brick.isSolid) {
                        brick.destroyed = true;
                        shakeTime = 0.5f;
                        effects.shake = true;
                    }
                    Direction dir = collision.getValue1();
                    Vector2f diff_vector = collision.getValue2();
                    if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                        ball.velocity.x = -ball.velocity.x;
                        float penetration = ball.radius - Math.abs(diff_vector.x);
                        if (dir == Direction.LEFT)
                            ball.position.x += penetration;
                        else
                            ball.position.x -= penetration;
                    } else {
                        ball.velocity.y = -ball.velocity.y;
                        float penetration = ball.radius - Math.abs(diff_vector.y);
                        if (dir == UP) {
                            ball.position.y -= penetration;
                        } else {
                            ball.position.y += penetration;
                        }
                    }
                }
            }
        }
        Triplet<Boolean, Direction, Vector2f> result = checkCollision(ball, player);
        if (ball.stuck && result.getValue0()) {

            float centerBoard = player.position.x + player.size.x / 2.0f;
            float distance = (ball.position.x + ball.radius) - centerBoard;
            float percentage = distance / (player.size.x / 2.0f);

            float strength = 2.0f;
            Vector2f oldVelocity = ball.velocity;
            ball.velocity.x = INITIAL_BALL_VELOCITY.x * percentage * strength;
            ball.velocity = ball.velocity.normalize(new Vector2f()).mul(oldVelocity.length(), new Vector2f());
            ball.velocity.y = -1.0f * Math.abs(ball.velocity.y);
        }
    }

    public boolean checkCollision(GameObject one, GameObject two) {
        boolean collisionX = one.position.x + one.size.x >= two.position.x &&
                two.position.x + two.size.x >= one.position.x;

        boolean collisionY = one.position.y + one.size.y >= two.position.y &&
                two.position.y + two.size.y >= one.position.y;

        return collisionX && collisionY;
    }

    Triplet<Boolean, Direction, Vector2f> checkCollision(BallObject one, GameObject two) {
        Vector2f center = new Vector2f(one.position.x + one.radius, one.position.y + one.radius);

        Vector2f aabb_half_extents = new Vector2f(two.size.x / 2.0f, two.size.y / 2.0f);
        Vector2f aabb_center = new Vector2f(two.position.x + aabb_half_extents.x, two.position.y + aabb_half_extents.y);

        Vector2f difference = center.sub(aabb_center, new Vector2f());
        Vector2f clamped = new Vector2f(Math.max(-aabb_half_extents.x, Math.min(aabb_half_extents.x, difference.x)),
                Math.max(-aabb_half_extents.y, Math.min(aabb_half_extents.y, difference.y)));

        Vector2f closest = aabb_center.add(clamped, new Vector2f());

        difference = closest.sub(center, new Vector2f());

        if (difference.length() < one.radius) {
            return Triplet.with(true, vectorDirection(difference), difference);
        } else {
            return Triplet.with(false, UP, new Vector2f(0.0f, 0.0f));
        }
    }

    Direction vectorDirection(Vector2f target) {
        Vector2f[] compass = {
                new Vector2f(0.0f, 1.0f),    // up
                new Vector2f(1.0f, 0.0f),    // right
                new Vector2f(0.0f, -1.0f),    // down
                new Vector2f(-1.0f, 0.0f)    // left
        };
        float max = 0.0f;
        int best_match = -1;
        for (int i = 0; i < 4; i++) {
            float dot_product = target.normalize(new Vector2f()).dot(compass[i]);
            if (dot_product > max) {
                max = dot_product;
                best_match = i;
            }
        }
        return Direction.values()[best_match];
    }

    public void render() {
        if (this.state == GameState.GAME_ACTIVE) {

            effects.beginRender();

            renderer.drawSprite(ResourceManager.getTexture("background"), new Vector2f(0.0f, 0.0f), new Vector2f(this.width, this.height), 0.0f, new Vector3f(1));
            //renderer.drawSprite(ResourceManager.getTexture("face"), new Vector2f(200, 200), new Vector2f(300, 400), 45.0f, new Vector3f(0.0f, 1.0f, 0.0f), width, height);
            this.levels.get(this.level).draw(renderer);
            player.draw(renderer);
            particle.draw();
            ball.draw(renderer);

            effects.endRender();
            effects.render((float) glfwGetTime());
        }
    }
}
