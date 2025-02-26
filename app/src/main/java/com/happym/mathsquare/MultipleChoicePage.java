package com.happym.mathsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintSet;
import com.google.firebase.FirebaseApp;
import com.happym.mathsquare.dialog.PauseDialog;
import com.happym.mathsquare.dialog.PauseDialog.PauseDialogListener; // Adjust to match your project
// structure

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.Set;
import java.util.HashSet;

import com.happym.mathsquare.MusicManager;

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

import androidx.constraintlayout.widget.ConstraintLayout;

public class MultipleChoicePage extends AppCompatActivity
        implements PauseDialog.PauseDialogListener {

    private int questionCount = 0;
    private CountDownTimer countDownTimer;
    private boolean isGameOver = false;
    private boolean repeatTaskUser = false;
    private long timeLeftInMillis; // Stores remaining time
    private boolean isTimerRunning = false;
    private int score = 0;
    private int num1, num2, heartLimit, timerLimit, questionLimits;

    private int currentQuestionIndex = 0;
    private List<MathProblem> problemSet = new ArrayList<>();
    private List<MathProblem> answeredQuestions = new ArrayList<>();

    private ImageButton imgBtn_pause;

    private String operationText, difficulty, gametype, quidId, levelid, levelNext, worldType;
    private TextView givenOneTextView,
            givenTwoTextView,
            operationTextView,
            feedbackTextView,
            questionProgressTextView,
            text_operator,
            heartTxt,
            timerTxt,
   operationDisplay;
    private Button btnChoice1, btnChoice2, btnChoice3, btnChoice4;
    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;

    // Define constants for SharedPreferences
    private static final String PREFS_NAME = "MathAppPrefs";
    private static final String KEY_OPERATION_SET = "selectedOperationSet";
    private static final int REQUEST_CODE_RESULTS = 1;
   private String currentOperation, newOperation;
    private FrameLayout numberContainer;
    private FrameLayout backgroundFrame;
    private ArrayList<String> operationList;
    private ConstraintLayout gameView;
    
    private final Random random = new Random();
    private final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final int numberCount = 3; // Number of numbers per side
    private ValueAnimator vignetteAnimator;
private boolean isRedTransitionApplied = false; // Prevents unnecessary re-animation
 
private List<String> usedOperations = new ArrayList<>();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_multiplechoice);
        FirebaseApp.initializeApp(this);
        imgBtn_pause = findViewById(R.id.imgBtn_pause);

        operationTextView = findViewById(R.id.text_operation);
       
        
        givenOneTextView = findViewById(R.id.text_givenone);
        givenTwoTextView = findViewById(R.id.text_giventwo);
        feedbackTextView = findViewById(R.id.text_feedback);
        
        text_operator = findViewById(R.id.text_operator);
        heartTxt = findViewById(R.id.heart_txt);
        timerTxt = findViewById(R.id.timer_txt);
        questionProgressTextView = findViewById(R.id.text_question_progress);

        gameView = findViewById(R.id.gameView);
       operationDisplay = findViewById(R.id.operationDisplay);
        
        gameView.setVisibility(View.VISIBLE);
        
        
        ImageButton imageButton = findViewById(R.id.imgBtn_home);
        imageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    playSound("click.mp3");
                        Intent intent = new Intent(MultipleChoicePage.this, MainActivity.class);
                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        imgBtn_pause.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    playSound("click.mp3");
                        int currentScore = currentQuestionIndex;
                        currentScore++;
                        boolean isPaused = true; // Set based on your conditions
                        PauseDialog pauseDialog =
                                PauseDialog.newInstance(isPaused, 10 - currentScore);
                        pauseDialog.setListener(MultipleChoicePage.this);
                        pauseDialog.show(
                                getSupportFragmentManager(), "PauseDialog"); // Show the dialog
                        if (countDownTimer != null) {
                            countDownTimer.cancel(); // Stop the timer
                        }
                    }
                });

        btnChoice1 = findViewById(R.id.btn_choice1);
        btnChoice2 = findViewById(R.id.btn_choice2);
        btnChoice3 = findViewById(R.id.btn_choice3);
        btnChoice4 = findViewById(R.id.btn_choice4);

        animateButtonFocus(btnChoice1);
        animateButtonFocus(btnChoice2);
        animateButtonFocus(btnChoice3);
        animateButtonFocus(btnChoice4);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String gameType = getIntent().getStringExtra("game_type");
        FrameLayout heart_choice = findViewById(R.id.heart_choice);
       FrameLayout timer_choice = findViewById(R.id.timer_choice);
        quidId = getIntent().getStringExtra("quidId");
        worldType = getIntent().getStringExtra("passing_world");
        levelid = getIntent().getStringExtra("passing");
        levelNext = getIntent().getStringExtra("passing_next_level");
        heartLimit = getIntent().getIntExtra("heartLimit", 3);
        timerLimit = getIntent().getIntExtra("timerLimit", 5);
        questionLimits = getIntent().getIntExtra("questionLimit", 10);

        if("quiz".equals(gameType)){
           heart_choice.setVisibility(View.GONE);
           timer_choice.setVisibility(View.GONE);
        }else{
           
            
            updateHeartDisplay();
        startTimer(timerLimit * 60 * 1000);
        }
        

        
        operationText =
                getIntent().getStringExtra("operation");
        
       operationList = getIntent().getStringArrayListExtra("operationList");
        
if (operationList == null || operationList.isEmpty()) {
    operationList = new ArrayList<>();
    operationList.add("Subtraction");
}
        
        difficulty =
                getIntent().getStringExtra("difficulty") != null
                        ? getIntent().getStringExtra("difficulty")
                        : "";

        if("quiz".equals(gameType)) {
            
            Toast.makeText(this, operationList.toString() , Toast.LENGTH_SHORT)
                    .show();
            
           switchOperation(difficulty);
            
        } else{
            
            
           if (operationText == null ) {
            Toast.makeText(this, "No Math Operation detected at the moment. :(", Toast.LENGTH_SHORT)
                    .show();
        } else {
            operationTextView.setText(operationText);
            feedbackTextView.setText("Operation detected");

            setupProblemSet(operationText, difficulty);

            if (repeatTaskUser) {

            } else {
                generateNewQuestion(currentQuestionIndex, problemSet);
            }
        }
            
          
        }
        
       

        btnChoice1.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice1.getText().toString()), btnChoice1, gameType));
        btnChoice2.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice2.getText().toString()), btnChoice2,gameType));
        btnChoice3.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice3.getText().toString()), btnChoice3, gameType));
        btnChoice4.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice4.getText().toString()), btnChoice4,gameType));
        
        
        playBGGame("ingame.mp3");
    backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        startNumberAnimationLoop();
        
 backgroundFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            applyDefaultVignetteEffect(); // Set the default effect first
            
        }
    });
}
    
  

private void switchOperation(String difficulty) {
    if (operationList.isEmpty()) {
        Toast.makeText(this, "No more operations available!", Toast.LENGTH_SHORT).show();
        return;
    }

    // Reset used operations when all are used
    if (usedOperations.size() == operationList.size()) {
        usedOperations.clear();
    }

    // Get next unused operation
     newOperation = null;
    for (String op : operationList) {
        if (!usedOperations.contains(op)) {
            newOperation = op;
            break;
        }
    }

    // Add to used operations and update UI
    if (newOperation != null) {
        usedOperations.add(newOperation);
        setupProblemSetList(difficulty, newOperation);

    } else {
        Toast.makeText(this, "Error selecting new operation!", Toast.LENGTH_SHORT).show();
    }
}


private void setupProblemSetList(String difficulty, String operation) {
    BufferedReader bufferedReader;
    CSVProcessor csvProcessor = new CSVProcessor();
    String fileName = "";
    text_operator.setText("");

    switch (operation) {
        case "Addition":
            fileName = "additionProblemSet.csv";
            text_operator.setText("+");
            break;
        case "Subtraction":
            fileName = "subtractionProblemSet.csv";
            text_operator.setText("-");
            break;
        case "Multiplication":
            fileName = "multiplicationProblemSet.csv";
            text_operator.setText("×");
            break;
        case "Division":
            fileName = "divisionProblemSet.csv";
            text_operator.setText("÷");
            break;
    }

    try {
        bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    List<MathProblem> mathProblemList = csvProcessor.readCSVFile(bufferedReader);
    List<MathProblem> filteredProblems = csvProcessor.getProblemsByOperation(mathProblemList, difficulty);

    // 🔹 Clear old problem set
    problemSet.clear();

    // 🔹 Add only 5 new questions
    List<MathProblem> selectedProblems = filteredProblems.subList(0, Math.min(5, filteredProblems.size()));
    problemSet.addAll(selectedProblems);

    // 🔹 Ensure the problem set is filled up to 20 questions
    while (problemSet.size() < 20 && filteredProblems.size() > 0) {
        problemSet.addAll(selectedProblems);
    }

    // Trim if exceeds 20
    if (problemSet.size() > 20) {
        problemSet = problemSet.subList(problemSet.size() - 20, problemSet.size());
    }

    // 🔹 Reset UI and generate the first question
    updateUI(operation);
}


private void updateUI(String nextOperation) {
    String questionProgressText = (currentQuestionIndex + 1) + "/20";
    questionProgressTextView.setText(questionProgressText);
operationTextView.setText(nextOperation);
        operationDisplay.setText(nextOperation);
        gameView.setVisibility(View.GONE);
        operationDisplay.setVisibility(View.VISIBLE);
        operationDisplay.setAlpha(0f);
        operationDisplay.animate().alpha(1f).setDuration(1000).withEndAction(() -> {
            new Handler().postDelayed(() -> {
                operationDisplay.animate().alpha(0f).setDuration(1000).withEndAction(() -> {
                    operationDisplay.setVisibility(View.GONE);
                    gameView.setVisibility(View.VISIBLE);
                    generateNewQuestionList(currentQuestionIndex, problemSet);
                });
            }, 2000);
        });
    
}
    
private void generateNewQuestionList(int currentQIndex, List<MathProblem> sourceQuestions) {
    if (currentQIndex < sourceQuestions.size()) {
        MathProblem currentProblem = sourceQuestions.get(currentQIndex);

        int[] givens = currentProblem.getGivenNumbers();
        num1 = givens[0];
        num2 = givens[1];
        givenOneTextView.setText(String.valueOf(num1));
        givenTwoTextView.setText(String.valueOf(num2));

        List<String> choicesList = new ArrayList<>(Arrays.asList(currentProblem.getChoicesArray()));
        Collections.shuffle(choicesList);

        btnChoice1.setText(choicesList.get(0));
        btnChoice2.setText(choicesList.get(1));
        btnChoice3.setText(choicesList.get(2));
        btnChoice4.setText(choicesList.get(3));

        questionProgressTextView.setText((currentQIndex + 1) + "/20");
    } else {
        Toast.makeText(this, "All questions completed.", Toast.LENGTH_SHORT).show();
    }
}

private void generateNewQuestion(int currentQIndex, List<MathProblem> sourceQuestions) {
        if (currentQIndex < sourceQuestions.size()) {
            MathProblem currentProblem = sourceQuestions.get(currentQIndex);

            int[] givens = currentProblem.getGivenNumbers();
            num1 = givens[0];
            num2 = givens[1];
            givenOneTextView.setText(String.valueOf(num1));
            givenTwoTextView.setText(String.valueOf(num2));

            List<String> choicesList =
                    new ArrayList<>(Arrays.asList(currentProblem.getChoicesArray()));
            Collections.shuffle(choicesList);

            btnChoice1.setText(choicesList.get(0));
            btnChoice2.setText(choicesList.get(1));
            btnChoice3.setText(choicesList.get(2));
            btnChoice4.setText(choicesList.get(3));

            questionProgressTextView.setText((currentQIndex + 1) + "/" + sourceQuestions.size());
        } else {
            Toast.makeText(this, "All questions repeated.", Toast.LENGTH_SHORT).show();
            // Navigate to Results or perform other logic here
        }
    }
    

    
    private void setupProblemSet(String operationText, String difficulty) {
        BufferedReader bufferedReader = null;
        CSVProcessor csvProcessor = new CSVProcessor();
        String fileName = "additionProblemSet.csv";
        text_operator.setText("");

        if (operationText.equalsIgnoreCase("addition")) {
            fileName = "additionProblemSet.csv";
            text_operator.setText("+");
        } else if (operationText.equalsIgnoreCase("subtraction")) {
            fileName = "subtractionProblemSet.csv";
            text_operator.setText("-");
        } else if (operationText.equalsIgnoreCase("multiplication")) {
            fileName = "multiplicationProblemSet.csv";
            text_operator.setText("x");
        } else if (operationText.equalsIgnoreCase("division")) {
            fileName = "divisionProblemSet.csv";
            text_operator.setText("÷");
        }

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<MathProblem> mathProblemList = csvProcessor.readCSVFile(bufferedReader);
        List<MathProblem> filteredProblems =
                csvProcessor.getProblemsByOperation(mathProblemList, difficulty);

        // Ensure questionLimits is within the allowed range (3 to 10)
        if (questionLimits < 3) {
            questionLimits = 3;
        } else if (questionLimits > 20) {
            questionLimits = 20;
        }

        // Trim the problemSet based on the selected limit
        problemSet = filteredProblems.subList(0, Math.min(questionLimits, filteredProblems.size()));

        String questionProgressText = (currentQuestionIndex + 1) + "/" + problemSet.size();
        questionProgressTextView.setText(questionProgressText);

        // Detect repeated questions
        Set<String> questionSet = new HashSet<>();
        for (MathProblem problem : problemSet) {
            if (!questionSet.add(problem.getQuestion())) {
                Toast.makeText(
                                this,
                                "Repeated question found: " + problem.getQuestion(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
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

// ✅ Apply default vignette effect (not animated)
private void applyDefaultVignetteEffect() {
    int width = backgroundFrame.getWidth();
    int height = backgroundFrame.getHeight();
    if (width == 0 || height == 0) return;

    // Create a bitmap
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    // Define the default vignette effect
    RadialGradient gradient = new RadialGradient(
            width / 2f, height / 2f, 
            Math.max(width, height) * 0.8f, 
            new int[]{Color.parseColor("#FFEF47"), Color.parseColor("#898021"), Color.parseColor("#504A31")},
            new float[]{0.2f, 0.6f, 1f}, 
            Shader.TileMode.CLAMP);

    Paint paint = new Paint();
    paint.setShader(gradient);
    paint.setAlpha(180); 

    // Draw gradient on the canvas
    canvas.drawRect(0, 0, width, height, paint);
    backgroundFrame.setBackground(new BitmapDrawable(getResources(), bitmap));
}

// ✅ Smooth transition to red when heartLimit == 1
private void startVignetteEffect() {
    if (backgroundFrame.getWidth() == 0 || backgroundFrame.getHeight() == 0) return;

    if (heartLimit == 1 && !isRedTransitionApplied) {
        isRedTransitionApplied = true; // Prevents unnecessary re-animation

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(6000); // 1-second transition
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();

            // Blend colors from default to red effect
            int blendedColor1 = blendColors(Color.parseColor("#FFEF47"), Color.parseColor("#DD4E47"), progress);
            int blendedColor2 = blendColors(Color.parseColor("#898021"), Color.parseColor("#A8403B"), progress);
            int blendedColor3 = blendColors(Color.parseColor("#504A31"), Color.parseColor("#6C211D"), progress);

            // Create animated vignette effect
            Bitmap bitmap = Bitmap.createBitmap(backgroundFrame.getWidth(), backgroundFrame.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            RadialGradient gradient = new RadialGradient(
                    backgroundFrame.getWidth() / 2f, backgroundFrame.getHeight() / 2f,
                    Math.max(backgroundFrame.getWidth(), backgroundFrame.getHeight()) * 0.8f,
                    new int[]{blendedColor1, blendedColor2, blendedColor3},
                    new float[]{0.2f, 0.6f, 1f},
                    Shader.TileMode.CLAMP
            );

            Paint paint = new Paint();
            paint.setShader(gradient);
            paint.setAlpha(200);

            // Draw gradient on canvas
            canvas.drawRect(0, 0, backgroundFrame.getWidth(), backgroundFrame.getHeight(), paint);
            backgroundFrame.setBackground(new BitmapDrawable(getResources(), bitmap));
        });

        animator.start();
    }
}

// ✅ Color blending function
private int blendColors(int colorStart, int colorEnd, float ratio) {
    int startA = (colorStart >> 24) & 0xff;
    int startR = (colorStart >> 16) & 0xff;
    int startG = (colorStart >> 8) & 0xff;
    int startB = colorStart & 0xff;

    int endA = (colorEnd >> 24) & 0xff;
    int endR = (colorEnd >> 16) & 0xff;
    int endG = (colorEnd >> 8) & 0xff;
    int endB = colorEnd & 0xff;

    return ((int) (startA + (endA - startA) * ratio) << 24) |
           ((int) (startR + (endR - startR) * ratio) << 16) |
           ((int) (startG + (endG - startG) * ratio) << 8) |
           ((int) (startB + (endB - startB) * ratio));
}


    /* //Not Working

        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RESULTS && resultCode == RESULT_OK && data != null) {
            String operationText = data.getStringExtra("operation");
            String difficulty = data.getStringExtra("difficulty");
            ArrayList<MathProblem> returnedAnsweredQuestions = data.getParcelableArrayListExtra("EXTRA_ANSWERED_QUESTIONS");
            boolean repeatTask = data.getBooleanExtra("repeatTask", false);

            if (repeatTask) {
                    recreate();
            }
        }
    }
        */

    @Override
    public void onResumeGame(boolean resumeGame) {
        if (resumeGame) {
            startTimer(timeLeftInMillis);
        }
    }

    
    private void updateHeartDisplay() {
        heartTxt.setText(String.valueOf(heartLimit));
    }

    private void startTimer(long millisInFuture) {
        timeLeftInMillis = millisInFuture; // Set the time left

        countDownTimer =
                new CountDownTimer(timeLeftInMillis, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timeLeftInMillis = millisUntilFinished; // Update remaining time
                        int seconds = (int) (millisUntilFinished / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        timerTxt.setText(String.format("%d:%02d", minutes, seconds));
                    }

                    public void onFinish() {
                        timerTxt.setText("0:00");
                        showPauseDialog();
                        isTimerRunning = false;
                    }
                }.start();

        isTimerRunning = true;
    }

    private void resumeTimer() {
        if (!isTimerRunning) {
            startTimer(timeLeftInMillis); // Resume from remaining time
        }
    }

    private void checkAnswer(int btnText, Button btnChoice, String gameType) {
    if (isGameOver) return;
    playEffectSound("click.mp3");

    int actualAnswer = problemSet.get(currentQuestionIndex).getAnswer();
    boolean isCorrect = (btnText == actualAnswer);

    if (isCorrect) {
        score++;
        feedbackTextView.setText("Correct!");
        animateCorrectAnswer(btnChoice);
        playEffectSound("correct.mp3");
    } else {
        feedbackTextView.setText("Wrong! The correct answer is " + actualAnswer);
        animateIncorrectAnswer(btnChoice);
        playEffectSound("wrong.mp3");
        heartLimit--;
        updateHeartDisplay();

        if (heartLimit == 0) {
            playSound("failed.mp3");
            showGameOver(gameType);
            return;
        }

        if (heartLimit == 1) {
            startVignetteEffect();
        }

        highlightCorrectAnswer(actualAnswer);
    }

    answeredQuestions.add(problemSet.get(currentQuestionIndex));
    currentQuestionIndex++;
        
        if("quiz".equals(gameType)){
             // **Check if operation needs to switch every 5 questions**
    if (currentQuestionIndex % 5 == 0 && !operationList.isEmpty()) {
        switchOperation(difficulty);  // Use difficulty when switching
    }

        }
   
    btnChoice.postDelayed(() -> {
        feedbackTextView.setText("");
                
                if("quiz".equals(gameType)){
                   if (currentQuestionIndex < 20) {
            generateNewQuestionList(currentQuestionIndex, problemSet);
        } else {
            Toast.makeText(this, "All Questions Completed!", Toast.LENGTH_SHORT).show();
        }
                }else{
                                       if (currentQuestionIndex < problemSet.size()) {
                        generateNewQuestion(currentQuestionIndex, problemSet);
                    }
                }
        
    }, 1000);
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

private void playEffectSound(String fileName) {
    

    // Stop any previous sound effect before playing a new one
    if (soundEffectPlayer != null) {
        soundEffectPlayer.release();
        soundEffectPlayer = null;
    }

    try {
        AssetFileDescriptor afd = getAssets().openFd(fileName);
        soundEffectPlayer = new MediaPlayer();
        soundEffectPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        soundEffectPlayer.prepare();
        
        // Set volume to max
        soundEffectPlayer.setVolume(1.0f, 1.0f);

        soundEffectPlayer.setOnCompletionListener(mp -> {
            mp.release();
            soundEffectPlayer = null;
        });

        soundEffectPlayer.start();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    private void playBGGame(String fileName) {
    if (bgMediaPlayer == null) { // Prevent re-initializing
        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            bgMediaPlayer = new MediaPlayer();
            bgMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            bgMediaPlayer.prepare();
            
            // Set looping to true
            bgMediaPlayer.setLooping(true);

            bgMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


    private void highlightCorrectAnswer(int actualAnswer) {
        if (Integer.parseInt(btnChoice1.getText().toString()) == actualAnswer) {
            animateCorrectAnswer(btnChoice1);
        } else if (Integer.parseInt(btnChoice2.getText().toString()) == actualAnswer) {
            animateCorrectAnswer(btnChoice2);
        } else if (Integer.parseInt(btnChoice3.getText().toString()) == actualAnswer) {
            animateCorrectAnswer(btnChoice3);
        } else if (Integer.parseInt(btnChoice4.getText().toString()) == actualAnswer) {
            animateCorrectAnswer(btnChoice4);
        }
    }

    private void showPauseDialog() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop the timer
        }
        PauseDialog pauseDialog = PauseDialog.newInstance(true, heartLimit);
        pauseDialog.setListener(this);
        pauseDialog.show(getSupportFragmentManager(), "PauseDialog");
    }

    private void showGameOver(String gameType) {
        isGameOver = true;
        countDownTimer.cancel();

        Intent intent = new Intent(MultipleChoicePage.this, Results.class);
        int totalQuestions = problemSet.size();

        // Add result messages as before
        if (score == totalQuestions) {
            intent.putExtra("EXTRA_RESULT", "Congratulations");
        } else if (score > totalQuestions * 0.75) {
            intent.putExtra("EXTRA_RESULT", "Good Job!");
        } else if (score > totalQuestions / 2) {
            intent.putExtra("EXTRA_RESULT", "Nice Try!");
        } else {
            intent.putExtra("EXTRA_RESULT", "Failed");
        }

        // Pass data to Results activity
      
                    intent.putExtra("quizid", quidId);
                    intent.putExtra("passinglevelnext", levelNext);
                    intent.putExtra("leveltype", levelid);
                    intent.putExtra("passingworldtype",worldType);
                    intent.putExtra("gametype",gameType);
        
        intent.putExtra("EXTRA_SCORE", score);
        intent.putExtra("EXTRA_TOTAL", totalQuestions);
        intent.putExtra("EXTRA_OPERATIONTEXT", operationText);
        intent.putExtra("EXTRA_DIFFICULTY", difficulty);
        startActivity(intent);
    }

    private void endGame() {
        countDownTimer.cancel();
        Intent intent = new Intent(MultipleChoicePage.this, Results.class);
        intent.putExtra("EXTRA_SCORE", score);
        intent.putExtra("EXTRA_TOTAL", problemSet.size());
        startActivity(intent);
        finish();
    }

    @Override
    public void onRepeatGame(boolean shouldRepeat) {
        if (shouldRepeat) {
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MultipleChoicePage.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void animateCorrectAnswer(Button button) {

        button.setBackgroundResource(R.drawable.btn_condition_create);

        ObjectAnimator bounceAnimator = ObjectAnimator.ofFloat(button, "translationY", 0, -30f, 0);
        bounceAnimator.setDuration(400);
        bounceAnimator.setRepeatCount(2);

        bounceAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Reset the button background to the specified drawable
                        button.setBackgroundResource(R.drawable.btn_short_condition);
                    }
                });

        bounceAnimator.start();
    }

    private void animateIncorrectAnswer(Button button) {
        
        
        // Change the background color to red
        button.setBackgroundResource(R.drawable.btn_condition_red);

        // Create the shake animation
        ObjectAnimator shakeAnimator =
                ObjectAnimator.ofFloat(button, "translationX", 0, 20f, -20f, 20f, -20f, 0);
        shakeAnimator.setDuration(350);

        // Add a listener to change the background back after the animation ends
        shakeAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Reset the button background to the specified drawable
                        button.setBackgroundResource(R.drawable.btn_short_condition);
                    
                    }
                });

        // Start the animation
        shakeAnimator.start();
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
        scaleX.setDuration(2000);
        scaleY.setDuration(2000);

        // AccelerateDecelerateInterpolator for smooth pulsing
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        scaleX.setInterpolator(interpolator);
        scaleY.setInterpolator(interpolator);

        // Set repeat count and mode on each ObjectAnimator
        scaleX.setRepeatCount(ObjectAnimator.INFINITE); // Infinite repeat
        scaleX.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation on repeat
        scaleY.setRepeatCount(ObjectAnimator.INFINITE); // Infinite repeat
        scaleY.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation on repeat

        // Combine the animations into an AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }

    private void animateButtonPushDowm(View button) {
        ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f); // Scale down slightly
        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f); // Scale down slightly

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
            animatorSet.cancel(); // Stop the animation when focus is lost
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
    protected void onStart() {
        super.onStart();
        MusicManager.stop();
        playBGGame("ingame.mp3");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (bgMediaPlayer != null) {
            bgMediaPlayer.pause();
        }
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        bgMediaPlayer.start();
    }
}
