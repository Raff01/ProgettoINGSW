package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Pattern;

import application.model.DBManager;
import application.model.LoginHandler;
import application.model.Profile;
import application.model.User;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class SignUpController extends BorderPane {

	@FXML
	private PasswordField password;

	@FXML
	private TextField surname;

	@FXML
	private TextField name;

	@FXML
	private PasswordField confirmPassword;

	@FXML
	private RadioButton standardType;

	@FXML
	private DatePicker birthDate;

	@FXML
	private TextField email;

	@FXML
	private RadioButton VIPType;

	@FXML
	private TextField username;

	@FXML
	private Label errorText;

	private static String errorEmpty = "Campo vuoto";

	private static String errorEmail = "Email non valida";

	private static String errorPassword = "Password errata";

	private static String differentPassword = "Password diverse";

	private static String errorDate = "Devi avere almeno 18 anni per utilizzare la piattaforma";

	private FXMLLoader loader;

	private DBManager dbManager;

	public SignUpController() {
		loader = new FXMLLoader(getClass().getResource("/application/view/SignUpView.fxml"));
		loader.setController(this);
		try {
			AnchorPane root = (AnchorPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean existingEmail() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<String> emails = new ArrayList<String>();
		emails = dbManager.getAllEmail();
		for (int i = 0; i < emails.size(); i++) {
			if (email.getText().equals(emails.get(i))) {
				dbManager.closedConnection();
				return true;
			}
		}
		dbManager.closedConnection();
		return false;
	}

	public boolean existingUsername() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<String> usernames = new ArrayList<String>();
		usernames = dbManager.getAllUsername();
		for (int i = 0; i < usernames.size(); i++) {
			if (username.getText().equals(usernames.get(i))) {
				dbManager.closedConnection();
				return true;
			}
		}
		dbManager.closedConnection();
		return false;
	}

	@FXML
	public void signUp() {
		if (usernameCheck().equals(errorEmpty) || nameCheck().equals(errorEmpty) || surnameCheck().equals(errorEmpty)
				|| dateCheck().equals(errorEmpty) || emailCheck().equals(errorEmpty)
				|| passwordCheck().equals(errorEmpty) || confirmPasswordCheck().equals(errorEmpty)
				|| radioButtonCheck().equals(errorEmpty)) {
			errorText.setText("Compilare tutti i campi");
			return;
		}
		if (dateCheck().equals(errorDate)) {
			errorText.setText(errorDate);
			return;
		}
		if (passwordCheck().equals(errorPassword)) {
			errorText.setText(errorPassword);
		}
		if (confirmPasswordCheck().equals("Password diverse")) {
			errorText.setText("Password errate");
			return;
		}
		if (emailCheck().equals(errorEmail)) {
			errorText.setText("Email non valida");
			return;
		}
		if (existingEmail()) {
			errorText.setText("Email esistente");
		}
		if (existingUsername())
			errorText.setText("Username esistente");
		if (passwordCheck().equals(errorPassword)) {
			errorText.setText("La password deve contenere almeno una lettera maiuscola, una minuscola e un numero");
			return;
		}
		if (confirmPasswordCheck().equals(differentPassword)) {
			errorText.setText(differentPassword);
			return;
		}

		if (VIPType.isSelected())
			selectVIP();
		if (standardType.isSelected()) {
			User u = new User(email.getText(), name.getText(), surname.getText(), birthDate.getValue().toString(),
					username.getText());
			dbManager = DBManager.getInstance();
			dbManager.startConnection();
			dbManager.insertUser(u);
			dbManager.insertProfile(new Profile(username.getText(), password.getText(), VIPType.isSelected()));
			dbManager.closedConnection();
			Alert alert = new Alert(AlertType.INFORMATION, "Registrazione effettuata con successo", ButtonType.OK);
			Utilities.getIstance().setStyleAlert(alert);
			alert.showAndWait();
			LoginHandler loginSceneHandler = LoginHandler.getIstance();
			loginSceneHandler.goLogin();
		}
	}

	@FXML
	void confirmOnKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			signUp();
	}

	@FXML
	void confirm(ActionEvent event) {
		signUp();
	}

	@FXML
	public void initialize() {
		standardType.setSelected(true);
		birthDate.setEditable(false);

	}

	@FXML
	void cancel(ActionEvent event) {
		LoginHandler loginSceneHandler = LoginHandler.getIstance();
		loginSceneHandler.goBack();
	}

	@FXML
	void showMore(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION, "Con l'abbonamento VIP hai accesso a tante offerte esclusive!"
				+ '\n' + "L'abbonamento VIP ha un costo di 1€ mesile o 10€ annuali!", ButtonType.CLOSE);
		Utilities.getIstance().setStyleAlert(alert);
		alert.showAndWait();
	}

	String usernameCheck() {
		if (username.getText().isEmpty() || username.getText() == null || username.getText().trim().isEmpty())
			return errorEmpty;
		return "";
	}

	String nameCheck() {
		if (name.getText().isEmpty() || name.getText() == null || name.getText().trim().isEmpty())
			return errorEmpty;
		return "";
	}

	String surnameCheck() {
		if (surname.getText().isEmpty() || surname.getText() == null || surname.getText().trim().isEmpty())
			return errorEmpty;
		return "";
	}

	String dateCheck() {
		if (birthDate.getValue() == null)
			return errorEmpty;
		if (birthDate.getValue().isAfter(LocalDate.now().minusYears(18)))
			return errorDate;
		return "";
	}

	String emailCheck() {
		if (email.getText().isEmpty() || email.getText() == null)
			return errorEmpty;
		if (!Pattern.matches("([a-zA-Z]+|[0-9]*|[\\.|_|-|]*)+@([a-zA-Z]+\\.)+(com|gov|it)", email.getText()))
			return errorEmail;
		return "";
	}

	String passwordCheck() {
		if (password.getText().isEmpty() || password.getText() == null)
			return errorEmpty;
		if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>\\.]).{8,20}$",
				password.getText()))
			return errorPassword;
		return "";
	}

	String confirmPasswordCheck() {
		if (confirmPassword.getText().isEmpty() || confirmPassword.getText() == null)
			return errorEmpty;
		else if (!confirmPassword.getText().equals(password.getText()))
			return differentPassword;
		return "";
	}

	@FXML
	void checkType(ActionEvent event) {
		if (((RadioButton) event.getSource()).getId().equals("standardType"))
			VIPType.setSelected(false);
		if (((RadioButton) event.getSource()).getId().equals("VIPType"))
			standardType.setSelected(false);
	}

	String radioButtonCheck() {
		if (!standardType.isSelected() && !VIPType.isSelected())
			return errorEmpty;
		return "";
	}

	void selectVIP() {
		User u = new User(email.getText(), name.getText(), surname.getText(), birthDate.getValue().toString(),
				username.getText());
		Profile p = new Profile(username.getText(), password.getText(), VIPType.isSelected());
		PaymentController paymentController = new PaymentController(u, p, true);
		Scene scene = new Scene(paymentController);
		LoginHandler loginSceneHandler = LoginHandler.getIstance();
		loginSceneHandler.addScene(scene);
	}

	@FXML
	void back(MouseEvent event) {
		errorText.setText("Inserisci le tue credenziali");
	}

	@FXML
	void information(MouseEvent event) {
		errorText.setText("La password deve essere composta da almeno 8 caratteri," + '\n'
				+ "deve contenere almeno una lettera maiuscola, una minuscola e un numero");
	}
}