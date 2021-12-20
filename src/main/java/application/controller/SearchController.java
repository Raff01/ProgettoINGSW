package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import application.model.DBManager;
import application.model.Product;
import application.model.Profile;
import application.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class SearchController extends BorderPane {

	@FXML
	private Label text;

	@FXML
	private GridPane gridPane;

	private User user;

	private String animalType;

	private String type;

	private DBManager dbManager;

	private Thread t;

	private boolean start = true;

	private String searched;

	public SearchController(User user, String searched) {
		super();
		this.searched = searched;
		this.user = user;
		animalType = null;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/SearchView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SearchController(User user, String animalType, String type) {
		super();
		searched = null;
		this.type = type;
		this.animalType = animalType;
		this.user = user;
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
		if (animalType != null)
			text.setText(animalType);
		else
			text.setText(searched);
		t = new Thread(this::routine);
		t.start();
	}

	private synchronized void routine() {
		while (start) {
			start = false;
			Platform.runLater(() -> {
				if (user != null) {
					dbManager = DBManager.getInstance();
					if (!dbManager.isEnstablished())
						dbManager.startConnection();
					ArrayList<Product> prod = new ArrayList<Product>();
					if (animalType != null) {
						if ((animalType.equals("Cane") || animalType.equals("Gatto"))
								&& (type.equals("Cibo_Secco") || type.equals("Cibo_Umido"))) {
							if (dbManager.getFoodForAnimalType(animalType, type) != null)
								prod.addAll(dbManager.getFoodForAnimalType(animalType, type));
						} else if (dbManager.getProductForAnimalType(animalType, type) != null) {
							prod.addAll(dbManager.getProductForAnimalType(animalType, type));
						}
					}
					if (searched != null) {
						String[] s = searched.split("\\s+");
						ArrayList<Product> tmp = new ArrayList<Product>();
						tmp = dbManager.getProduct();
						for (Product p : tmp)
							for (int i = 0; i < s.length && prod != null; i++) {
								if ((p.getName().toLowerCase().contains(s[i].toLowerCase())
										|| p.getDescription().toLowerCase().contains(s[i].toLowerCase()))
										&& !prod.contains(p))
									prod.add(p);
							}
					}
					Profile profile = dbManager.getProfile(user.getUsername());
					dbManager.closedConnection();
					int contProduct = 0;
					int e = 0;
					if (prod != null) {
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
