package application;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;

public class GameTimer {
	
	private Timer timer;
	private TimerTask timerTask;
	public IntegerProperty time = new SimpleIntegerProperty();

	private int totalTime;
	private Label gameTimer;
	
	public GameTimer(Label gameTimer, int totalTime) {
		this.gameTimer = gameTimer;
		this.totalTime = totalTime;
		gameTimer.textProperty().bind(time.asString());
	}
	
	public void startCountdown() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		time.set(totalTime + 1);
		timerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					if (time.get() >= 0) {
						time.set(time.get()-1);
					} else {
						timer.cancel();
					}
				});
			}
		};
		timer.schedule(timerTask, 0, 1000);
	}
	
	public void cancelTimer() {
		timer.cancel();
	}
	
	
}
