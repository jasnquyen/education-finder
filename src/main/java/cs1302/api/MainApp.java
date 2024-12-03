package cs1302.api;

import cs1302.api.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Handles creation of GUI window.
 *
 */

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Controller mainController = new Controller();
        Scene scene = new Scene(mainController.getLayout(), 800, 600);

        primaryStage.setTitle("Education Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
