package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Profile;
import application.model.User;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class ProfileController extends BorderPane {

	@FXML
	private Button cancel;

	@FXML
	private Button change;

	@FXML
	private PasswordField newPassword;

	@FXML
	private DatePicker birthDate;

	@FXML
	private RadioButton VIPType;

	@FXML
	private PasswordField currentPassword;

	@FXML
	private Label errorText;

	@FXML
	private TextField surname;

	@FXML
	private TextField name;

	@FXML
	private PasswordField confirmPassword;

	@FXML
	private RadioButton standardType;

	@FXML
	private TextField email;

	@FXML
	private TextField username;

	private FXMLLoader loader;

	private User user;

	private Profile profile;

	private boolean initialTypeProfile;

	private DBManager dbManager;

	public ProfileController(User user, Profile profile) {
		super();
		this.profile = profile;
		this.user = user;
		initialTypeProfile = profile.isTypeProfile();
		loader = new FXMLLoader(getClass().getResource("/application/view/ProfileView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}

	@FXML
	public void initialize() {
		birthDate.setEditable(false);
		dbManager = DBManager.getInstance();
		if (!profile.isTypeProfile()) {
			standardType.setSelected(true);
			initialTypeProfile = false;
		} else {
			VIPType.setSelected(true);
			initialTypeProfile = true;
		}
		email.setText(user.getEmail());
		name.setText(user.getName());
		surname.setText(user.getSurname());
		birthDate.setValue(LOCAL_DATE(user.getBirthDate()));
		username.setText(user.getUsername());
	}

	public void updateName() {
		if (name.getText().isEmpty() || name.getText() == null)
			errorText.setText("Campo nome vuoto");
		if (!name.getText().equals(user.getName())) {
			dbManager.startConnection();
			dbManager.updateName(user, name.getText());
			user.setName(name.getText());
			dbManager.closedConnection();
		}
	}

	public void updateSurname() {
		if (surname.getText().isEmpty() || surname.getText() == null)
			errorText.setText("Campo cognome vuoto");
		if (!surname.getText().equals(user.getSurname())) {
			dbManager.startConnection();
			dbManager.updateSurname(user, surname.getText());
			user.setSurname(surname.getText());
			dbManager.closedConnection();
		}
	}

	public void updatePassword() {
		dbManager.startConnection();
		if (!dbManager.updatePassword(profile, currentPassword.getText().toString(), newPassword.getText().toString())
				&& currentPassword.getText() == null)
			errorText.setText("Errore password");
		if (!passwordCheck())
			errorText.setText("La password La password deve essere composta da almeno 8 caratteri," + '\n'
					+ "deve contenere almeno una lettera maiuscola, una minuscola e un numero");
		dbManager.closedConnection();
	}

	public boolean updateVIP() {
		if (VIPType.isSelected() && !profile.isTypeProfile()) {
			return true;
		}
		return false;
	}

	public boolean updateStandard() {
		if (standardType.isSelected() && profile.isTypeProfile())
			return true;
		return false;
	}

	public boolean passwordCheck() {
		if (newPassword.getText().isEmpty() || newPassword.getText() == null)
			return false;
		if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>\\.]).{8,20}$",
				newPassword.getText()))
			return false;
		return true;
	}

	public void change() {
		updateName();
		updateSurname();
		updatePassword();
		HomeHandler home = HomeHandler.getInstance();
		if (!initialTypeProfile && updateVIP()) {
			PaymentController payment = new PaymentController(user, profile, false);
			home.addView(payment);
			return;
		} else if (initialTypeProfile && updateStandard()) {
			if (profile.getDate() != null) {
				dbManager.startConnection();
				if (!profile.isTypeVIP() && LOCAL_DATE(profile.getDate()).plusMonths(1).isBefore(LocalDate.now()))
					profile.setTypeProfile(false);
				else if (!profile.isTypeVIP()
						&& !LOCAL_DATE(profile.getDate()).plusMonths(1).isBefore(LocalDate.now())) {
					LocalDate date = LOCAL_DATE(profile.getDate()).plusMonths(1);
					Alert alert = new Alert(AlertType.INFORMATION, "Il tuo abbonamento scade: " + date.plusDays(1),
							ButtonType.OK);
					Utilities.getIstance().setStyleAlert(alert);
					alert.showAndWait();
				} else if (profile.isTypeVIP()
						&& !LOCAL_DATE(profile.getDate()).plusYears(1).isBefore(LocalDate.now())) {
					LocalDate date = LOCAL_DATE(profile.getDate()).plusYears(1);
					Alert alert = new Alert(AlertType.INFORMATION, "Il tuo abbonamento scade: " + date.plusDays(1),
							ButtonType.OK);
					Utilities.getIstance().setStyleAlert(alert);
					alert.showAndWait();
				} else if (profile.isTypeVIP()
						&& LOCAL_DATE(profile.getDate()).plusYears(1).isBefore(LocalDate.now())) {
					profile.setTypeProfile(false);
				}
			}
			dbManager.updateTypeProfile(profile);
		}
		CenterHomeController centerHome = new CenterHomeController(profile);
		home.addView(centerHome);
		dbManager.updateDate(profile);
		dbManager.closedConnection();
	}

	@FXML
	void confirmOnKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			change();
	}

	@FXML
	void confirm(ActionEvent event) {
		change();
	}

	@FXML
	void cancel(ActionEvent event) {
		HomeHandler home = HomeHandler.getInstance();
		home.goBack();
	}

	@FXML
	void checkType(ActionEvent event) {
		if (((RadioButton) event.getSource()).getId().equals("standardType"))
			VIPType.setSelected(false);
		if (((RadioButton) event.getSource()).getId().equals("VIPType"))
			standardType.setSelected(false);
	}

	@FXML
	void showMore(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION, "Con l'abbonamento VIP hai accesso a tante offerte esclusive!"
				+ '\n' + "L'abbonamento VIP ha un costo di 2€ mesile o 20€ annuali!", ButtonType.CLOSE);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/mainStyle.css").toString());
		alert.getDialogPane().getStyleClass().add("alert");
		alert.showAndWait();
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
