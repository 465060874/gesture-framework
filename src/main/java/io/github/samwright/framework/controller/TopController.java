package io.github.samwright.framework.controller;

/**
 * User: Sam Wright Date: 15/07/2013 Time: 19:39
 */
public class TopController {
    private static TopController ourInstance = new TopController();

    public static TopController getInstance() {
        return ourInstance;
    }

    private TopController() {
    }
}
