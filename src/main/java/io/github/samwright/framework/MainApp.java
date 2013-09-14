package io.github.samwright.framework;

import io.github.samwright.framework.controller.MainWindowController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 18:15
 */
public class MainApp extends Application {

    public final static BeanFactory beanFactory;
    @Getter private static Stage stage;

    static{
        beanFactory = new ClassPathXmlApplicationContext("/META-INF/beans.xml");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MainApp.stage = stage;
        Parent root = new MainWindowController();
        Scene scene = new Scene(root, 500, 600);

        stage.setTitle("Workflow Manager");
        stage.setScene(scene);
        stage.show();
    }
}
