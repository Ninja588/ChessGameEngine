package com.chess;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SoundManager {
    private static final Logger logger = Logger.getLogger(SoundManager.class.getName());
    public static void playSound(String soundFileName) {
        try {
            if (System.getenv("CI") != null) {
                return;
            }
            File soundFile = new File("sounds/" + soundFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            clip.start();
        } catch(UnsupportedAudioFileException e) {
            logger.log(Level.SEVERE, "Niewspierany plik audio: " + soundFileName, e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Blad IO podczas puszczania dzwieku: " + soundFileName, e);
        } catch (LineUnavailableException e) {
            logger.log(Level.SEVERE, "Brak sciezki audio w pliku: " + soundFileName, e);
        }
    }
}



