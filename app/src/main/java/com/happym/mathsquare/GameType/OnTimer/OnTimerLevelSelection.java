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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.Animation.VignetteEffect;
import com.happym.mathsquare.MultipleChoicePage;
import com.happym.mathsquare.MultipleChooser;
import com.happym.mathsquare.MusicManager;
import com.happym.mathsquare.Service.FirebaseDb;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;

import com.happym.mathsquare.R;
import com.happym.mathsquare.sharedPreferences;

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


public class OnTimerLevelSelection extends AppCompatActivity {
    private FirebaseFirestore db;
    private MediaPlayer soundEffectPlayer;
    private FrameLayout[] levels;
    private FrameLayout ontimerone, ontimertwo, ontimerthree;
    private String difficultySection;
    private String sectionId;
    private String operation;
    private Snackbar loadingSnackbar;
    private FrameLayout numberContainer, backgroundFrame;
    private final Random random = new Random();
    private final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final int numberCount = 3; // Number of numbers per side


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.ontimer_level_select);
        db = FirebaseDb.getFirestore();

        operation = getIntent().getStringExtra("operation");
        sectionId = getIntent().getStringExtra("sectionId");
        difficultySection = getIntent().getStringExtra("difficulty");

        ontimerone = findViewById(R.id.ontimer_one);

        ontimertwo = findViewById(R.id.ontimer_two);
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

        if ("grade_one".equals(sectionId)) {
            difficultySection = "Easy";
        } else if ("grade_two".equals(sectionId)) {
            difficultySection = "Easy";
        } else if ("grade_three".equals(sectionId)) {
            difficultySection = "Medium";
        } else if ("grade_four".equals(sectionId)) {
            difficultySection = "Medium";
        } else if ("grade_five".equals(sectionId)) {
            difficultySection = "Medium";
        } else if ("grade_six".equals(sectionId)) {
            difficultySection = "Harf";
        } else {
            difficultySection = "Easy";
        }

        levels = new FrameLayout[]{ontimerone, ontimertwo, ontimerthree};

        for (FrameLayout level : levels) {
            animateButtonFocus(level);
        }
        backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container);

        setLevelsEnabled(levels, false);
        loadOnTimerProgress();

        // Background animations
        findViewById(R.id.main).post(() -> VignetteEffect.apply(this, findViewById(R.id.main)));
        startNumberAnimationLoop();

    }

    private void loadOnTimerProgress() {
        String section = sharedPreferences.getSection(this);
        String grade = sharedPreferences.getGrade(this);
        String firstName = sharedPreferences.getFirstN(this);
        String lastName = sharedPreferences.getLastN(this);
        String currentOperation = getIntent().getStringExtra("operation");
        loadingSnackbar = Snackbar.make(findViewById(R.id.main), "Loading progress...", Snackbar.LENGTH_INDEFINITE);
        loadingSnackbar.show();

        db.collection("Accounts").document("Students").collection("MathSquare")
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("section", section)
                .whereEqualTo("grade", grade)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        updateUI(new ArrayList<>(), new HashMap<>());
                        if (loadingSnackbar != null) loadingSnackbar.dismiss();
                        return;
                    }

                    for (DocumentSnapshot studentDoc : queryDocumentSnapshots) {
                        studentDoc.getReference().collection("OnTimerHistory")
                                .whereEqualTo("operation", currentOperation)
                                .whereEqualTo("difficulty", sectionId)
                                .whereEqualTo("gameType", "OnTimer")
                                .get()
                                .addOnSuccessListener(historySnapshots -> {
                                    List<Integer> completedLevels = new ArrayList<>();
                                    Map<Integer, String> statsMap = new HashMap<>();

                                    for (DocumentSnapshot doc : historySnapshots) {
                                        if (doc.contains("level")) {
                                            int lvl = doc.getLong("level").intValue();
                                            long score = doc.contains("score") ? doc.getLong("score") : 0;
                                            long points = doc.contains("points") ? doc.getLong("points") : 0;
                                            long percent = doc.contains("speed_percent") ? doc.getLong("speed_percent") : 0;

                                            completedLevels.add(lvl);
                                            statsMap.put(lvl, score + "/" + "10" + " (" + percent + "%)");
                                        }
                                    }

                                    if (loadingSnackbar != null) loadingSnackbar.dismiss();
                                    updateUI(completedLevels, statsMap);
                                })
                                .addOnFailureListener(e -> {
                                    if (loadingSnackbar != null) loadingSnackbar.dismiss();
                                });
                    }
                });
    }

    private void updateUI(List<Integer> completed, Map<Integer, String> stats) {
        int maxCompleted = completed.stream().max(Integer::compare).orElse(0);
        int availableLevel = maxCompleted + 1;

        for (int i = 0; i < levels.length; i++) {
            int currentLvlNum = i + 1;
            FrameLayout levelFrame = levels[i];
            LinearLayout parent = (LinearLayout) levelFrame.getParent();
            LinearLayout statsBox = (LinearLayout) parent.getChildAt(2);
            TextView statsText = (TextView) statsBox.getChildAt(1);
            ImageView trophyIcon = (ImageView) statsBox.getChildAt(0);

            if (completed.contains(currentLvlNum)) {
                levelFrame.setContentDescription("Completed");
                levelFrame.setBackgroundResource(R.drawable.btn_short_condition);
                statsText.setText(stats.get(currentLvlNum));
            } else if (currentLvlNum == availableLevel) {
                levelFrame.setContentDescription("Available");
                levelFrame.setBackgroundResource(R.drawable.btn_short_condition);
            } else {
                levelFrame.setContentDescription("Not_Available");
                levelFrame.setBackgroundResource(R.drawable.btn_short_condition_off);
            }
            setupClickListener(levelFrame, currentLvlNum);
        }

        loadingSnackbar.dismiss();
        setLevelsEnabled(levels, true);
    }

    private void setupClickListener(FrameLayout frame, int levelNum) {
        frame.setOnClickListener(v -> {
            String state = (String) frame.getContentDescription();
            if ("Available".equals(state) || "Completed".equals(state)) {
                Intent intent = new Intent(this, MultipleChoicePage.class);
                intent.putExtra("operation", operation);
                intent.putExtra("difficulty", difficultySection);
                intent.putExtra("sectionId", sectionId);
                intent.putExtra("game_type", "OnTimer");
                intent.putExtra("ontimer_level", levelNum);
                if(levelNum == 1) { intent.putExtra("heartLimit", 5); intent.putExtra("timerLimit", 2); }
                else if(levelNum == 2) { intent.putExtra("heartLimit", 3); intent.putExtra("timerLimit", 2); }
                else { intent.putExtra("heartLimit", 2); intent.putExtra("timerLimit", 2); }

                playSound("click.mp3");
                animateButtonClick(frame);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Complete previous level first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLevelsEnabled(FrameLayout[] levels, boolean enabled) {
        for (FrameLayout l : levels) {
            l.setEnabled(enabled);
            l.setAlpha(enabled ? 1.0f : 0.6f);
        }
    }
    private void startNumberAnimationLoop() {
        changeNumbers();
    }

    private void changeNumbers() {
        numberContainer.removeAllViews(); // Clear old numbers

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        for (int i = 0; i < numberCount; i++) {
            // Generate unique Y positions for better spread
            float randomY1 = random.nextInt(screenHeight - 300) + 100;
            float randomY2 = random.nextInt(screenHeight - 300) + 100;

            // Left-side number variants (move to the right)
            float startXLeft1 = -300f; // Start outside the screen
            float startXLeft2 = -600f; // Start further outside

            float endXLeft1 = screenWidth - (random.nextInt(screenWidth / 2 - 100) + 100);
            float endXLeft2 = screenWidth - (random.nextInt(screenWidth / 2 - 100) + 150);

            TextView numberLeft1 = createNumberTextView();
            numberLeft1.setText(String.valueOf(numbers[random.nextInt(numbers.length)]));
            numberLeft1.setX(startXLeft1); // Start outside
            numberLeft1.setY(randomY1);
            numberContainer.addView(numberLeft1);
            numberLeft1.postDelayed(() -> animateNumber(numberLeft1, startXLeft1, endXLeft1), random.nextInt(3000)); // Delay up to 3s

            TextView numberLeft2 = createNumberTextView();
            numberLeft2.setText(String.valueOf(numbers[random.nextInt(numbers.length)]));
            numberLeft2.setX(startXLeft2); // Start further outside
            numberLeft2.setY(randomY2);
            numberContainer.addView(numberLeft2);
            numberLeft2.postDelayed(() -> animateNumber(numberLeft2, startXLeft2, endXLeft2), random.nextInt(6000)); // Delay up to 6s

            // Right-side number variants (move to the left)
            float startXRight1 = screenWidth + 300f; // Start outside the screen
            float startXRight2 = screenWidth + 600f; // Start further outside

            float endXRight1 = random.nextInt(screenWidth / 2 - 300) + 100;
            float endXRight2 = random.nextInt(screenWidth / 2 - 300) + 150;

            TextView numberRight1 = createNumberTextView();
            numberRight1.setText(String.valueOf(numbers[random.nextInt(numbers.length)]));
            numberRight1.setX(startXRight1); // Start outside
            numberRight1.setY(randomY1);
            numberContainer.addView(numberRight1);
            numberRight1.postDelayed(() -> animateNumber(numberRight1, startXRight1, endXRight1), random.nextInt(3000)); // Delay up to 3s

            TextView numberRight2 = createNumberTextView();
            numberRight2.setText(String.valueOf(numbers[random.nextInt(numbers.length)]));
            numberRight2.setX(startXRight2); // Start further outside
            numberRight2.setY(randomY2);
            numberContainer.addView(numberRight2);
            numberRight2.postDelayed(() -> animateNumber(numberRight2, startXRight2, endXRight2), random.nextInt(6000)); // Delay up to 6s
        }

        // Repeat the cycle
        numberContainer.postDelayed(this::changeNumbers, 20000);
    }


    private TextView createNumberTextView() {
        TextView textView = new TextView(this);
        textView.setTextSize(200); // Big size
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.GRAY); // Gray text with border
        textView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        return textView;
    }

    private void animateNumber(TextView textView, float startX, float endX) {
        textView.setX(startX);
        textView.setAlpha(0.65f); // Ensure visibility at start

        // Move from left/right to center
        ObjectAnimator moveAnimation = ObjectAnimator.ofFloat(textView, "translationX", startX, endX);
        moveAnimation.setDuration(9000); // Smooth movement
        moveAnimation.setInterpolator(new DecelerateInterpolator());

        // Rotate slightly while moving
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(textView, "rotation", -20f, 20f);
        rotateAnimation.setDuration(5000);
        rotateAnimation.setRepeatMode(ValueAnimator.REVERSE);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        // Play Move & Rotate Together
        AnimatorSet moveAndRotate = new AnimatorSet();
        moveAndRotate.playTogether(moveAnimation, rotateAnimation);
        moveAndRotate.start();

        moveAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Once movement ends, start fading out
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 0.65f, 0);
                fadeOut.setDuration(4000); // Slow disappearance
                fadeOut.setInterpolator(new LinearInterpolator());
                fadeOut.start();
            }
        });
    }

    private void applyVignetteEffect() {


        int width = backgroundFrame.getWidth();
        int height = backgroundFrame.getHeight();

        if (width == 0 || height == 0) return; // Prevents crash if layout is not measured yet

        // Create a bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Define the vignette effect
        RadialGradient gradient = new RadialGradient(
                width / 2f, height / 2f, // Center of the gradient
                Math.max(width, height) * 0.8f, // Radius (70% of the largest dimension)
                new int[]{Color.parseColor("#FFEF47"), Color.parseColor("#898021"), Color.parseColor("#504A31")},
                new float[]{0.2f, 0.6f, 1f}, // Gradient stops
                Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setShader(gradient);
        paint.setAlpha(180); // Adjust transparency

        // Draw gradient on the canvas
        canvas.drawRect(0, 0, width, height, paint);

        // Set as background
        backgroundFrame.setBackground(new BitmapDrawable(getResources(), bitmap));
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