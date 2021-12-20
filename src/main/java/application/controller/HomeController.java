package application.controller;

import java.io.IOException;
import application.model.HomeHandler;
import application.model.LoginStage;
import application.model.Profile;
import application.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class HomeController extends BorderPane {
	@FXML
	private BorderPane borderPane;

	@FXML
	private HBox hbox;

	@FXML
	private MenuButton userMenu;

	@FXML
	private ImageView logo;

	@FXML
	private HBox hboxSearch;

	private User user;

	private Profile profile;

	private HomeHandler home;

	public HomeController(User user, Profile profile) {
		super();
		this.profile = profile;
		this.user = user;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/HomeView.fxml"));
		try {
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			this.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToCenterHome(MouseEvent event) {
		HomeHandler home = HomeHandler.getInstance();
		CenterHomeController centerHome = new CenterHomeController(profile);
		home.addView(centerHome);
	}

	@FXML
	public void initialize() {
		home = HomeHandler.getInstance();
		SearchBarController searchBar = new SearchBarController(user);
		hboxSearch.getChildren().add(searchBar);
		ImageView profileImage = new ImageView("/Images/Profile.png");
		profileImage.setFitHeight(30);
		profileImage.setFitWidth(30);
		userMenu.setGraphic(profileImage);
		CenterHomeController centerHome = new CenterHomeController(profile);
		home.setBorderPane(borderPane);
		home.addView(centerHome);
		Image img = new Image("/Images/Logo.png");
		logo.setImage(img);
	}

	@FXML
	void goToProfile(ActionEvent event) {
		ProfileController profilecontroller = new ProfileController(user, profile);
		home.addView(profilecontroller);
	}

	@FXML
	void goToOrderList(ActionEvent event) {
		OrderListController orderList = new OrderListController(profile);
		home.addView(orderList);
	}

	@FXML
	void goToCart(ActionEvent event) {
		CartController cart = new CartController(profile);
		home.addView(cart);
	}

	@FXML
	void goToFavorite(ActionEvent event) {
		FavoritesController favorite = new FavoritesController(profile);
		home.addView(favorite);
	}

	@FXML
	void clickDogFood(ActionEvent event) {
		String name = "Cane";
		String type = "Cibo_Secco";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void ClickDogHygiene(ActionEvent event) {
		String name = "Cane";
		String type = "Igiene";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickDogAccessories(ActionEvent event) {
		String name = "Cane";
		String type = "Accessorio";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickCatFood(ActionEvent event) {
		String name = "Gatto";
		String type = "Cibo_Secco";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickCatHygienic(ActionEvent event) {
		String name = "Gatto";
		String type = "Igiene";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickCatAccessories(ActionEvent event) {
		String name = "Gatto";
		String type = "Accessorio";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickFishFood(ActionEvent event) {
		String name = "Pesce";
		String type = "Cibo";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickFishAquariums(ActionEvent event) {
		String name = "Pesce";
		String type = "Acquario";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickBirdFood(ActionEvent event) {
		String name = "Uccello";
		String type = "Cibo";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickBirdAccessories(ActionEvent event) {
		String name = "Uccello";
		String type = "Accessorio";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickRodentsFood(ActionEvent event) {
		String name = "Roditore";
		String type = "Cibo";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void clickRodentAccessories(ActionEvent event) {
		String name = "Roditore";
		String type = "Accessorio";
		SearchController search = new SearchController(user, name, type);
		home.addView(search);
	}

	@FXML
	void logout(ActionEvent event) {
		home.close();
		LoginStage loginStage = new LoginStage();
	}
}
