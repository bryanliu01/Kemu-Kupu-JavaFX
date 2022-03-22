package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;


public class LeaderboardController implements Initializable{
    /* Custom class object to record statistics */
    public class Stats {
        String name;
        String score;
        String topic;
        String date;
        public Stats(String name, String score, String topic, String date) {
            this.name = name;
            this.score = score;
            this.topic = topic;
            this.date = date;
        }
        /* getter methods */
        public String getName(){
            return this.name;
        }
        public String getScore(){
            return this.score;
        }
        public String getTopic(){
            return this.topic;
        }
        public String getDate(){
            return this.date;
        }
    }

    private Stage stage;
    private Scene scene;
    private static List<String> data = new ArrayList<>();

    @FXML
    TableView<Stats> table;
    @FXML
    TableColumn<Stats, String> nameCol;
    @FXML
    TableColumn<Stats, String> scoreCol;
    @FXML
    TableColumn<Stats, String> topicCol;
    @FXML
    TableColumn<Stats, String> dateCol;

    public static void getFromFile(List<String> list, String fileName) {
        File file = new File("./data/"+fileName);
        if(!list.isEmpty()){
            list.clear();
        }
        if (file.length() != 0) {
            try {
                Scanner Scanner = new Scanner(file);
                while (Scanner.hasNextLine()) {
                    list.add(Scanner.nextLine());
            
                }
                Scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getFromFile(data, "leaderboard");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
		topicCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        populateTable();
        scoreCol.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(scoreCol);
        table.sort();
    }

    public void populateTable(){
        for (String dataString : data) {
            String[] dataElements = dataString.split(" ");
            table.getItems().add(new Stats(dataElements[0].replace("_", " "), dataElements[1],dataElements[2].replace("_", " "), dataElements[3]));
        }
    }

    public void returnToMainScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
