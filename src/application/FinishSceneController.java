package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class FinishSceneController{
    
    @FXML
    private Label scoreBox;
    @FXML
    private Button startQuiz;
    @FXML
    private Button returnToMenu;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private ImageView firstStar;
    @FXML
    private ImageView secondStar;
    @FXML
    private ImageView thirdStar;

    private Stage stage;
    private Scene scene;
    private double Score;

    public void getScore(double score2){
        this.Score = score2;
        scoreBox.setText("Final Score: "+Score);
    }


    public void setStarImages(double score) {
        //1000 = 1 star, 1750 = 1.5 stars, 2500 = 2 stars, 3250 = 2.5 stars, 4000 = 3 stars increment by 750
        if((Score>=1000)&&(Score<1750)){
            changeImage("star.png", firstStar);

        }else if((Score>=1750)&&(Score<2500)){
            changeImage("star.png", firstStar);
            changeImage("star-half.png", secondStar);

        }else if((Score>=2500)&&(Score<3250)){
            changeImage("star.png", firstStar);
            changeImage("star.png", secondStar);

        }else if((Score>=3250)&&(Score<4000)){
            changeImage("star.png", firstStar);
            changeImage("star.png", secondStar);
            changeImage("star-half.png", thirdStar);

        }else if(Score==4000){
            changeImage("star.png", firstStar);
            changeImage("star.png", secondStar);
            changeImage("star.png", thirdStar);
        }


    }

    public void changeImage(String imageName, ImageView imageview){
        Image newImage = new Image(getClass().getResourceAsStream("./images/"+imageName));
        imageview.setImage(newImage);

    }



    public void YesButton(ActionEvent event) throws IOException {
    	GameSceneController.score = 0;
		GameSceneController.count = 0;
		GameSceneController.finished = false;
        Parent root = FXMLLoader.load(getClass().getResource("GameModuleScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void NoButton(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }


}
