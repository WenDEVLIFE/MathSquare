package com.happym.mathsquare.GameType.OnTimer;

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
import com.happym.mathsquare.MultipleChoicePage;
import com.happym.mathsquare.MultipleChooser;
import com.happym.mathsquare.MusicManager;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;

import com.happym.mathsquare.R;
import java.io.IOException;

public class OnTimerLevelSelection extends AppCompatActivity {
    private FirebaseFirestore db;
    
    private MediaPlayer soundEffectPlayer;
    private FrameLayout ontimerone, ontimertwo, ontimerthree;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.ontimer_level_select);
        
       String operation = getIntent().getStringExtra("operation");
       String difficulty = getIntent().getStringExtra("difficulty");
        
     ontimerone = findViewById(R.id.ontimer_one);

         ontimertwo  = findViewById(R.id.ontimer_two);
         ontimerthree = findViewById(R.id.ontimer_three);
        
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
        FrameLayout[] levels = {
            ontimerone, ontimertwo, ontimerthree
            };
        
    for (FrameLayout level : levels) {
        animateButtonFocus(level);
    }

        ontimerone.setOnClickListener(v -> {
            
                Intent intent = new Intent(OnTimerLevelSelection.this, MultipleChoicePage.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "Easy");
                intent.putExtra("game_type", "ontimer_easy");
                playSound("click.mp3");
                animateButtonClick(ontimerone);
        stopButtonFocusAnimation(ontimerone);
                
    startActivity(intent);
                
        });
        
        ontimertwo.setOnClickListener(v -> {
            
                Intent intent = new Intent(OnTimerLevelSelection.this, MultipleChoicePage.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "Medium");
                intent.putExtra("game_type", "ontimer_medium");
                playSound("click.mp3");
                animateButtonClick(ontimertwo);
        stopButtonFocusAnimation(ontimertwo);
                
    startActivity(intent);
                
        });
        
        ontimerthree.setOnClickListener(v -> {
            
                Intent intent = new Intent(OnTimerLevelSelection.this, MultipleChoicePage.class);
    intent.putExtra("operation", operation);
    intent.putExtra("difficulty", "Hard");
                intent.putExtra("game_type", "ontimer_hard");
                playSound("click.mp3");
                animateButtonClick(ontimerthree);
        stopButtonFocusAnimation(ontimerthree);
                
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
     @Override
    protected void onStart() {
        super.onStart();

            MusicManager.resume();
        
    }
}