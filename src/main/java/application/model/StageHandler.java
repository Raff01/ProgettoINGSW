package application.model;

import java.util.ArrayList;

import javafx.stage.Stage;

public class StageHandler {
	private ArrayList<Stage> stages;
	private static StageHandler stageHandler;

	private StageHandler() {
		stages = new ArrayList<Stage>();
	}

	public static StageHandler getIstance() {
		if (stageHandler == null)
			stageHandler = new StageHandler();
		return stageHandler;
	}

	public void addStage(Stage s) {
		stages.add(s);
		showStage();
	}

	public void closeStage() {
		if (!stages.isEmpty()) {
			stages.get(stages.size() - 1).close();
			stages.remove(stages.size() - 1);
		}
	}

	public void showStage() {
		stages.get(stages.size() - 1).show();
	}
}
