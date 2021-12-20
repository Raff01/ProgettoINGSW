package application.controller;

import java.io.IOException;
import java.text.DecimalFormat;

import application.model.HomeHandler;
import application.model.Product;
import application.model.Profile;
import application.model.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ProductController extends BorderPane {
	private Product product;
	@FXML
	private ImageView productImage;

	@FXML
	private Label productName;

	@FXML
	private Label productPrice;

	private Profile profile;

	private static DecimalFormat df = new DecimalFormat("0.00");

	public ProductController(Product p, Profile profile) {
		super();
		product = p;
		this.profile = profile;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ProductView.fxml"));
		loader.setController(this);
		try {
			AnchorPane root = (AnchorPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
		productImage.setImage(new Image(product.getImageProduct()));
		productName.setText(product.getName());
		if (profile.isTypeProfile())
			productPrice.setText("Prezzo: " + product.getPriceVIP() + "€");
		else
			productPrice.setText("Prezzo: " + product.getPrice() + "€");
	}

	@FXML
	void openProduct(MouseEvent event) {
		HomeHandler home = HomeHandler.getInstance();
		ProductPageController productPage = new ProductPageController(product, profile);
		home.addView(productPage);
	}
}
