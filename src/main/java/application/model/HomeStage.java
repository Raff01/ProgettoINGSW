package application.model;


import application.controller.HomeController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HomeStage extends Stage {
	private User u;

	public HomeStage(User u) {
		this.u = u;
		this.setMinWidth(600);
		this.setMinHeight(600);
		this.setTitle("Animal Shop");
		this.getIcons().add(new Image(getClass().getResource("/Images/LogoIcon.png").toExternalForm()));
		StageHandler stageHandler = StageHandler.getIstance();
		stageHandler.closeStage();
		stageHandler.addStage(this);
		DBManager dbManager = DBManager.getInstance();
		dbManager.startConnection();
		HomeController homeController = new HomeController(u, dbManager.getProfile(u.getUsername()));

		dbManager.closedConnection();
		Scene scene = new Scene(homeController);
		setScene(scene);
	}
}
