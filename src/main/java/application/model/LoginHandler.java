package application.model;

import java.util.ArrayList;

import application.controller.LoginController;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginHandler {

	private ArrayList<Scene> scenes;
	private Stage stage;
	private static LoginHandler login;

	public LoginHandler() {
		scenes = new ArrayList<Scene>();
		stage = new Stage();
	}

	public static LoginHandler getIstance() {
		if (login == null)
			login = new LoginHandler();
		return login;
	}

	public void setStage(Stage s) {
		stage = s;
	}

	public void addScene(Scene s) {
		scenes.add(s);
		stage.setScene(s);
	}

	public void goBack() {
		if (scenes.size() > 1) {
			scenes.remove(scenes.size() - 1);
			stage.setScene(scenes.get(scenes.size() - 1));
		}
	}

	public void goLogin() {
		scenes.clear();
		LoginController loginController = new LoginController();
		Scene scene = new Scene(loginController);
		addScene(scene);
	}
}
