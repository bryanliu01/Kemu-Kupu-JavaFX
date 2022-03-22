package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class PracticeModeSceneController {
	
	public String boxValue;
	
    private Stage stage;
    private Scene scene;
    private Pane root;
    ObservableList<String> catagoryList=FXCollections.observableArrayList("Select Category                               ", "Colours","Days Of The Week 1",
            "Days Of The Week 2", "Months Of The Year 1", "Months Of The Year 2", "Babies", "Weather", "Compass Points",
            "Feelings", "Work", "Engineering", "Software", "Uni Life");
    
    @FXML
    private ChoiceBox CatagoryBox;

    public void initialize(){
        CatagoryBox.setItems(catagoryList);
    }


    public void moveToMainScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void submitPractice(ActionEvent event) throws IOException {
    	
    	//If a topic is selected, switch scenes, otherwise, do nothing
    	if ((!CatagoryBox.getSelectionModel().isEmpty()) && (!CatagoryBox.getValue().equals("Select Category                               "))) {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("PracticeScene.fxml"));
	        root = loader.load();
	        PracticeSceneController practiceSceneController = loader.getController();
	        //passes information to next scene
	        practiceSceneController.getCatagory((String) CatagoryBox.getValue());
	
	        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
	        scene = new Scene(root);
	        stage.setScene(scene);
	        stage.show();
	        
	        //Get five words
	        WordList.getAllWords(CatagoryBox.getValue().toString());
	        
	        practiceSceneController.initialise();

    	}
    }


}
