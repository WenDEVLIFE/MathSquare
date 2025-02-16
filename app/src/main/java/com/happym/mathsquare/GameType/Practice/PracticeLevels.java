package com.happym.mathsquare.GameType.Practice;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import android.graphics.drawable.Drawable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.MultipleChoicePage;
import com.happym.mathsquare.MusicManager;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;

import com.happym.mathsquare.R;
import java.io.IOException;
import java.util.HashSet;
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
import java.util.Random;




public class PracticeLevels extends AppCompatActivity {

    private FrameLayout levelone, leveltwo, levelthree, levelfour, heartChoice, timerChoice, questionChoice;
    private ImageView buttonOne, buttonTwo, buttonThree, buttonFour, iconHeart, iconClock, iconQuestion, imgone,imgtwo
    ,imgthree, imgfour;
    private MediaPlayer soundEffectPlayer;
    private TextView heartTxt, timerTxt, questionTxt;
    private String difficulty;
    HashSet<Integer> selectedButtons = new HashSet<>();
int selectedNumber = 0;
    
    private boolean isButtonOneOn = false;
    private boolean isButtonTwoOn = false;
    private boolean isButtonThreeOn = false;
    private boolean isButtonFourOn = false;

    int heartCount = 3;
    int timerCount = 5;
    int questionCount = 10;
    
    private FrameLayout numberContainer,backgroundFrame;
    private final Random random = new Random();
    private final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final int numberCount = 3; // Number of numbers per side
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.practice_levels);

         // Initialize the levels
        
        AppCompatButton btnNext = findViewById(R.id.btn_next);
        AppCompatButton btnBack = findViewById(R.id.btn_back);
        AppCompatButton btnEnter = findViewById(R.id.btn_enter);
        
        
        levelone = findViewById(R.id.level_one);
        leveltwo = findViewById(R.id.level_two);
        levelthree = findViewById(R.id.level_three);
        levelfour = findViewById(R.id.level_four);
        
        heartChoice = findViewById(R.id.heart_choice);
        timerChoice = findViewById(R.id.timer_choice);
        questionChoice = findViewById(R.id.question_choice);
        
        String operation = getIntent().getStringExtra("operation");
        difficulty = getIntent().getStringExtra("difficulty");
        
        ImageView operationDisplayIcon = findViewById(R.id.difficultyImage);
    // Initialize operation images
    imgone = findViewById(R.id.difficultyImageSrc);
    imgtwo = findViewById(R.id.difficultyImageSrcTwo);
    imgthree = findViewById(R.id.difficultyImageSrcThree);
    imgfour = findViewById(R.id.difficultyImageSrcFour);
        
    // Set images based on the operation
    if ("Addition".equals(operation)) {
        operationDisplayIcon.setImageResource(R.drawable.ic_operation_add);
        imgone.setImageResource(R.drawable.ic_operation_add);
        imgtwo.setImageResource(R.drawable.ic_operation_add);
        imgthree.setImageResource(R.drawable.ic_operation_add);
        imgfour.setImageResource(R.drawable.ic_operation_add);
    } else if ("Subtraction".equals(operation)) {
        operationDisplayIcon.setImageResource(R.drawable.ic_operation_subtract);
        imgone.setImageResource(R.drawable.ic_operation_subtract);
        imgtwo.setImageResource(R.drawable.ic_operation_subtract);
        imgthree.setImageResource(R.drawable.ic_operation_subtract);
        imgfour.setImageResource(R.drawable.ic_operation_subtract);
    } else if ("Multiplication".equals(operation)) {
        operationDisplayIcon.setImageResource(R.drawable.ic_operation_multiply);
        imgone.setImageResource(R.drawable.ic_operation_multiply);
        imgtwo.setImageResource(R.drawable.ic_operation_multiply);
        imgthree.setImageResource(R.drawable.ic_operation_multiply);
        imgfour.setImageResource(R.drawable.ic_operation_multiply);
    } else if ("Division".equals(operation)) {
        operationDisplayIcon.setImageResource(R.drawable.ic_operation_divide);
        imgone.setImageResource(R.drawable.ic_operation_divide);
        imgtwo.setImageResource(R.drawable.ic_operation_divide);
        imgthree.setImageResource(R.drawable.ic_operation_divide);
        imgfour.setImageResource(R.drawable.ic_operation_divide);
    } else {
        operationDisplayIcon.setImageResource(R.drawable.btn_operation_add);
        imgone.setImageResource(R.drawable.btn_operation_add);
        imgtwo.setImageResource(R.drawable.btn_operation_add);
        imgthree.setImageResource(R.drawable.btn_operation_add);
        imgfour.setImageResource(R.drawable.btn_operation_add);
    }


        animateButtonFocus(btnEnter);
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

        
        heartTxt = findViewById(R.id.heart_txt);
        timerTxt = findViewById(R.id.timer_txt);
        questionTxt = findViewById(R.id.question_txt);
        
      /*  
        levelfour = findViewById(R.id.level_four);
        levelfive = findViewById(R.id.level_five);
        levelsix = findViewById(R.id.level_six);
        levelseven = findViewById(R.id.level_seven);
        leveleight = findViewById(R.id.level_eight);
        levelnine = findViewById(R.id.level_nine);
        levelten = findViewById(R.id.level_ten); */
        
        iconHeart = findViewById(R.id.icon_heart);
        iconClock = findViewById(R.id.icon_timer);
        iconQuestion = findViewById(R.id.icon_question);

        buttonOne = findViewById(R.id.level_one_switch);
        buttonTwo = findViewById(R.id.level_two_switch);
        buttonThree = findViewById(R.id.level_three_switch);
        buttonFour = findViewById(R.id.level_four_switch);
     /*   buttonTwo = findViewById(R.id.level_two_flash_box);
        buttonThree = findViewById(R.id.level_three_flash_box);
        buttonFour = findViewById(R.id.level_four_flash_box);
        buttonFive = findViewById(R.id.level_five_flash_box);
        buttonSix = findViewById(R.id.level_six_flash_box);
        buttonSeven = findViewById(R.id.level_seven_flash_box);
        buttonEight = findViewById(R.id.level_eight_flash_box);
        buttonNine = findViewById(R.id.level_nine_flash_box);
        buttonTen = findViewById(R.id.level_ten_flash_box); */

// Set click listeners for the buttons
levelone.setOnClickListener(v -> {
                playSound("click.mp3");
    isButtonOneOn = !isButtonOneOn; // Toggle the state
    if (isButtonOneOn) {
        selectedButtons.add(1); // Add the number to the set
        buttonOne.setImageResource(R.drawable.button_on);
    } else {
        selectedButtons.remove(1); // Remove the number from the set
        buttonOne.setImageResource(R.drawable.button_off);
    }
    updateSelectedNumber(); // Update the total
});

leveltwo.setOnClickListener(v -> {
                playSound("click.mp3");
    isButtonTwoOn = !isButtonTwoOn; // Toggle the state
    if (isButtonTwoOn) {
        selectedButtons.add(2); // Add the number to the set
        buttonTwo.setImageResource(R.drawable.button_on);
    } else {
        selectedButtons.remove(2); // Remove the number from the set
        buttonTwo.setImageResource(R.drawable.button_off);
    }
    updateSelectedNumber(); // Update the total
});

levelthree.setOnClickListener(v -> {
                playSound("click.mp3");
    isButtonThreeOn = !isButtonThreeOn; // Toggle the state
    if (isButtonThreeOn) {
        selectedButtons.add(3); // Add the number to the set
        buttonThree.setImageResource(R.drawable.button_on);
    } else {
        selectedButtons.remove(3); // Remove the number from the set
        buttonThree.setImageResource(R.drawable.button_off);
    }
    updateSelectedNumber(); // Update the total
});

levelfour.setOnClickListener(v -> {
                playSound("click.mp3");
    isButtonFourOn = !isButtonFourOn; // Toggle the state
    if (isButtonFourOn) {
        selectedButtons.add(4); // Add the number to the set
        buttonFour.setImageResource(R.drawable.button_on);
    } else {
        selectedButtons.remove(4); // Remove the number from the set
        buttonFour.setImageResource(R.drawable.button_off);
    }
    updateSelectedNumber(); // Update the total
});
        
        btnEnter.setOnClickListener(v -> {
                playSound("click.mp3");
            // Determine difficulty based on selectedNumber
Random random = new Random();
if (selectedNumber > 4) {
    // Randomly set difficulty to Medium or Hard
    difficulty = random.nextBoolean() ? "Medium" : "Hard";
} else {
    // Randomly set difficulty to Easy or Medium
    difficulty = random.nextBoolean() ? "Easy" : "Medium";
}

// Create intent to start MultipleChoicePage
Intent intent = new Intent(PracticeLevels.this, MultipleChoicePage.class);
intent.putExtra("operation", operation);
intent.putExtra("difficulty", difficulty);
intent.putExtra("game_type", "practice");
                intent.putExtra("heartLimit", heartCount);
intent.putExtra("timerLimit", timerCount);
intent.putExtra("questionLimit", questionCount);
                
// Animate button click and stop animation
animateButtonClick(btnEnter);
stopButtonFocusAnimation(btnEnter);

// Start the activity
startActivity(intent);
  
        });
        
 
        FrameLayout[] levels = {
                levelone, leveltwo, levelthree, levelfour
            /*levelfour, levelfive,
                levelsix, levelseven, leveleight, levelnine, levelten*/
        };
        
        // Apply animations to all levels and flash boxes
    for (FrameLayout level : levels) {
        animateButtonFocus(level);
    }
        
        heartTxt.setText(String.valueOf(heartCount));
    timerTxt.setText(String.valueOf(timerCount));
    questionTxt.setText(String.valueOf(questionCount));
        
        // Set onClickListeners for each FrameLayout
        heartChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playSound("click.mp3");
                    animateButtonClick(heartChoice);
                heartCount++;
                heartTxt.setText(String.valueOf(heartCount));
            }
        });

        timerChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playSound("click.mp3");
                    animateButtonClick(timerChoice);
                timerCount++;
                timerTxt.setText(String.valueOf(timerCount));
            }
        });

        questionChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playSound("click.mp3");
                    animateButtonClick(questionChoice);
                questionCount++;
                questionTxt.setText(String.valueOf(questionCount));
            }
        });
        
        animateButtonFocus(iconHeart);
        animateButtonFocus(iconClock);
        animateButtonFocus(iconQuestion);
        
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
    private void updateSelectedNumber() {
    selectedNumber = 0;
    for (int number : selectedButtons) {
        selectedNumber += number;
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