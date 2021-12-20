package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Product;
import application.model.Profile;
import application.model.Utilities;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.event.ActionEvent;

public class CenterHomeController extends BorderPane {
	@FXML
	private HBox vipProducts;

	@FXML
	private Button buttonVip;

	@FXML
	private HBox favoriteProducts;

	@FXML
	private MenuButton orderMenu;

	@FXML
	private GridPane gridProducts;

	@FXML
	private Label textVIP;

	@FXML
	private Label favorite;

	private DBManager dbManager;

	private Profile profile;

	private boolean stop = false;

	private ArrayList<Integer> idProductGrid;

	private Thread t;

	public CenterHomeController(Profile profile) {
		super();
		this.profile = profile;
		idProductGrid = new ArrayList<Integer>();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/CenterHomeView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void vip(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION,
				"Diventa VIP, accedi ad offerte esclusive." + '\n'
						+ "L'abbonamento VIP ha un costo di 2€ mesile o 20€ annuali!",
				ButtonType.CANCEL, ButtonType.OK);
		Utilities.getIstance().setStyleAlert(alert);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.OK) {
			HomeHandler home = HomeHandler.getInstance();
			dbManager.startConnection();
			PaymentController payment = new PaymentController(dbManager.getUser(profile.getUsername()), profile, false);
			home.addView(payment);
			dbManager.closedConnection();
		}
	}

	private static final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}

	public void checkTypeVIP() {
		if (profile.isTypeProfile()) {
			LocalDate dateMonth = LocalDate.now().minusMonths(1);
			LocalDate dateYear = LocalDate.now().minusYears(1);
			dbManager.startConnection();
			if (dbManager.getDate(profile) != null) {
				if ((!profile.isTypeVIP() && dateMonth.isAfter(LOCAL_DATE(profile.getDate())))
						|| (profile.isTypeVIP() && dateYear.isAfter(LOCAL_DATE(profile.getDate())))) {
					Alert alert = new Alert(AlertType.INFORMATION, "Abbonamento VIP scaduto", ButtonType.OK);
					profile.setDate(null);
					dbManager.removeDate(profile);
					Utilities.getIstance().setStyleAlert(alert);
					alert.showAndWait();
					profile.setTypeProfile(false);
					dbManager.updateTypeProfile(profile);
					dbManager.removeDate(profile);
					dbManager.closedConnection();
					buttonVip.setVisible(true);
				}
			}
		}
	}

	@FXML
	public void initialize() {
		dbManager = DBManager.getInstance();
		checkTypeVIP();
		if (profile.isTypeProfile()) {
			buttonVip.setVisible(false);
			vipProducts.setVisible(false);
			textVIP.setVisible(false);
		}
		t = new Thread(this::routine);
		t.start();
	}

	private synchronized void routine() {
		while (!stop) {
			Platform.runLater(() -> {
				stop = true;
				if (!dbManager.isEnstablished())
					dbManager.startConnection();
				ArrayList<ArrayList<Product>> product = new ArrayList<ArrayList<Product>>(3);
				product.add(0, dbManager.getProductFavorites(profile.getUsername()));
				product.add(1, dbManager.getProductHome(9));
				product.add(2, dbManager.getProductHome(36));
				dbManager.closedConnection();
				for (int i = 0; i < 9; i++)
					if (!profile.isTypeProfile())
						vipProducts.getChildren().add(new ProductVIPController(product.get(1).get(i), profile));
				if (product.get(0) != null)
					for (int i = 0; i < product.get(0).size() && i < 9; i++)
						favoriteProducts.getChildren().add(new ProductController(product.get(0).get(i), profile));
				else
					favorite.setVisible(false);
				ArrayList<Integer> indexs = new ArrayList<Integer>();
				int contProduct = 0;
				int e = 0;
				for (int i = 0; contProduct != product.get(2).size(); i++) {
					Random rnd = new Random();
					int index = rnd.nextInt(product.get(2).size());
					if (indexs.contains(index))
						i--;
					else {
						indexs.add(index);
						ProductController p = new ProductController(product.get(2).get(contProduct), profile);
						idProductGrid.add(product.get(2).get(index).getId());
						gridProducts.add(p, i, e);
						RowConstraints row = new RowConstraints();
						gridProducts.getRowConstraints().add(row);
						contProduct++;
						if (i == 8) {
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

	@FXML
	void ascendingOrder(ActionEvent event) {
		StringBuilder id = new StringBuilder();
		for (int i = 0; i < idProductGrid.size(); i++)
			if (i != idProductGrid.size() - 1)
				id.append(idProductGrid.get(i) + ", ");
			else
				id.append(idProductGrid.get(i));
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<Product> prod = dbManager.ascendingOrderProducts(id.toString(), profile);
		dbManager.closedConnection();
		gridProducts.getChildren().clear();
		int contProduct = 0;
		int e = 0;
		for (int i = 0; contProduct != prod.size(); i++) {
			ProductController p = new ProductController(prod.get(contProduct), profile);
			gridProducts.add(p, i, e);
			RowConstraints row = new RowConstraints();
			gridProducts.getRowConstraints().add(row);
			contProduct++;
			if (i == 8) {
				i = -1;
				e++;
			}
		}
	}

	@FXML
	void descendingOrder(ActionEvent event) {
		StringBuilder id = new StringBuilder();
		for (int i = 0; i < idProductGrid.size(); i++)
			if (i != idProductGrid.size() - 1)
				id.append(idProductGrid.get(i) + ", ");
			else
				id.append(idProductGrid.get(i));
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<Product> prod = dbManager.discendingOrderProducts(id.toString(), profile);
		dbManager.closedConnection();
		gridProducts.getChildren().clear();
		int contProduct = 0;
		int e = 0;
		for (int i = 0; contProduct != prod.size(); i++) {
			ProductController p = new ProductController(prod.get(contProduct), profile);
			gridProducts.add(p, i, e);
			RowConstraints row = new RowConstraints();
			gridProducts.getRowConstraints().add(row);
			contProduct++;
			if (i == 8) {
				i = -1;
				e++;
			}
		}
	}

	@FXML
	void alphabeticalOrder(ActionEvent event) {
		StringBuilder id = new StringBuilder();
		for (int i = 0; i < idProductGrid.size(); i++)
			if (i != idProductGrid.size() - 1)
				id.append(idProductGrid.get(i) + ", ");
			else
				id.append(idProductGrid.get(i));
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<Product> prod = dbManager.alphabeticalOrderProducts(id.toString());
		dbManager.closedConnection();
		gridProducts.getChildren().clear();
		int contProduct = 0;
		int e = 0;
		for (int i = 0; contProduct != prod.size(); i++) {
			ProductController p = new ProductController(prod.get(contProduct), profile);
			gridProducts.add(p, i, e);
			RowConstraints row = new RowConstraints();
			gridProducts.getRowConstraints().add(row);
			contProduct++;
			if (i == 8) {
				i = -1;
				e++;
			}
		}
	}
}