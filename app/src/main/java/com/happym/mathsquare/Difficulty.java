package com.happym.mathsquare;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.core.view.WindowCompat;
// import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class Difficulty extends AppCompatActivity {
    private MediaPlayer soundEffectPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_difficulty);

        // get extras from previous activity
        String operation = getIntent().getStringExtra("operation");

        // difficulty buttons
        int btnEasyOneId = R.id.btn_easy; //Grade one
        int btnEasyTwoId = R.id.btn_easy_two; //Grade two
        int btnEasyThreeId = R.id.btn_easy_three; //Grade three
        int btnMediumId = R.id.btn_medium; //Grade four
        int btnHardId = R.id.btn_hard; //Grade five
        int btnSuperHardId = R.id.btn_superhard; //Grade six
        int btnNextId = R.id.btn_next;
        int btnBackId = R.id.btn_back;

        Button btnEasy = findViewById(btnEasyOneId);
        Button btnEasyTwo = findViewById(btnEasyTwoId);
        Button btnEasyThree = findViewById(btnEasyThreeId);
        Button btnMedium = findViewById(btnMediumId);
        Button btnHard = findViewById(btnHardId);
        Button btnSuperHard = findViewById(btnSuperHardId);
        Button btnNext = findViewById(btnNextId);
        Button btnBack = findViewById(btnBackId);
        
        LinearLayout difficulty_one = findViewById(R.id.difficulty_one);
        LinearLayout difficulty_two = findViewById(R.id.difficulty_two);
        
        animateButtonFocus(btnEasy);
        animateButtonFocus(btnEasyTwo);
        animateButtonFocus(btnEasyThree);
        animateButtonFocus(btnMedium);
        animateButtonFocus(btnHard);
        animateButtonFocus(btnSuperHard);
        animateButtonFocus(btnNext);
        animateButtonFocus(btnBack);
        
        
        btnNext.setOnClickListener(v -> {
                
                animateButtonClick(btnNext);
        stopButtonFocusAnimation(btnNext);
               playSound("click.mp3");
            difficulty_one.setVisibility(View.GONE);
                difficulty_two.setVisibility(View.VISIBLE);
        });
        
        btnBack.setOnClickListener(v -> {
                
                animateButtonClick(btnBack);
        stopButtonFocusAnimation(btnBack);
               playSound("click.mp3");
            difficulty_one.setVisibility(View.VISIBLE);
                difficulty_two.setVisibility(View.GONE);
        });

        btnEasy.setOnClickListener(v -> {
                animateButtonClick(btnEasy);
                
                Intent intent = new Intent(Difficulty.this, MultipleChooser.class);
                String difficutlyType = null;
                intent.putExtra("operation", operation);
                intent.putExtra("difficulty", "grade_one");
                startActivity(intent);
                
                });
       
btnEasyTwo.setOnClickListener(v -> {
    animateButtonClick(btnEasyTwo);

    Intent intent = new Intent(Difficulty.this, MultipleChooser.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "grade_two");
               playSound("click.mp3");
    startActivity(intent);
});

btnEasyThree.setOnClickListener(v -> {
    animateButtonClick(btnEasyThree);

    Intent intent = new Intent(Difficulty.this, MultipleChooser.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "grade_three");
               playSound("click.mp3");
    startActivity(intent);
});

btnMedium.setOnClickListener(v -> {
    animateButtonClick(btnMedium);

    Intent intent = new Intent(Difficulty.this, MultipleChooser.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "grade_four");
               playSound("click.mp3");
    startActivity(intent);
});

btnHard.setOnClickListener(v -> {
    animateButtonClick(btnHard);

    Intent intent = new Intent(Difficulty.this, MultipleChooser.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "grade_five");
               playSound("click.mp3");
    startActivity(intent);
});

btnSuperHard.setOnClickListener(v -> {
    animateButtonClick(btnSuperHard);

    Intent intent = new Intent(Difficulty.this, MultipleChooser.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "grade_six");
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
    
    private void animateButtonClick(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.6f, 1.1f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.6f, 1.1f, 1f);

    // Set duration for the animations
    scaleX.setDuration(3000);
    scaleY.setDuration(3000);

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
    scaleX.setDuration(2000);
    scaleY.setDuration(2000);

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

    private void animateButtonPushDowm(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f);  // Scale down slightly
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f);  // Scale down slightly

    // Set shorter duration for a quick push effect
    scaleX.setDuration(200);
    scaleY.setDuration(200);

    // Use a smooth interpolator
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    scaleX.setInterpolator(interpolator);
    scaleY.setInterpolator(interpolator);

    // Combine the animations into an AnimatorSet
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(scaleX, scaleY);
    
    // Start the animation
    animatorSet.start();
}

// Stop Focus Animation
private void stopButtonFocusAnimation(View button) {
    AnimatorSet animatorSet = (AnimatorSet) button.getTag();
    if (animatorSet != null) {
        animatorSet.cancel();  // Stop the animation when focus is lost
    }
}
     @Override
    protected void onStart() {
        super.onStart();

            MusicManager.resume();
        
    }
}