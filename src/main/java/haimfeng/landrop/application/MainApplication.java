package haimfeng.landrop.application;

import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/haimfeng/landrop/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle(AppConstants.APP_NAME);
        stage.setScene(scene);
        MainController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        AppConstants.resetToDefaults();
        if (args.length > 0) {
            AppConstants.setDebugConstants(args);
        }
        launch();
    }
}