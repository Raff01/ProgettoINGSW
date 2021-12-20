package application.controller;

import java.io.IOException;

import application.model.HomeHandler;
import application.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class SearchBarController extends BorderPane {

	private User user;

	@FXML
	private Button searchBtn;

	public SearchBarController(User user) {
		super();
		this.user = user;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/SearchBarView.fxml"));
		loader.setController(this);
		try {
			AnchorPane root = (AnchorPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageView img = new ImageView("/Images/lens.png");
		img.setFitWidth(15);
		img.setFitHeight(15);
		searchBtn.setGraphic(img);

	}

	@FXML
	private TextField text;

	@FXML
	void search(ActionEvent event) {
		searchFor();
	}

	@FXML
	void searchFromKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			searchFor();
	}

	public void searchFor() {
		if (text.getText() != null && !text.getText().trim().isEmpty() && !text.getText().isEmpty()) {
			SearchController searchPage = new SearchController(user, text.getText() + " ");
			HomeHandler home = HomeHandler.getInstance();
			home.addView(searchPage);
		}
	}

}