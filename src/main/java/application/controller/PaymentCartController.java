package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Pattern;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Order;
import application.model.Product;
import application.model.Profile;
import application.model.Settings;
import application.model.User;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PaymentCartController extends BorderPane {

	@FXML
	private TextField cvc;

	@FXML
	private Label errorText;

	@FXML
	private TextField address;

	@FXML
	private TextField cap;

	@FXML
	private TextField city;

	@FXML
	private TextField surname;

	@FXML
	private TextField name;

	@FXML
	private TextField cardHolder;

	@FXML
	private TextField cardNumber;

	@FXML
	private DatePicker expirationDate;

	private User user;

	private Profile profile;

	private Double price;

	public PaymentCartController(User user, Profile profile, Double price) {
		super();
		this.user = user;
		this.profile = profile;
		this.price = price;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/PaymentCartView.fxml"));
		loader.setController(this);
		try {
			AnchorPane root = (AnchorPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean checkName() {
		if (name.getText() == null || name.getText().isEmpty() || name.getText().trim().isEmpty())
			return false;
		return true;
	}

	public boolean checkSurname() {
		if (surname.getText() == null || surname.getText().isEmpty() || surname.getText().trim().isEmpty())
			return false;
		return true;
	}

	public boolean checkAddress() {
		if (city.getText() == null || city.getText().isEmpty() || address.getText().isEmpty()
				|| city.getText().trim().isEmpty() || address.getText().trim().isEmpty() || address.getText() == null
				|| cap.getText().isEmpty() || cap.getText() == null)
			return false;
		return true;
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

	public void pay() {
		if (!checkAddress() || !checkName() || !checkSurname() || expirationDate.getValue() == null
				|| cap.getText().isEmpty() || cap.getText() == null || cap.getText().trim().isEmpty()
				|| cardNumber.getText() == null || cardNumber.getText().isEmpty() || cardHolder.getText() == null
				|| cardHolder.getText().isEmpty() || cardHolder.getText().trim().isEmpty() || cvc.getText() == null
				|| cvc.getText().isEmpty())
			errorText.setText("Compilare tutti i campi");
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
	void confirm(ActionEvent event) {
		pay();
	}

	@FXML
	void confirmOnKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			pay();
	}

	private void confirmPayment() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		String date = LocalDate.now().toString();
		Order o = new Order(dbManager.getLastIdOrder() + 1, date, Settings.IN_PREPARATION, name.getText(),
				surname.getText(), user.getEmail(), city.getText(), address.getText(), cap.getText(), price);
		dbManager.insertOrder(o);
		ArrayList<Product> product = dbManager.getProductCart(profile.getUsername());
		int idOrder = dbManager.getLastIdOrder();
		for (int i = 0; i < product.size(); i++) {
			int quant = dbManager.getQuantity(product.get(i).getId(), profile);
			dbManager.insertProductToOrder(product.get(i), idOrder, quant);
		}
		Alert alert = new Alert(AlertType.CONFIRMATION, "Sicuro di voler confermare l'acquisto", ButtonType.YES,
				ButtonType.NO);
		Utilities.getIstance().setStyleAlert(alert);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			dbManager.insertOrderToOrderList(user.getUsername(), idOrder);
			dbManager.clearCart(user);
			HomeHandler home = HomeHandler.getInstance();
			CenterHomeController centerHome = new CenterHomeController(profile);
			home.addView(centerHome);
		}
		dbManager.closedConnection();
	}

	@FXML
	void cancel(ActionEvent event) {
		HomeHandler home = HomeHandler.getInstance();
		home.goBack();
	}
}
