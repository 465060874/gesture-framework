package io.github.samwright.framework.controller.helper;

import javafx.application.Platform;

import java.util.LinkedList;
import java.util.Queue;

/**
 * User: Sam Wright Date: 06/09/2013 Time: 19:15
 */
public abstract class DelayedNotifier<T> {

    private final Queue<T> queue = new LinkedList<>();

    public void notify(T newObject) {
        synchronized (queue) {
            queue.add(newObject);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                synchronized (queue) {
                    handleNewObject(queue.poll());
                }
            }
        });

    }

    public abstract void handleNewObject(T newObject);

}
