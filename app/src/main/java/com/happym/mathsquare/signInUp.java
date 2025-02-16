package com.happym.mathsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
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

import android.view.View;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.studentSignUp;
import com.happym.mathsquare.teacherSignUp;
import com.happym.mathsquare.sharedPreferences;
import com.happym.mathsquare.NumberAnimation;


public class signInUp extends AppCompatActivity {
private MediaPlayer soundEffectPlayer;
    private MediaPlayer bgMediaPlayer;
   private FirebaseFirestore db;
    
    private FrameLayout numberContainer,backgroundFrame;
    private final Random random = new Random();
    private final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final int numberCount = 3; // Number of numbers per side
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opening);

        playBGGame("bgmusic.mp3");
        
        db = FirebaseFirestore.getInstance();
        
         // Show Loading Dialog
    ProgressDialog loadingDialog = new ProgressDialog(this);
    loadingDialog.setMessage("Checking account...");
    loadingDialog.setCancelable(true);
    loadingDialog.show();
        
        
             
        if(sharedPreferences.isLoggedIn(this)){
            loadingDialog.dismiss();
                Intent intent = new Intent(signInUp.this, Dashboard.class);
                startActivity(intent);
            Toast.makeText(this, "Welcome back Teacher!", Toast.LENGTH_SHORT).show();
            finish();
            
        }else if(sharedPreferences.StudentIsLoggedIn(this)){
            
                
            String section = sharedPreferences.getSection(this);
    String grade = sharedPreferences.getGrade(this);
    String firstName = sharedPreferences.getFirstN(this);
    String lastName = sharedPreferences.getLastN(this);


    CollectionReference collectionRef = db.collection("Accounts")
            .document("Students")
            .collection("MathSquare");

    // Query to check if a document with the same firstName, lastName, and quizid = "N/A" exists
    collectionRef
            .whereEqualTo("firstName", firstName)
            .whereEqualTo("lastName", lastName)
            .whereEqualTo("grade", lastName)
            .whereEqualTo("section", lastName)
            .get()
            .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        
                       Intent intent = new Intent(signInUp.this, 
MainActivity.class);
    startActivity(intent);
            
                        sharedPreferences.setLoggedIn(this, false);
                        finish();
                    if (!task.getResult().isEmpty()) {
                            Toast.makeText(this, "Account Deleted, Sign Up a new Student Account", Toast.LENGTH_LONG).show();
                            
                            Intent intenttwo = new Intent(signInUp.this, studentSignUp.class);
    startActivity(intenttwo);
                            sharedPreferences.setLoggedIn(this, false);
                            finish();
                            }
                      }  
                        
                   })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error fetching student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    
                   Intent intent = new Intent(signInUp.this, 
MainActivity.class);
    startActivity(intent);
                    finish();
            });
            
        }else{
            loadingDialog.dismiss();
            Toast.makeText(this, "Select to Sign Up", Toast.LENGTH_SHORT).show();
            LinearLayout btnEnterStudent = findViewById(R.id.btn_playgame_as_student);
        LinearLayout btnEnterTeacher = findViewById(R.id.btn_signinteacher);
            LinearLayout btnEnterAsGuest = findViewById(R.id.btn_playgame_as_guest);
        Button btnExit = findViewById(R.id.btn_exitgame);

        animateButtonFocus(btnEnterStudent);
        animateButtonFocus(btnEnterTeacher);
            animateButtonFocus(btnEnterAsGuest);
        animateButtonFocus(btnExit);
        
            
            btnEnterStudent.setOnClickListener(view -> {
                    playSound("click.mp3");
    animateButtonPushDowm(btnEnterStudent);  
    Intent intent = new Intent(signInUp.this, studentSignUp.class);
    startActivity(intent);
                    
                stopButtonFocusAnimation(btnEnterStudent);
                animateButtonFocus(btnEnterStudent);
});

btnEnterTeacher.setOnClickListener(view -> {
                    playSound("click.mp3");
    animateButtonPushDowm(btnEnterTeacher);  
    Intent intent = new Intent(signInUp.this, teacherLogIn.class);
    startActivity(intent);
                stopButtonFocusAnimation(btnEnterTeacher);
                animateButtonFocus(btnEnterTeacher);
});
            
            btnEnterAsGuest.setOnClickListener(view -> {
                    playSound("click.mp3");
    animateButtonPushDowm(btnEnterAsGuest);  
    Intent intent = new Intent(signInUp.this, NumberAnimation.class);
    startActivity(intent);
                stopButtonFocusAnimation(btnEnterAsGuest);
                animateButtonFocus(btnEnterAsGuest);
});

btnExit.setOnClickListener(view -> {
                    playSound("click.mp3");
    animateButtonPushDowm(btnExit);  
    finishAffinity();
    System.exit(0);
});

// Focus Listeners
btnEnterStudent.setOnTouchListener((v, event) -> {
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            animateButtonClick(btnEnterStudent);  // Start touch animation
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            stopButtonFocusAnimation(btnEnterStudent);  // Stop animation when touch is released or canceled
            break;
    }
    return false;  // Return false to allow long click events to be handled
});

btnEnterStudent.setOnLongClickListener(v -> {
    animateButtonPushDowm(btnEnterStudent);  // Start long press animation
    return true;  // Return true to indicate the long press was handled
});


        btnEnterTeacher.setOnTouchListener((v, event) -> {
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            animateButtonClick(btnEnterTeacher);  // Start touch animation
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            stopButtonFocusAnimation(btnEnterTeacher);  // Stop animation when touch is released or canceled
            break;
    }
    return false;  // Return false to allow long click events to be handled
});

btnEnterTeacher.setOnLongClickListener(v -> {
    animateButtonPushDowm(btnEnterTeacher);  // Start long press animation
    return true;  // Return true to indicate the long press was handled
});
        

        
        btnExit.setOnTouchListener((v, event) -> {
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            animateButtonClick(btnExit);  // Start touch animation
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            stopButtonFocusAnimation(btnExit);  // Stop animation when touch is released or canceled
            break;
    }
    return false;  // Return false to allow long click events to be handled
});

btnExit.setOnLongClickListener(v -> {
    animateButtonPushDowm(btnExit);  // Start long press animation
    return true;  // Return true to indicate the long press was handled
});
        


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
            
        }
backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        startNumberAnimationLoop();
        
backgroundFrame.post(this::applyVignetteEffect);

    }
    
   private void playBGGame(String fileName) {
    if (bgMediaPlayer == null) { // Prevent re-initializing
            try {
        AssetFileDescriptor afd = getAssets().openFd(fileName);
        bgMediaPlayer = new MediaPlayer();
        bgMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        bgMediaPlayer.prepare();
        bgMediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            bgMediaPlayer = null;
        });
                
         // Enable looping
        bgMediaPlayer.setLooping(true);
                       
        bgMediaPlayer.start();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
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

    //Game Button Animation Press 

private void animateButtonClick(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.6f, 1.1f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.6f, 1.1f, 1f);

    // Set duration for the animations
    scaleX.setDuration(3000);
    scaleY.setDuration(3000);

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

    private void animateButtonPushDowm(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f);  // Scale down slightly
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f);  // Scale down slightly

    // Set shorter duration for a quick push effect
    scaleX.setDuration(200);
    scaleY.setDuration(200);

    // Use a smooth interpolator
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    scaleX.setInterpolator(interpolator);
    scaleY.setInterpolator(interpolator);

    // Combine the animations into an AnimatorSet
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(scaleX, scaleY);
    
    // Start the animation
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
protected void onDestroy() {
    super.onDestroy();
   if (bgMediaPlayer != null) {
        bgMediaPlayer.release();
        bgMediaPlayer = null;
    }
    
}

    @Override
    protected void onResume() {
        super.onResume();
        
       if (bgMediaPlayer != null) {
            bgMediaPlayer.start();
            }
    }
    
@Override
    protected void onPause() {
        super.onPause();

        if (bgMediaPlayer != null) {
            bgMediaPlayer.pause();
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

    
}


    
