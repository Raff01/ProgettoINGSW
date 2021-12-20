package application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Order;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class OrderController extends BorderPane {

	@FXML
	private Label stateOrder;

	@FXML
	private ImageView image;

	@FXML
	private Label orderNumber;

	@FXML
	private Label totPrice;

	private Profile profile;

	private Order order;

	private DBManager dbManager;

	public OrderController(Profile profile, Order order) {
		super();
		this.profile = profile;
		this.order = order;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/OrderView.fxml"));
		loader.setController(this);
		try {
			BorderPane root = (BorderPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}

	@FXML
	public void initialize() {
		dbManager = DBManager.getInstance();
		if(!dbManager.isEnstablished())
			dbManager.startConnection();
		if (dbManager.getProductToOrder(order) != null) {
			Product p = dbManager.getProductToOrder(order).get(0);
			image.setImage(dbManager.getImage(p.getName()));
		}
		LocalDate dateOrder = LOCAL_DATE(order.getDateOrder());
		LocalDate date = LocalDate.now().minusDays(1);
		if (dateOrder.equals(date)) {
			dbManager.updateOrderState(order.getIdOrder(), Settings.DELIVERING);
			order.setState(Settings.DELIVERING);
		} else if (dateOrder.equals(date) || dateOrder.isBefore(date)) {
			date = LocalDate.now().minusDays(3);
			dbManager.updateOrderState(order.getIdOrder(), Settings.SEND);
			order.setState(Settings.SEND);
		}
		dbManager.closedConnection();
		orderNumber.setText("Numero ordine: " + Integer.toString(order.getIdOrder()));
		totPrice.setText("Totale ordine: " + Double.toString(order.getPrice()) + "€");
		if (order.getState() == Settings.IN_PREPARATION)
			stateOrder.setText("Ordine in elaborazione");
		else if (order.getState() == Settings.DELIVERING)
			stateOrder.setText("Ordine in consegna");
		else
			stateOrder.setText("Ordine consegnato");
	}

	@FXML
	void cancel(ActionEvent event) {
		dbManager.startConnection();
		dbManager.removeOrderToOrderList(order, profile.getUsername());
		dbManager.deleteOrder(order.getIdOrder());
		dbManager.closedConnection();
		Alert alert = new Alert(AlertType.INFORMATION, "Ordine " + order.getIdOrder() + " annullato", ButtonType.OK);
		Utilities.getIstance().setStyleAlert(alert);
		alert.showAndWait();
		HomeHandler home = HomeHandler.getInstance();
		OrderListController orderList = new OrderListController(profile);
		home.addView(orderList);
	}

	@FXML
	void showMore(ActionEvent event) {
		HomeHandler home = HomeHandler.getInstance();
		OrderPageController orderPage = new OrderPageController(order, profile);
		home.addView(orderPage);
	}

}
