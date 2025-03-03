package com.happym.mathsquare;

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
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.BounceInterpolator;

// import androidx.activity.EdgeToEdge;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;


import android.view.View;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.util.Random;
import org.w3c.dom.Text;

import com.happym.mathsquare.sharedPreferences;
import com.happym.mathsquare.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar toolbar;
    
    private FrameLayout numberContainer,backgroundFrame;
    private final Random random = new Random();
    private final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final int numberCount = 3; // Number of numbers per side
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.layout_web);

        FirebaseApp.initializeApp(this);
        
        AppCompatButton tutaddition = findViewById(R.id.btn_tut_addition);
        AppCompatButton tutmultiplication = findViewById(R.id.btn_tut_multiplication);
        AppCompatButton tutdivision = findViewById(R.id.btn_tut_division);
        AppCompatButton tutsubtraction = findViewById(R.id.btn_tut_subtraction);
        
        animateButtonFocus(tutaddition);
        animateButtonFocus(tutmultiplication);
        animateButtonFocus(tutdivision);
        animateButtonFocus(tutsubtraction);
        
        /*

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        getSupportActionBar().setTitle("WebView");

        webView = findViewById(R.id.webView);
        setupWebView();

        String webURL = getIntent().getStringExtra("URL");
        if (webURL != null) {
            webView.loadUrl(webURL);
        }
        
        */
    backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        startNumberAnimationLoop();
        
backgroundFrame.post(this::applyVignetteEffect);
        
    webView = findViewById(R.id.webView);
setupWebView();
        // Button click listeners to open URLs
        tutaddition.setOnClickListener(v -> openTutorial("https://m.youtube.com/watch?v=KgZIXq04ee8&feature=shared&fbclid=IwZXh0bgNhZW0CMTEAAR1NqDxPiHGZd-70EHra2Qm9z1IR4ZUl3GaOewIq8JavBPhbddLLXd5DhOo_aem_CNm_cgh5-6TRAXGwPiWSkg"));
        tutmultiplication.setOnClickListener(v -> openTutorial("https://m.youtube.com/watch?feature=shared&fbclid=IwZXh0bgNhZW0CMTEAAR0yEFb1isx15HTDg1ATFktn1BrkPhEbYVV8AhHv0aF95E9rjdXryWfsCY4_aem_TuiRf27rDiPWwcEqI9qEZg&v=CFDCG1b4ahk"));
        tutdivision.setOnClickListener(v -> openTutorial("https://m.youtube.com/watch?feature=shared&fbclid=IwZXh0bgNhZW0CMTEAAR0FYlLQk0ZhLN81dRV4dJAzx0EghZWw-YJvMFAnAjXerm5lv8vrfJv-ZU0_aem_yJPLA3WD-g9T-rYNfYN6pA&v=irfMCIgFJZY"));
        tutsubtraction.setOnClickListener(v -> openTutorial("https://m.youtube.com/watch?feature=shared&fbclid=IwZXh0bgNhZW0CMTEAAR0BRu1ovOE4WpGHC_Xbh5rjgZjKY6rpV-kmXC3VhVEYUyQCUPHqCy9QSvc_aem_peTZ13YhfMU34HB_5N_FsQ&v=Ds226Vh7epg"));
    }

    // Method to open the tutorial in WebView
    private void openTutorial(String url) {
        webView.setVisibility(View.VISIBLE);  // Show the WebView
        webView.loadUrl(url);                 // Load the URL
        MusicManager.pause();
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


    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
    }
    
    @Override
public void onBackPressed() {
    if (webView.getVisibility() == View.VISIBLE) {
        // Check if WebView can go back
        if (webView.canGoBack()) {
            webView.goBack();  // Navigate back in WebView history if possible
        } else {
            webView.setVisibility(View.GONE);  // Hide WebView if no more history
                MusicManager.resume();
        }
    } else {
        super.onBackPressed();  // Exit activity if WebView is not visible
    }
}


    /*

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    */
}
