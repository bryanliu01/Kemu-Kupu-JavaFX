package application;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PracticeSceneController {
	
	private Stage stage;
    private Scene scene;
	
    @FXML
    private Label titleLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Button skipButton;
    @FXML
    private TextField answerField;
    @FXML
    private Slider festivalSpeed;
    @FXML
    private Label festivalSpeedLabel;
    @FXML
    private Label scoreText;
    @FXML
    private Label blankWord;
    @FXML
    private Button continueButton;
    @FXML
    private Button repeatButton;
    @FXML
    private Button macronButton;
    @FXML
    private Label selfReflection;
    
    private String catagory;
    private Double festivalSpeedValue = 1.00;
    private boolean repeatPressed = false;
    private Double previousSpeed = 0.00;
    private String randomWord;
    ArrayList<String> wordList = new ArrayList<String>();
    
    boolean wrongOnce = false;
	private boolean gameIsPaused;
    public static boolean finished = false;
    private static DecimalFormat df = new DecimalFormat("0.00");
    
    
    public void initialise(){
		randomWord = getRandomWord();
		
    	BashCommand.changeSpeedMaori(1, randomWord);
    	//Sets the number of blank characters "_" depending on the length of the current word
    	blankWord.setText(getNumBlanks(randomWord.length()));
    	
        /**gets text in TextField whenever the enter key is pressed */
    	/**MAIN GAME**/
        answerField.setOnKeyPressed((KeyEvent keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
        		
                stateMachineCheck();
                answerField.clear();
                answerField.requestFocus();
            }
            
        });
        
        updateTextFieldWithMacrons();
        
        //Listener for slider to dynamically change value
        festivalSpeed.valueProperty().addListener(
            new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number > observable, Number oldValue, Number newValue)
           {
               festivalSpeedLabel.setText("Text Speed: " + df.format(2 - newValue.doubleValue())+"x");
               festivalSpeedValue = newValue.doubleValue();
            }
        });
        
        repeatButton.setOnAction((ActionEvent event) -> {
            if(repeatPressed && (previousSpeed.equals(festivalSpeedValue))){
                BashCommand.play();
            }
            else{ 
            BashCommand.changeSpeedMaori(festivalSpeedValue, randomWord);
            repeatPressed = true;
            previousSpeed = festivalSpeedValue;
            }
            
            });
        submitButton.setOnAction((ActionEvent event) -> {
            stateMachineCheck();
            answerField.clear();
            answerField.requestFocus();
        });
        skipButton.setOnAction((ActionEvent event) -> {
            repeatPressed = false;
            BashCommand.speak("Skipped. Good try!"); BashCommand.wait(2500);
            //getNumBlanks method invoke
            randomWord = getRandomWord();
            blankWord.setText(getNumBlanks(randomWord.length()));
            BashCommand.speakMaori(randomWord); BashCommand.wait(2000);
                
            
        });
    };
    
    private void stateMachineCheck(){
    	
        String answer = answerField.getText().toLowerCase();
        answer = WordList.macronConversionCheck(answer);
        
        /**STATE MACHINE**/
        //state = correct first time
        if (checkCorrectFirstTime(answer));

        //state = correct second time
        else if (checkCorrectSecondTime(answer));

        //state = incorrect first time
        //hint is given, some letters are revealed
        else if (checkIncorrectFirstTime(answer));
        
        //state = incorrect second time
        else if (checkIncorrectSecondTime(answer));
        
        else if (pauseTheGame()) {
        	
        }
    }
    
	

	private boolean pauseTheGame() {
		if (gameIsPaused) {
			selfReflection.setVisible(false);
        	gameIsPaused = false;
        	
        	//Get next random Word
        	randomWord = getRandomWord();
            blankWord.setText(getNumBlanks(randomWord.length()));
            BashCommand.speakMaori(randomWord); BashCommand.wait(2000);
            
            return true;
		}
		
		else return false;
	}

	private boolean checkIncorrectSecondTime(String answer) {
		if (wrongOnce && !answer.equals(randomWord) && !gameIsPaused) {
        	blankWord.setText(showWord());
        	wrongOnce = false;
        	BashCommand.speak("Incorrect. Good try!"); BashCommand.wait(2000);
        	
        	gameIsPaused = true;
        	selfReflection.setVisible(true);
        	
        	return true;
        }
		else return false;
	}

	private boolean checkIncorrectFirstTime(String answer) {
		if (!wrongOnce && !answer.equals(randomWord) && !gameIsPaused) {
        	
        	wrongOnce = true;
        	BashCommand.speak("incorrect, try once more"); BashCommand.wait(2000);
        	
        	//Show some letters to user
        	blankWord.setText(showSomeLetters(randomWord.length()));
        	BashCommand.speakMaori(randomWord); BashCommand.wait(2000);
        	
        	return true;
        }
		
		else return false;
	}

	private boolean checkCorrectFirstTime(String answer) {
		if (answer.equals(randomWord) && wrongOnce == false && !gameIsPaused) {
        	wrongOnce = false;
        	
            repeatPressed = false;
        	BashCommand.speak("correct"); BashCommand.wait(2000);
        	
            //Get next random word
        	randomWord = getRandomWord();
            blankWord.setText(getNumBlanks(randomWord.length()));
            BashCommand.speakMaori(randomWord); BashCommand.wait(2000);
            
            return true;
        }
		else return false;
		
	}
	
	private boolean checkCorrectSecondTime(String answer) {
		if (answer.equals(randomWord) && wrongOnce == true && !gameIsPaused) {
        	wrongOnce = false;
            repeatPressed = false;
        	BashCommand.speak("correct"); BashCommand.wait(2000);
        	
        	//Get next random Word
        	randomWord = getRandomWord();
            blankWord.setText(getNumBlanks(randomWord.length()));
            BashCommand.speakMaori(randomWord); BashCommand.wait(2000);
            	
            return true;
        }
		
		else return false;
	}

	
    

	//Convert to macron in real time when period is typed
	private void updateTextFieldWithMacrons() {
		answerField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent k) {
                
                if (k.getCode().equals(KeyCode.PERIOD)) {
                    if (answerField.getText().length() > 1) {
                        int macronPosition = answerField.getCaretPosition() - 2;
                        int newPosition = macronPosition + 1;
                        StringBuilder answerFieldText = new StringBuilder(answerField.getText());
                        char secondLastChar = answerFieldText.charAt(macronPosition);
                        
                        switch (secondLastChar) {
                        
                        	case ('a'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'ā');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        	
                        	case ('e'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'ē');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('i'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'ī');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('o'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'ō');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('u'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'ū');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('A'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'Ā');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('E'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'Ē');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('I'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'Ī');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('O'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'Ō');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        		
                        	case ('U'): 
                        		answerFieldText = deleteTwoChar(answerFieldText, macronPosition, 'Ū');
                        		answerField.setText(answerFieldText.toString());
                        		answerField.positionCaret(newPosition);
                        		break;
                        	
                        }
                        
                    }
                }
            }
        });
		
	}
	
	public StringBuilder deleteTwoChar(StringBuilder userInputText, int macronPosition, char macron) {
		
		userInputText = userInputText.deleteCharAt(macronPosition);
        userInputText = userInputText.deleteCharAt(macronPosition);
        userInputText = userInputText.insert(macronPosition, macron);
        return userInputText;
    }

	//This method returns the number of blanks required for the current word
    //This will then be displayed in the label blankWords
    private String getNumBlanks(int wordSize) {
    	String blank = "_";
		for (int i = 1; i < wordSize; i++) {
			
			//If the word being tested has a space in the middle, concatenate a space as blank
			if (randomWord.charAt(i) == ' ')
				blank = blank + "  ";
			//If the word being tested has a hyphen in the middle, concatenate a space as hyphen
			else if (randomWord.charAt(i) == '-')
				blank = blank + " -";
			//Else concatenate with normal blank
			else
				blank = blank + " _";
		}
		return blank;
	}
    
    
    //This method is called when the word is spelled incorrectly once
    //Returns the string where some letters of the word are shown
    private String showSomeLetters(int wordSize) {
    	String blank = "_";
    	
    	if (wordSize <= 2) {
			for (int i = 1; i < wordSize; i++) {
				
				//If the word being tested has a space in the middle, concatenate a space as blank
				if (randomWord.charAt(i) == ' ' && i != 2)
					blank = blank + "  "; //Two empty spaces
				
				//Else concatenate the second letter of the word
				else if (i == 1) 
					blank = blank + " " + randomWord.charAt(i);
				
				//If the word being tested has a hyphen in the middle, concatenate a space as hyphen
				else if (randomWord.charAt(i) == '-')
					blank = blank + " -";
				
				//Else concatenate with normal blank
				else
					blank = blank + " _";
				}
    	}
    	
    	else {
    		Random rand = new Random();
    		ArrayList<Integer>indexArray = new ArrayList<Integer>();
    		double numberOfLetters = Math.floor(0.5*wordSize);
    		for (int i = 0; i < numberOfLetters; i++) {
    			indexArray.add(rand.nextInt(wordSize));
    		}
    		
    		for (int i = 1; i < wordSize; i++) {
				
				//If the word being tested has a space in the middle, concatenate a space as blank
				if (randomWord.charAt(i) == ' ' && i != 2)
					blank = blank + "  "; //Two empty spaces
				
				//Else concatenate the second letter of the word
				else if (indexArray.contains(i)) 
					blank = blank + " " + randomWord.charAt(i);
				
				//If the word being tested has a hyphen in the middle, concatenate a space as hyphen
				else if (randomWord.charAt(i) == '-')
					blank = blank + " -";
				
				//Else concatenate with normal blank
				else
					blank = blank + " _";
				}
    		
    		indexArray.clear();
    	}
    	
		return blank;
    }
    
    private String showWord() {
    	
    	return randomWord;
    }

	public void getCatagory(String catagory){
        this.catagory = catagory;
        titleLabel.setText(catagory);
    }
    


    public void moveToPracticeModuleScene(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("PracticeModuleScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


    }

    public void macronButton(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Macron");
        alert.setHeaderText("Please type in a vowel followed by a full stop to make a macron!\nFor example, type a. to make ā");
        alert.show();

    }
    
    public String getRandomWord() {
    	Random rand = new Random();
		randomWord = WordList.quizList.get(rand.nextInt(WordList.quizList.size()));
		return randomWord;
    }


}
