package com.happym.mathsquare;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.InputFilter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.Service.FirebaseDb;
import com.happym.mathsquare.Utils.PasswordUtils;
import com.happym.mathsquare.sharedPreferences;

import androidx.core.view.WindowCompat;

public class teacherLogIn extends AppCompatActivity {
    private FirebaseFirestore db;

    private FrameLayout loadingContainer;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        db = FirebaseDb.getFirestore();
        mAuth = FirebaseAuth.getInstance();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        setContentView(R.layout.layoutteacher_log_in);

        // Firestore instance


        TextInputLayout emailLayout = findViewById(R.id.email_address_layout);
        TextInputLayout passwordLayout = findViewById(R.id.password);
        AppCompatButton submitButton = findViewById(R.id.btn_sign_in);
        loadingContainer = findViewById(R.id.loading_container);

        TextInputEditText emailEditText = (TextInputEditText) emailLayout.getEditText();

        InputFilter noSpacesFilter = (source, start, end, dest, dstart, dend) -> {
            if (source.toString().contains(" ")) {
                return "";
            }
            return source;
        };

        if (emailEditText != null) {
            emailEditText.setFilters(new InputFilter[]{noSpacesFilter});
        }

        TextInputEditText passwordEditText = (TextInputEditText) passwordLayout.getEditText();


        if (passwordEditText != null) {
            passwordEditText.setFilters(new InputFilter[]{noSpacesFilter});
        }


        submitButton.setOnClickListener(v -> {
            boolean hasError = false;
            emailLayout.setError(null);
            passwordLayout.setError(null);

            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.setError("Valid email address is required");
                animateShakeRotateEditTextErrorAnimation(emailLayout);
                hasError = true;
            }

            if (TextUtils.isEmpty(password)) {
                passwordLayout.setError("Password is required");
                animateShakeRotateEditTextErrorAnimation(passwordLayout);
                hasError = true;
            }

            if (!hasError) {
                showLoading(true, submitButton);

                // Authenticate the user securely via Firebase Auth
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();
                                String hashedInputPassword = PasswordUtils.hashPassword(password); // Used for teachers

                                db.collection("Accounts")
                                        .document("Teachers")
                                        .collection(email)
                                        .document("MyProfile")
                                        .get()
                                        .addOnSuccessListener(teacherSnapshot -> {
                                            if (teacherSnapshot.exists()) {
                                                String storedPassword = teacherSnapshot.getString("password");
                                                // Teachers use the hashed password
                                                if (storedPassword != null && storedPassword.equals(hashedInputPassword)) {
                                                    // SUCCESS: User is a Teacher & Hashes Match
                                                    sharedPreferences.setLoggedIn(teacherLogIn.this, true);
                                                    sharedPreferences.saveEmail(teacherLogIn.this, email);
                                                    sharedPreferences.notifyGuestAccountStatus(teacherLogIn.this, false);

                                                    Intent intent = new Intent(teacherLogIn.this, Dashboard.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                    finish();
                                                } else {
                                                    // FAILED: Firebase let them in, but your manual hash failed
                                                    mAuth.signOut();
                                                    showLoading(false, submitButton);
                                                    passwordLayout.setError("Database password mismatch");
                                                    animateShakeRotateEditTextErrorAnimation(passwordLayout);
                                                }

                                            } else {
                                                db.collection("Admin").document(uid).get()
                                                        .addOnSuccessListener(adminSnapshot -> {
                                                            if (adminSnapshot.exists()) {

                                                                String storedPassword = adminSnapshot.getString("password");
                                                                if (storedPassword != null && storedPassword.equals(password)) {
                                                                    sharedPreferences.setAdminLoggedIn(teacherLogIn.this, true);
                                                                    sharedPreferences.saveEmail(teacherLogIn.this, email);
                                                                    sharedPreferences.notifyGuestAccountStatus(teacherLogIn.this, false);

                                                                    Intent intent = new Intent(teacherLogIn.this, AdminActivity.class);
                                                                    startActivity(intent);
                                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                    finish();
                                                                } else {
                                                                    // FAILED: Firebase let them in, but your manual hash failed
                                                                    mAuth.signOut();
                                                                    showLoading(false, submitButton);
                                                                    passwordLayout.setError("Database password mismatch");
                                                                    animateShakeRotateEditTextErrorAnimation(passwordLayout);
                                                                }

                                                            } else {
                                                                // FAILED: They logged in, but have no role profile in the DB
                                                                mAuth.signOut(); // Kick them out
                                                                showLoading(false, submitButton);
                                                                emailLayout.setError("Account role not found");
                                                                animateShakeRotateEditTextErrorAnimation(emailLayout);
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            mAuth.signOut();
                                                            showLoading(false, submitButton);
                                                            Toast.makeText(teacherLogIn.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            mAuth.signOut();
                                            showLoading(false, submitButton);
                                            Toast.makeText(teacherLogIn.this, "Error accessing account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                showLoading(false, submitButton);
                                passwordLayout.setError("Incorrect email or password");
                                animateShakeRotateEditTextErrorAnimation(passwordLayout);
                                animateShakeRotateEditTextErrorAnimation(emailLayout);
                            }
                        });
            }
        });
    }

    private void showLoading(boolean isLoading, AppCompatButton button) {
        if (isLoading) {
            button.setEnabled(false);
            button.setAlpha(0.5f);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            if (button.getBackground() != null) {
                button.getBackground().setColorFilter(filter);
            }
            button.setTextColor(Color.LTGRAY);
            loadingContainer.setVisibility(View.VISIBLE);

        } else {
            button.setEnabled(true);
            button.setAlpha(1.0f);
            if (button.getBackground() != null) {
                button.getBackground().clearColorFilter();
            }
            button.setTextColor(Color.WHITE);
            loadingContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void animateShakeRotateEditTextErrorAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();

        // Shake animation
        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, 20f, -20f, 20f, -20f, 0);
        shakeAnimator.setDuration(350);

        // Rotate animation
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 5f, -5f, 5f, -5f, 0);
        rotateAnimator.setDuration(350);

        // Play both animations together
        animatorSet.playTogether(shakeAnimator, rotateAnimator);
        animatorSet.start();
    }

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
        scaleX.setDuration(4000);
        scaleY.setDuration(4000);

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


}
