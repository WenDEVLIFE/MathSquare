package com.happym.mathsquare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private String operationText, difficulty, gametype, quidId, levelid;
    private TextView givenOneTextView,
            givenTwoTextView,
            operationTextView,
            feedbackTextView,
            questionProgressTextView,
            text_operator,
            heartTxt,
            timerTxt;
    private Button btnChoice1, btnChoice2, btnChoice3, btnChoice4;
    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;

    // Define constants for SharedPreferences
    private static final String PREFS_NAME = "MathAppPrefs";
    private static final String KEY_OPERATION_SET = "selectedOperationSet";
    private static final int REQUEST_CODE_RESULTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        gametype = getIntent().getStringExtra("game_type");
        quidId = getIntent().getStringExtra("quidId");
        levelid = getIntent().getStringExtra("passing");
        heartLimit = getIntent().getIntExtra("heartLimit", 3);
        timerLimit = getIntent().getIntExtra("timerLimit", 5);
        questionLimits = getIntent().getIntExtra("questionLimit", 10);

        updateHeartDisplay();
        startTimer(timerLimit * 60 * 1000);

        operationText =
                getIntent().getStringExtra("operation") != null
                        ? getIntent().getStringExtra("operation")
                        : "Subtraction";
        difficulty =
                getIntent().getStringExtra("difficulty") != null
                        ? getIntent().getStringExtra("difficulty")
                        : "Easy";

        if (operationText == null) {
            Toast.makeText(this, "No Operation detected at the moment. :(", Toast.LENGTH_SHORT)
                    .show();
        } else {
            operationText =
                    operationText.substring(0, 1).toUpperCase()
                            + operationText.substring(1).toLowerCase();
            operationTextView.setText(operationText);
            feedbackTextView.setText("Operation detected");

            setupProblemSet(operationText, difficulty);

            if (repeatTaskUser) {

            } else {
                generateNewQuestion(currentQuestionIndex, problemSet);
            }
        }

        btnChoice1.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice1.getText().toString()), btnChoice1));
        btnChoice2.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice2.getText().toString()), btnChoice2));
        btnChoice3.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice3.getText().toString()), btnChoice3));
        btnChoice4.setOnClickListener(
                view -> checkAnswer(Integer.parseInt(btnChoice4.getText().toString()), btnChoice4));
        playBGGame("ingame.mp3");
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
        } else if (questionLimits > 10) {
            questionLimits = 10;
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

    private void checkAnswer(int btnText, Button btnChoice) {
        if (isGameOver) return;

        int actualAnswer = problemSet.get(currentQuestionIndex).getAnswer();
        boolean isCorrect = (btnText == actualAnswer);

        if (isCorrect) {
            score++;
            feedbackTextView.setText("Correct!");
            animateCorrectAnswer(btnChoice);
        } else {
            feedbackTextView.setText("Wrong! The correct answer is " + actualAnswer);
            animateIncorrectAnswer(btnChoice);
            heartLimit--; // Decrease heart count
            updateHeartDisplay();

            if (heartLimit == 0) {
                playSound("failed.mp3"); // Stop background music & play failed sound
                showGameOver();
                return;
            }

            highlightCorrectAnswer(actualAnswer);
        }

        answeredQuestions.add(problemSet.get(currentQuestionIndex));
        currentQuestionIndex++;

        btnChoice.postDelayed(
                () -> {
                    if (currentQuestionIndex < problemSet.size()) {
                        generateNewQuestion(currentQuestionIndex, problemSet);
                    } else {
                        questionProgressTextView.setText("");
                        Intent intent = new Intent(MultipleChoicePage.this, Results.class);
                        int totalQuestions = problemSet.size();

                        // Stop background music before playing victory/defeat sound
                        if (score == totalQuestions) {
                            intent.putExtra("EXTRA_RESULT", "Congratulations!");
                            playSound("victory.mp3");
                        } else if (score > totalQuestions * 0.75) {
                            intent.putExtra("EXTRA_RESULT", "Good Job!");
                            playSound("victory.mp3");
                        } else if (score > totalQuestions / 2) {
                            intent.putExtra("EXTRA_RESULT", "Nice Try!");
                            playSound("victory.mp3");
                        } else {
                            intent.putExtra("EXTRA_RESULT", "Failed");
                            playSound("failed.mp3");
                        }

                        // Pass data to Results activity
                        intent.putExtra("EXTRA_SCORE", score);
                    intent.putExtra("quizid", quidId);
                    intent.putExtra("passing", levelid);
                    intent.putExtra("game_type",gametype);
                        intent.putExtra("EXTRA_TOTAL", totalQuestions);
                        intent.putExtra("EXTRA_OPERATIONTEXT", operationText);
                        intent.putExtra("EXTRA_DIFFICULTY", difficulty);
                        intent.putParcelableArrayListExtra(
                                "EXTRA_ANSWERED_QUESTIONS", new ArrayList<>(answeredQuestions));
                        startActivity(intent);
                        finish();
                    }
                    feedbackTextView.setText("");
                },
                1000);
    }

    private void playSound(String fileName) {
        // Stop background music before playing sound effect
        if (bgMediaPlayer != null) {
            bgMediaPlayer.stop();
            bgMediaPlayer.release();
            bgMediaPlayer = null;
        }

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

    private void playBGGame(String fileName) {
        if (bgMediaPlayer == null) { // Prevent re-initializing
            try {
                AssetFileDescriptor afd = getAssets().openFd(fileName);
                bgMediaPlayer = new MediaPlayer();
                bgMediaPlayer.setDataSource(
                        afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                bgMediaPlayer.prepare();
                bgMediaPlayer.setOnCompletionListener(
                        mp -> {
                            mp.release();
                            bgMediaPlayer = null;
                        });
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

    private void showGameOver() {
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
