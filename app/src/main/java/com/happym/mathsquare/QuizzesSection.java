package com.happym.mathsquare;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.BounceInterpolator;
import java.util.Random;

public class QuizzesSection extends AppCompatActivity {
    
       private FirebaseFirestore db;
private MediaPlayer soundEffectPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.layout_quizzes_section); // your layout file

        FirebaseApp.initializeApp(this);

        LinearLayout quizone = findViewById(R.id.quiz_1);
        LinearLayout quiztwo = findViewById(R.id.quiz_2);
        LinearLayout quizthree = findViewById(R.id.quiz_3);
        LinearLayout quizfour = findViewById(R.id.quiz_4);
        LinearLayout quizfive = findViewById(R.id.quiz_5);
        LinearLayout quizsix = findViewById(R.id.quiz_6);

        // Firestore instance
        db = FirebaseFirestore.getInstance();

        animateButtonFocus(quizone);
        animateButtonFocus(quiztwo);
        animateButtonFocus(quizthree);
        animateButtonFocus(quizfour);
        animateButtonFocus(quizfive);
        animateButtonFocus(quizsix);

        // Set OnClickListeners and update status for each quiz
        setupQuizButton("quiz_1", quizone);
        setupQuizButton("quiz_2", quiztwo);
        setupQuizButton("quiz_3", quizthree);
        setupQuizButton("quiz_4", quizfour);
        setupQuizButton("quiz_5", quizfive);
        setupQuizButton("quiz_6", quizsix);
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
    
    private void setupQuizButton(String quizId, LinearLayout quizButton) {
        updateQuizStatus(quizId, quizButton);
playSound("click.mp3");
        quizButton.setOnClickListener(view -> {
            db.collection("Quizzes").document("Status").collection(quizId)
                .document("status")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        if ("closed".equalsIgnoreCase(status)) {
                            // Do nothing if the quiz is closed
                            Log.d("Quiz", quizId + " is closed");
                        } else {
                            // Quiz is open; proceed with intent
                            Intent intent = new Intent(QuizzesSection.this, MultipleChoicePage.class);

                            // Generate random operation and difficulty
                            String[] operations = {"Addition", "Multiplication", "Division", "Subtraction"};
                            String[] difficulties = {"Easy", "Medium", "Hard"};

                            String randomOperation = operations[new Random().nextInt(operations.length)];
                            String randomDifficulty = difficulties[new Random().nextInt(difficulties.length)];

                            // Add extras to intent
                            intent.putExtra("quizId", quizId);
                            intent.putExtra("operation", randomOperation);
                            intent.putExtra("difficulty", randomDifficulty);

                            startActivity(intent);
                        }
                    } else {
                        Log.d("Quiz", quizId + " status document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("Quiz", "Failed to retrieve status for " + quizId, e));
        });
    }

    private void updateQuizStatus(String quizId, LinearLayout quizButton) {
        db.collection("Quizzes").document("Status").collection(quizId)
            .document("status")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String status = documentSnapshot.getString("status");

                    if ("closed".equalsIgnoreCase(status)) {
                        quizButton.setBackgroundResource(R.drawable.btn_short_condition_off);
                        quizButton.setClickable(false);
                    } else {
                        quizButton.setBackgroundResource(R.drawable.btn_short_condition);
                        quizButton.setClickable(true);
                    }
                } else {
                    // Document doesn't exist; create with default status "open"
                    Map<String, String> defaultStatus = new HashMap<>();
                    defaultStatus.put("status", "open");
                    db.collection("Quizzes").document("Status").collection(quizId)
                        .document("status")
                        .set(defaultStatus)
                        .addOnSuccessListener(aVoid -> Log.d("Quiz", quizId + " created with default status"))
                        .addOnFailureListener(e -> Log.e("Quiz", "Failed to create document for " + quizId, e));
                }
            })
            .addOnFailureListener(e -> Log.e("Quiz", "Failed to retrieve status for " + quizId, e));
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


