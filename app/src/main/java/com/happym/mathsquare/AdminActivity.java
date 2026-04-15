package com.happym.mathsquare;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.happym.mathsquare.Animation.VignetteEffect;

public class AdminActivity extends AppCompatActivity {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        executeFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Note: You might want to use setOnNavigationItemSelectedListener instead of
        // setOnNavigationItemReselectedListener depending on your exact navigation flow,
        // but I kept it as you had it.
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            if (item.getItemId() == R.id.user) {
                executeFragment();
            } else if (item.getItemId() == R.id.logout) {
                showLogoutDialog();
            }
        });
    }

    void executeFragment() {
        fragment = new UserFragment();
        ReplaceFragment(fragment);
    }

    void ReplaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    // --- CUSTOM LOGOUT DIALOG ---
    private void showLogoutDialog() {
        Dialog logoutDialog = new Dialog(AdminActivity.this);
        logoutDialog.setContentView(R.layout.layout_dialog_logout);

        LinearLayout dialogContainer = logoutDialog.findViewById(R.id.dialog_container);
        LinearLayout btnNo = logoutDialog.findViewById(R.id.btn_dialog_no);
        FrameLayout btnYes = logoutDialog.findViewById(R.id.btn_dialog_yes);
        TextView tvYesText = logoutDialog.findViewById(R.id.tv_yes_text);
        ProgressBar pbYesLoading = logoutDialog.findViewById(R.id.pb_yes_loading);

        // Apply Vignette if the method is available globally in your project
        if (dialogContainer != null) {
            VignetteEffect.apply(AdminActivity.this, dialogContainer, 24f);
        }

        // Setup Window attributes to match your loading screen style
        if (logoutDialog.getWindow() != null) {
            logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            logoutDialog.getWindow().setGravity(Gravity.CENTER);
            logoutDialog.getWindow().setDimAmount(0.7f);
        }

        logoutDialog.setCancelable(false); // Force user to click Yes or No

        btnNo.setOnClickListener(v -> {
            animateButtonClick(btnNo);

            // Add a small delay so the animation finishes before closing the dialog
            new Handler(Looper.getMainLooper()).postDelayed(logoutDialog::dismiss, 200);
        });

        btnYes.setOnClickListener(v -> {
            animateButtonClick(btnYes);

            // Hide the "Yes" text and show the loading spinner inside the button
            tvYesText.setVisibility(View.GONE);
            pbYesLoading.setVisibility(View.VISIBLE);

            // Disable buttons so user can't click them repeatedly while loading
            btnNo.setEnabled(false);
            btnYes.setEnabled(false);

            // Simulate a brief loading delay before executing the logout
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                logoutDialog.dismiss();

                // Clear Admin Login state
                sharedPreferences.setAdminLoggedIn(AdminActivity.this, false);

                Intent intent = new Intent(AdminActivity.this, signInUp.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                Toast.makeText(AdminActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            }, 1200); // Wait 1.2 seconds for visual feedback
        });

        logoutDialog.show();
    }

    // --- GAME BUTTON ANIMATION ---
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
}