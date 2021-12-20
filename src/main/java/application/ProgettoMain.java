package application;

import application.controller.LoginController;
import application.model.LoginHandler;
import application.model.StageHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ProgettoMain extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		StageHandler stageHandler=StageHandler.getIstance();
		stageHandler.addStage(primaryStage);
		LoginHandler loginSceneHandler= LoginHandler.getIstance();
		loginSceneHandler.setStage(primaryStage);
		LoginController loginController=new LoginController();
		Scene scene=new Scene(loginController);
		loginSceneHandler.addScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
