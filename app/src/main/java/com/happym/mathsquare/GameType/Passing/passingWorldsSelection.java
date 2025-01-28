package com.happym.mathsquare.GameType.Passing;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.GameType.Passing.passingStageSelection;
import com.happym.mathsquare.MusicManager;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;

import com.happym.mathsquare.R;
import java.io.IOException;

public class passingWorldsSelection extends AppCompatActivity {
    private FirebaseFirestore db;
    private LinearLayout world1, world2, world3, world4, world5;
    private String worldlevel;
    private MediaPlayer soundEffectPlayer;
    private String operation, difficulty;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.passing_stage_worlds);
        
        world1 = findViewById(R.id.stage_1);
        world2 = findViewById(R.id.stage_2);
        world3 = findViewById(R.id.stage_3);
        world4 = findViewById(R.id.stage_4);
        world5 = findViewById(R.id.stage_5);
        
        operation = getIntent().getStringExtra("operation");
        difficulty = getIntent().getStringExtra("difficulty");
        
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
        
        world1.setOnClickListener(v -> {
                
            worldlevel = "world_one";
                
                Intent intent = new Intent(passingWorldsSelection.this, passingStageSelection.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", difficulty);
                intent.putExtra("passing", "passing_level_1");
                
                playSound("click.mp3");
    startActivity(intent);
                
            });
        
        world2.setOnClickListener(v -> {
                
            worldlevel = "world_two";
                
                Intent intent = new Intent(passingWorldsSelection.this, passingStageSelection.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", difficulty);
                intent.putExtra("passing", "passing_level_2");
                
                playSound("click.mp3");
    startActivity(intent);
                
            });
        
        world3.setOnClickListener(v -> {
                
            worldlevel = "world_three";
                
                Intent intent = new Intent(passingWorldsSelection.this, passingStageSelection.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", difficulty);
                playSound("click.mp3");
                intent.putExtra("passing", "passing_level_3");
                
    startActivity(intent);
                
            });
        
        world4.setOnClickListener(v -> {
                
            worldlevel = "world_four";
                
                Intent intent = new Intent(passingWorldsSelection.this, passingStageSelection.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", difficulty);
                intent.putExtra("passing", "passing_level_4");
                
                playSound("click.mp3");
    startActivity(intent);
                
            });
        world5.setOnClickListener(v -> {
                
            worldlevel = "world_five";
                
                Intent intent = new Intent(passingWorldsSelection.this, passingStageSelection.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", difficulty);
                intent.putExtra("passing", "passing_level_5");
                
                playSound("click.mp3");
    startActivity(intent);
                
            });
        
        
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
