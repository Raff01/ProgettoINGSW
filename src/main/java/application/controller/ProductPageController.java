package application.controller;

import java.io.IOException;
import java.util.ArrayList;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Product;
import application.model.Profile;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ProductPageController extends BorderPane {
	@FXML
	private Button favorites;

	@FXML
	private Button cart;

	@FXML
	private Label quantity;

	@FXML
	private ImageView image;

	@FXML
	private Label price;

	@FXML
	private Label name;

	@FXML
	private Label description;

	private Product product;

	private Profile profile;

	private ArrayList<Product> prodCart;

	private DBManager dbManager;

	public ProductPageController(Product product, Profile profile) {
		super();
		this.product = product;
		this.profile = profile;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ProductPageView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
		dbManager = DBManager.getInstance();
		dbManager.startConnection();
		image.setImage(dbManager.getImage(product.getName()));
		ImageView img = new ImageView("/Images/unfavorite.png");
		img.setFitHeight(20);
		img.setFitWidth(20);
		favorites.setGraphic(img);
		if (profile.isTypeProfile())
			price.setText("Prezzo: " + product.getPriceVIP() + "€");
		else
			price.setText("Prezzo: " + product.getPrice() + " VIP: " + product.getPriceVIP());
		name.setText(product.getName());
		description.setText(product.getDescription());
		prodCart = dbManager.getProductCart(profile.getUsername());
		if (prodCart != null)
			for (Product p : prodCart) {
				if (p.getId() == product.getId()) {
					cart.setText("Modifica Q.tà");
					quantity.setText(Integer.toString(dbManager.getQuantity(p.getId(), profile)));
					break;
				}
			}
		Utilities.getIstance().checkFavorite(favorites, product, profile);
		dbManager.closedConnection();
	}

	@FXML
	void goBack(ActionEvent event) {
		HomeHandler home = HomeHandler.getInstance();
		CenterHomeController center = new CenterHomeController(profile);
		home.addView(center);
	}

	@FXML
	void add(ActionEvent event) {
		int x = Integer.parseInt(quantity.getText());
		quantity.setText(Integer.toString(x + 1));
	}

	@FXML
	void remove(ActionEvent event) {
		int x = Integer.parseInt(quantity.getText());
		if (prodCart.contains(product)) {
			if (x == 0) {
				dbManager.startConnection();
				dbManager.removeProductToCart(product.getId(), profile);
				dbManager.closedConnection();
			} else
				quantity.setText(Integer.toString(x - 1));
		} else {
			if (x == 1)
				return;
			else
				quantity.setText(Integer.toString(x - 1));
		}
	}

	@FXML
	void addToCart(ActionEvent event) {
		dbManager.startConnection();
		int quant = Integer.parseInt(quantity.getText());
		if (prodCart == null || !prodCart.contains(product)) {
			if (quant != 0) {
				dbManager.addProductToCart(product.getId(), profile, quant);
				Alert alert = new Alert(AlertType.INFORMATION,
						quantity.getText() + " " + product.getName() + " aggiunti al carrello", ButtonType.OK);
				Utilities.getIstance().setStyleAlert(alert);
				alert.showAndWait();
				HomeHandler handler = HomeHandler.getInstance();
				CenterHomeController centerHome = new CenterHomeController(profile);
				handler.addView(centerHome);
			}
		} else if (prodCart.contains(product)) {
			if (quant != 0) {
				dbManager.updateQuantityCart(product.getId(), profile, quant);
				Alert alert = new Alert(AlertType.INFORMATION,
						quantity.getText() + " " + product.getName() + " aggiunti al carrello", ButtonType.OK);
				Utilities.getIstance().setStyleAlert(alert);
				alert.showAndWait();
				goCart();

			} else {
				dbManager.removeProductToCart(product.getId(), profile);
				Alert alert = new Alert(AlertType.INFORMATION, product.getName() + " rimosso dal carrello",
						ButtonType.OK);
				Utilities.getIstance().setStyleAlert(alert);
				alert.showAndWait();
				goCart();
			}
		}
		dbManager.closedConnection();
	}

	public void goCart() {
		HomeHandler home = HomeHandler.getInstance();
		CartController cart = new CartController(profile);
		home.addView(cart);
	}

	@FXML
	void addToFavorite(ActionEvent event) {
		Utilities.getIstance().addOrDeleteFavorite(favorites, profile, product);
	}
}