package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.LoginHandler;
import application.model.Profile;
import application.model.User;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class PaymentController extends BorderPane {

	@FXML
	private TextField cvc;

	@FXML
	private RadioButton monthly;

	@FXML
	private RadioButton annual;

	@FXML
	private TextField cardHolder;

	@FXML
	private TextField cardNumber;

	@FXML
	private DatePicker expirationDate;

	@FXML
	private Label errorText;

	@FXML
	private Label priceText;

	private FXMLLoader loader;

	private User user;

	private Profile profile;

	private boolean login;

	public PaymentController(User user, Profile profile, boolean login) {
		super();
		this.user = user;
		this.profile = profile;
		this.login = login;
		loader = new FXMLLoader(getClass().getResource("/application/view/PaymentView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
		monthly.setSelected(true);
		priceText.setText("Costo mensile: 2€");
	}

	@FXML
	void checkSubscription(ActionEvent event) {
		if (((RadioButton) event.getSource()).getId().equals("annual")) {
			monthly.setSelected(false);
			priceText.setText("Costo annuale: 20€");
		}
		if (((RadioButton) event.getSource()).getId().equals("monthly")) {
			annual.setSelected(false);
			priceText.setText("Costo mensile: 2€");
		}
	}

	@FXML
	void confirm(ActionEvent event) {
		pay();
	}

	@FXML
	void confirmOnKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			pay();
	}

	public void pay() {
		if (expirationDate.getValue() == null || cardNumber.getText() == null || cardNumber.getText().isEmpty()
				|| cardHolder.getText() == null || cardHolder.getText().trim().isEmpty() || cardHolder.getText().isEmpty() || cvc.getText() == null
				|| cvc.getText().isEmpty() || !radioButtonCheck())
			errorText.setText("Compila tutti i campi");
		else if (!checkDate())
			errorText.setText("Carta non valida");
		else if (!checkCardNumber())
			errorText.setText("Errore numero carta");
		else if (!checkCVC())
			errorText.setText("Errore CVC/CVV");
		else
			confirmPayment();
	}

	@FXML
	void cancel(ActionEvent event) {
		if (login) {
			LoginHandler loginSceneHandler = LoginHandler.getIstance();
			loginSceneHandler.goBack();
		} else {
			HomeHandler home = HomeHandler.getInstance();
			home.goBack();
		}
	}

	boolean checkCardNumber() {
		if (!Pattern.matches("[0-9]{13,16}", cardNumber.getText()))
			return false;
		return true;
	}

	boolean checkCVC() {
		if (!Pattern.matches("[0-9]{3}", cvc.getText()))
			return false;
		return true;
	}

	boolean checkDate() {
		if (expirationDate.getValue().isAfter(LocalDate.now()))
			return true;
		return false;
	}

	boolean radioButtonCheck() {
		if (!monthly.isSelected() && !annual.isSelected())
			return false;
		return true;
	}

	void confirmPayment() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		boolean exist = true;
		if (dbManager.getUser(profile.getUsername()) != null) {
			profile.setTypeProfile(true);
			dbManager.updateTypeProfile(profile);
			dbManager.updateDate(profile);
			dbManager.updateTypeVIP(profile, annual.isSelected());
			dbManager.closedConnection();
		} else {
			exist = false;
			dbManager.insertUser(user);
			dbManager.insertProfile(profile);
			dbManager.closedConnection();
		}
		Alert alert = new Alert(AlertType.CONFIRMATION, "Pagamento effettuato", ButtonType.CLOSE);
		Utilities.getIstance().setStyleAlert(alert);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.CLOSE) {
			if (!exist) {
				LoginHandler loginSceneHandler = LoginHandler.getIstance();
				loginSceneHandler.goLogin();
			} else {
				HomeHandler home = HomeHandler.getInstance();
				CenterHomeController centerHome = new CenterHomeController(profile);
				home.addView(centerHome);
			}
		}
	}

	public boolean getVIP() {
		if (monthly.isSelected()) {
			return false;
		}
		return true;
	}
}