package application.model;

import java.util.ArrayList;

import javafx.scene.layout.BorderPane;

public class HomeHandler {
	private BorderPane center;
	private ArrayList<BorderPane> view;
	private static HomeHandler homeHandler = null;

	private HomeHandler() {
		view = new ArrayList<BorderPane>();
	}

	public static HomeHandler getInstance() {
		if (homeHandler == null)
			homeHandler = new HomeHandler();
		return homeHandler;
	}

	public void setBorderPane(BorderPane b) {
		center = b;
	}

	public void addView(BorderPane b) {
		view.add(b);
		center.setCenter(b);
	}

	public void goBack() {
		if (view.size() > 1) {
			view.remove(view.size() - 1);
			center.setCenter(view.get(view.size() - 1));
		}
	}

	public void close() {
		view.clear();
		homeHandler = null;
	}
}
