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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.MusicManager;
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
    private FrameLayout numberContainer,backgroundFrame;
    private NumBGAnimation numBGAnimation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.passing_levels);

        // Initialize the levels
        String operation = getIntent().getStringExtra("operation");
       String difficulty = getIntent().getStringExtra("difficulty");
        String passingWorldType = getIntent().getStringExtra("passing");
        
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

        
       
    FirebaseFirestore db = FirebaseFirestore.getInstance();
        
    String section = sharedPreferences.getSection(this);
    String grade = sharedPreferences.getGrade(this);
    String firstName = sharedPreferences.getFirstN(this);
    String lastName = sharedPreferences.getLastN(this);
    
    CollectionReference collectionRef = db.collection("Accounts")
    .document("Students")
    .collection("MathSquare");

// Query to retrieve the student record
collectionRef.whereEqualTo("firstName", firstName)
    .whereEqualTo("lastName", lastName)
    .whereEqualTo("section", section)
    .whereEqualTo("gameType", "Passing")
    .whereEqualTo("grade", grade)
    .get()
    .addOnSuccessListener(queryDocumentSnapshots -> {
        if (queryDocumentSnapshots.isEmpty()) {
             // Loop through UI levels
                        for (int i = 0; i < levels.length; i++) {
                            String currentLevel = "level_" + (i + 1);
                            String nextLevelToUnlock = "level_" + (i + 2);
                            FrameLayout level = levels[i];
                            ImageView flashbox = flashboxes[i];

                            // Handle level clicks)
                            String levelState = (String) level.getContentDescription();
                            if ("Available".equals(levelState)) {
                                level.setOnClickListener(v -> {
                                    playSound("click.mp3");

                                    Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                                    intent.putExtra("operation", operation);
                                    intent.putExtra("passing", currentLevel);
                                    intent.putExtra("game_type", "Passing");
                                    intent.putExtra("passing_world", "world_one");
                                    intent.putExtra("passing_next_level", nextLevelToUnlock);
                                    intent.putExtra("difficulty", difficultySection);

                                    animateButtonClick(level);
                                    stopButtonFocusAnimation(level);
                                    startActivity(intent);
                                });
                            } else {
                                level.setBackgroundResource(R.drawable.btn_short_condition_off);
                                level.setOnClickListener(v ->
                                    Toast.makeText(this, "Complete previous " + currentLevel + " to unlock.", Toast.LENGTH_SHORT).show()
                                );
                            }
                        }
            return;
        }

        for (DocumentSnapshot document : queryDocumentSnapshots) {
            String docId = document.getId();
            DocumentReference studentDocRef = collectionRef.document(docId);

            // Pull the "passing_level_must_complete" if it exists
            passingNextLevel = document.getString("passing_level_must_complete");

            // Prepare containers
            List<String> completedLevels = new ArrayList<>();
            Map<String, String> starsPerLevel = new HashMap<>();

            // Load PassingHistory
            studentDocRef.collection("PassingHistory")
                .get()
                .addOnSuccessListener(historySnapshots -> {
                    for (DocumentSnapshot historyDoc : historySnapshots) {
                        // 1) completed levels in this history entry (if you still need them)
                        List<String> levelList = (List<String>) document.get("passing_completed_levels");
                        if (levelList != null) {
                            completedLevels.addAll(levelList);
                        }

                        // 2) parse the "stars_list" entries
                        List<String> starsList = (List<String>) document.get("stars_list");
                        if (starsList != null) {
                            for (String entry : starsList) {
                                // entry format: "level_3_2 Stars"
                                int lastUnderscore = entry.lastIndexOf('_');
                                if (lastUnderscore > 0) {
                                    String levelKey = entry.substring(0, lastUnderscore);          // "level_3"
                                    String starRating = entry.substring(lastUnderscore + 1);      // "2 Stars"
                                    starsPerLevel.put(levelKey, starRating);
                                }
                            }
                        }
                    }

                    // Determine which levels are completed and which is next
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

                    // Update the UI
                    for (int i = 0; i < levels.length; i++) {
                        String previousLevel = "level_" + (i + 0);
                        String currentLevel = "level_" + (i + 1);
                        String nextLevelToUnlock = "level_" + (i + 2);
                        FrameLayout level = levels[i];
                        ImageView flashbox = flashboxes[i];

                        boolean isCompleted = completedLevels.contains(currentLevel);
                        boolean isAvailable = (i == availableIndex);

                        if (isCompleted) {
                            level.setContentDescription("Completed");
                            level.setBackgroundResource(R.drawable.btn_short_condition);
                            flashbox.setImageResource(R.drawable.transparent_box);

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
                            flashbox.setImageResource(R.drawable.white_box);
                            startFlashingAnimation(flashbox);
                        } else {
                            level.setContentDescription("Not_Available");
                            level.setBackgroundResource(R.drawable.btn_short_condition_off);
                        }

                        // Click handling
                        level.setOnClickListener(v -> {
                            String state = (String) level.getContentDescription();
                            if ("Available".equals(state)) {
                                playSound("click.mp3");
                                Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                                intent.putExtra("operation", operation);
                                intent.putExtra("passing", currentLevel);
                                intent.putExtra("game_type", "Passing");
                                intent.putExtra("passing_world", "world_one");
                                intent.putExtra("passing_next_level", nextLevelToUnlock);
                                intent.putExtra("difficulty", difficultySection);
                                animateButtonClick(level);
                                stopButtonFocusAnimation(level);
                                startActivity(intent);
                            }else if ("Completed".equals(state)) {
                                playSound("click.mp3");
                                Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
                                intent.putExtra("operation", operation);
                                intent.putExtra("passing", currentLevel);
                                intent.putExtra("game_type", "Passing");
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
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                        "Error loading history: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                });
        }
    })
    .addOnFailureListener(e -> {
        Toast.makeText(this,
            "Failed to retrieve student: " + e.getMessage(),
            Toast.LENGTH_SHORT).show();
    });

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
