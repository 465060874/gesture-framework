package io.github.samwright.framework.controller.helper;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 12:39
 */
public class Controllers {

    public static void bindViewToController(String fxmlResource, Object controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(Controllers.class.getResource(fxmlResource));

        fxmlLoader.setRoot(controller);
        fxmlLoader.setController(controller);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
