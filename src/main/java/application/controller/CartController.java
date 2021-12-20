package application.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Product;
import application.model.Profile;
import application.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CartController extends BorderPane {

	@FXML
	private Label price;

	@FXML
	private Label error;

	@FXML
	private VBox vboxProduct;

	private Profile profile;

	private static DecimalFormat df = new DecimalFormat("0.00");

	public CartController(Profile profile) {
		super();
		this.profile = profile;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/CartView.fxml"));
		try {
			loader.setController(this);
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void clickToPay(ActionEvent event) {
		if (getPrice() > 0.0) {
			DBManager dbManager = DBManager.getInstance();
			dbManager.startConnection();
			User u = dbManager.getUser(profile.getUsername());
			PaymentCartController p = new PaymentCartController(u, profile, getPrice());
			HomeHandler home = HomeHandler.getInstance();
			home.addView(p);
		} else
			error.setText("Carrello vuoto");
	}

	@FXML
	public void initialize() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<Product> product = new ArrayList<Product>();
		if (dbManager.getProductCart(profile.getUsername()) != null)
			product = dbManager.getProductCart(profile.getUsername());
		if (product == null) {
			dbManager.closedConnection();
			return;
		}
		for (int i = 0; i < product.size(); i++) {
			ProductCartController prod = new ProductCartController(profile, product.get(i));
			vboxProduct.getChildren().add(prod);
		}
		dbManager.closedConnection();
		price.setText("Totale: " + getPrice() + "€");
	}

	public double getPrice() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<Product> product = dbManager.getProductCart(profile.getUsername());
		double p = 0;
		if (product == null)
			return 0;
		int quant;
		for (int i = 0; i < product.size(); i++) {
			quant = dbManager.getQuantity(product.get(i).getId(), profile);
			p += (dbManager.getPriceProduct(product.get(i).getId(), profile) * quant);
		}
		String x = df.format(p);
		x = x.replace(',', '.');
		p = Double.parseDouble(x);
		return p;
	}
}
