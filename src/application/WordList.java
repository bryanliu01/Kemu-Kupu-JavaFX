package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 
 * @author Bryan Liu
 * This class contains method(s) for word list manipulation
 */

public class WordList {

	public static List<String> quizList = new ArrayList<String>();
	
	//Grab random words from the word list for testing. Input the text file name.
	//@param wordListFileName
	public static List<String> getWords(String wordListFileName) throws IOException {
		
		int numWords = 5;
		
		//When getting a new set of words, clear the quiz list first.
		quizList.clear();
		
		List<String> wordList = new ArrayList<>();
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader("words/" +wordListFileName));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				wordList.add(line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		
		//Create random seed object
		Random rand = new Random();
		
		//Find five random unique words in the word list, all words converted to lower case
		for (int i = 0; i < numWords; i++) {
			String randWord = wordList.get(rand.nextInt(wordList.size()));
			String lowerRandWord = randWord.toLowerCase();
			
			if (!quizList.contains(lowerRandWord)) {
				quizList.add(lowerRandWord);
			}
			else {
				while (quizList.contains(lowerRandWord)) {
					randWord = wordList.get(rand.nextInt(wordList.size()));
					lowerRandWord = randWord.toLowerCase();
				}
				
				quizList.add(lowerRandWord);
			}
		}
		
		//**REVEAL FIVE WORDS FOR TESTING**//
		//System.out.println(quizList);
		
		return quizList;
	}
	
	public static String macronConversionCheck(String answer) {
		
		//If string contains ', then replace it and the character in front of it with a macron letter
		//For example, 'a becomes ā
		answer = answer.replaceAll("\\'a", "ā");
		answer = answer.replaceAll("\\'e", "ē");
		answer = answer.replaceAll("\\'i", "ī");
		answer = answer.replaceAll("\\'o", "ō");
		answer = answer.replaceAll("\\'u", "ū");

		return answer;
		
	}
	
	public static List<String> getAllWords(String wordListFileName) throws IOException {
		
		quizList.clear();
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader("words/" +wordListFileName));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				quizList.add(line.toLowerCase());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		
		
		return quizList;

	
	}
}
