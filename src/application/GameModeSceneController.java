package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class GameModeSceneController {

    private Stage stage;
    private Scene scene;
    private Pane root;
    
    @FXML
    private ChoiceBox CatagoryBox;

    public void initialize(){
        //sets default value to select category
        CatagoryBox.getItems().add("Select Category                               ");
        CatagoryBox.setValue("Select Category                               ");
        getFileNames();
    }

    public void getFileNames(){
        //gets each file name and adds it to the category box
        File folder = new File("./words");
        File[] files = folder.listFiles();
        for (File f:files){
            CatagoryBox.getItems().add(f.getName());
        }
    }
    public void switchToMainScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void Submit(ActionEvent event) throws IOException {
    	
    	//If a topic is selected, switch scenes, otherwise, do nothing
    	if ((!CatagoryBox.getSelectionModel().isEmpty()) && (!CatagoryBox.getValue().equals("Select Category                               "))) {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameScene.fxml"));
	        root = loader.load();
	        GameSceneController gameSceneController = loader.getController();
	        //passes information to next scene
	        gameSceneController.getCatagory((String) CatagoryBox.getValue());
	
	        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
	        scene = new Scene(root);
	        stage.setScene(scene);
	        stage.show();
	        
	        //Get five words
	        WordList.getWords(CatagoryBox.getValue().toString());
	        
	        
	        gameSceneController.initialise();

    	}
    }


}
