package com.qqqopengl;

public class QqqGameEngine implements Runnable{

    private final Thread gameLoopThread;

    private QqqIGameLogic gameLogic;

    public QqqGameEngine(String windowTitle, int width, int height, boolean vsSync, QqqIGameLogic gameLogic) {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        QqqWindow window = new QqqWindow();
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {

    }
}
