package com.happym.mathsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;

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


import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;
    private FloatingTextView floatingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
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

        if (sharedPreferences.StudentIsLoggedIn(this)) {
            Toast.makeText(this, "Welcome back Student!", Toast.LENGTH_SHORT).show();
        } else {
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
        if (soundEffectPlayer != null) {
            soundEffectPlayer.release();
            soundEffectPlayer = null;
        }

        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            soundEffectPlayer = new MediaPlayer();
            soundEffectPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            soundEffectPlayer.prepare();
            soundEffectPlayer.setOnCompletionListener(mp -> {
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
        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(2f);
        scaleX.setInterpolator(overshootInterpolator);
        scaleY.setInterpolator(overshootInterpolator);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }

    private void animateButtonFocus(View button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.06f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.06f, 1f);
        scaleX.setDuration(2000);
        scaleY.setDuration(2000);
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        scaleX.setInterpolator(interpolator);
        scaleY.setInterpolator(interpolator);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }

    private void stopButtonFocusAnimation(View button) {
        AnimatorSet animatorSet = (AnimatorSet) button.getTag();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    private void startRotationAnimation(TextView txtTitle) {
        ObjectAnimator rotateTo30 = ObjectAnimator.ofFloat(txtTitle, "rotation", 0f, 30f);
        rotateTo30.setDuration(500);
        rotateTo30.setInterpolator(new LinearInterpolator());
        ObjectAnimator rotateToMinus30 = ObjectAnimator.ofFloat(txtTitle, "rotation", 30f, -30f);
        rotateToMinus30.setDuration(1000);
        rotateToMinus30.setInterpolator(new LinearInterpolator());
        ObjectAnimator rotateTo0 = ObjectAnimator.ofFloat(txtTitle, "rotation", -30f, 0f);
        rotateTo0.setDuration(500);
        rotateTo0.setInterpolator(new LinearInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotateTo30, rotateToMinus30, rotateTo0);
        animatorSet.setStartDelay(500);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet.start();
            }
        });
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