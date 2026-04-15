package com.happym.mathsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.SetOptions;
import com.happym.mathsquare.Service.FirebaseDb;
import com.happym.mathsquare.studentSignUp;
import com.happym.mathsquare.teacherSignUp;
import com.happym.mathsquare.sharedPreferences;
import com.happym.mathsquare.NumberAnimation;
import com.happym.mathsquare.Animation.*;

public class signInUp extends AppCompatActivity {

    private MediaPlayer soundEffectPlayer;
    private MediaPlayer bgMediaPlayer;
    private FirebaseFirestore db;
    private NumBGAnimation numBGAnimation;
    private FrameLayout numberContainer, backgroundFrame;
    private final Random random = new Random();

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opening);

        playBGGame("bgmusic.mp3");

        db = FirebaseDb.getFirestore();

        boolean isGuestCheckingIn = getIntent().getBooleanExtra("guest_checkingin_for_login", false);

        showCustomLoadingDialog("Checking Your Account...");
        boolean isGuest = sharedPreferences.getGuestAccountStatus(this);

        if (sharedPreferences.isAdminLoggedIn(this) && !isGuestCheckingIn) {
            dismissLoadingDialog();
            Intent intent = new Intent(signInUp.this, AdminActivity.class);
            startActivity(intent);
            finish();
        }
        else if (sharedPreferences.isLoggedIn(this) && !isGuestCheckingIn) {
            dismissLoadingDialog();
            Intent intent = new Intent(signInUp.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
        else if (isGuest && !isGuestCheckingIn) {
            dismissLoadingDialog();
            Intent intent = new Intent(signInUp.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            Toast.makeText(this, "Welcome back, Guest!", Toast.LENGTH_SHORT).show();
        }
        else if (sharedPreferences.StudentIsLoggedIn(this) && !isGuest && !isGuestCheckingIn) {

            String section = sharedPreferences.getSection(this);
            String grade = sharedPreferences.getGrade(this);
            String firstName = sharedPreferences.getFirstN(this);
            String lastName = sharedPreferences.getLastN(this);

            CollectionReference collectionRef = db.collection("Accounts")
                    .document("Students")
                    .collection("MathSquare");

            String studentDocId = (firstName.toLowerCase() + "_" +
                    lastName.toLowerCase() + "_" +
                    grade.toLowerCase() + "_" +
                    section.toLowerCase()).replaceAll("\\s+", "");

            DocumentReference docRef = db.collection("Accounts")
                    .document("Students")
                    .collection("MathSquare")
                    .document(studentDocId);

            docRef.get().addOnCompleteListener(task -> {
                dismissLoadingDialog();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Intent intent = new Intent(signInUp.this, MainActivity.class);
                        startActivity(intent);
                        // Added Fade Transition
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                        Toast.makeText(this, "Welcome back, " + firstName + "!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Account not found. Please Sign Up again.", Toast.LENGTH_LONG).show();

                        sharedPreferences.StudentIsSetLoggedIn(signInUp.this, false);

                        Intent intent = new Intent(signInUp.this, studentSignUp.class);
                        startActivity(intent);
                        // Added Fade Transition
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }
            }).addOnFailureListener(e -> {
                dismissLoadingDialog();
                Toast.makeText(this, "Error checking account: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });

        }
        else {
            dismissLoadingDialog();
            if (isGuestCheckingIn) {
                Toast.makeText(this, "Select an account type to log in", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Select to Sign Up", Toast.LENGTH_SHORT).show();
            }

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
                // Added Fade Transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                stopButtonFocusAnimation(btnEnterStudent);
                animateButtonFocus(btnEnterStudent);
            });

            btnEnterTeacher.setOnClickListener(view -> {
                playSound("click.mp3");
                animateButtonPushDowm(btnEnterTeacher);
                Intent intent = new Intent(signInUp.this, teacherLogIn.class);
                startActivity(intent);
                // Added Fade Transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                stopButtonFocusAnimation(btnEnterTeacher);
                animateButtonFocus(btnEnterTeacher);
            });

            btnEnterAsGuest.setOnClickListener(view -> {
                playSound("click.mp3");
                animateButtonPushDowm(btnEnterAsGuest);

                String guestId = sharedPreferences.getOrCreateGuestId(this);
                String guestFirstName = "Guest";
                String guestLastName = guestId.substring(0, 8);
                String defaultSection = "GuestSection";
                String defaultGrade = "GuestGrade";

                sharedPreferences.saveFirstN(this, guestFirstName);
                sharedPreferences.saveLastN(this, guestLastName);
                sharedPreferences.saveSection(this, defaultSection);
                sharedPreferences.saveGrade(this, defaultGrade);
                sharedPreferences.StudentIsSetLoggedIn(this, true);
                sharedPreferences.notifyGuestAccountStatus(this, true);
                sharedPreferences.saveTeacherN(this, "");

                Map<String, Object> guestData = new HashMap<>();
                guestData.put("firstName", guestFirstName);
                guestData.put("lastName", guestLastName);
                guestData.put("section", defaultSection);
                guestData.put("grade", defaultGrade);
                guestData.put("gameType", "Passing");
                guestData.put("operation_type", "Addition");
                guestData.put("timestamp", FieldValue.serverTimestamp());
                showCustomLoadingDialog("Logging you in as Guest...");

                db.collection("Accounts").document("Students")
                        .collection("MathSquare")
                        .document(guestId)
                        .set(guestData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            dismissLoadingDialog();
                            Intent intent = new Intent(signInUp.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            dismissLoadingDialog();
                            Toast.makeText(this, "Guest Sync Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

            btnExit.setOnClickListener(view -> {
                playSound("click.mp3");
                animateButtonPushDowm(btnExit);
                finishAffinity();
                System.exit(0);
            });

            btnEnterStudent.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animateButtonClick(btnEnterStudent);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopButtonFocusAnimation(btnEnterStudent);
                        break;
                }
                return false;
            });

            btnEnterStudent.setOnLongClickListener(v -> {
                animateButtonPushDowm(btnEnterStudent);
                return true;
            });

            btnEnterTeacher.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animateButtonClick(btnEnterTeacher);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopButtonFocusAnimation(btnEnterTeacher);
                        break;
                }
                return false;
            });

            btnEnterTeacher.setOnLongClickListener(v -> {
                animateButtonPushDowm(btnEnterTeacher);
                return true;
            });

            btnExit.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animateButtonClick(btnExit);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopButtonFocusAnimation(btnExit);
                        break;
                }
                return false;
            });

            btnExit.setOnLongClickListener(v -> {
                animateButtonPushDowm(btnExit);
                return true;
            });

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

        }

        backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container);

        numBGAnimation = new NumBGAnimation(this, numberContainer);
        numBGAnimation.startNumberAnimationLoop();

        backgroundFrame.post(() -> {
            VignetteEffect.apply(this, backgroundFrame, 0);
        });

    }

    private void showCustomLoadingDialog(String message) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            TextView loadingText = loadingDialog.findViewById(R.id.loading_text);
            if (loadingText != null) loadingText.setText(message);
            return;
        }

        if (!isFinishing() && !isDestroyed()) {
            loadingDialog = new Dialog(this);
            loadingDialog.setContentView(R.layout.dialog_checking_account);

            LinearLayout dialogContainer = loadingDialog.findViewById(R.id.dialog_container);
            TextView loadingText = loadingDialog.findViewById(R.id.loading_text);
            if (loadingText != null) {
                loadingText.setText(message);
            }

            if (dialogContainer != null) {
                VignetteEffect.apply(this, dialogContainer, 24f);
            }

            if (loadingDialog.getWindow() != null) {
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                loadingDialog.getWindow().setGravity(Gravity.CENTER);
                loadingDialog.getWindow().setDimAmount(0.7f);
            }

            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            Context context = ((ContextWrapper) loadingDialog.getContext()).getBaseContext();
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    loadingDialog.dismiss();
                }
            } else {
                loadingDialog.dismiss();
            }
        }
        loadingDialog = null;
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

}


    
