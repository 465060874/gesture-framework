package io.github.samwright.framework;

import io.github.samwright.framework.controller.MainWindowController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 18:15
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new MainWindowController();
        Scene scene = new Scene(root, 500, 600);

        stage.setTitle("Transductive Workflow Manager");
        stage.setScene(scene);
        stage.show();
    }
}
