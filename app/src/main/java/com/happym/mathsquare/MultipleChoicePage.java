package com.happym.mathsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.recognition.Ink;
import com.happym.mathsquare.Model.MathProblemGenerator;
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
import java.util.regex.Pattern;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.happym.mathsquare.Animation.*;

public class MultipleChoicePage extends AppCompatActivity
        implements PauseDialogListener {

    private int questionCount = 0;
    private int totalPoints = 0;

    private int studentGradeLevel = 1;

    private CountDownTimer countDownTimer;
    private LinearLayout btnHelp;
    private int helpLimit = 10;
    private int helpRemaining = helpLimit;
    private TextView helpTextView;
    private boolean isGameOver = false;
    private boolean repeatTaskUser = false;
    private long timeLeftInMillis; // Stores remaining time
    private boolean isTimerRunning = false;
    private int score = 0;
    private int num1, num2, heartLimit, timerLimit, questionLimits, selHeart, selTimer;

    private int currentQuestionIndex = 0;
    private List<MathProblem> problemSet = new ArrayList<>();
    private List<MathProblem> answeredQuestions = new ArrayList<>();

    private ImageButton imgBtn_pause;
    private ImageView clearBtn;

    private String operationText, gametypeGame, difficulty, gametype, quidId, levelid, levelNext, worldType;
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
    private int onTimerLevel;
    private String currentOperation, newOperation, sectionId;
    private FrameLayout numberContainer;
    private FrameLayout backgroundFrame;
    private ArrayList<String> operationList;
    private ConstraintLayout gameView;
    private String numberRunlimit;
    private final Random random = new Random();
    private NumBGAnimation numBGAnimation;
    private ValueAnimator vignetteAnimator;
    private boolean isRedTransitionApplied = false; // Prevents unnecessary re-animation
    private List<String> usedOperations = new ArrayList<>();
    private DrawingView drawingView;
    private TextView hintQuestionMark;
    private DigitalInkRecognizer recognizer;
    private Handler checkHandler = new Handler();
    private Runnable checkRunnable;
    DigitalInkRecognitionModelIdentifier modelIdentifier =
            null;
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
        } catch (MlKitException e) {
            throw new RuntimeException(e);
        }

        assert modelIdentifier != null;
        DigitalInkRecognitionModel model =
                DigitalInkRecognitionModel.builder(modelIdentifier).build();

        recognizer =
                DigitalInkRecognition.getClient(
                        DigitalInkRecognizerOptions.builder(model).build());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_multiplechoice);
        FirebaseApp.initializeApp(this);
        imgBtn_pause = findViewById(R.id.imgBtn_pause);
        drawingView = findViewById(R.id.drawing_view);
        hintQuestionMark = findViewById(R.id.hint_question_mark);

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
        btnHelp = findViewById(R.id.btn_help);
        helpTextView = findViewById(R.id.helpTextView);
        gameView.setVisibility(View.VISIBLE);

        clearBtn = findViewById(R.id.btn_clear);

        clearBtn.setOnClickListener(v -> {
            drawingView.clearCanvas();
        });

        updateHelpText();

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpRemaining > 0) {
                    helpRemaining--;                  // decrease remaining
                    updateHelpText();                 // update the TextView
                    showHintWithAnimation();          // your existing function
                } else {
                    // Optional: disable the button or give feedback
                    btnHelp.setEnabled(false);
                }
            }
        });

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

        /*
        btnChoice1 = findViewById(R.id.btn_choice1);
        btnChoice2 = findViewById(R.id.btn_choice2);
        btnChoice3 = findViewById(R.id.btn_choice3);
        btnChoice4 = findViewById(R.id.btn_choice4);


        animateButtonFocus(btnChoice1);
        animateButtonFocus(btnChoice2);
        animateButtonFocus(btnChoice3);
        animateButtonFocus(btnChoice4);
*/

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String gameType = getIntent().getStringExtra("game_type");
        gametypeGame = getIntent().getStringExtra("game_type");
        FrameLayout heart_choice = findViewById(R.id.heart_choice);
        FrameLayout timer_choice = findViewById(R.id.timer_choice);
        quidId = getIntent().getStringExtra("quizId");
        worldType = getIntent().getStringExtra("passing_world");
        sectionId = getIntent().getStringExtra("sectionId");
        levelid = getIntent().getStringExtra("passing");
        levelNext = getIntent().getStringExtra("passing_next_level");
        heartLimit = getIntent().getIntExtra("heartLimit", 3);
        timerLimit = getIntent().getIntExtra("timerLimit", 10);
        selHeart = getIntent().getIntExtra("heartLimit", 3);
        selTimer = getIntent().getIntExtra("timerLimit", 10);
        questionLimits = getIntent().getIntExtra("questionLimit", 10);
        onTimerLevel = getIntent().getIntExtra("ontimer_level", 0);
        if ("Quiz".equals(gameType)) {
            String studentGradeStr = getIntent().getStringExtra("student_grade");
            if (studentGradeStr != null) {
                try {
                    studentGradeLevel = Integer.parseInt(studentGradeStr.trim());
                } catch (NumberFormatException e) {
                    studentGradeLevel = 1;
                }
            }
            heart_choice.setVisibility(View.GONE);
            timer_choice.setVisibility(View.GONE);
        } else {


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

        if ("Quiz".equals(gameType)) {

            Toast.makeText(this, operationList.toString(), Toast.LENGTH_SHORT)
                    .show();

            switchOperation(difficulty);

        } else {

            operationDisplay.setVisibility(View.GONE);

            if (operationText == null) {
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
        
       
/*
        btnChoice1.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice1.getText().toString()), btnChoice1, gameType));
        btnChoice2.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice2.getText().toString()), btnChoice2,gameType));
        btnChoice3.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice3.getText().toString()), btnChoice3, gameType));
        btnChoice4.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice4.getText().toString()), btnChoice4,gameType));
        */

        playBGGame("ingame.mp3");
        backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        numBGAnimation = new NumBGAnimation(this, numberContainer);
        numBGAnimation.startNumberAnimationLoop();

        backgroundFrame.post(
                () -> {
                    VignetteEffect.apply(this, backgroundFrame);
                });


        drawingView.setOnDrawingListener(new DrawingView.OnDrawingListener() {
            @Override
            public void onStrokeStart() {
                if (checkRunnable != null) {
                    checkHandler.removeCallbacks(checkRunnable);
                }
            }

            @Override
            public void onDrawingFinished() {

                if (hintQuestionMark.getVisibility() == View.VISIBLE) {
                    hintQuestionMark.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction(() ->
                                    hintQuestionMark.setVisibility(View.GONE));
                }

                if (checkRunnable != null) {
                    checkHandler.removeCallbacks(checkRunnable);
                }

                checkRunnable = () -> {

                    if (drawingView.getInk().getStrokes().isEmpty()) {
                        return;
                    }

                    recognizeDrawing(gameType);
                };
                checkHandler.postDelayed(checkRunnable, 1800);
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void updateHelpText() {
        helpTextView.setText("Need a Help? " + helpRemaining + "/" + helpLimit);
    }

    private void showHintWithAnimation() {
        MathProblem currentProblem = problemSet.get(currentQuestionIndex);
        String answer = String.valueOf(currentProblem.getAnswer());

        drawingView.clearCanvas();
        if (hintQuestionMark.getVisibility() == View.VISIBLE) {
            hintQuestionMark.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() ->
                            hintQuestionMark.setVisibility(View.GONE));
        }
        drawingView.showAnimatedHint(answer);

    }

    private int resolveGradeLevel(String difficulty) {
        if (difficulty == null) return 1;

        switch (difficulty) {
            case "grade_one":
                return 1;
            case "grade_two":
                return 2;
            case "grade_three":
                return 3;
            case "grade_four":
                return 4;
            case "grade_five":
                return 5;
            case "grade_six":
                return 6;
            default:
                return 1;
        }
    }

    private void checkAnswer(int btnText, Button btnChoice, String gameType) {
        if (isGameOver) return;

        playEffectSound("click.mp3");

        if (currentQuestionIndex >= problemSet.size()) {
            launchResultsActivity(gameType);
            return;
        }

        MathProblem currentProblem = problemSet.get(currentQuestionIndex);
        int actualAnswer = currentProblem.getAnswer();
        boolean isCorrect = (btnText == actualAnswer);
        String questionText = currentProblem.getQuestion();

        if (isCorrect) {
            feedbackTextView.setText("Correct!");
            animateCorrectAnswer();
            playEffectSound("correct.mp3");

            // --- CLEAR DRAWING VIEW ---
            if (drawingView != null) {
                drawingView.clearCanvas();
            }

            answeredQuestions.add(currentProblem);

            // Advance ONLY for Practice / OnTimer
            if (!"Quiz".equals(gameType) && !"passing".equals(gameType)) {
                currentQuestionIndex++;
            }

            // ---------- END-OF-GAME CHECK ----------
            if ("Quiz".equals(gameType)) {
                // Quiz ends by score, NOT question index
                if (score >= 21) {
                    launchResultsActivity(gameType);
                    return;
                }
            } else if ("passing".equals(gameType)) {
                // Passing ends when hearts are gone (handled elsewhere)
                // Do nothing here
            } else {
                // Practice / OnTimer
                if (currentQuestionIndex >= problemSet.size()) {
                    launchResultsActivity(gameType);
                    return;
                }
            }
        } else {
            feedbackTextView.setText("Wrong! The correct answer is " + actualAnswer);

            feedbackTextView.setText("Wrong! The correct answer is " + actualAnswer);

            heartLimit--;
            updateHeartDisplay();

            showKidFriendlyErrorAnimated(currentProblem, () -> {

                // ---------- GAME OVER CHECK ----------
                if (!"Quiz".equals(gameType)) {
                    if (heartLimit == 0) {
                        playSound("failed.mp3");
                        showGameOver(gameType);
                        return;
                    }
                    if (heartLimit == 1) startVignetteEffect();
                }

                // ---------- TRACK ANSWER ----------
                answeredQuestions.add(currentProblem);

                // Increment ONLY for Quiz or passing
                if ("Quiz".equals(gameType) || "passing".equals(gameType)) {
                    currentQuestionIndex++;
                }

                // ---------- LOAD NEXT QUESTION ----------
                if ("Quiz".equals(gameType)) {
                    if (currentQuestionIndex < 20) {
                        generateNewQuestionList(currentQuestionIndex, problemSet);
                    } else {
                        Toast.makeText(this, "All Questions Completed!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (currentQuestionIndex < problemSet.size()) {
                        generateNewQuestion(currentQuestionIndex, problemSet);
                    }
                }

                feedbackTextView.postDelayed(() -> feedbackTextView.setText(""), 500);
            });

            if (drawingView != null) {
                drawingView.setFeedbackColor(false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> drawingView.clearCanvas(), 1000);
            }
            return;

        }

        if (currentQuestionIndex >= problemSet.size()) {
            launchResultsActivity(gameType);
            return;
        }
        if ("Quiz".equals(gameType)) {
            if (currentQuestionIndex % 5 == 0 && !operationList.isEmpty()) {
                switchOperation(difficulty);
            }
        }
        feedbackTextView.postDelayed(() -> {
            feedbackTextView.setText("");
            if (hintQuestionMark != null) hintQuestionMark.setVisibility(View.VISIBLE);

            if ("Quiz".equals(gameType)) {
                if (currentQuestionIndex < 20) {
                    generateNewQuestionList(currentQuestionIndex, problemSet);
                } else {
                    Toast.makeText(this, "All Questions Completed!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (currentQuestionIndex < problemSet.size()) {
                    generateNewQuestion(currentQuestionIndex, problemSet);
                }
            }
        }, 1000);
    }

    private void recognizeDrawing(String gameType) {
        Ink ink = drawingView.getInk();
        recognizer.recognize(ink)
                .addOnSuccessListener(result -> {
                    String candidate = result.getCandidates().get(0).getText();
                    try {
                        int drawnNumber = Integer.parseInt(candidate);
                        checkAnswer(drawnNumber, null, gameType);
                    } catch (NumberFormatException e) {
                        feedbackTextView.setText("I can't read that! Try again.");
                    }
                });
    }

    private void switchOperation(String difficulty) {
        if (operationList.isEmpty()) {

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

    /**
     * Maps the student's actual grade + quiz difficulty tier
     * to a generator grade level so number ranges are always
     * age-appropriate but still scale within the student's grade.
     *
     * Easy   → slightly below grade level (builds confidence)
     * Medium → at grade level
     * Hard   → slightly above grade level (stretches ability)
     */
    private int resolveQuizGradeLevel(int actualGrade, String difficulty) {
        switch (difficulty) {
            case "Easy":
                return Math.max(1, actualGrade - 1);
            case "Medium":
                return actualGrade;
            case "Hard":
                return Math.min(6, actualGrade + 1);
            default:
                return actualGrade;
        }
    }
    private void setupProblemSetList(String difficulty, String operation) {
        text_operator.setText(getOperatorSymbol(operation));
        int gradeLevel = resolveQuizGradeLevel(studentGradeLevel, difficulty);

        problemSet.clear();
        problemSet.addAll(
                MathProblemGenerator.generate(
                        operation,
                        gradeLevel,
                        levelid,
                        20
                )
        );

        currentQuestionIndex = 0;
        updateUI(operation);
    }

    private void setupProblemSet(String operationText, String difficulty) {

        text_operator.setText(getOperatorSymbol(operationText));

        int gradeLevel = resolveGradeLevel(difficulty);

        // Clamp question limits
        if (questionLimits < 3) questionLimits = 3;
        if (questionLimits > 20) questionLimits = 20;

        problemSet = MathProblemGenerator.generate(
                operationText,
                gradeLevel,
                levelid,
                questionLimits
        );

        currentQuestionIndex = 0;

        questionProgressTextView.setText(
                (currentQuestionIndex + 1) + "/" + problemSet.size()
        );
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

            // Update the Chalkboard Numbers
            int[] givens = currentProblem.getGivenNumbers();
            num1 = givens[0];
            num2 = givens[1];
            givenOneTextView.setText(String.valueOf(num1));
            givenTwoTextView.setText(String.valueOf(num2));

            // --- DRAWING BOARD RESET ---
            // Clear the board for the new question
            if (drawingView != null) {
                drawingView.clearCanvas();
            }
            // Show the "?" hint again
            if (hintQuestionMark != null) {
                hintQuestionMark.setVisibility(View.VISIBLE);
                hintQuestionMark.setAlpha(0.5f);
            }

            // Update Progress
            questionProgressTextView.setText((currentQIndex + 1) + "/20");

            // NOTE: If you don't have OCR, you need a way to input the answer.
            // If you are using a single numeric keypad instead of 4 choices,
            // update that keypad here instead of btnChoice.

        } else {
            // Handle Results Activity (Final Score Processing)
            int totalQuestions = problemSet.size();
            Intent intent = new Intent(MultipleChoicePage.this, Results.class);

            // Result Messaging Logic
            if (score == 20 || score == 10) {
                intent.putExtra("EXTRA_RESULT", "Congratulations");
            } else if (score > 15 || score > 8) {
                intent.putExtra("EXTRA_RESULT", "Good Job!");
            } else if (score > 5 || score > 3) {
                intent.putExtra("EXTRA_RESULT", "Nice Try!");
            } else {
                intent.putExtra("EXTRA_RESULT", "Failed");
            }

            // Pass Session Data
            intent.putExtra("quizid", quidId);
            intent.putStringArrayListExtra("operationList", new ArrayList<>(operationList));
            intent.putExtra("passinglevelnext", levelNext);
            intent.putExtra("leveltype", levelid);
            intent.putExtra("passingworldtype", worldType);
            intent.putExtra("gametype", gametypeGame);
            intent.putExtra("heartLimit", selHeart);
            intent.putExtra("timerLimit", selTimer);
            intent.putExtra("EXTRA_SCORE", score);
            intent.putExtra("EXTRA_TOTAL", totalQuestions);
            intent.putExtra("EXTRA_OPERATIONTEXT", operationText);
            intent.putExtra("EXTRA_DIFFICULTY", difficulty);

            startActivity(intent);
            finish(); // Ensure the quiz page is removed from the stack
        }
    }

    private void generateNewQuestion(int currentQIndex, List<MathProblem> sourceQuestions) {
        if (currentQIndex < sourceQuestions.size()) {
            MathProblem currentProblem = sourceQuestions.get(currentQIndex);

            // 1. Set the numbers on the chalkboard
            int[] givens = currentProblem.getGivenNumbers();
            num1 = givens[0];
            num2 = givens[1];
            givenOneTextView.setText(String.valueOf(num1));
            givenTwoTextView.setText(String.valueOf(num2));

            // 2. Clear the drawing board for the new question
            if (drawingView != null) {
                drawingView.clearCanvas();
            }
            if (hintQuestionMark != null) {
                hintQuestionMark.setVisibility(View.VISIBLE);
                hintQuestionMark.setAlpha(0.5f);
            }

            // 3. Shuffle and set the choice buttons (Keep these as the "Submit" mechanism)
            List<String> choicesList = new ArrayList<>(Arrays.asList(currentProblem.getChoicesArray()));
            Collections.shuffle(choicesList);

            questionProgressTextView.setText((currentQIndex + 1) + "/" + sourceQuestions.size());
        } else {
            launchResultsActivity(gametypeGame);
        }
    }

    private String getOperatorSymbol(String op) {
        switch (op.toLowerCase()) {
            case "addition":
                return "+";
            case "subtraction":
                return "-";
            case "multiplication":
                return "×";
            case "division":
                return "÷";
            default:
                return "";
        }
    }


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
        timeLeftInMillis = millisInFuture;
        countDownTimer =
                new CountDownTimer(timeLeftInMillis, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timeLeftInMillis = millisUntilFinished;
                        int seconds = (int) (millisUntilFinished / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;

                        numberRunlimit = String.format("%d:%02d", minutes, seconds);
                        timerTxt.setText(numberRunlimit);
                    }

                    public void onFinish() {
                        numberRunlimit = "0:00";
                        timerTxt.setText("0:00");
                        isTimerRunning = false;

                        if ("Quiz".equals(gametypeGame)) {
                        } else {
                            int totalQuestions = problemSet.size();
                            Intent intent = new Intent(MultipleChoicePage.this, Results.class);
                            intent.putExtra("EXTRA_RESULT", "Times Up!");
                            intent.putExtra("quizid", quidId);
                            intent.putExtra("passinglevelnext", levelNext);
                            intent.putExtra("leveltype", levelid);
                            intent.putExtra("passingworldtype", worldType);
                            intent.putExtra("gametype", gametypeGame);
                            intent.putExtra("heartLimit", selHeart);
                            intent.putExtra("timerLimit", selTimer);
                            intent.putExtra("EXTRA_SCORE", score);
                            intent.putExtra("EXTRA_TOTAL", totalQuestions);
                            intent.putExtra("EXTRA_OPERATIONTEXT", operationText);
                            intent.putExtra("EXTRA_DIFFICULTY", difficulty);
                            long totalTimeMillis = (long) selTimer * 60 * 1000;
                            intent.putExtra("EXTRA_ONTIMER_LEVEL", onTimerLevel);
                            intent.putExtra("EXTRA_SECTION_ID", sectionId);

                            Log.d("MathSquare", "[NodeSync] GRADELEVEL EXISTS ON FINISH TIMER: " + sectionId);
                            Log.d("MathSquare", "[NodeSync] LEVEL EXISTS ON FINISH TIMER: " + onTimerLevel);
                            intent.putExtra("EXTRA_TOTAL_TIME", totalTimeMillis);
                            intent.putExtra("EXTRA_TIME_LEFT", 0L);
                            intent.putExtra("EXTRA_POINTS", totalPoints);

                            startActivity(intent);
                            finish();
                        }
                    }
                }.start();

        isTimerRunning = true;
    }

    private void resumeTimer() {
        if (!isTimerRunning) {
            startTimer(timeLeftInMillis);
        }
    }
    private void launchResultsActivity(String gameType) {
        int totalQuestions = problemSet.size();
        Intent intent = new Intent(MultipleChoicePage.this, Results.class);
        if (("Quiz".equals(gameType) && score == 20) || (!"quiz".equals(gameType) && score == 10)) {
            intent.putExtra("EXTRA_RESULT", "Congratulations");
        } else if (score > 15 || score > 8) {
            intent.putExtra("EXTRA_RESULT", "Good Job!");
        } else if (score > 5 || score > 3) {
            intent.putExtra("EXTRA_RESULT", "Nice Try!");
        } else {
            intent.putExtra("EXTRA_RESULT", "Failed");
        }
        if ("Quiz".equals(gameType)) {
            intent.putStringArrayListExtra("operationList", new ArrayList<>(operationList));
        }
        intent.putExtra("quizid", quidId);
        intent.putExtra("passinglevelnext", levelNext);
        intent.putExtra("leveltype", levelid);
        intent.putExtra("passingworldtype", worldType);
        intent.putExtra("gametype", gameType);
        intent.putExtra("heartLimit", selHeart);
        intent.putExtra("timerLimit", selTimer);
        intent.putExtra("EXTRA_SCORE", score);
        intent.putExtra("EXTRA_TOTAL", totalQuestions);
        intent.putExtra("EXTRA_OPERATIONTEXT", operationText);
        intent.putExtra("EXTRA_DIFFICULTY", difficulty);

        Log.d("MathSquare", "[NodeSync] LEVEL EXISTS COMPLETE: " + onTimerLevel);
        Log.d("MathSquare", "[NodeSync] GRADELEVEL EXISTS COMPLETE: " + sectionId);

        intent.putExtra("EXTRA_ONTIMER_LEVEL", onTimerLevel);
        intent.putExtra("EXTRA_SECTION_ID", sectionId);

        long totalTimeMillis = (long) selTimer * 60 * 1000;
        intent.putExtra("EXTRA_TOTAL_TIME", totalTimeMillis);
        intent.putExtra("EXTRA_TIME_LEFT", timeLeftInMillis);
        intent.putExtra("EXTRA_POINTS", totalPoints);
        startActivity(intent);
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
                bgMediaPlayer.setLooping(true);

                bgMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop the timer
        }

        Intent intent = new Intent(MultipleChoicePage.this, Results.class);
        int totalQuestions = problemSet.size();

        // Pass data to Results activity

        if (score == 20 || score == 10) {
            intent.putExtra("EXTRA_RESULT", "Congratulations");
        } else if (score > 15 || score > 8) {
            intent.putExtra("EXTRA_RESULT", "Good Job!");
        } else if (score > 5 || score > 3) {
            intent.putExtra("EXTRA_RESULT", "Nice Try!");
        } else {
            intent.putExtra("EXTRA_RESULT", "Failed");
        }
        intent.putStringArrayListExtra("operationList", new ArrayList<>(operationList));

        intent.putExtra("quizid", quidId);
        intent.putExtra("passinglevelnext", levelNext);
        intent.putExtra("leveltype", levelid);
        intent.putExtra("passingworldtype", worldType);
        intent.putExtra("gametype", gameType);
        intent.putExtra("heartLimit", selHeart);
        intent.putExtra("timerLimit", selTimer);
        intent.putExtra("EXTRA_SCORE", score);

        // POINTS EARNED IN THIS ROUND
        intent.putExtra("EXTRA_POINTS", totalPoints);

        intent.putExtra("EXTRA_TOTAL", totalQuestions);
        intent.putExtra("EXTRA_OPERATIONTEXT", operationText);
        intent.putExtra("EXTRA_DIFFICULTY", difficulty);

        intent.putExtra("EXTRA_ONTIMER_LEVEL", onTimerLevel);
        intent.putExtra("EXTRA_SECTION_ID", sectionId);
        Log.d("MathSquare", "[NodeSync] GRADELEVEL EXISTS ON GAMEOVER: " + sectionId);
        Log.d("MathSquare", "[NodeSync] LEVEL EXISTS ON GAMEOVER: " + onTimerLevel);

        long totalTimeMillis = (long) selTimer * 60 * 1000;
        intent.putExtra("EXTRA_TOTAL_TIME", totalTimeMillis);
        intent.putExtra("EXTRA_TIME_LEFT", timeLeftInMillis);
        intent.putExtra("EXTRA_POINTS", totalPoints);

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
        super.onBackPressed();
        Intent intent = new Intent(MultipleChoicePage.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void animateCorrectAnswer() {

        playEffectSound("correct.mp3");

        int basePoints = 10;
        int bonusPoints = 0;
        long totalTimeAllowed = selTimer * 1000L;
        if (timeLeftInMillis > (totalTimeAllowed / 2)) {
            bonusPoints = 5;
        }

        int pointsThisRound = basePoints + bonusPoints;
        totalPoints += pointsThisRound;
        score++;

        TextView pointsEarnedTxt = findViewById(R.id.pointsEarnedTxt);
        pointsEarnedTxt.setText("+" + pointsThisRound + " Points!");
        pointsEarnedTxt.setVisibility(View.VISIBLE);

        if (bonusPoints > 0) {
            TextView bonusTxt = findViewById(R.id.bonusTxt);
            bonusTxt.setText("⚡ Quick Thinker Bonus! +5");
            bonusTxt.setVisibility(View.VISIBLE);
        }

        if (drawingView != null) {
            drawingView.setFeedbackColor(true);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                drawingView.clearCanvas();
            }, 1000);
        }

        RelativeLayout overlay = findViewById(R.id.dialogOverlay);
        LinearLayout successDialog = findViewById(R.id.customDialogSuccess);

        overlay.setVisibility(View.VISIBLE);
        successDialog.setVisibility(View.VISIBLE);

        // Pop-in animation
        successDialog.setScaleX(0.7f);
        successDialog.setScaleY(0.7f);
        successDialog.setAlpha(0f);

        successDialog.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            drawingView.resetMarkerColor();
            hintQuestionMark.setVisibility(View.VISIBLE);
            successDialog.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
        }, 2500);
    }

    private void showKidFriendlyErrorAnimated(MathProblem problem, Runnable onComplete) {
        playEffectSound("wrong.mp3");
        drawingView.setDrawingEnabled(false);
        drawingView.setFeedbackColor(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> drawingView.clearCanvas(), 1000);

        RelativeLayout overlay = findViewById(R.id.dialogOverlay);
        LinearLayout failedDialog = findViewById(R.id.customDialogFailed);
        FlexboxLayout visualContainer = failedDialog.findViewById(R.id.visualContainer);
        visualContainer.removeAllViews();
        TextView messageView = failedDialog.findViewById(R.id.dialogMessageFailed);
        int[] numbers = problem.getGivenNumbers();
        String operationName = problem.getOperation();
        String correctAnswer = String.valueOf(problem.getAnswer());

        overlay.setVisibility(View.VISIBLE);
        failedDialog.setVisibility(View.VISIBLE);
        failedDialog.setScaleX(0.7f);
        failedDialog.setScaleY(0.7f);
        failedDialog.setAlpha(0f);
        failedDialog.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).start();

        // Create a horizontal LinearLayout for problem display
        FlexboxLayout problemLayout = new FlexboxLayout(this);
        problemLayout.setFlexWrap(FlexWrap.WRAP);
        problemLayout.setJustifyContent(JustifyContent.CENTER);
        problemLayout.setAlignItems(AlignItems.CENTER);
        visualContainer.addView(problemLayout);

        // Add first operand apples
        for (int i = 0; i < numbers[0]; i++) {
            ImageView apple = new ImageView(this);
            apple.setImageResource(R.drawable.ic_apple);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(80, 80);
            lp.setMargins(4, 0, 4, 0);
            apple.setLayoutParams(lp);
            problemLayout.addView(apple);
            addIncreasingBounce(apple, i * 100, 3, 1.2f, 0.05f); // ✅ all apples bounce
        }

        // Add operator as TextView
        TextView operatorView = new TextView(this);
        String operatorSymbol = "";
        switch (operationName) {
            case "Addition":
                operatorSymbol = "+";
                break;
            case "Subtraction":
                operatorSymbol = "-";
                break;
            case "Multiplication":
                operatorSymbol = "×";
                break;
            case "Division":
                operatorSymbol = "÷";
                break;
        }
        operatorView.setText(operatorSymbol);
        operatorView.setTextSize(28f);
        operatorView.setTextColor(Color.parseColor("#3E2723"));
        operatorView.setPadding(16, 0, 16, 0);
        problemLayout.addView(operatorView);

        // Add second operand apples (or crosses for subtraction)
        for (int i = 0; i < numbers[1]; i++) {
            ImageView img = new ImageView(this);
            if ("Subtraction".equals(operationName)) img.setImageResource(R.drawable.ic_cross);
            else img.setImageResource(R.drawable.ic_apple);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(80, 80);
            lp.setMargins(4, 0, 4, 0);
            img.setLayoutParams(lp);
            problemLayout.addView(img);
            addIncreasingBounce(img, i * 100, 3, 1.2f, 0.05f); // ✅ also bounces
        }

        if ("Multiplication".equals(operationName)) {
            visualContainer.removeAllViews();
            for (int i = 0; i < numbers[0]; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER);
                visualContainer.addView(row);
                for (int j = 0; j < numbers[1]; j++) {
                    ImageView apple = new ImageView(this);
                    apple.setImageResource(R.drawable.ic_apple);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(80, 80);
                    lp.setMargins(4, 4, 4, 4);
                    apple.setLayoutParams(lp);
                    row.addView(apple);
                    addIncreasingBounce(apple, (i * numbers[1] + j) * 100, 3, 1.2f, 0.05f); // ✅ bouncing
                }
            }
        }

        if ("Division".equals(operationName)) {
            visualContainer.removeAllViews();
            int groups = numbers[1];
            int perGroup = numbers[0] / groups;
            for (int i = 0; i < groups; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER);
                visualContainer.addView(row);
                for (int j = 0; j < perGroup; j++) {
                    ImageView apple = new ImageView(this);
                    apple.setImageResource(R.drawable.ic_apple);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(80, 80);
                    lp.setMargins(4, 4, 4, 4);
                    apple.setLayoutParams(lp);
                    row.addView(apple);
                    addIncreasingBounce(apple, (i * perGroup + j) * 100, 3, 1.2f, 0.05f); // ✅ bouncing
                }
            }
        }
        messageView.setText("Correct answer: " + correctAnswer + "\nDon't worry, try again!");
        messageView.setTextColor(Color.parseColor("#388E3C"));

        // Hide dialog after 5 seconds and re-enable drawing
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            drawingView.resetMarkerColor();
            hintQuestionMark.setVisibility(View.VISIBLE);
            failedDialog.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            drawingView.setDrawingEnabled(true);
            if (onComplete != null) onComplete.run();
        }, 5000);
    }

    private void addIncreasingBounce(View view, long startDelay, int bounces, float startScale, float increment) {
        view.setScaleX(0f);
        view.setScaleY(0f);

        // Pop in first
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .setStartDelay(startDelay)
                .withEndAction(() -> startBounceLoop(view, 0, bounces, startScale, increment))
                .start();
    }

    private void startBounceLoop(View view, int currentBounce, int totalBounces, float scale, float increment) {
        if (currentBounce >= totalBounces) return;

        float nextScale = scale + increment;
        view.animate()
                .scaleX(nextScale)
                .scaleY(nextScale)
                .setDuration(150)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .withEndAction(() -> startBounceLoop(view, currentBounce + 1, totalBounces, nextScale, increment))
                        .start())
                .start();
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
