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