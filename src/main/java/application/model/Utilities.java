package application.model;

import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class Utilities {
	private static Utilities utilities;
	public String favorite;

	public String unfavorite;

	private Utilities() {
		favorite = "/Images/favorite.png";
		unfavorite = "/Images/unfavorite.png";
	}

	public static Utilities getIstance() {
		if (utilities == null)
			utilities = new Utilities();
		return utilities;
	}

	public void addOrDeleteFavorite(Button button, Profile profile, Product product) {
		DBManager dbManager = DBManager.getInstance();
		if (!dbManager.isEnstablished())
			dbManager.startConnection();
		ArrayList<Product> prod = dbManager.getProductFavorites(profile.getUsername());
		if (prod != null) {
			if (!prod.isEmpty() && prod.contains(product)) {
				dbManager.removeProductFromFavorites(product, profile.getUsername());
				removeProdFromFavorite(button);
			} else {
				dbManager.addProductToFavorites(product, profile.getUsername());
				addProdFromFavorite(button);
			}
		} else if (prod == null) {
			dbManager.addProductToFavorites(product, profile.getUsername());
			addProdFromFavorite(button);
		}
		dbManager.closedConnection();
	}

	public void removeProdFromFavorite(Button button) {
		ImageView img = new ImageView(unfavorite);
		img.setFitWidth(20);
		img.setFitHeight(20);
		button.setGraphic(img);
	}

	public void addProdFromFavorite(Button button) {
		ImageView img = new ImageView(favorite);
		img.setFitWidth(20);
		img.setFitHeight(20);
		button.setGraphic(img);
	}

	public void checkFavorite(Button button, Product product, Profile profile) {
		DBManager dbManager = DBManager.getInstance();
		if (!dbManager.isEnstablished())
			dbManager.startConnection();
		ArrayList<Product> prod = dbManager.getProductFavorites(profile.getUsername());
		dbManager.closedConnection();
		if (prod != null) {
			if (prod.contains(product)) {
				ImageView img = new ImageView(favorite);
				img.setFitWidth(20);
				img.setFitHeight(20);
				button.setGraphic(img);
			}
		} else {
			ImageView img = new ImageView(unfavorite);
			img.setFitWidth(20);
			img.setFitHeight(20);
			button.setGraphic(img);
		}
	}

	public void setStyleAlert(Alert alert) {
		alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/mainStyle.css").toString());
		alert.getDialogPane().getStyleClass().add("alert");
	}
}