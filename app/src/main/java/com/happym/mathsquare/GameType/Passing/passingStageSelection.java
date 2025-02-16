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
import java.util.List;
import java.util.Random;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import com.happym.mathsquare.sharedPreferences;

public class passingStageSelection extends AppCompatActivity {

    private FrameLayout levelone, leveltwo, levelthree, levelfour, levelfive,
            levelsix, levelseven, leveleight, levelnine, levelten;
    private ImageView flashone, flashtwo, flashthree, flashfour, flashfive,
            flashsix, flashseven, flasheight, flashnine, flashten;
    private MediaPlayer soundEffectPlayer;
    
    private FrameLayout numberContainer,backgroundFrame;
    private final Random random = new Random();
    private final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final int numberCount = 3; // Number of numbers per side
    
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
        String passingWorldType = getIntent().getStringExtra("passing");
        
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        
       String section = sharedPreferences.getSection(this);
    String grade = sharedPreferences.getGrade(this);
    String firstName = sharedPreferences.getFirstN(this);
    String lastName = sharedPreferences.getLastN(this);
        
CollectionReference collectionRef = db.collection("Accounts")
        .document("Students")
        .collection("MathSquare");

// Query to retrieve the document based on firstName, lastName, section, and grade
collectionRef.whereEqualTo("firstName", firstName)
        .whereEqualTo("lastName", lastName)
        .whereEqualTo("section", section)
        .whereEqualTo("grade", grade)
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    // Retrieve the array of completed levels
                    List<String> completedLevels = (List<String>) document.get("passing_completed_levels");
                    String passingNextLevel = document.getString("passing_level_must_complete");

                    for (int i = 0; i < levels.length; i++) {
                        String levelName = "level_" + (i + 1);

                        // If the level is in the completed array, mark it as "Completed"
                        if (completedLevels != null && completedLevels.contains(levelName)) {
                            levels[i].setContentDescription("Completed");
                            levels[i].setBackgroundResource(R.drawable.btn_short_condition);

                            // Get stars earned for this level
                            String starsEarned = document.getString("passing_" + levelName);
                            if ("1 Star".equals(starsEarned)) {
                                flashboxes[i].setImageResource(R.drawable.ic_star_one);
                            } else if ("2 Stars".equals(starsEarned)) {
                                flashboxes[i].setImageResource(R.drawable.ic_star_two);
                            } else if ("3 Stars".equals(starsEarned)) {
                                flashboxes[i].setImageResource(R.drawable.ic_star_three);
                            }
                        }

                        // Unlock the next level if it matches `passing_level_must_complete`
                        if (levelName.equals(passingNextLevel)) {
                            levels[i].setContentDescription("Available");
                            levels[i].setBackgroundResource(R.drawable.btn_short_condition);
                            flashboxes[i].setImageResource(R.drawable.transparent_box);
                        }
                    }
                }
            }
        })
        .addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to retrieve data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        
// Set click listeners for all levels
for (int i = 0; i < levels.length; i++) {
    int index = i; // Required to use inside lambda
            
     String levelName = "level_" + (i + 0);           
                
    levels[i].setOnClickListener(v -> {
              
               
        Intent intent = new Intent(passingStageSelection.this, MultipleChoicePage.class);
        intent.putExtra("operation", operation);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("game_type", "passing_level");
        intent.putExtra("passing", levelName);           
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
            String levelName = "level_" + (i + 0);
            String levelNameMustBeCompletedNext = "level_" + (i + 1);
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
                        intent.putExtra("passing", levelName);
                intent.putExtra("game_type", "passing_level");
                       intent.putExtra("passing_world", passingWorldType);
                        intent.putExtra("passing_next_level", levelNameMustBeCompletedNext);
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
        
        
        
    backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        startNumberAnimationLoop();
        
backgroundFrame.post(this::applyVignetteEffect);
        
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
