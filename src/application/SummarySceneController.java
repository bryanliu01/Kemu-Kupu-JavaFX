package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SummarySceneController extends LeaderboardController{


    public SummarySceneController() {
    }

    /* Custom class object to record statistics */
    public class Word {
        String word;
        String correct;
        public Word(String word, String correct) {
            this.word = word;
            this.correct = correct;

        }
        /* getter methods */
        public String getWord(){
            return this.word;
        }
        public String getCorrect(){
            return this.correct;
        }

    }

    public double Score;
    public String leaderboardInfo;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private static List<String> testedWords = new ArrayList<>();
    private static List<String> correctWords = new ArrayList<>();
    private static List<String> incorrectWords = new ArrayList<>();

    @FXML
    TableView<Word> tableView;

    @FXML
    TableColumn<Word, String > wordColumn;

    @FXML
    TableColumn<Word, String> correctColumn;

    @FXML
    Label summaryLabel;

    @Override
    public void populateTable() {
        for (String word : testedWords) {
            for(String correct_words : correctWords){
                if (correct_words.equals(word)){
                    tableView.getItems().add(new Word(word,"Correct"));
                }
            }
            for(String incorrect_words : incorrectWords){
                if (incorrect_words.equals(word)){
                    tableView.getItems().add(new Word(word,"Incorrect"));
                }
            }

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getFromFile(testedWords,"quizzed_words");
        getFromFile(correctWords, "correct_words");
        getFromFile(incorrectWords, "incorrect_words");
        wordColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
        correctColumn.setCellValueFactory(new PropertyValueFactory<>("correct"));
        populateTable();


    }
    public void getScore(double score2){
        this.Score = score2;
    }

    public void pressContinueButton(ActionEvent event) throws IOException {
        deleteFile("quizzed_words");
        deleteFile("correct_words");
        deleteFile("incorrect_words");
        addToLeaderboard();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FinishScene.fxml"));
        root = loader.load();
        FinishSceneController finishSceneController = loader.getController();
        finishSceneController.getScore(Score); //grabs score to pass information to next scene
        finishSceneController.setStarImages(Score);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void getLeaderboardString(String string){
        this.leaderboardInfo = string;
    }
    public void addToLeaderboard(){
        TextInputDialog textInput = new TextInputDialog();

        textInput.setTitle("Leaderboard");
        textInput.setHeaderText("Enter your name to go into \nThe Leaderboard");
        textInput.getDialogPane().setContentText("Name:");
        // textInput.setGraphic(arg0);
        Optional<String> result = textInput.showAndWait();
        TextField input = textInput.getEditor();

        if(input.getText() != null && input.getText().toString().length() != 0){
            writeToFile( input.getText().toString().replace(" ", "_") +leaderboardInfo+"\n","leaderboard");
        }
    }

    public static void writeToFile(String string, String file) {
        try {
            String statistics = ("./data/"+file);
            FileWriter statWriter = new FileWriter(statistics, true); // the true will append the new data
            statWriter.write(string);
            statWriter.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    public static void deleteFile(String fileName){
        try {
            File file = new File("./data/" + fileName);
            file.delete();
        }
        catch(Exception e) {
        }

    }




}