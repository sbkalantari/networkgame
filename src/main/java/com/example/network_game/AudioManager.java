package com.example.network_game;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioManager {
    private static final String BACKGROUND_PATH = "/sound/background.wav";
    private static final String CONNECT_PATH = "/sound/connect.wav";
    private static final String STAGE_COMPLETE_PATH = "/sound/stage_complete.wav";
    private static final String DAMAGE_PATH = "/sound/damage.wav";
    private static final String CUT_PATH = "/sound/cutting.wav";
    private static float masterVolume = 0.0001f;
    private static Clip backgroundClip;

    public static void playBackgroundMusic() {
        try {

            stopAllSounds();

            URL url = AudioManager.class.getResource(BACKGROUND_PATH);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(audioIn);
                setClipVolume(backgroundClip, masterVolume);
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                System.out.println("Background music started");
            } else {
                System.err.println("Background audio file not found: " + BACKGROUND_PATH);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error starting background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setMasterVolume(float volume) {
        masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (backgroundClip != null) {
            setClipVolume(backgroundClip, masterVolume);
        }
        System.out.println("Volume set to: " + masterVolume);
    }

    public static void playConnectSound() {
        playSound(CONNECT_PATH);
    }

    public static void playStageCompleteSound() {
        playSound(STAGE_COMPLETE_PATH);
    }

    public static void playDamageSound() {
        playSound(DAMAGE_PATH);
    }

    public static void playCutSound() {
        playSound(CUT_PATH);
    }

    private static void playSound(String path) {
        try {
            URL url = AudioManager.class.getResource(path);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                setClipVolume(clip, masterVolume);
                clip.start();
                System.out.println("Playing sound: " + path);
            } else {
                System.err.println("Audio file not found: " + path);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setClipVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume == 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            } catch (IllegalArgumentException e) {
                System.err.println("Volume control not supported for clip: " + e.getMessage());
            }
        }
    }

    public static void stopAllSounds() {
        if (backgroundClip != null) {
            if (backgroundClip.isRunning()) {
                backgroundClip.stop();
            }
            backgroundClip.close();
            backgroundClip = null;
            System.out.println("Background music stopped");
        }
    }
}