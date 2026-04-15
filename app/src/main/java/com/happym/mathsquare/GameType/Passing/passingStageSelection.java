package com.happym.mathsquare.GameType.Passing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.MusicManager;
import com.happym.mathsquare.Service.FirebaseDb;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;
import com.happym.mathsquare.MultipleChoicePage;

import com.happym.mathsquare.R;

import java.io.IOException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

// import androidx.activity.EdgeToEdge;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.BounceInterpolator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import com.happym.mathsquare.sharedPreferences;
import com.happym.mathsquare.Animation.*;

public class passingStageSelection extends AppCompatActivity {

    private FrameLayout levelone, leveltwo, levelthree, levelfour, levelfive,
            levelsix, levelseven, leveleight, levelnine, levelten;
    private ImageView flashone, flashtwo, flashthree, flashfour, flashfive,
            flashsix, flashseven, flasheight, flashnine, flashten;
    private ImageView levelOneStar, levelTwoStar, levelThreeStar,
            levelFourStar, levelFiveStar, levelSixStar,
            levelSevenStar, levelEightStar, levelNineStar, levelTenStar;

    private MediaPlayer soundEffectPlayer;
    private String difficultySection, passingNextLevel;
    private FrameLayout numberContainer, backgroundFrame;
    private NumBGAnimation numBGAnimation;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.passing_levels);

        db = FirebaseDb.getFirestore();

        // Initialize the levels
        String operation = getIntent().getStringExtra("operation");
        String sectionId = getIntent().getStringExtra("difficulty");
        String difficulty = getIntent().getStringExtra("difficulty");
        String passingWorldType = getIntent().getStringExtra("passing");
        boolean reloadProgress = getIntent().getBooleanExtra("reload_progress", false);

        AppCompatButton btnNext = findViewById(R.id.btn_next);
        AppCompatButton btnBack = findViewById(R.id.btn_back);


        animateButtonFocus(btnNext);
        animateButtonFocus(btnBack);

        if ("grade_one".equals(difficulty)) {
            difficultySection = "Easy";
        } else if ("grade_two".equals(difficulty)) {
            difficultySection = "Easy";
        } else if ("grade_three".equals(difficulty)) {
            difficultySection = "Medium";
        } else if ("grade_four".equals(difficulty)) {
            difficultySection = "Medium";
        } else if ("grade_five".equals(difficulty)) {
            difficultySection = "Medium";
        } else if ("grade_six".equals(difficulty)) {
            difficultySection = "Hard";
        } else {
            difficultySection = "Easy";
        }
        btnNext.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);

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

        levelOneStar = findViewById(R.id.level_one_stars);
        levelTwoStar = findViewById(R.id.level_two_stars);
        levelThreeStar = findViewById(R.id.level_three_stars);
        levelFourStar = findViewById(R.id.level_four_stars);
        levelFiveStar = findViewById(R.id.level_five_stars);
        levelSixStar = findViewById(R.id.level_six_stars);
        levelSevenStar = findViewById(R.id.level_seven_stars);
        levelEightStar = findViewById(R.id.level_eight_stars);
        levelNineStar = findViewById(R.id.level_nine_stars);
        levelTenStar = findViewById(R.id.level_ten_stars);


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

        // Array of game types
        String[] gameTypes = {
                "passing_level_1", "passing_level_2", "passing_level_3", "passing_level_4",
                "passing_level_5", "passing_level_6", "passing_level_7", "passing_level_8",
                "passing_level_9", "passing_level_10"
        };


        // Add levels and flashboxes to arrays for iteration
        FrameLayout[] levels = {
                levelone, leveltwo, levelthree, levelfour, levelfive,
                levelsix, levelseven, leveleight, levelnine, levelten
        };

        ImageView[] flashboxes = {
                flashone, flashtwo, flashthree, flashfour, flashfive,
                flashsix, flashseven, flasheight, flashnine, flashten
        };

        ImageView[] starViews = {
                levelOneStar, levelTwoStar, levelThreeStar, levelFourStar, levelFiveStar,
                levelSixStar, levelSevenStar, levelEightStar, levelNineStar, levelTenStar
        };

        for (int i = 0; i < levels.length; i++) {
            String previousLevel = "level_" + (i);
            String currentLevel = "level_" + (i + 1);
            String nextLevelToUnlock = "level_" + (i + 2);
            FrameLayout level = levels[i];
            ImageView flashbox = flashboxes[i];

            if (i == 0) {
                // For level 1 (index 0), set as available and flash
                level.setContentDescription("Available");
                level.setBackgroundResource(R.drawable.btn_short_condition); // Resource for available state
                //startFlashingAnimation(flashbox);
            } else {
                // For all other levels, set as not available
                level.setContentDescription("Not_Available");
                level.setBackgroundResource(R.drawable.btn_short_condition_off);
            }

            // Handle level click actions based on the content description
            String levelState = (String) level.getContentDescription();
            if ("Available".equals(levelState)) {
                level.setOnClickListener(v -> {
                    playSound("click.mp3");

                    Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                    intent.putExtra("operation", operation);
                    intent.putExtra("passing", currentLevel);
                    intent.putExtra("game_type", "Passing");
                    intent.putExtra("sectionId", sectionId);
                    intent.putExtra("passing_world", "world_one");
                    intent.putExtra("passing_next_level", nextLevelToUnlock);
                    intent.putExtra("difficulty", difficultySection);

                    animateButtonClick(level);
                    stopButtonFocusAnimation(level);
                    startActivity(intent);
                });
            } else {
                level.setOnClickListener(v ->
                        Toast.makeText(this, "Complete previous " + previousLevel + " to unlock.", Toast.LENGTH_SHORT).show()
                );
            }
        }
        // Get a reference to the root view for Snackbar, e.g., the activity's content view.
        View rootView = findViewById(android.R.id.content);
        final Snackbar loadingSnackbar = Snackbar.make(rootView, "Loading progress, please wait...", Snackbar.LENGTH_INDEFINITE);
        loadingSnackbar.show();
        setLevelsEnabled(levels, false);
        final long startTime = System.currentTimeMillis();


        String section = sharedPreferences.getSection(this);
        String grade = sharedPreferences.getGrade(this);
        String firstName = sharedPreferences.getFirstN(this);
        String lastName = sharedPreferences.getLastN(this);

        CollectionReference collectionRef = db.collection("Accounts")
                .document("Students")
                .collection("MathSquare");

        Query query = collectionRef
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("section", section)
                .whereEqualTo("grade", grade)
                .whereEqualTo("operation_type", operation);

        if (reloadProgress) {
            // Force re-fetch
            query.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        long elapsed = System.currentTimeMillis() - startTime;
                        if (elapsed > 3000) {
                            Snackbar.make(rootView, "Slow connection detected. Data loaded in " + elapsed / 1000 + " seconds.",
                                    Snackbar.LENGTH_LONG).show();
                        }

                        if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                            for (int i = 0; i < levels.length; i++) {
                                String previousLevel = "level_" + (i);
                                String currentLevel = "level_" + (i + 1);
                                String nextLevelToUnlock = "level_" + (i + 2);
                                FrameLayout level = levels[i];

                                if (i == 0) {
                                    level.setContentDescription("Available");
                                    level.setBackgroundResource(R.drawable.btn_short_condition);
                                    startFlashingAnimation(level); // Flash the level button directly
                                } else {
                                    level.setContentDescription("Not_Available");
                                    level.setBackgroundResource(R.drawable.btn_short_condition_off);
                                    stopFlashingAnimation(level); // Ensure it doesn't flash
                                }

                                String levelState = (String) level.getContentDescription();
                                if ("Available".equals(levelState)) {
                                    level.setOnClickListener(v -> {
                                        playSound("click.mp3");
                                        Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                                        intent.putExtra("operation", operation);
                                        intent.putExtra("passing", currentLevel);
                                        intent.putExtra("game_type", "Passing");
                                        intent.putExtra("sectionId", sectionId);
                                        intent.putExtra("passing_world", "world_one");
                                        intent.putExtra("passing_next_level", nextLevelToUnlock);
                                        intent.putExtra("difficulty", difficultySection);
                                        animateButtonClick(level);
                                        stopButtonFocusAnimation(level);
                                        startActivity(intent);
                                    });
                                } else {
                                    level.setOnClickListener(v ->
                                            Toast.makeText(this, "Complete previous " + previousLevel + " to unlock.", Toast.LENGTH_SHORT).show()
                                    );
                                }
                            }
                            loadingSnackbar.dismiss();
                            setLevelsEnabled(levels, true);
                            return;
                        }

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String docId = document.getId();
                            DocumentReference studentDocRef = collectionRef.document(docId);

                            passingNextLevel = document.getString("passing_level_must_complete");

                            List<String> completedLevels = new ArrayList<>();
                            Map<String, String> starsPerLevel = new HashMap<>();

                            // Now fetch PassingHistory once
                            studentDocRef.collection("PassingHistory")
                                    .get()
                                    .addOnSuccessListener(historySnapshots -> {
                                        for (DocumentSnapshot historyDoc : historySnapshots) {
                                            List<String> levelList = (List<String>) document.get("passing_completed_levels");
                                            if (levelList != null) {
                                                completedLevels.addAll(levelList);
                                            }

                                            List<String> starsList = (List<String>) document.get("stars_list");
                                            if (starsList != null) {
                                                for (String entry : starsList) {
                                                    int lastUnderscore = entry.lastIndexOf('_');
                                                    if (lastUnderscore > 0) {
                                                        String levelKey = entry.substring(0, lastUnderscore);
                                                        String starRating = entry.substring(lastUnderscore + 1);
                                                        starsPerLevel.put(levelKey, starRating);
                                                    }
                                                }
                                            }
                                        }

                                        int maxCompleted = completedLevels.stream()
                                                .map(lvl -> {
                                                    String num = lvl.replaceAll("\\D+", "");
                                                    return num.isEmpty() ? 0 : Integer.parseInt(num);
                                                })
                                                .max(Integer::compare).orElse(0);

                                        if (passingNextLevel == null || passingNextLevel.isEmpty()) {
                                            passingNextLevel = "level_" + (maxCompleted + 1);
                                        }
                                        int availableIndex = Integer.parseInt(passingNextLevel.replaceAll("\\D+", "")) - 1;

                                        for (int i = 0; i < levels.length; i++) {
                                            String previousLevel = "level_" + (i);
                                            String currentLevel = "level_" + (i + 1);
                                            String nextLevelToUnlock = "level_" + (i + 2);
                                            FrameLayout level = levels[i];

                                            boolean isCompleted = completedLevels.contains(currentLevel);
                                            boolean isAvailable = (i == availableIndex);

                                            if (isCompleted) {
                                                level.setContentDescription("Completed");
                                                level.setBackgroundResource(R.drawable.btn_short_condition);
                                                stopFlashingAnimation(level); // Stop flashing for completed

                                                String starsEarned = starsPerLevel.get(currentLevel);
                                                if ("1 Stars".equals(starsEarned)) {
                                                    starViews[i].setImageResource(R.drawable.ic_star_one);
                                                } else if ("2 Stars".equals(starsEarned)) {
                                                    starViews[i].setImageResource(R.drawable.ic_star_two);
                                                } else if ("3 Stars".equals(starsEarned)) {
                                                    starViews[i].setImageResource(R.drawable.ic_star_three);
                                                }
                                            } else if (isAvailable) {
                                                level.setContentDescription("Available");
                                                level.setBackgroundResource(R.drawable.btn_short_condition);
                                                startFlashingAnimation(level); // Flash the available level directly

                                                boolean streak5 = isOnStarStreak(starsPerLevel, 1, 5);
                                                boolean streak3 = isOnStarStreak(starsPerLevel, 1, 3);

                                                if (streak5) {
                                                    showStarStreakDialog("Wow! You're on a 5-level 3-star streak! You're amazing!");
                                                } else if (streak3) {
                                                    showStarStreakDialog("Awesome! You got 3 Stars in a row! Keep it up, star champ!");
                                                }
                                            } else {
                                                level.setContentDescription("Not_Available");
                                                level.setBackgroundResource(R.drawable.btn_short_condition_off);
                                                stopFlashingAnimation(level); // Stop flashing for locked
                                            }

                                            level.setOnClickListener(v -> {
                                                String state = (String) level.getContentDescription();
                                                if ("Available".equals(state) || "Completed".equals(state)) {
                                                    playSound("click.mp3");
                                                    Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                                                    intent.putExtra("operation", operation);
                                                    intent.putExtra("passing", currentLevel);
                                                    intent.putExtra("game_type", "Passing");
                                                    intent.putExtra("sectionId", sectionId);
                                                    intent.putExtra("passing_world", "world_one");
                                                    intent.putExtra("passing_next_level", nextLevelToUnlock);
                                                    intent.putExtra("difficulty", difficultySection);
                                                    animateButtonClick(level);
                                                    stopButtonFocusAnimation(level);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(this,
                                                            "Complete previous " + previousLevel + " to unlock.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            loadingSnackbar.dismiss();
                                            setLevelsEnabled(levels, true);
                                        }
                                    })
                                    .addOnFailureListener(historyError -> {
                                        Toast.makeText(this, "Error loading history: " + historyError.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            // Normal load
            query.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        long elapsed = System.currentTimeMillis() - startTime;
                        if (elapsed > 3000) {
                            Snackbar.make(rootView, "Slow connection detected. Data loaded in " + elapsed / 1000 + " seconds.",
                                    Snackbar.LENGTH_LONG).show();
                        }

                        if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                            for (int i = 0; i < levels.length; i++) {
                                String previousLevel = "level_" + (i);
                                String currentLevel = "level_" + (i + 1);
                                String nextLevelToUnlock = "level_" + (i + 2);
                                FrameLayout level = levels[i];

                                if (i == 0) {
                                    level.setContentDescription("Available");
                                    level.setBackgroundResource(R.drawable.btn_short_condition);
                                    startFlashingAnimation(level); // Flash the level button directly
                                } else {
                                    level.setContentDescription("Not_Available");
                                    level.setBackgroundResource(R.drawable.btn_short_condition_off);
                                    stopFlashingAnimation(level); // Ensure it doesn't flash
                                }

                                String levelState = (String) level.getContentDescription();
                                if ("Available".equals(levelState)) {
                                    level.setOnClickListener(v -> {
                                        playSound("click.mp3");
                                        Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                                        intent.putExtra("operation", operation);
                                        intent.putExtra("passing", currentLevel);
                                        intent.putExtra("game_type", "Passing");
                                        intent.putExtra("sectionId", sectionId);
                                        intent.putExtra("passing_world", "world_one");
                                        intent.putExtra("passing_next_level", nextLevelToUnlock);
                                        intent.putExtra("difficulty", difficultySection);
                                        animateButtonClick(level);
                                        stopButtonFocusAnimation(level);
                                        startActivity(intent);
                                    });
                                } else {
                                    level.setOnClickListener(v ->
                                            Toast.makeText(this, "Complete previous " + previousLevel + " to unlock.", Toast.LENGTH_SHORT).show()
                                    );
                                }
                            }
                            loadingSnackbar.dismiss();
                            setLevelsEnabled(levels, true);
                            return;
                        }

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String docId = document.getId();
                            DocumentReference studentDocRef = collectionRef.document(docId);

                            passingNextLevel = document.getString("passing_level_must_complete");

                            List<String> completedLevels = new ArrayList<>();
                            Map<String, String> starsPerLevel = new HashMap<>();

                            // Now fetch PassingHistory once
                            studentDocRef.collection("PassingHistory")
                                    .get()
                                    .addOnSuccessListener(historySnapshots -> {
                                        for (DocumentSnapshot historyDoc : historySnapshots) {
                                            List<String> levelList = (List<String>) document.get("passing_completed_levels");
                                            if (levelList != null) {
                                                completedLevels.addAll(levelList);
                                            }

                                            List<String> starsList = (List<String>) document.get("stars_list");
                                            if (starsList != null) {
                                                for (String entry : starsList) {
                                                    int lastUnderscore = entry.lastIndexOf('_');
                                                    if (lastUnderscore > 0) {
                                                        String levelKey = entry.substring(0, lastUnderscore);
                                                        String starRating = entry.substring(lastUnderscore + 1);
                                                        starsPerLevel.put(levelKey, starRating);
                                                    }
                                                }
                                            }
                                        }

                                        int maxCompleted = completedLevels.stream()
                                                .map(lvl -> {
                                                    String num = lvl.replaceAll("\\D+", "");
                                                    return num.isEmpty() ? 0 : Integer.parseInt(num);
                                                })
                                                .max(Integer::compare).orElse(0);

                                        if (passingNextLevel == null || passingNextLevel.isEmpty()) {
                                            passingNextLevel = "level_" + (maxCompleted + 1);
                                        }
                                        int availableIndex = Integer.parseInt(passingNextLevel.replaceAll("\\D+", "")) - 1;

                                        for (int i = 0; i < levels.length; i++) {
                                            String previousLevel = "level_" + (i);
                                            String currentLevel = "level_" + (i + 1);
                                            String nextLevelToUnlock = "level_" + (i + 2);
                                            FrameLayout level = levels[i];

                                            boolean isCompleted = completedLevels.contains(currentLevel);
                                            boolean isAvailable = (i == availableIndex);

                                            if (isCompleted) {
                                                level.setContentDescription("Completed");
                                                level.setBackgroundResource(R.drawable.btn_short_condition);
                                                stopFlashingAnimation(level); // Stop flashing for completed

                                                String starsEarned = starsPerLevel.get(currentLevel);
                                                if ("1 Stars".equals(starsEarned)) {
                                                    starViews[i].setImageResource(R.drawable.ic_star_one);
                                                } else if ("2 Stars".equals(starsEarned)) {
                                                    starViews[i].setImageResource(R.drawable.ic_star_two);
                                                } else if ("3 Stars".equals(starsEarned)) {
                                                    starViews[i].setImageResource(R.drawable.ic_star_three);
                                                }
                                            } else if (isAvailable) {
                                                level.setContentDescription("Available");
                                                level.setBackgroundResource(R.drawable.btn_short_condition);
                                                startFlashingAnimation(level); // Flash the available level directly

                                                boolean streak5 = isOnStarStreak(starsPerLevel, 1, 5);
                                                boolean streak3 = isOnStarStreak(starsPerLevel, 1, 3);

                                                if (streak5) {
                                                    showStarStreakDialog("Wow! You're on a 5-level 3-star streak! You're amazing!");
                                                } else if (streak3) {
                                                    showStarStreakDialog("Awesome! You got 3 Stars in a row! Keep it up, star champ!");
                                                }
                                            } else {
                                                level.setContentDescription("Not_Available");
                                                level.setBackgroundResource(R.drawable.btn_short_condition_off);
                                                stopFlashingAnimation(level); // Stop flashing for locked
                                            }

                                            level.setOnClickListener(v -> {
                                                String state = (String) level.getContentDescription();
                                                if ("Available".equals(state) || "Completed".equals(state)) {
                                                    playSound("click.mp3");
                                                    Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                                                    intent.putExtra("operation", operation);
                                                    intent.putExtra("passing", currentLevel);
                                                    intent.putExtra("game_type", "Passing");
                                                    intent.putExtra("sectionId", sectionId);
                                                    intent.putExtra("passing_world", "world_one");
                                                    intent.putExtra("passing_next_level", nextLevelToUnlock);
                                                    intent.putExtra("difficulty", difficultySection);
                                                    animateButtonClick(level);
                                                    stopButtonFocusAnimation(level);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(this,
                                                            "Complete previous " + previousLevel + " to unlock.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            loadingSnackbar.dismiss();
                                            setLevelsEnabled(levels, true);
                                        }
                                    })
                                    .addOnFailureListener(historyError -> {
                                        Toast.makeText(this, "Error loading history: " + historyError.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }


        // Apply animations to all levels and flash boxes.
        for (FrameLayout level : levels) {
            animateButtonFocus(level);
        }

        for (ImageView flash : flashboxes) {
            animateButtonFocus(flash);
        }


        backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        numBGAnimation = new NumBGAnimation(this, numberContainer);
        numBGAnimation.startNumberAnimationLoop();

        backgroundFrame.post(() -> {
            VignetteEffect.apply(this, backgroundFrame);
        });

    }

    private void startShimmerAnimation(ImageView flashbox) {
        flashbox.setVisibility(View.VISIBLE);
        GradientDrawable shimmer = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0x00FFFFFF, 0x66FFFFFF, 0x00FFFFFF}
        );
        flashbox.setImageDrawable(shimmer);
        flashbox.setScaleType(ImageView.ScaleType.FIT_XY);
        flashbox.post(() -> {
            float w = flashbox.getWidth();
            ObjectAnimator sweep = ObjectAnimator.ofFloat(flashbox, "translationX", -w, w);
            sweep.setDuration(1200);
            sweep.setInterpolator(new LinearInterpolator());
            sweep.setRepeatCount(ValueAnimator.INFINITE);
            sweep.setRepeatMode(ValueAnimator.RESTART);
            sweep.setStartDelay(400);
            sweep.start();
            flashbox.setTag(sweep);
        });
    }

    private void startBreatheAnimation(FrameLayout level) {
        // Scale up
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(level, "scaleX", 1f, 1.09f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(level, "scaleY", 1f, 1.09f);
        ObjectAnimator fadeUp   = ObjectAnimator.ofFloat(level, "alpha", 0.85f, 1f);

        // Scale down
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(level, "scaleX", 1.09f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(level, "scaleY", 1.09f, 1f);
        ObjectAnimator fadeDown   = ObjectAnimator.ofFloat(level, "alpha", 1f, 0.85f);

        AnimatorSet expand = new AnimatorSet();
        expand.playTogether(scaleUpX, scaleUpY, fadeUp);
        expand.setDuration(600);
        expand.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet shrink = new AnimatorSet();
        shrink.playTogether(scaleDownX, scaleDownY, fadeDown);
        shrink.setDuration(600);
        shrink.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet breathe = new AnimatorSet();
        breathe.playSequentially(expand, shrink);
        breathe.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                breathe.start();
            }
        });
        breathe.start();
        level.setTag(R.id.level_one, breathe);
    }
    private void stopBreatheAnimation(FrameLayout level) {
        Object tag = level.getTag(R.id.level_one);
        if (tag instanceof AnimatorSet) {
            ((AnimatorSet) tag).cancel();
        }
        level.setScaleX(1f);
        level.setScaleY(1f);
        level.setAlpha(1f);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setLevelsEnabled(FrameLayout[] levels, boolean enabled) {
        for (FrameLayout level : levels) {
            level.setEnabled(enabled);
            level.setClickable(enabled);
            level.setAlpha(enabled ? 1f : 0.5f);
        }
    }

    private boolean isOnStarStreak(Map<String, String> starsPerLevel, int fromLevel, int toLevel) {
        for (int i = fromLevel; i <= toLevel; i++) {
            String levelKey = "level_" + i;
            String stars = starsPerLevel.get(levelKey);
            if (!"3 Stars".equals(stars)) {
                return false;
            }
        }
        return true;
    }

    private void showStarStreakDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_star_streak);
        dialog.setCancelable(true);

        TextView messageText = dialog.findViewById(R.id.starStreakMessage);
        AppCompatButton okBtn = dialog.findViewById(R.id.okBtn);

        messageText.setText(message);
        okBtn.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
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
        Object tag = button.getTag();

        if (tag instanceof Animator) {
            Animator animator = (Animator) tag;
            animator.cancel();
        }
    }
    private void startFlashingAnimation(FrameLayout view) {
        // Prevent stacking multiple animators on the same view
        if (view.getTag() instanceof ValueAnimator) {
            return;
        }

        Drawable background = view.getBackground();
        if (background == null) return;

        // MUST mutate() so we only animate this specific button's background
        final Drawable mutatedBackground = background.mutate();

        // Changed 150 to 255.
        // This will make the tint transition from normal to SOLID WHITE.
        ValueAnimator animator = ValueAnimator.ofInt(0, 255);
        animator.setDuration(600); // 600ms pulse
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.addUpdateListener(animation -> {
            int alpha = (int) animation.getAnimatedValue();
            // Construct a white color with the current animated alpha
            int filterColor = Color.argb(alpha, 255, 255, 255);

            // Apply it over the top of the button shape
            mutatedBackground.setColorFilter(filterColor, PorterDuff.Mode.SRC_ATOP);
        });

        animator.start();

        // Store animator so we can cancel it later
        view.setTag(animator);
    }

    private void stopFlashingAnimation(FrameLayout view) {
        if (view.getTag() instanceof ValueAnimator) {
            ValueAnimator animator = (ValueAnimator) view.getTag();
            animator.cancel();
            view.setTag(null); // Clear the tag
        }

        // Clear the color filter to return the background to its exact original state
        Drawable background = view.getBackground();
        if (background != null) {
            background.clearColorFilter();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        MusicManager.resume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.resume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

}
