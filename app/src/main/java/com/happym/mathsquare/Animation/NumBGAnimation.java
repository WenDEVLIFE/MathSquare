package com.happym.mathsquare.Animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

public class NumBGAnimation {
    private final Context context;
    private final FrameLayout numberContainer;
    private final Random random = new Random();

    private final String[] digits = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private final String[] operators = {
            "+", "−", "×", "÷", "="
    };

    private final int[] symbolColors = {
            Color.parseColor("#1A237E"),
            Color.parseColor("#4A148C"),
            Color.parseColor("#B71C1C"),
            Color.parseColor("#1B5E20"),
            Color.parseColor("#0D47A1"),
            Color.parseColor("#880E4F"),
            Color.parseColor("#E65100"),
            Color.parseColor("#006064"),
    };

    // How many symbols live on screen at once
    private final int numberCount = 10;

    public NumBGAnimation(Context context, FrameLayout numberContainer) {
        this.context = context;
        this.numberContainer = numberContainer;
    }

    public void startNumberAnimationLoop() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth  = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Stagger the initial batch so they don't all arrive together
        for (int i = 0; i < numberCount; i++) {
            final int delay = i * 500 + random.nextInt(400);
            numberContainer.postDelayed(
                    () -> spawnSymbol(screenWidth, screenHeight),
                    delay
            );
        }
    }

    // Each call spawns exactly one symbol. When that symbol finishes
    // fading out it schedules the next one — keeping the count steady
    // without any global removeAllViews() or synchronised clock.
    private void spawnSymbol(int screenWidth, int screenHeight) {
        TextView view = createSymbolTextView();

        String symbol = (random.nextInt(100) < 75)
                ? digits[random.nextInt(digits.length)]
                : operators[random.nextInt(operators.length)];
        view.setText(symbol);

        boolean fromLeft = random.nextBoolean();
        float startX = fromLeft ? -300f : screenWidth + 300f;
        float endX   = random.nextInt((int)(screenWidth * 0.75f))
                + (int)(screenWidth * 0.1f);
        float startY = random.nextInt(screenHeight);

        view.setX(startX);
        view.setY(startY);
        view.setAlpha(0f);

        numberContainer.addView(view);
        animateSymbol(view, startX, endX, screenWidth, screenHeight);
    }

    private TextView createSymbolTextView() {
        TextView tv = new TextView(context);

        int size = random.nextInt(89) + 72;
        tv.setTextSize(size);
        tv.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        tv.setTextColor(symbolColors[random.nextInt(symbolColors.length)]);

        // Lowered ceiling: 0.10–0.30 so symbols stay clearly behind UI text
        tv.setTag(random.nextFloat() * 0.20f + 0.10f);

        tv.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        return tv;
    }

    private void animateSymbol(TextView tv,
                               float startX, float endX,
                               int screenWidth, int screenHeight) {

        float targetAlpha    = (float) tv.getTag();
        int entranceDuration = 5800 + random.nextInt(1000);

        ObjectAnimator moveX = ObjectAnimator.ofFloat(tv, "translationX", startX, endX);
        moveX.setDuration(entranceDuration);
        moveX.setInterpolator(new DecelerateInterpolator(1.8f));
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(tv, "alpha", 0f, targetAlpha);
        fadeIn.setDuration(entranceDuration);
        fadeIn.setInterpolator(new DecelerateInterpolator(2.0f)); // eases heavily at start

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(tv, "scaleX", 0.7f, 1f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(tv, "scaleY", 0.7f, 1f);
        scaleInX.setDuration(entranceDuration);
        scaleInY.setDuration(entranceDuration);
        scaleInX.setInterpolator(new DecelerateInterpolator(1.5f)); // no overshoot on entrance
        scaleInY.setInterpolator(new DecelerateInterpolator(1.5f));

        // rest of idle + fade-out unchanged ...

        // --- IDLE (kicks in after entrance) ---

        int idleDelay = entranceDuration;

        float bobHeight  = 25f + random.nextInt(40);
        int   bobDur     = 2800 + random.nextInt(2200);
        ObjectAnimator floatY = ObjectAnimator.ofFloat(tv, "translationY",
                tv.getY(), tv.getY() - bobHeight);
        floatY.setStartDelay(idleDelay);
        floatY.setDuration(bobDur);
        floatY.setRepeatMode(ValueAnimator.REVERSE);
        floatY.setRepeatCount(ValueAnimator.INFINITE);
        floatY.setInterpolator(new AccelerateDecelerateInterpolator());

        float rotDir   = random.nextBoolean() ? 1f : -1f;
        float rotAngle = 5f + random.nextInt(16);
        int   rotSpeed = 3000 + random.nextInt(4000);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(tv, "rotation",
                0f, rotDir * rotAngle);
        rotate.setStartDelay(idleDelay);
        rotate.setDuration(rotSpeed);
        rotate.setInterpolator(new CycleInterpolator(1));
        rotate.setRepeatCount(ValueAnimator.INFINITE);

        float pulseMin = 0.92f + random.nextFloat() * 0.05f;
        float pulseMax = 1.03f + random.nextFloat() * 0.05f;
        int   pulseDur = 2200 + random.nextInt(1800);
        ObjectAnimator pulseX = ObjectAnimator.ofFloat(tv, "scaleX", pulseMin, pulseMax);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(tv, "scaleY", pulseMin, pulseMax);
        pulseX.setStartDelay(idleDelay);
        pulseY.setStartDelay(idleDelay);
        pulseX.setDuration(pulseDur);
        pulseY.setDuration(pulseDur);
        pulseX.setRepeatCount(ValueAnimator.INFINITE);
        pulseY.setRepeatCount(ValueAnimator.INFINITE);
        pulseX.setRepeatMode(ValueAnimator.REVERSE);
        pulseY.setRepeatMode(ValueAnimator.REVERSE);

        AnimatorSet idleAnim = new AnimatorSet();
        idleAnim.playTogether(moveX, fadeIn, scaleInX, scaleInY,
                floatY, rotate, pulseX, pulseY);
        idleAnim.start();

        // --- FADE OUT (each symbol on its own random lifetime) ---
        // Lifetime is randomised per symbol (6–10s) so they never
        // all fade at the same moment. The next symbol starts spawning
        // partway through the fade so there's always overlap.

        int lifetime    = 6000 + random.nextInt(4000);  // 6–10s on screen
        int fadeDur     = 1800 + random.nextInt(800);    // 1.8–2.6s fade out

        tv.postDelayed(() -> {
            // Cancel idle so floatY/rotate/pulse don't fight the fade
            idleAnim.cancel();

            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                    tv, "alpha", tv.getAlpha(), 0f);
            fadeOut.setDuration(fadeDur);
            fadeOut.setInterpolator(new DecelerateInterpolator());
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    numberContainer.removeView(tv);
                }
            });
            fadeOut.start();

            // Spawn the replacement partway into this fade so the new
            // symbol is already drifting in before this one disappears
            int spawnDelay = fadeDur / 2;
            tv.postDelayed(() -> spawnSymbol(screenWidth, screenHeight), spawnDelay);

        }, lifetime);
    }
}