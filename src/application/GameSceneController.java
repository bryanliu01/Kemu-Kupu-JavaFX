package application;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

public class GameSceneController {
	
	private Stage stage;
    private Scene scene;
    private Parent root;
	
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
    private Label gameTimerLabel;
    
    private String catagory;
    private Double festivalSpeedValue = 1.00;
    private boolean isPressed = false;
    private Double previousSpeed = 0.00;
    private boolean inFile = false;
    public static int score = 0;
    public static int flawlessBaseScore = 1000;
    public static int imperfectBaseScore = 500;
    public static int scoreMultiplier = 20;
    public static int count = 0;
    boolean wrongOnce = false;
    public static boolean finished = false;
    private static DecimalFormat df = new DecimalFormat("0.00");
    
    private int gracePeriodThreshold = 22;
    private final int countdownTime = 25;
    private GameTimer gameTimer;
	public static int currentTime = 26;
    
    private void stateMachineCheck(){
    	
        String answer = answerField.getText().toLowerCase();
        answer = WordList.macronConversionCheck(answer);
        
        //checks if word is already in file
        if(!inFile) {
            writeToFile(WordList.quizList.get(count) + "\n", "quizzed_words");
            inFile = true;
        }
        
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

        currentTime = countdownTime + 1;
    }
    
	private boolean checkIncorrectSecondTime(String answer) {
		if (wrongOnce && !answer.equals(WordList.quizList.get(count)) && count < 5 && !isPressed) {
            writeToFile(WordList.quizList.get(count)+"\n","incorrect_words");
            increment();
        	wrongOnce = false;
        	BashCommand.speak("Incorrect. Good try!"); BashCommand.wait(2000);
        	
        	isPressed = true;
        	
        	gameTimer.cancelTimer();
        	startGameTimer();
        	if (!finished) {
                //getNumBlanks method invoke
        		blankWord.setText(getNumBlanks(WordList.quizList.get(count).length()));
            	BashCommand.speakMaori(WordList.quizList.get(count)); BashCommand.wait(2000);
            	
        	}
        	
        	else switchStatus();
            
        	return true;
        }
		else return false;
	}

	private boolean checkIncorrectFirstTime(String answer) {
		if (!wrongOnce && !answer.equals(WordList.quizList.get(count)) && count < 5 && !isPressed) {
        	
        	wrongOnce = true;
        	BashCommand.speak("incorrect, try once more"); BashCommand.wait(2000);
        	
        	isPressed = true;
        	
        	gameTimer.cancelTimer();
        	startGameTimer();
        	
        	
        	//showSecondLetter method invoke
        	blankWord.setText(showSomeLetters(WordList.quizList.get(count).length()));
        	
        	return true;
        }
		else return false;
	}

	private boolean checkCorrectSecondTime(String answer) {
		if (answer.equals(WordList.quizList.get(count)) && wrongOnce == true && count < 5 && !isPressed) {
            writeToFile(WordList.quizList.get(count)+"\n","correct_words");
            increment();
        	wrongOnce = false;
        	
        	score = score + Math.min(imperfectBaseScore - ((gracePeriodThreshold - currentTime)*scoreMultiplier), imperfectBaseScore);
            
        	isPressed = true;
        	scoreText.setText("Score: " + score);
        	BashCommand.speak("correct"); BashCommand.wait(2000);
        	gameTimer.cancelTimer();
        	startGameTimer();
        	
        	if (!finished) {
                inFile = false;
                //getNumBlanks method invoke
            	blankWord.setText(getNumBlanks(WordList.quizList.get(count).length()));
            	BashCommand.speakMaori(WordList.quizList.get(count)); BashCommand.wait(2000);
            	
        	}
        	else switchStatus();
        	
        	return true;
        }
		else return false;
	}

	private boolean checkCorrectFirstTime(String answer) {
		if (answer.equals(WordList.quizList.get(count)) && wrongOnce == false && count < 5 && !isPressed) {
            writeToFile(WordList.quizList.get(count)+"\n","correct_words");
            increment();
        	wrongOnce = false;
        	score = score + Math.min(flawlessBaseScore - ((gracePeriodThreshold - currentTime)*scoreMultiplier), flawlessBaseScore);
        	
            isPressed = true;
        	scoreText.setText("Score: " + score);
        	BashCommand.speak("correct"); BashCommand.wait(2000);
        	gameTimer.cancelTimer();
        	startGameTimer();
        	
        	if (!finished) {
        	    inFile = false;
                //getNumBlanks method invoke
            	blankWord.setText(getNumBlanks(WordList.quizList.get(count).length()));
            	BashCommand.speakMaori(WordList.quizList.get(count)); BashCommand.wait(2000);
            	
        	}
        	else switchStatus();
        	
        	return true;

        }
		else return false;
	}

	public void initialise() {

		isPressed = false;
		count = 0;
		score = 0;
		finished = false;

		//Set up timer for game
		startGameTimer();
		listenToTime();

		BashCommand.changeSpeedMaori(1, WordList.quizList.get(count));
		//Sets the number of blank characters "_" depending on the length of the current word
		blankWord.setText(getNumBlanks(WordList.quizList.get(count).length()));

		/**gets text in TextField whenever the enter key is pressed */
		/**MAIN GAME**/
		answerField.setOnKeyPressed((KeyEvent keyEvent) -> {
			if (keyEvent.getCode().equals(KeyCode.ENTER)) {
				//Listen for timer

				stateMachineCheck();

				answerField.clear();
				answerField.requestFocus();

				isPressed = false;
			}

		});
		
		//Update text field when conditions are met for macron usage
		updateTextFieldWithMacrons();
		
		//Listener for slider to dynamically change value
		festivalSpeed.valueProperty().addListener(
				new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
						festivalSpeedLabel.setText("Text Speed: " + df.format(2 - newValue.doubleValue()) + "x");
						festivalSpeedValue = newValue.doubleValue();
					}
				});

		repeatButton.setOnAction((ActionEvent event) -> {
			if (isPressed && (previousSpeed.equals(festivalSpeedValue))) {
				skipButton.setDisable(true);
				repeatButton.setDisable(true);
				submitButton.setDisable(true);
				macronButton.setDisable(true);
				BashCommand.play();
				enableButtons();
			} else {
				BashCommand.changeSpeedMaori(festivalSpeedValue, WordList.quizList.get(count));
				isPressed = true;
				previousSpeed = festivalSpeedValue;
			}

		});
		submitButton.setOnAction((ActionEvent event) -> {
			stateMachineCheck();
			answerField.clear();
			answerField.requestFocus();
		});
		skipButton.setOnAction((ActionEvent event) -> {

        	skipButton.setDisable(true);
        	repeatButton.setDisable(true);
        	submitButton.setDisable(true);
        	macronButton.setDisable(true);

			if (!inFile) {
				writeToFile(WordList.quizList.get(count) + "\n", "quizzed_words");
			}
			answerField.clear();
			answerField.requestFocus();
			writeToFile(WordList.quizList.get(count) + "\n", "incorrect_words");
			increment();
			isPressed = false;

			BashCommand.speak("Skipped. Good try!");BashCommand.wait(2500);
			enableButtons();


			gameTimer.cancelTimer();
			startGameTimer();

			if (!finished) {
				//getNumBlanks method invoke
				blankWord.setText(getNumBlanks(WordList.quizList.get(count).length()));
				BashCommand.speakMaori(WordList.quizList.get(count));
				BashCommand.wait(2000);

			} else switchStatus();

		});
	};


    
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

	private void startGameTimer() {
		gameTimer = new GameTimer(gameTimerLabel, countdownTime);
		gameTimer.startCountdown();
		
	}
	
	public void listenToTime() {
		gameTimerLabel.textProperty().addListener((ov, t, t1) -> {
			currentTime--;

			if (Integer.parseInt(t1) < 0) {
				gameTimer.cancelTimer();
				startGameTimer();
				
				answerField.setEditable(false);
				PauseTransition pause = new PauseTransition(Duration.seconds(1));
				pause.setOnFinished(event -> {
					answerField.setEditable(true);
					answerField.requestFocus();
		        	
		        	if (!wrongOnce) {
		        		BashCommand.speak("Time is up, try once more."); BashCommand.wait(2000);
		        		blankWord.setText(showSomeLetters(WordList.quizList.get(count).length()));
		        		wrongOnce = true;
		        	}
		        	else if (wrongOnce) {
		        		wrongOnce = false;
		        		BashCommand.speak("Time is up, good try."); BashCommand.wait(2000);
						if (!inFile) {
							writeToFile(WordList.quizList.get(count) + "\n", "quizzed_words");
						}
		        		writeToFile(WordList.quizList.get(count)+"\n","incorrect_words");
		        		increment();
		        		if (!finished) {
		                    //getNumBlanks method invoke
		            		blankWord.setText(getNumBlanks(WordList.quizList.get(count).length()));
		                	BashCommand.speakMaori(WordList.quizList.get(count)); BashCommand.wait(2000);
		                	
		            	}
		        	}
					if (count >= 4) {
						gameTimer.cancelTimer();
						switchStatus();
					}
				});
				pause.play();
			}
			
		});
	}

	//This method returns the number of blanks required for the current word
    //This will then be displayed in the label blankWords
    private String getNumBlanks(int wordSize) {
    	String blank = "_";
		for (int i = 1; i < wordSize; i++) {
			
			//If the word being tested has a space in the middle, concatenate a space as blank
			if (WordList.quizList.get(count).charAt(i) == ' ')
				blank = blank + "  ";
			//If the word being tested has a hyphen in the middle, concatenate a space as hyphen
			else if (WordList.quizList.get(count).charAt(i) == '-')
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
				if (WordList.quizList.get(count).charAt(i) == ' ' && i != 2)
					blank = blank + "  "; //Two empty spaces
				
				//Else concatenate the second letter of the word
				else if (i == 1) 
					blank = blank + " " + WordList.quizList.get(count).charAt(i);
				
				//If the word being tested has a hyphen in the middle, concatenate a space as hyphen
				else if (WordList.quizList.get(count).charAt(i) == '-')
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
				if (WordList.quizList.get(count).charAt(i) == ' ' && i != 2)
					blank = blank + "  "; //Two empty spaces
				
				//Else concatenate the second letter of the word
				else if (indexArray.contains(i)) 
					blank = blank + " " + WordList.quizList.get(count).charAt(i);
				
				//If the word being tested has a hyphen in the middle, concatenate a space as hyphen
				else if (WordList.quizList.get(count).charAt(i) == '-')
					blank = blank + " -";
				
				//Else concatenate with normal blank
				else
					blank = blank + " _";
				}
    		
    		indexArray.clear();
    	}
    	
		return blank;
    }

	public void getCatagory(String catagory){
        this.catagory = catagory;
        titleLabel.setText(catagory);
    }
    
    
    
    public void increment() {
    	if (count < 4) {
    		count++;
    	}
    	else {
    		finished = true;
    		
    	}
    }
    
    //Toggle visibility to move towards FinishScene
    public void switchStatus() {
    	if (finished) {
    		gameTimer.cancelTimer();
    		currentTime = countdownTime + 1;
    		
    		submitButton.setVisible(false);
        	answerField.setVisible(false);
        	blankWord.setVisible(false);
        	repeatButton.setVisible(false);
        	skipButton.setVisible(false);
        	festivalSpeed.setVisible(false);
        	festivalSpeedLabel.setVisible(false);
        	macronButton.setVisible(false);
        	gameTimerLabel.setVisible(false);
        	continueButton.setVisible(true);
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

    public void switchToSummaryScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SummaryScene.fxml"));
        root = loader.load();
        SummarySceneController summarySceneController = loader.getController();
        summarySceneController.getScore(score); //grabs score to pass information to next scene
        summarySceneController.getLeaderboardString(" " + String.valueOf(score) + " " + this.catagory.replace(" ", "_") + " " + java.time.LocalDate.now());

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

    public void enableButtons() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				submitButton.setDisable(false);
				skipButton.setDisable(false);
				repeatButton.setDisable(false);
				macronButton.setDisable(false);

			}
		},2050);

	}


}
