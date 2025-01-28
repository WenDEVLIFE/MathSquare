package com.happym.mathsquare.GameType.Passing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.MusicManager;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;
import com.happym.mathsquare.MultipleChoicePage;

import com.happym.mathsquare.R;
import java.io.IOException;

public class passingStageSelection extends AppCompatActivity {

    private FrameLayout levelone, leveltwo, levelthree, levelfour, levelfive,
            levelsix, levelseven, leveleight, levelnine, levelten;
    private ImageView flashone, flashtwo, flashthree, flashfour, flashfive,
            flashsix, flashseven, flasheight, flashnine, flashten;
    private MediaPlayer soundEffectPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.passing_levels);

        // Initialize the levels
        
        AppCompatButton btnNext = findViewById(R.id.btn_next);
        AppCompatButton btnBack = findViewById(R.id.btn_back);
        
        animateButtonFocus(btnNext);
        animateButtonFocus(btnBack);
        
        
        btnNext.setOnClickListener(v -> {
                
                animateButtonClick(btnNext);
        stopButtonFocusAnimation(btnNext);
                animateButtonFocus(btnBack);
                playSound("click.mp3");
            btnNext.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
        });
        
        btnBack.setOnClickListener(v -> {
                
                animateButtonClick(btnBack);
        stopButtonFocusAnimation(btnBack);
                animateButtonFocus(btnNext);
                playSound("click.mp3");
            btnNext.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.GONE);
        });
        
        levelone = findViewById(R.id.level_one);
        leveltwo = findViewById(R.id.level_two);
        levelthree = findViewById(R.id.level_three);
        levelfour = findViewById(R.id.level_four);
        levelfive = findViewById(R.id.level_five);
        levelsix = findViewById(R.id.level_six);
        levelseven = findViewById(R.id.level_seven);
        leveleight = findViewById(R.id.level_eight);
        levelnine = findViewById(R.id.level_nine);
        levelten = findViewById(R.id.level_ten);

        flashone = findViewById(R.id.level_one_flash_box);
        flashtwo = findViewById(R.id.level_two_flash_box);
        flashthree = findViewById(R.id.level_three_flash_box);
        flashfour = findViewById(R.id.level_four_flash_box);
        flashfive = findViewById(R.id.level_five_flash_box);
        flashsix = findViewById(R.id.level_six_flash_box);
        flashseven = findViewById(R.id.level_seven_flash_box);
        flasheight = findViewById(R.id.level_eight_flash_box);
        flashnine = findViewById(R.id.level_nine_flash_box);
        flashten = findViewById(R.id.level_ten_flash_box);
        
        String operation = getIntent().getStringExtra("operation");
       String difficulty = getIntent().getStringExtra("difficulty");
        
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

        
        animateButtonFocus(levelone);
        animateButtonFocus(flashone);

        // Add levels and flashboxes to arrays for iteration
        FrameLayout[] levels = {
                levelone, leveltwo, levelthree, levelfour, levelfive,
                levelsix, levelseven, leveleight, levelnine, levelten
        };

        ImageView[] flashboxes = {
                flashone, flashtwo, flashthree, flashfour, flashfive,
                flashsix, flashseven, flasheight, flashnine, flashten
        };
        
        // Array of game types
String[] gameTypes = {
    "passing_level_1", "passing_level_2", "passing_level_3", "passing_level_4", 
    "passing_level_5", "passing_level_6", "passing_level_7", "passing_level_8", 
    "passing_level_9", "passing_level_10"
};

// Set click listeners for all levels
for (int i = 0; i < levels.length; i++) {
    int index = i; // Required to use inside lambda
    levels[i].setOnClickListener(v -> {
        Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
        intent.putExtra("operation", operation);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("game_type", gameTypes[index]);
playSound("click.mp3");
        // Animate and stop button focus
        animateButtonClick(levels[index]);
        stopButtonFocusAnimation(levels[index]);

        startActivity(intent);
    });
}

        
         // Apply animations to all levels and flash boxes
    for (FrameLayout level : levels) {
        animateButtonFocus(level);
    }

    for (ImageView flash : flashboxes) {
        animateButtonFocus(flash);
    }

        for (int i = 0; i < levels.length; i++) {
            FrameLayout level = levels[i];
            ImageView flashbox = flashboxes[i];
            int levelnumber  = i;
            String levelName = "level " + (i + 0);
playSound("click.mp3");
            // Check contentDescription for each level
            String description = (String) level.getContentDescription();
            if ("Not_Available".equals(description)) {
                // If not available, show toast on click and set background
                flashbox.setImageResource(R.drawable.transparent_box);
                level.setOnClickListener(v ->
                        Toast.makeText(this, "Complete previous " + levelName + " to unlock.", Toast.LENGTH_SHORT).show()
                );
            } else if ("Available".equals(description)) {
                // If available, make it flash and enable click
                flashbox.setImageResource(R.drawable.white_box);
                startFlashingAnimation(flashbox);

                level.setOnClickListener(v->{
                    
                        Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
        intent.putExtra("operation", operation);
                
                if(levelnumber > 4){
                    
                    intent.putExtra("difficulty", "Medium");
                }else if(levelnumber > 8){
                    
                    intent.putExtra("difficulty", "Hard");
                }else{
                    
                     
                    intent.putExtra("difficulty", "Easy");
                }
        

        // Animate and stop button focus
        animateButtonClick(level);
        stopButtonFocusAnimation(level);

        startActivity(intent);
                });
            }
        }
        
        
        
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
      private void animateButtonClick(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.6f, 1.1f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.6f, 1.1f, 1f);

    // Set duration for the animations
    scaleX.setDuration(1000);
    scaleY.setDuration(1000);

    // OvershootInterpolator for game-like snappy effect
    OvershootInterpolator overshootInterpolator = new OvershootInterpolator(2f);
    scaleX.setInterpolator(overshootInterpolator);
    scaleY.setInterpolator(overshootInterpolator);

    // Combine animations into a set
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(scaleX, scaleY);
    animatorSet.start();
}

   // Function to animate button focus with a smooth pulsing bounce effect
private void animateButtonFocus(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.06f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.06f, 1f);

    // Set duration for a slower, smoother pulsing bounce effect
    scaleX.setDuration(1000);
    scaleY.setDuration(1000);

    // AccelerateDecelerateInterpolator for smooth pulsing
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    scaleX.setInterpolator(interpolator);
    scaleY.setInterpolator(interpolator);

    // Set repeat count and mode on each ObjectAnimator
    scaleX.setRepeatCount(ObjectAnimator.INFINITE);  // Infinite repeat
    scaleX.setRepeatMode(ObjectAnimator.REVERSE);    // Reverse animation on repeat
    scaleY.setRepeatCount(ObjectAnimator.INFINITE);  // Infinite repeat
    scaleY.setRepeatMode(ObjectAnimator.REVERSE);    // Reverse animation on repeat

    // Combine the animations into an AnimatorSet
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(scaleX, scaleY);
    animatorSet.start();
}

// Stop Focus Animation
private void stopButtonFocusAnimation(View button) {
    AnimatorSet animatorSet = (AnimatorSet) button.getTag();
    if (animatorSet != null) {
        animatorSet.cancel();  // Stop the animation when focus is lost
    }
}
    
    private void startFlashingAnimation(View view) {
        // Create an ObjectAnimator to animate the alpha property
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(500); // Duration for the fade in and out
        animator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse the animation
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat indefinitely
        animator.start();
    }
     @Override
    protected void onStart() {
        super.onStart();

            MusicManager.resume();
        
    }
}
