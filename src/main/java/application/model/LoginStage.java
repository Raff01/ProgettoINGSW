package application.model;

import application.controller.LoginController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginStage extends Stage {

	public LoginStage() {
		this.setMinHeight(400);
		this.setMinWidth(600);
		this.setResizable(false);
		this.getIcons().add(new Image(getClass().getResource("/Images/LogoIcon.png").toExternalForm()));
		this.setTitle("Animal Shop");
		StageHandler stageHandler = StageHandler.getIstance();
		stageHandler.closeStage();
		stageHandler.addStage(this);
		LoginController loginController = new LoginController();
		LoginHandler loginHandler = LoginHandler.getIstance();
		loginHandler.setStage(this);
		Scene scene = new Scene(loginController);
		loginHandler.addScene(scene);
	}
}