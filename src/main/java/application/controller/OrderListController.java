package application.controller;

import java.io.IOException;
import java.util.ArrayList;

import application.model.DBManager;
import application.model.HomeHandler;
import application.model.Order;
import application.model.Profile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class OrderListController extends BorderPane {
	private Profile profile;
	@FXML

	private VBox vboxOrders;

	public OrderListController(Profile profile) {
		super();
		this.profile = profile;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/OrderListView.fxml"));
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
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		ArrayList<Integer> idOrder = dbManager.getIdOrderList(profile);
		ArrayList<Order> tmp = new ArrayList<>();
		if(idOrder!=null)
			for (int i = 0; i < idOrder.size(); i++) 
				tmp.add(dbManager.getOrder(idOrder.get(i)));
		if (tmp != null) {
			for (Order o : tmp) {
				OrderController order = new OrderController(profile, o);
				vboxOrders.getChildren().add(order);
			}
		}
		dbManager.closedConnection();
	}
}
