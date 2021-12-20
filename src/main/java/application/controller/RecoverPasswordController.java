package application.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import application.model.DBManager;
import application.model.EmailSender;
import application.model.LoginHandler;
import application.model.Profile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class RecoverPasswordController extends BorderPane {
	@FXML
	private TextField code;

	@FXML
	private PasswordField newPassword;

	@FXML
	private PasswordField confirmPassword;

	@FXML
	private Label errorText;

	private FXMLLoader loader;

	private EmailSender emailSender;

	private String us;

	public RecoverPasswordController(EmailSender emailSender, String us) {
		super();
		this.emailSender = emailSender;
		this.us = us;
		loader = new FXMLLoader(getClass().getResource("/application/view/RecoverPasswordView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void cancel(ActionEvent event) {
		LoginHandler login = LoginHandler.getIstance();
		login.goBack();
	}

	@FXML
	void confirm(ActionEvent event) {
		if (emailSender.getPass().equals(code.getText())) {
			if (newPassword.getText().equals(confirmPassword.getText()) && passwordCheck()) {
				DBManager dbManager = DBManager.getInstance();
				dbManager.startConnection();
				dbManager.changePassword(us, newPassword.getText());
				dbManager.closedConnection();
				LoginController login = new LoginController();
				Scene scene = new Scene(login);
				LoginHandler loginHandler = LoginHandler.getIstance();
				loginHandler.addScene(scene);
			} else if (!newPassword.getText().equals(confirmPassword.getText())) {
				errorText.setText("Password diverse");
			} else if (!passwordCheck())
				errorText.setText("La password La password deve essere composta da almeno 8 caratteri," + '\n'
						+ "deve contenere almeno una lettera maiuscola, una minuscola e un numero");
		} else
			errorText.setText("Codice errato");
	}

	public boolean passwordCheck() {
		if (newPassword.getText().isEmpty() || newPassword.getText() == null)
			return false;
		if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>\\.]).{8,20}$",
				newPassword.getText()))
			return false;
		return true;
	}

	@FXML
	void back(MouseEvent event) {
		errorText.setText("");
	}

	@FXML
	void information(MouseEvent event) {
		errorText.setText("La password deve essere composta da almeno 8 caratteri," + '\n'
				+ "deve contenere almeno una lettera maiuscola, una minuscola e un numero");
	}
}
