package com.yykj.kepw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KepwApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(KepwApplication.class.getResource("kepw-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 210);
        stage.setTitle("KETTLE-DBPWDTOOLS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}