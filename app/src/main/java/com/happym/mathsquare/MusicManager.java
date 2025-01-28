package com.happym.mathsquare;
import android.media.MediaPlayer;
import java.util.ArrayList;
import java.util.List;

public class MusicManager {
    private static List<MediaPlayer> mediaPlayers = new ArrayList<>();

    public static void addMediaPlayer(MediaPlayer mediaPlayer) {
        mediaPlayers.add(mediaPlayer);
    }

    public static void shutdown() {
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        mediaPlayers.clear();
    }
    
    public static void pause() {
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }
    
    public static void resume() {
    for (MediaPlayer mediaPlayer : mediaPlayers) {
        if (!mediaPlayer.isPlaying()) { // Check if it's NOT playing
            mediaPlayer.start(); // Resume playback
        }
    }
}

}