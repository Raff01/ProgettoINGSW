package application.controller;

import java.io.IOException;
import java.util.ArrayList;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Product;
import application.model.Profile;
import application.model.Settings;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class ProductCartController extends BorderPane {
	private Product product;

	private Profile profile;

	@FXML
	private Label quantity;

	@FXML
	private ImageView image;

	@FXML
	private Label price;

	@FXML
	private Label name;

	private DBManager dbManager;

	public ProductCartController(Profile profile, Product product) {
		super();
		this.profile = profile;
		this.product = product;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ProductCartView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void clickToRemove(ActionEvent event) {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		dbManager.removeProduct(product.getId(), profile.getUsername());
		dbManager.closedConnection();
		HomeHandler home = HomeHandler.getInstance();
		CartController cart = new CartController(profile);
		home.addView(cart);
	}

	@FXML
	void openProduct(MouseEvent event) {
		ProductPageController prod = new ProductPageController(product, profile);
		HomeHandler home = HomeHandler.getInstance();
		home.addView(prod);
	}

	@FXML
	public void initialize() {
		image.setImage(new Image(product.getImageProduct()));
		name.setText(product.getName());
		if (profile.isTypeProfile())
			price.setText("Prezzo " + "VIP: " + product.getPriceVIP() + "€");
		else
			price.setText("Prezzo: " + product.getPrice() + "€ ");
		dbManager = DBManager.getInstance();
		dbManager.startConnection();
		quantity.setText(Integer.toString(dbManager.getQuantity(product.getId(), profile)));
		dbManager.closedConnection();
	}

	@FXML
	void clickAdd(ActionEvent event) {
		int x = Integer.parseInt(quantity.getText());
		quantity.setText(Integer.toString(x + 1));
		dbManager.startConnection();
		dbManager.updateQuantityCart(product.getId(), profile, Integer.parseInt(quantity.getText()));
		dbManager.closedConnection();
		HomeHandler home = HomeHandler.getInstance();
		CartController cart = new CartController(profile);
		home.addView(cart);
		clickToEdit();
	}

	@FXML
	void clickRemove(ActionEvent event) {
		int x = Integer.parseInt(quantity.getText());
		if (x == 0)
			return;
		else
			quantity.setText(Integer.toString(x - 1));
		dbManager.startConnection();
		dbManager.updateQuantityCart(product.getId(), profile, Integer.parseInt(quantity.getText()));
		dbManager.closedConnection();
		HomeHandler home = HomeHandler.getInstance();
		CartController cart = new CartController(profile);
		home.addView(cart);
		clickToEdit();
	}

	void clickToEdit() {
		ArrayList<Product> prod = new ArrayList<Product>();
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		int quant = Integer.parseInt(quantity.getText());
		if (prod == null || !prod.contains(product))
			dbManager.addProductToCart(product.getId(), profile, quant);
		else
			dbManager.updateQuantityCart(product.getId(), profile, quant);
		if (quant == 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Rimuovere " + product.getName() + "dal carrello?",
					ButtonType.OK, ButtonType.NO);
			Utilities.getIstance().setStyleAlert(alert);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.OK) {
				dbManager.removeProductToCart(product.getId(), profile);
				dbManager.closedConnection();
				HomeHandler home = HomeHandler.getInstance();
				CartController cart = new CartController(profile);
				home.addView(cart);
			} else
				return;
		}
		dbManager.closedConnection();
	}
}
