package application.controller;

import java.io.IOException;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Product;
import application.model.Profile;
import application.model.Settings;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class ProductSearchController extends BorderPane {

	@FXML
	private Button favorite;

	@FXML
	private ImageView image;

	@FXML
	private Label price;

	@FXML
	private Label name;

	@FXML
	private Label quantity;

	private Product product;

	private Profile profile;

	private int idOrder;

	private DBManager dbManager;

	private boolean favoriteButton=true;
	
	public ProductSearchController(Product product, Profile profile) {
		super();
		this.product = product;
		this.profile = profile;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ProductSearchView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		quantity.setVisible(false);
	}

	public ProductSearchController(Product product, Profile profile, int idOrder) {
		super();
		this.product = product;
		this.profile = profile;
		this.idOrder = idOrder;
		this.favoriteButton=false;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ProductSearchView.fxml"));
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
		image.setImage(new Image(product.getImageProduct()));
		name.setText(product.getName());
		ImageView img=new ImageView("/Images/unfavorite.png");
		img.setFitHeight(20);
		img.setFitWidth(20);
		favorite.setGraphic(img);
		quantity.setText("Quantità: " + dbManager.getQuantityToOrder(product.getId(), idOrder));
		favorite.setVisible(favoriteButton);
		Utilities.getIstance().checkFavorite(favorite, product, profile);
		if (profile.isTypeProfile())
			price.setText("Prezzo: " + product.getPriceVIP() + "€");
		else
			price.setText("Prezzo: " + product.getPrice() + "€");
		dbManager.closedConnection();
	}

	@FXML
	void openProduct(MouseEvent event) {
		ProductPageController p = new ProductPageController(product, profile);
		HomeHandler home = HomeHandler.getInstance();
		home.addView(p);
	}

	@FXML
	void addOrDeleteFavorite(ActionEvent event) {
		Utilities.getIstance().addOrDeleteFavorite(favorite, profile, product);
	}
}
