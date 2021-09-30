package com.qqqopengl;

import com.qqqopengl.graphic.QqqWindow;

public interface QqqIGameLogic {

    void init() throws Exception;

    void input(QqqWindow window);

    void update(float interval);

    void render(QqqWindow window);
}
