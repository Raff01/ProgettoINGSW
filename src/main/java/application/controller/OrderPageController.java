package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import application.model.DBManager;
import application.model.EmailSender;
import application.model.HomeHandler;
import application.model.Order;
import application.model.PDFCreator;
import application.model.Product;
import application.model.Profile;
import application.model.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class OrderPageController extends BorderPane {
	@FXML
	private Label totPrice;

	@FXML
	private VBox vboxProduct;

	@FXML
	private Label order;

	private Order o;

	private Profile profile;

	private ArrayList<Product> product;

	private static String name = "Riepilogo_ordine_numero";

	public OrderPageController(Order o, Profile profile) {
		super();
		this.o = o;
		this.profile = profile;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/OrderPageView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void sendPDF(ActionEvent event) {
		EmailThread t = new EmailThread();
		t.start();
		Alert alert = new Alert(AlertType.INFORMATION,
				"A breve riceverai il pdf contenente il riepilogo dell'ordine sull'email!", ButtonType.OK);
		Utilities.getIstance().setStyleAlert(alert);
		alert.showAndWait();
	}

	@FXML
	public void initialize() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		product = dbManager.getProductToOrder(o);
		dbManager.closedConnection();
		for (int i = 0; i < product.size(); i++) {
			ProductSearchController p = new ProductSearchController(product.get(i), profile, o.getIdOrder());
			vboxProduct.getChildren().add(p);
		}
		totPrice.setText("Totale: " + o.getPrice().toString() + "€");
	}

	public void pdf() {
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();

		if (PDFCreator.getIstance().createDocument(name, o.getIdOrder())) {
			for (int i = 0; i < product.size(); i++) {
				PDFCreator.getIstance().appendText(product.get(i).getName());
				int quantity = dbManager.getQuantityToOrder(product.get(i).getId(), o.getIdOrder());
				PDFCreator.getIstance().appendText("Quantità: " + Integer.toString(quantity));
				boolean typeProfile = profile.isTypeProfile();
				double price;
				if (typeProfile)
					price = product.get(i).getPriceVIP();
				else
					price = product.get(i).getPrice();
				PDFCreator.getIstance()
						.appendText("Prezzo: " + Double.toString(price) + " x" + Integer.toString(quantity));
				PDFCreator.getIstance().newLine();
			}
			PDFCreator.getIstance().appendText("Totale: " + o.getPrice().toString() + "€");
			PDFCreator.getIstance().getPDF();
			EmailSender email = new EmailSender(profile.getUsername());
			email.sendEmail("petshopingsw@gmail.com", "Ingsw2021.", dbManager.getEmail(profile.getUsername()),
					name + o.getIdOrder() + ".pdf");
			dbManager.closedConnection();

		}
	}

	private class EmailThread extends Thread {

		@Override
		public void run() {
			super.run();
			pdf();
		}

	}
}
