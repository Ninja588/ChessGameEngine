package com.chess;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}



