package com.happym.mathsquare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.happym.mathsquare.Animation.VignetteEffect;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        float scale = getResources().getDisplayMetrics().density;

        FrameLayout backgroundFrame = new FrameLayout(this);
        backgroundFrame.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        backgroundFrame.setBackgroundColor(ContextCompat.getColor(this, R.color.yellowbg));

        LinearLayout contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.gravity = Gravity.CENTER;
        contentContainer.setLayoutParams(containerParams);

        ImageView logoView = new ImageView(this);
        logoView.setImageResource(R.drawable.ic_schoollogo);
        int logoSizePx = (int) (180 * scale + 0.5f);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(logoSizePx, logoSizePx);
        logoView.setLayoutParams(logoParams);

        // Logo Animation
        logoView.setAlpha(0f);
        logoView.animate().alpha(1f).setDuration(1500).start();

        FrameLayout spinnerFrame = new FrameLayout(this);
        int spinnerFrameSizePx = (int) (120 * scale + 0.5f);
        LinearLayout.LayoutParams spinnerFrameParams = new LinearLayout.LayoutParams(spinnerFrameSizePx, spinnerFrameSizePx);
        spinnerFrameParams.topMargin = (int) (20 * scale);
        spinnerFrame.setLayoutParams(spinnerFrameParams);

        View trackView = new View(this);
        int trackSizePx = (int) (100 * scale + 0.5f);
        FrameLayout.LayoutParams trackParams = new FrameLayout.LayoutParams(trackSizePx, trackSizePx);
        trackParams.gravity = Gravity.CENTER;
        trackView.setLayoutParams(trackParams);
        trackView.setBackgroundResource(R.drawable.spinner_track);

        ProgressBar progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(trackSizePx, trackSizePx);
        progressParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(progressParams);
        progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.custom_spinner_ring));
        spinnerFrame.addView(trackView);
        spinnerFrame.addView(progressBar);
        contentContainer.addView(logoView);
        contentContainer.addView(spinnerFrame);
        backgroundFrame.addView(contentContainer);
        setContentView(backgroundFrame);
        VignetteEffect.apply(this, backgroundFrame, 0);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, signInUp.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}