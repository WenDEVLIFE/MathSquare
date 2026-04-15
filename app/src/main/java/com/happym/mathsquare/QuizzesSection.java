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
        setContentView(R.layout.layout_quizzes_section);

        FirebaseApp.initializeApp(this);

        LinearLayout quizone = findViewById(R.id.quiz_1);
        LinearLayout quiztwo = findViewById(R.id.quiz_2);
        LinearLayout quizthree = findViewById(R.id.quiz_3);
        LinearLayout quizfour = findViewById(R.id.quiz_4);
        LinearLayout quizfive = findViewById(R.id.quiz_5);
        LinearLayout quizsix = findViewById(R.id.quiz_6);

        db = FirebaseDb.getFirestore();

        animateButtonFocus(quizone);
        animateButtonFocus(quiztwo);
        animateButtonFocus(quizthree);
        animateButtonFocus(quizfour);
        animateButtonFocus(quizfive);
        animateButtonFocus(quizsix);

        // Retrieve student's grade and section from SharedPreferences
        String studentGrade = sharedPreferences.getGrade(this);       // e.g. "3"
        String studentSection = sharedPreferences.getSection(this);   // e.g. "Sampaguita"

        setupQuizButton("quiz_1", quizone, studentGrade, studentSection);
        setupQuizButton("quiz_2", quiztwo, studentGrade, studentSection);
        setupQuizButton("quiz_3", quizthree, studentGrade, studentSection);
        setupQuizButton("quiz_4", quizfour, studentGrade, studentSection);
        setupQuizButton("quiz_5", quizfive, studentGrade, studentSection);
        setupQuizButton("quiz_6", quizsix, studentGrade, studentSection);

        backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container);

        numBGAnimation = new NumBGAnimation(this, numberContainer);
        numBGAnimation.startNumberAnimationLoop();

        backgroundFrame.post(() -> VignetteEffect.apply(this, backgroundFrame));
    }

    /**
     * Sets up a quiz button with Firestore status check and click listener
     */
    private void setupQuizButton(String quizId, LinearLayout quizButton,
                                 String studentGrade, String studentSection) {

        fetchQuizStatus(quizId, quizButton, studentGrade, studentSection);

        quizButton.setOnClickListener(view -> {
            if (!quizButton.isEnabled()) return;

            playSound("click.mp3");
            Intent intent = new Intent(view.getContext(), MultipleChoicePage.class);

            String[] operations = {"Addition", "Multiplication", "Division", "Subtraction"};
            List<String> operationList = new ArrayList<>(Arrays.asList(operations));
            Collections.shuffle(operationList);

            String difficulty;
            switch (quizId) {
                case "quiz_1": case "quiz_2": difficulty = "Easy";   break;
                case "quiz_3": case "quiz_4": difficulty = "Medium"; break;
                case "quiz_5": case "quiz_6": difficulty = "Hard";   break;
                default: difficulty = "Easy";
            }

            intent.putExtra("quizId", quizId);
            intent.putStringArrayListExtra("operationList", new ArrayList<>(operationList));
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("game_type", "Quiz");
            intent.putExtra("student_grade", studentGrade);

            view.getContext().startActivity(intent);
        });
    }
    /**
     * Fetches quiz status from the teacher-created document:
     * Quizzes/{quizId}/{quizId}/quiz_{N}_grade{G}_section{S}
     * <p>
     * Matches exactly how the teacher dashboard saves it via buildSectionQuizSwitches().
     */
    private void fetchQuizStatus(String quizId, LinearLayout quizButton,
                                 String studentGrade, String studentSection) {

        // Build the same docId the teacher dashboard uses
        // e.g. "quiz_1_grade3_sectionSampaguita"
        String docId = quizId + "_grade" + studentGrade + "_section" + studentSection;

        DocumentReference statusRef = db.collection("Quizzes")
                .document(quizId)
                .collection(quizId)   // sub-collection matches teacher's write path
                .document(docId);

        // Default to disabled while loading
        setQuizButtonState(quizButton, false);

        statusRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Quiz", "Failed to listen to status for " + docId, e);
                setQuizButtonState(quizButton, false);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String status = snapshot.getString("status");
                boolean isOpen = "open".equalsIgnoreCase(status);
                setQuizButtonState(quizButton, isOpen);
                Log.d("Quiz", docId + " status: " + status);
            } else {
                // No document yet — teacher hasn't configured this quiz for this section
                setQuizButtonState(quizButton, false);
                Log.d("Quiz", docId + " not found — defaulting to closed");
            }
        });
    }

    /**
     * Updates quiz button UI based on open/closed status
     */
    private void setQuizButtonState(LinearLayout quizButton, boolean isOpen) {
        quizButton.setBackgroundResource(
                isOpen ? R.drawable.btn_short_condition : R.drawable.btn_short_condition_off);
        quizButton.setClickable(isOpen);
        quizButton.setEnabled(isOpen);
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


    @Override
    protected void onStart() {
        super.onStart();

        MusicManager.resume();
    }
}
