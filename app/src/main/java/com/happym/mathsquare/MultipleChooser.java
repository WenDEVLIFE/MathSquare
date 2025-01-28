package com.happym.mathsquare;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.happym.mathsquare.GameType.Practice.PracticeLevels;
import com.happym.mathsquare.GameType.Passing.passingWorldsSelection;
import com.happym.mathsquare.GameType.OnTimer.OnTimerLevelSelection;
import java.io.IOException;

public class MultipleChooser extends AppCompatActivity {
    private MediaPlayer soundEffectPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_multiple_chooser);
        
        String operation = getIntent().getStringExtra("operation");
String gradeLevel = getIntent().getStringExtra("difficulty");

ImageView operationDisplayIcon = findViewById(R.id.difficultyImage);

if ("Addition".equals(operation)) {
    operationDisplayIcon.setImageResource(R.drawable.ic_operation_add);
} else if ("Subtraction".equals(operation)) {
    operationDisplayIcon.setImageResource(R.drawable.ic_operation_subtract);
} else if ("Multiplication".equals(operation)) {
    operationDisplayIcon.setImageResource(R.drawable.ic_operation_multiply);
} else if ("Division".equals(operation)) {
    operationDisplayIcon.setImageResource(R.drawable.ic_operation_divide);
} else {
    // Default icon if no match is found
    operationDisplayIcon.setImageResource(R.drawable.btn_operation_add);
}

        TextView operationDisplay = findViewById(R.id.selectedDifficulty);
        LinearLayout practicebtn = findViewById(R.id.btn_practice);
        LinearLayout passingBtn = findViewById(R.id.passing_btn);
        LinearLayout ontimerBtn = findViewById(R.id.ontimer_btn);
        
        practicebtn.setOnClickListener(v -> {
            Intent intent = new Intent(MultipleChooser.this, PracticeLevels.class);
                intent.putExtra("operation", operation);
    intent.putExtra("difficulty", gradeLevel);
                playSound("click.mp3");
            startActivity(intent);
            });
        
        passingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MultipleChooser.this, passingWorldsSelection.class);
                intent.putExtra("operation", operation);
    intent.putExtra("difficulty", gradeLevel);
                playSound("click.mp3");
            startActivity(intent);
            });
        
        ontimerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MultipleChooser.this, OnTimerLevelSelection.class);
                intent.putExtra("operation", operation);
    intent.putExtra("difficulty", gradeLevel);
                playSound("click.mp3");
            startActivity(intent);
            });
        
        operationDisplay.setText(operation);
        
    }
    
      private void playSound(String fileName) {
        // Stop any previous sound effect before playing a new one
        if (soundEffectPlayer != null) {
            soundEffectPlayer.release();
            soundEffectPlayer = null;
        }

        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            soundEffectPlayer = new MediaPlayer();
            soundEffectPlayer.setDataSource(
                    afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            soundEffectPlayer.prepare();
            soundEffectPlayer.setOnCompletionListener(
                    mp -> {
                        mp.release();
                        soundEffectPlayer = null;
                    });
            soundEffectPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     @Override
    protected void onStart() {
        super.onStart();

            MusicManager.resume();
        
    }
}
