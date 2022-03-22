package application;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * 
 * @author Bryan Liu
 * This class contains bash commands to be called from other classes
 * Example usage: BashCommand.speakMaori("Aotearoa");
 */
public class BashCommand {
	
	//This is for commands that are not necessary to have a method
	//Type in the full string command to execute
	//@param string
	public static void Bash(String string) {
		try {
			new ProcessBuilder(
					"bash",
					"-c",
					string
					).start();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is a problem with the bash code - General");
		}
	}
	
	//Using the Maori Module pack to speak the words from the word list
	//@param string
	public static void speakMaori(String string) {
		try {
			String newString = removeHyphenFromString(string);
			new ProcessBuilder(
					"/usr/bin/festival", 
					"(voice_akl_mi_pk06_cg)", 
					"(SayText \"" + newString + "\")"
					).start();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is a problem with the bash code - Maori Festival.");
		}
	}
	
	public static void changeSpeedMaori(double speed, String word) {
		try {
			String newString = removeHyphenFromString(word);
			new ProcessBuilder("/usr/bin/festival",
											"(voice_akl_mi_pk06_cg)",
											"(Parameter.set 'Duration_Stretch "+String.valueOf(speed)+")",
											"(utt.save.wave (SayText \""+newString+"\") \".word.wav\" 'riff)").start();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is a problem with the bash code - Maori Festival.");
		}

	}

	public static void play(){
    try
    {
		Media media = new Media(new File("./.word.wav").toURI().toString());  
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
    }
    catch (Exception exc)
    {
        exc.printStackTrace(System.out);
    }
}
	//Using Festival to speak other words
	//Possible uses include "correct" or "incorrect, try once more" etc.
	//@param string
	public static void speak(String string) {
		try {
			String newString = removeHyphenFromString(string);
			new ProcessBuilder(
					"bash", 
					"-c", 
					"echo " + newString + " | festival --tts"
					).start();

			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is a problem with the bash code - Festival.");
		}
	}
	
	public static void wait(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	//Remove hyphens from words so that festival can speak it, i.e. hyphen does not 
	//get passed in as command
	private static String removeHyphenFromString(String string) {
		
		if (string.contains("-")) {
			
			String newString = string.replaceAll("\\-", "");
			return newString;
		}
		return string;
		
	}
	
	
}
