package com.designPattern.observer;

import java.util.Observable;

/**
 * @author Johnson
 * 2021/9/10
 */
public class QqqObserable extends Observable implements Runnable{

    private static QqqObserable instance;

    private static volatile int count = 1;

    public static QqqObserable init() {
        if (instance == null) {
            instance = new QqqObserable();
        }
        QqqObserver qqqObserver = new QqqObserver();
        instance.addObserver(qqqObserver);
        return instance;
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            count++;
            instance.setChanged();
            instance.notifyObservers(count);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count == 100) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        QqqObserable qqqObserable =QqqObserable.init();
        Thread thread = new Thread(qqqObserable);
        thread.start();
    }
}
