package application;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {

	public void Quit(Stage stage){

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Exit");
		alert.setHeaderText("You're about to exit the game!");
		alert.setContentText("Are you sure?");

		if(alert.showAndWait().get() == ButtonType.OK) {
			System.out.println("Thank you for playing");
			stage.close();
		}

	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
			primaryStage.setTitle("Kēmu Kupu");
			primaryStage.setScene(new Scene(root));
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		//checks if user press (x) to quit the application
		primaryStage.setOnCloseRequest(event -> {
			//if user x closes the application during the game it will delete all the files
			try {
				File quizzed_words = new File("./data/quizzed_words");
				File correct_words = new File("./data/correct_words");
				File incorrect_words = new File("./data/incorrect_words");
				quizzed_words.delete();
				correct_words.delete();
				incorrect_words.delete();
			}
			catch(Exception e) {
			}
			event.consume();
			Quit(primaryStage);
		});
	}
	
	public static void main(String[] args) {
		try {
            File f = new File("./data/leaderboard");
            f.createNewFile();
        } catch (Exception e) {
            System.out.println("error");
        }
		launch(args);
	}
}
