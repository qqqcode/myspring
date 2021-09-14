package com.designPattern.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Johnson
 * 2021/9/10
 */
public class QqqObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        int count = (int)arg;
        System.out.println("observer find observable changed " + count);
    }
}
