package application.controller;

import java.io.IOException;
import java.util.ArrayList;

import application.model.DBManager;
import application.model.Product;
import application.model.Profile;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FavoritesController extends BorderPane {
	private Profile profile;

	@FXML
	private VBox vbox;

	@FXML
	private Label text;

	@FXML
	private GridPane gridPane;

	private Thread t;

	private boolean start = true;

	private DBManager dbManager;

	public FavoritesController(Profile profile) {
		super();
		this.profile = profile;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/SearchView.fxml"));
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
		text.setText("Preferiti");
		dbManager = DBManager.getInstance();
		t = new Thread(this::routine);
		t.start();
	}

	private synchronized void routine() {
		while (start) {
			start = false;
			Platform.runLater(() -> {
				if (!dbManager.isEnstablished())
					dbManager.startConnection();
				ArrayList<Product> prod = dbManager.getProductFavorites(profile.getUsername());
				if (prod != null) {
					int contProduct = 0;
					int e = 0;
					for (int i = 0; contProduct != prod.size(); i++) {
						ProductSearchController p = new ProductSearchController(prod.get(contProduct), profile);
						gridPane.add(p, i, e);
						RowConstraints row = new RowConstraints();
						gridPane.getRowConstraints().add(row);
						contProduct++;
						if (i == 3) {
							i = -1;
							e++;
						}
					}
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
