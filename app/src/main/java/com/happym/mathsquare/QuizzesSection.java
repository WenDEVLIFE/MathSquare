package com.happym.mathsquare;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;

import java.util.Random;

// import androidx.activity.EdgeToEdge;
import android.widget.FrameLayout;

import com.happym.mathsquare.Animation.*;
import com.happym.mathsquare.Service.FirebaseDb;

public class QuizzesSection extends AppCompatActivity {

    private FirebaseFirestore db;
    private MediaPlayer soundEffectPlayer;
    private String quizType = "quiz";
    private FrameLayout numberContainer, backgroundFrame;
    private final Random random = new Random();
    private NumBGAnimation numBGAnimation;

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
        db = FirebaseDb.getFirestore();

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
        backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        numBGAnimation = new NumBGAnimation(this, numberContainer);
        numBGAnimation.startNumberAnimationLoop();

        backgroundFrame.post(
                () -> {
                    VignetteEffect.apply(this, backgroundFrame);
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

    /** Sets up a quiz button with Firestore status check and click listener */
    private void setupQuizButton(String quizId, LinearLayout quizButton) {
        // Fetch Firestore status and create if missing
        fetchOrCreateQuizStatus(quizId, quizButton);

        // Set click listener
        quizButton.setOnClickListener(view -> {
            if (!quizButton.isEnabled()) return;

            playSound("click.mp3");
            Intent intent = new Intent(view.getContext(), MultipleChoicePage.class);

            // Shuffle operations
            String[] operations = {"Addition", "Multiplication", "Division", "Subtraction"};
            List<String> operationList = new ArrayList<>(Arrays.asList(operations));
            Collections.shuffle(operationList);

            // Determine difficulty
            String difficulty;
            switch (quizId) {
                case "quiz_1":
                case "quiz_2":
                    difficulty = "Easy";
                    break;
                case "quiz_3":
                case "quiz_4":
                    difficulty = "Medium";
                    break;
                case "quiz_5":
                case "quiz_6":
                    difficulty = "Hard";
                    break;
                default:
                    difficulty = "Easy";
            }

            intent.putExtra("quizId", quizId);
            intent.putStringArrayListExtra("operationList", new ArrayList<>(operationList));
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("game_type", "Quiz");

            view.getContext().startActivity(intent);
        });
    }

    /** Fetches quiz status or creates it if it doesn't exist */
    private void fetchOrCreateQuizStatus(String quizId, LinearLayout quizButton) {
        DocumentReference statusRef = db.collection("Quizzes").document(quizId);

        statusRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, read status
                        String status = documentSnapshot.getString("status");
                        boolean isOpen = "open".equalsIgnoreCase(status);
                        setQuizButtonState(quizButton, isOpen);
                        Log.d("Quiz", quizId + " status: " + status);
                    } else {
                        // Document doesn't exist, create with default status "closed"
                        Map<String, Object> defaultStatus = new HashMap<>();
                        defaultStatus.put("status", "closed");

                        statusRef.set(defaultStatus)
                                .addOnSuccessListener(aVoid -> {
                                    setQuizButtonState(quizButton, false);
                                    Log.d("Quiz", quizId + " status document created with default 'closed'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Quiz", "Failed to create status for " + quizId, e);
                                    setQuizButtonState(quizButton, false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Quiz", "Failed to fetch status for " + quizId, e);
                    setQuizButtonState(quizButton, false);
                });
    }

    /** Updates quiz button UI based on status */
    private void setQuizButtonState(LinearLayout quizButton, boolean isOpen) {
        quizButton.setBackgroundResource(
                isOpen ? R.drawable.btn_short_condition : R.drawable.btn_short_condition_off);
        quizButton.setClickable(isOpen);
        quizButton.setEnabled(isOpen);
    }


    private void updateQuizStatus(String quizId, LinearLayout quizButton) {
        db.collection("Quizzes")
                .document("Status")
                .collection(quizId)
                .document("status")
                .get()
                .addOnSuccessListener(
                        documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String status = documentSnapshot.getString("status");

                                if ("closed".equalsIgnoreCase(status)) {
                                    quizButton.setBackgroundResource(
                                            R.drawable.btn_short_condition_off);
                                    quizButton.setClickable(false);
                                } else {
                                    quizButton.setBackgroundResource(
                                            R.drawable.btn_short_condition);
                                    quizButton.setClickable(true);
                                }
                            } else {
                                // Document doesn't exist; create with default status "open"
                                Map<String, String> defaultStatus = new HashMap<>();
                                defaultStatus.put("status", "open");
                                db.collection("Quizzes")
                                        .document("Status")
                                        .collection(quizId)
                                        .document("status")
                                        .set(defaultStatus)
                                        .addOnSuccessListener(
                                                aVoid ->
                                                        Log.d(
                                                                "Quiz",
                                                                quizId
                                                                        + " created with default status"))
                                        .addOnFailureListener(
                                                e ->
                                                        Log.e(
                                                                "Quiz",
                                                                "Failed to create document for "
                                                                        + quizId,
                                                                e));
                            }
                        })
                .addOnFailureListener(
                        e -> Log.e("Quiz", "Failed to retrieve status for " + quizId, e));
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
        scaleX.setRepeatCount(ObjectAnimator.INFINITE); // Infinite repeat
        scaleX.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation on repeat
        scaleY.setRepeatCount(ObjectAnimator.INFINITE); // Infinite repeat
        scaleY.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation on repeat

        // Combine the animations into an AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }

    // Stop Focus Animation
    private void stopButtonFocusAnimation(View button) {
        AnimatorSet animatorSet = (AnimatorSet) button.getTag();
        if (animatorSet != null) {
            animatorSet.cancel(); // Stop the animation when focus is lost
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        MusicManager.resume();
    }
}
