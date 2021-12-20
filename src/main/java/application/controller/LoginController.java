package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCrypt;
import application.model.DBManager;
import application.model.EmailSender;
import application.model.HomeStage;
import application.model.LoginHandler;
import application.model.User;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class LoginController extends BorderPane {

	@FXML
	private PasswordField password;

	@FXML
	private Label errorMessage;

	@FXML
	private TextField username;

	private FXMLLoader loader;

	private DBManager dbManager;

	private String recover;

	private EmailSender email;
	public LoginController() {
		super();
		dbManager = DBManager.getInstance();
		loader = new FXMLLoader(getClass().getResource("/application/view/LoginView.fxml"));
		loader.setController(this);
		try {
			AnchorPane root = (AnchorPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean credentialCheck() {
		dbManager.startConnection();
		if (dbManager.getEmail(username.getText()) != null) {
			String pw = dbManager.getPassword(username.getText());
			dbManager.closedConnection();
			if (BCrypt.checkpw(password.getText(), pw))
				return true;
		}
		return false;
	}

	public boolean check() {
		if (credentialCheck()) {
			return true;
		}
		return false;
	}

	public void openHome() {
		if (check()) {
			dbManager.startConnection();
			User u = dbManager.getUser(username.getText());
			dbManager.closedConnection();
			HomeStage home = new HomeStage(u);
		} else {
			errorMessage.setText("Credenziali errate!");
		}
	}

	@FXML
	void login(ActionEvent event) {
		openHome();
	}

	@FXML
	void loginFromKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			openHome();
	}

	@FXML
	void recoverPassword(MouseEvent event) {
		EmailThread t = new EmailThread();
		TextInputDialog dialog = new TextInputDialog();
		dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/mainStyle.css").toString());
		dialog.getDialogPane().getStyleClass().add("alert");
		dialog.setTitle("Recupero password");
		dialog.setHeaderText("Inserisci l'username per recuperare la password");
		dialog.setContentText("Username:");
		Optional<String> result = dialog.showAndWait();
		String us = "";
		if (result.isPresent()) {
			us = result.get();
		}
		dbManager.startConnection();
		ArrayList<String> username = dbManager.getAllUsername();
		if (username.contains(us)) {
			recover = us;
			email = new EmailSender(recover);
			dbManager.closedConnection();
			t.start();
			Alert alert = new Alert(AlertType.CONFIRMATION, "Email inviata", ButtonType.OK);
			Utilities.getIstance().setStyleAlert(alert);
			alert.showAndWait();
			RecoverPasswordController recoverPass = new RecoverPasswordController(email, recover);
			Scene scene = new Scene(recoverPass);
			LoginHandler loginSceneHandler = LoginHandler.getIstance();
			loginSceneHandler.addScene(scene);
		} else if (!us.isEmpty() && us != null) {
			Alert alert = new Alert(AlertType.ERROR, "Non è stato trovato nessun utente con questo username",
					ButtonType.OK);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/mainStyle.css").toString());
			alert.getDialogPane().getStyleClass().add("alert");
			alert.showAndWait();
		}
	}

	@FXML
	void signUp(ActionEvent event) {
		SignUpController signUpController = new SignUpController();
		Scene scene = new Scene(signUpController);
		LoginHandler loginSceneHandler = LoginHandler.getIstance();
		loginSceneHandler.addScene(scene);
	}

	private class EmailThread extends Thread {

		@Override
		public void run() {
			super.run();
			if(!dbManager.isEnstablished())
				dbManager.startConnection();
			email.sendEmail("animalshopigpe@gmail.com", "Igpe2021!", dbManager.getEmail(recover));
			dbManager.closedConnection();
		}

	}

}
