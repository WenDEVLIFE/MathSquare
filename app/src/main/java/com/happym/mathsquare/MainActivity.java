package com.happym.mathsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.BounceInterpolator;

// import androidx.activity.EdgeToEdge;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;


import android.view.View;
import com.google.firebase.FirebaseApp;


import com.happym.mathsqure.WebViewActivity;
import java.io.IOException;
import org.w3c.dom.Text;

import com.happym.mathsquare.sharedPreferences;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;
    private FloatingTextView floatingTextView;

        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        floatingTextView = findViewById(R.id.floatingTextView);
        floatingTextView.setUseLetters(false); // Set true for letters, false for numbers
        
        FirebaseApp.initializeApp(this);
        
       MusicManager.playBGGame(this, "music.mp3"); // Replace with actual file
        
        TextView txtTitle = findViewById(R.id.text_title);
        Button btnPlay = findViewById(R.id.btn_playgame);
        Button btnTutorial = findViewById(R.id.btn_tutorials);
        Button btnEExit = findViewById(R.id.btn_exitgame);
        LinearLayout btnPlayQuiz = findViewById(R.id.btn_play_quiz);
        LinearLayout btnLogOut = findViewById(R.id.btn_logout);
        
        
       animateButtonFocus(btnLogOut);
        animateButtonFocus(btnPlay);
        animateButtonFocus(btnTutorial);
        animateButtonFocus(btnEExit);
        animateButtonFocus(btnPlayQuiz);
        animateButtonFocus(btnLogOut);
        startRotationAnimation(txtTitle);
        
        if(sharedPreferences.StudentIsLoggedIn(this)){
            
            Toast.makeText(this, "Welcome back Student!", Toast.LENGTH_SHORT).show();
            
        }else{
            
            btnPlayQuiz.setVisibility(View.GONE);
        }
        
        
        btnPlay.setOnClickListener(view -> {
                animateButtonClick(btnPlay);
            Intent intent = new Intent(MainActivity.this, MathOperations.class);
               playSound("click.mp3");
            startActivity(intent);
                stopButtonFocusAnimation(btnPlay);
                animateButtonFocus(btnPlay);
                
        });
        
        
        btnLogOut.setOnClickListener(view -> {
               animateButtonClick(btnLogOut);
               Intent intent = new Intent(this, signInUp.class);
                                sharedPreferences.StudentIsSetLoggedIn(this, false);
                                            sharedPreferences.setLoggedIn(this, false);
sharedPreferences.clearSection(this);
                    sharedPreferences.clearGrade(this);
                    sharedPreferences.clearFirstName(this);
                    sharedPreferences.clearLastName(this);
               playSound("click.mp3");
                                startActivity(intent);
                finish();
                                Toast.makeText(this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                                stopButtonFocusAnimation(btnLogOut);
                                animateButtonFocus(btnLogOut);
                
        });
        
        btnPlayQuiz.setOnClickListener(view -> {
                animateButtonClick(btnPlayQuiz);
            Intent intent = new Intent(MainActivity.this, QuizzesSection.class);
                playSound("click.mp3");
            startActivity(intent);
                stopButtonFocusAnimation(btnPlayQuiz);
                animateButtonFocus(btnPlayQuiz);
        });
        
btnTutorial.setOnClickListener(view -> {
    animateButtonClick(btnTutorial);
    playSound("click.mp3");
    stopButtonFocusAnimation(btnTutorial);
    animateButtonFocus(btnTutorial);

    String youtubeUrl = "https://www.youtube.com/";
   
    Intent intent = new Intent(this, WebViewActivity.class);
    intent.putExtra("URL", youtubeUrl);
    startActivity(intent);
});




        btnEExit.setOnClickListener(view -> {
                animateButtonClick(btnEExit);
                stopButtonFocusAnimation(btnEExit);
               playSound("click.mp3");
            finishAffinity();
            System.exit(0);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
    
    //Game Button Animation Press 

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
    
    //Text Title Animation
    
    private void startRotationAnimation(TextView txtTitle) {
    // Create an ObjectAnimator for rotation to 30 degrees
    ObjectAnimator rotateTo30 = ObjectAnimator.ofFloat(txtTitle, "rotation", 0f, 30f);
    rotateTo30.setDuration(500); // 500 milliseconds for smooth rotation
    rotateTo30.setInterpolator(new LinearInterpolator());

    // Create an ObjectAnimator for rotation to -30 degrees
    ObjectAnimator rotateToMinus30 = ObjectAnimator.ofFloat(txtTitle, "rotation", 30f, -30f);
    rotateToMinus30.setDuration(1000); // 1000 milliseconds to add some time to rotate back
    rotateToMinus30.setInterpolator(new LinearInterpolator());

    // Create an ObjectAnimator for rotation back to 0 degrees
    ObjectAnimator rotateTo0 = ObjectAnimator.ofFloat(txtTitle, "rotation", -30f, 0f);
    rotateTo0.setDuration(500); // 500 milliseconds for smooth rotation back to 0
    rotateTo0.setInterpolator(new LinearInterpolator());

    // Create an AnimatorSet to play animations in sequence
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(rotateTo30, rotateToMinus30, rotateTo0);
    animatorSet.setStartDelay(500); // Add delay between animations if needed
    animatorSet.setInterpolator(new LinearInterpolator());

    // Add a listener to restart the animation once it ends
    animatorSet.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            animatorSet.start(); // Restart the animation when it ends
        }
    });

    // Start the animation
    animatorSet.start();
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
