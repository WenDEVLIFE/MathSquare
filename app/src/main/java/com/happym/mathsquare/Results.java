package com.happym.mathsquare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

import com.google.firebase.firestore.SetOptions;
import com.happym.mathsquare.Animation.*;
import com.happym.mathsquare.GameType.Passing.*;
import com.happym.mathsquare.GameType.Practice.*;
import com.happym.mathsquare.Model.LeaderboardEntry;
import com.happym.mathsquare.Service.FirebaseDb;

public class Results extends AppCompatActivity {
    private FirebaseFirestore db;
    private String quizId, quizScore;
    private Dialog loadingDialog;
    private TextView showScore, showResult, showMotive;
    private List<MathProblem> problemSet = new ArrayList<>();
    private MediaPlayer soundEffectPlayer;
    private boolean saveSuccesfully = false;
    private int selHeart, selTimer;
    private ArrayList<String> operationList;
    private String quizIds;
    private String starRating;
    private int number;
    private FrameLayout numberContainer, backgroundFrame;
    private final Random random = new Random();
    private NumBGAnimation numBGAnimation;
    String completedLevelsField = "";
    String worldCompletedField = "";

    private int totalPoints;
    private String difficulty;
    private String sectionId;
    private TextView showPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Firestore instance
        db = FirebaseDb.getFirestore();

        selHeart = getIntent().getIntExtra("heartLimit", 3);
        selTimer = getIntent().getIntExtra("timerLimit", 10);
        String gameType = getIntent().getStringExtra("gametype");
        String getQuiz = getIntent().getStringExtra("quizid");
        final String getOperationText = getIntent().getStringExtra("EXTRA_OPERATIONTEXT");
        final String getDifficulty = getIntent().getStringExtra("EXTRA_DIFFICULTY");
        operationList = getIntent().getStringArrayListExtra("operationList");

        String getResult = getIntent().getStringExtra("EXTRA_RESULT");
        String worldType = getIntent().getStringExtra("passingworldtype");
        String levelType = getIntent().getStringExtra("leveltype");
        sectionId = getIntent().getStringExtra("EXTRA_SECTION_ID");
        difficulty = getIntent().getStringExtra("EXTRA_DIFFICULTY");

        String levelNext = getIntent().getStringExtra("passinglevelnext");

        ImageButton imageButton = findViewById(R.id.imgBtn_home);
        imageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSound("click.mp3");
                        // Use .equals() to compare string values.
                        if ("Passing".equals(gameType)) {
                            Intent intent = new Intent(Results.this, passingStageSelection.class);
                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("difficulty", getDifficulty);
                            intent.putExtra("operation", getOperationText);
                            intent.putExtra("reload_progress", true);
                            startActivity(intent);
                            finish();
                        } else if ("Quiz".equals(gameType)) {
                            Intent intent = new Intent(Results.this, QuizzesSection.class);
                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(Results.this, MainActivity.class);
                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        ImageButton imageButton_pause = findViewById(R.id.imgBtn_retry);
        imageButton_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound("click.mp3");

                Intent resultIntent = new Intent(Results.this, MultipleChoicePage.class);
                resultIntent.putExtra("game_type", gameType);
                resultIntent.putExtra("passingworldtype", worldType); // Pass the world back
                resultIntent.putExtra("passing", levelType);       // Pass the level back
                resultIntent.putExtra("passinglevelnext", levelNext); // Pass the next level
                // --------------------------------

                if ("Quiz".equals(gameType)) {
                    resultIntent.putStringArrayListExtra("operationList", new ArrayList<>(operationList));
                    resultIntent.putExtra("quizId", getQuiz);
                } else {
                    resultIntent.putExtra("operation", getOperationText);
                }

                resultIntent.putExtra("difficulty", getDifficulty);
                resultIntent.putExtra("heartLimit", selHeart);
                resultIntent.putExtra("timerLimit", selTimer);

                startActivity(resultIntent);
                finish();
            }
        });

        TextView textView = findViewById(R.id.textViewResults);
        textView.setText("");
        textView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSound("click.mp3");
                        Intent intent = new Intent(Results.this, Difficulty.class);
                        startActivity(intent);
                    }
                });

        int getScore = getIntent().getIntExtra("EXTRA_SCORE", 0);
        int getTotal = getIntent().getIntExtra("EXTRA_TOTAL", 1);
        totalPoints = getIntent().getIntExtra("EXTRA_POINTS", 0);
        int onTimerLevel = getIntent().getIntExtra("EXTRA_ONTIMER_LEVEL", 0);
        int speedPercent = (int) (((double) getScore / getTotal) * 100);
        long timeLeft = getIntent().getLongExtra("EXTRA_TIME_LEFT", 0);
        long totalTime = getIntent().getLongExtra("EXTRA_TOTAL_TIME", 1);

        if (timeLeft > 0 && getScore > 0) {
            double timeBonus = ((double) timeLeft / totalTime) * 10;
            speedPercent += (int) timeBonus;
            if (speedPercent > 100) speedPercent = 100;
        }
        showPoints = findViewById(R.id.textViewPoints);
        showPoints.setText("Points: " + totalPoints);

        showResult = findViewById(R.id.textViewResult);
        showScore = findViewById(R.id.textViewScore);
        showMotive = findViewById(R.id.textViewMotive);

        // Result and score text
        showResult.setText(getResult);
        String scoreDisplay = getScore + "/" + getTotal;
        showScore.setText(scoreDisplay);

        // Display Motivational message based on the result
        switch (getResult) {
            case "Congratulations":
                playSound("victory.mp3");
                if (sharedPreferences.StudentIsLoggedIn(Results.this)) {
                    // Show the loading dialog before sending the score
                    showCustomLoadingDialog("Saving Progress...");
                    sendScoreResult(getScore, totalPoints, getQuiz, gameType, levelType, levelNext, worldType, getOperationText, sectionId, difficulty, onTimerLevel, speedPercent);
                }
                showMotive.setText("Excellent!");
                break;
            case "Good Job!":
                playSound("victory.mp3");
                if (sharedPreferences.StudentIsLoggedIn(Results.this)) {
                    showCustomLoadingDialog("Saving Progress...");
                    sendScoreResult(getScore, totalPoints, getQuiz, gameType, levelType, levelNext, worldType, getOperationText, sectionId, difficulty, onTimerLevel, speedPercent);
                }
                showMotive.setText("Keep it Up!");
                break;
            case "Nice Try!":
                playSound("victory.mp3");
                if (sharedPreferences.StudentIsLoggedIn(Results.this)) {
                    showCustomLoadingDialog("Saving Progress...");
                    sendScoreResult(getScore, totalPoints, getQuiz, gameType, levelType, levelNext, worldType, getOperationText, sectionId, difficulty, onTimerLevel, speedPercent);
                }
                showMotive.setText("You can do even better!");
                break;
            case "Failed":
                if (sharedPreferences.StudentIsLoggedIn(Results.this)) {
                    showCustomLoadingDialog("Saving Progress...");
                    sendScoreResult(getScore, totalPoints, getQuiz, gameType, levelType, levelNext, worldType, getOperationText, sectionId, difficulty, onTimerLevel, speedPercent);
                }
                showMotive.setText("Try Again!");
                break;
            case "Times Up!":
                if (sharedPreferences.StudentIsLoggedIn(Results.this)) {
                    showCustomLoadingDialog("Saving Progress...");
                    sendScoreResult(getScore, totalPoints, getQuiz, gameType, levelType, levelNext, worldType, getOperationText, sectionId, difficulty, onTimerLevel, speedPercent);
                }

                if ("Times Up!".equals(getResult)) {
                    showMotive.setText("Out of time! Try to be faster!");
                } else {
                    showMotive.setText("Try Again!");
                }
                break;
        }

        // Initialize background animation
        backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        numBGAnimation = new NumBGAnimation(this, numberContainer);
        numBGAnimation.startNumberAnimationLoop();

        backgroundFrame.post(
                new Runnable() {
                    @Override
                    public void run() {
                        VignetteEffect.apply(Results.this, backgroundFrame);
                    }
                });
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

    /**
     * Sends the score result to the Firebase backend for different game types.
     *
     * @param Score             The score achieved by the student.
     * @param quizid            The quiz identifier.
     * @param gametype          The type of game ("passing_level", "quiz", "OnTimer", or "Practicd").
     * @param levelNum          The level number (e.g., "level_1", "level_10").
     * @param nextlevel         The identifier for the next level to complete.
     * @param worldType         The world in which the level is played (e.g., "world_one", "world_two").
     * @param OnTimerDifficulty The difficulty setting for OnTimer mode.
     */
    private void sendScoreResult(
            int Score,
            int points,
            String quizid,
            String gametype,
            String levelNum,
            String nextlevel,
            String worldType,
            String operation,
            String gradeLevelId,
            String OnTimerDifficulty,
            int onTimerLevel,
            int speedPercent) {

        // Retrieve student-related information from shared preferences.
        String section = sharedPreferences.getSection(this);
        String grade = sharedPreferences.getGrade(this);
        String firstName = sharedPreferences.getFirstN(this);
        String lastName = sharedPreferences.getLastN(this);

        // Determine quiz name & number
        int number;

        if (quizid == null) {
            quizIds = "Quiz 1";
            number = 1;
        } else {
            switch (quizid) {
                case "quiz_1":
                    quizIds = "Quiz 1";
                    number = 1;
                    break;
                case "quiz_2":
                    quizIds = "Quiz 2";
                    number = 2;
                    break;
                case "quiz_3":
                    quizIds = "Quiz 3";
                    number = 3;
                    break;
                case "quiz_4":
                    quizIds = "Quiz 4";
                    number = 4;
                    break;
                case "quiz_5":
                    quizIds = "Quiz 5";
                    number = 5;
                    break;
                case "quiz_6":
                    quizIds = "Quiz 6";
                    number = 6;
                    break;
                default:
                    quizIds = "Quiz 1";
                    number = 1;
                    break;
            }
        }

        if ("OnTimer".equals(gametype)) {
            number = onTimerLevel;
        }

        String quizname = quizIds;
        String uuid = UUID.randomUUID().toString();

        String achievementBadge;

        if ((gametype.equals("OnTimer") && Score >= 10) ||
                (gametype.equals("Passing") && Score == 10) ||
                (gametype.equals("Quiz") && Score == 20)) {
            if (gametype.equals("Passing")) {
                achievementBadge = operation + " Master";
            } else if (gametype.equals("Quiz")) {
                achievementBadge = "Quiz Whiz";
            } else if (gametype.equals("OnTimer")) {
                achievementBadge = "Speed Demon";
            } else {
                achievementBadge = "";
            }
        } else if (Score >= 7) {
            achievementBadge = "Math Explorer";
        } else {
            achievementBadge = "";
        }

        String badgeField = "achievement_badge_" + levelNum;
        String leaderboardDocId = firstName + "_" + lastName + "_" + section;
        DocumentReference leaderboardRef = db.collection("Leaderboards").document(leaderboardDocId);

        leaderboardRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long existingPoints = documentSnapshot.getLong("points");
                long newPoints = (existingPoints != null ? existingPoints : 0) + points;
                leaderboardRef.update("points", newPoints)
                        .addOnSuccessListener(aVoid -> Log.d("Leaderboard", "Points updated: " + newPoints))
                        .addOnFailureListener(e -> Log.e("Leaderboard", "Failed to update points", e));
            } else {
                LeaderboardEntry entry = new LeaderboardEntry(firstName, lastName, section, grade, points);
                leaderboardRef.set(entry)
                        .addOnSuccessListener(aVoid -> Log.d("Leaderboard", "Leaderboard entry created"))
                        .addOnFailureListener(e -> Log.e("Leaderboard", "Failed to create leaderboard entry", e));
            }
        }).addOnFailureListener(e -> Log.e("Leaderboard", "Failed to fetch leaderboard document", e));

        Map<String, Object> studentDataQuiz = new HashMap<>();
        studentDataQuiz.put("firstName", firstName);
        studentDataQuiz.put("lastName", lastName);
        studentDataQuiz.put("total_points", points);
        studentDataQuiz.put("section", section);
        studentDataQuiz.put("gameType", "Quiz");
        studentDataQuiz.put("grade", grade);
        studentDataQuiz.put("timestamp", FieldValue.serverTimestamp());
        studentDataQuiz.put("quizno", quizname);
        studentDataQuiz.put("quizno_int", number);
        studentDataQuiz.put("quizscore", String.valueOf(Score));
        studentDataQuiz.put("achievement_badge", achievementBadge);

        HashMap<String, Object> OnTimerData = new HashMap<>();
        OnTimerData.put("firstName", firstName);
        OnTimerData.put("lastName", lastName);
        OnTimerData.put("total_points", points);
        OnTimerData.put("section", section);
        OnTimerData.put("grade", grade);
        OnTimerData.put("gameType", "OnTimer");
        OnTimerData.put("quizno", "OnTimer");
        OnTimerData.put("ontimer_level", onTimerLevel);
        OnTimerData.put("quizno_int", number);
        OnTimerData.put("timestamp", FieldValue.serverTimestamp());
        OnTimerData.put("ontimer_difficulty", OnTimerDifficulty);
        OnTimerData.put("achievement_badge", achievementBadge);
        OnTimerData.put("quizscore", String.valueOf(Score));

        HashMap<String, Object> PracticeData = new HashMap<>();
        PracticeData.put("firstName", firstName);
        PracticeData.put("lastName", lastName);
        PracticeData.put("section", section);
        PracticeData.put("gameType", "Practice");
        PracticeData.put("grade", grade);
        PracticeData.put("quizno_int", number);
        PracticeData.put("quizno", "Practice" + "_" + operation);
        PracticeData.put("timestamp", FieldValue.serverTimestamp());
        PracticeData.put("practice_difficulty", OnTimerDifficulty);
        PracticeData.put("quizscore", String.valueOf(Score));

        HashMap<String, Object> passingData = new HashMap<>();
        passingData.put("firstName", firstName);
        passingData.put(badgeField, achievementBadge);
        passingData.put("gameType", "Passing");
        passingData.put("lastName", lastName);
        passingData.put("total_points", points);
        passingData.put("section", section);
        passingData.put("grade", grade);
        passingData.put("quizno_int", number);
        passingData.put("operation_type", operation);
        passingData.put("quizno", "Passing_" + levelNum + "_" + operation);
        passingData.put("timestamp", FieldValue.serverTimestamp());
        passingData.put("passing_level_must_complete", nextlevel);
        passingData.put("quizscore", String.valueOf(Score));

        String scoreStr = String.valueOf(Score);
        String starRating;
        switch (scoreStr) {
            case "0":
                starRating = "0 Stars";
                break;
            case "1":
            case "2":
            case "3":
            case "4":
                starRating = "1 Stars";
                break;
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                starRating = "2 Stars";
                break;
            case "10":
                starRating = "3 Stars";
                break;
            default:
                starRating = "0 Stars";
                break;
        }

        String newStarsEntry = levelNum + "_" + starRating;
        List<String> starsHistory = new ArrayList<>();
        starsHistory.add(newStarsEntry);
        passingData.put("stars_passing_" + levelNum, starRating);
        passingData.put("stars_list", starsHistory);

        CollectionReference collectionRef = db.collection("Accounts").document("Students").collection("MathSquare");

        if ("Passing".equals(gametype)) {
            if ("0 Stars".equals(starRating)) {
                dismissLoadingDialog();
                Toast.makeText(this, "Oops! You got 0 stars. Try again to save your score!", Toast.LENGTH_LONG).show();
                return;
            }

            collectionRef
                    .whereEqualTo("firstName", firstName)
                    .whereEqualTo("lastName", lastName)
                    .limit(1) // Just grab the exact account
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            dismissLoadingDialog();
                            Toast.makeText(this, "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            DocumentReference docRef = collectionRef.document(document.getId());

                            docRef.get().addOnSuccessListener(snapshot -> {
                                if (!snapshot.exists()) return;
                                List<String> completedLevels = (List<String>) snapshot.get("passing_completed_levels");
                                if (completedLevels == null) completedLevels = new ArrayList<>();

                                List<String> existingStarsList = (List<String>) snapshot.get("stars_list");
                                if (existingStarsList == null)
                                    existingStarsList = new ArrayList<>();

                                Iterator<String> iterator = existingStarsList.iterator();
                                while (iterator.hasNext()) {
                                    String entry = iterator.next();
                                    if (entry.startsWith(levelNum + "_")) {
                                        iterator.remove();
                                    }
                                }
                                existingStarsList.add(newStarsEntry);
                                existingStarsList.removeIf(Objects::isNull);
                                existingStarsList.sort((a, b) -> {
                                    String valA = a.replaceAll("\\D+", "");
                                    String valB = b.replaceAll("\\D+", "");
                                    int numA = valA.isEmpty() ? 0 : Integer.parseInt(valA);
                                    int numB = valB.isEmpty() ? 0 : Integer.parseInt(valB);
                                    return Integer.compare(numA, numB);
                                });

                                completedLevels.removeIf(Objects::isNull);
                                if (!completedLevels.contains(levelNum)) {
                                    completedLevels.add(levelNum);
                                }

                                completedLevels.sort((a, b) -> {
                                    String valA = a.replaceAll("\\D+", "");
                                    String valB = b.replaceAll("\\D+", "");
                                    int na = valA.isEmpty() ? 0 : Integer.parseInt(valA);
                                    int nb = valB.isEmpty() ? 0 : Integer.parseInt(valB);
                                    return Integer.compare(na, nb);
                                });

                                docRef.update(
                                        "passing_completed_levels", completedLevels,
                                        "stars_list", existingStarsList,
                                        "quizscore", Score,
                                        "total_points", points,
                                        "timestamp", FieldValue.serverTimestamp(),
                                        "stars_passing_" + levelNum, starRating,
                                        badgeField, achievementBadge,
                                        "gameType", "Passing",
                                        "operation_type", operation
                                );

                                Map<String, Object> historyData = new HashMap<>();
                                historyData.put("level", levelNum);
                                historyData.put("score", Score);
                                historyData.put("points", points);
                                historyData.put("stars", starRating);
                                historyData.put("timestamp", FieldValue.serverTimestamp());

                                docRef.collection("PassingHistory").whereEqualTo("level", levelNum).get().addOnSuccessListener(historyTask -> {
                                    if (!historyTask.isEmpty()) {
                                        for (DocumentSnapshot historyDoc : historyTask) {
                                            docRef.collection("PassingHistory").document(historyDoc.getId()).update(historyData).addOnSuccessListener(v -> {
                                                dismissLoadingDialog();
                                                Toast.makeText(this, "Passing history updated", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    } else {
                                        docRef.collection("PassingHistory").add(historyData).addOnSuccessListener(v -> {
                                            dismissLoadingDialog();
                                            Toast.makeText(this, "Passing history saved", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            });
                        } else {
                            passingData.put("passing_completed_levels", Arrays.asList(levelNum));

                            // Enforce strictly saving to Guest ID if missing
                            DocumentReference newDocRef = "Guest".equals(firstName)
                                    ? collectionRef.document(sharedPreferences.getOrCreateGuestId(this))
                                    : collectionRef.document();

                            newDocRef.set(passingData, SetOptions.merge()).addOnSuccessListener(docRefVoid -> {
                                Map<String, Object> historyData = new HashMap<>();
                                historyData.put("level", levelNum);
                                historyData.put("score", Score);
                                historyData.put("stars", starRating);
                                historyData.put("timestamp", FieldValue.serverTimestamp());

                                newDocRef.collection("PassingHistory").add(historyData).addOnSuccessListener(aVoid -> {
                                    dismissLoadingDialog();
                                    Toast.makeText(this, "Student and history created", Toast.LENGTH_SHORT).show();
                                });
                            });
                        }
                    });
        } else if ("Quiz".equals(gametype)) {
            int finalNumber = number;
            collectionRef
                    .whereEqualTo("firstName", firstName)
                    .whereEqualTo("lastName", lastName)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            dismissLoadingDialog();
                            return;
                        }

                        DocumentReference studentDocRef;

                        if (!task.getResult().isEmpty()) {
                            // Existing student document
                            studentDocRef = collectionRef.document(
                                    task.getResult().getDocuments().get(0).getId());
                        } else {
                            // New student document
                            studentDocRef = "Guest".equals(firstName)
                                    ? collectionRef.document(sharedPreferences.getOrCreateGuestId(this))
                                    : collectionRef.document();
                        }

                        final DocumentReference finalDocRef = studentDocRef;

                        // Always update/create the root student doc with latest info
                        Map<String, Object> studentRoot = new HashMap<>();
                        studentRoot.put("firstName", firstName);
                        studentRoot.put("lastName", lastName);
                        studentRoot.put("section", section);
                        studentRoot.put("grade", grade);
                        studentRoot.put("gameType", "Quiz");
                        studentRoot.put("timestamp", FieldValue.serverTimestamp());

                        finalDocRef.set(studentRoot, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    String quizHistoryDocId = "quiz_" + finalNumber;
                                    Map<String, Object> quizHistoryData = new HashMap<>();
                                    quizHistoryData.put("quizno", quizname);
                                    quizHistoryData.put("quizno_int", finalNumber);
                                    quizHistoryData.put("quizscore", String.valueOf(Score));
                                    quizHistoryData.put("total_points", points);
                                    quizHistoryData.put("achievement_badge", achievementBadge);
                                    quizHistoryData.put("section", section);
                                    quizHistoryData.put("grade", grade);
                                    quizHistoryData.put("gameType", "Quiz");
                                    quizHistoryData.put("firstName", firstName);
                                    quizHistoryData.put("lastName", lastName);
                                    quizHistoryData.put("timestamp", FieldValue.serverTimestamp());

                                    finalDocRef.collection("QuizHistory")
                                            .document(quizHistoryDocId)
                                            .set(quizHistoryData, SetOptions.merge())
                                            .addOnSuccessListener(v -> {
                                                dismissLoadingDialog();
                                                Toast.makeText(this,
                                                        "Quiz score saved!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(err -> {
                                                dismissLoadingDialog();
                                                Toast.makeText(this,
                                                        "Failed to save quiz history: " + err.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(err -> {
                                    dismissLoadingDialog();
                                    Toast.makeText(this,
                                            "Failed to save student record: " + err.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    });
        } else if ("OnTimer".equals(gametype)) {
            Map<String, Object> historyData = new HashMap<>();
            historyData.put("level", onTimerLevel);
            historyData.put("score", Score);
            historyData.put("points", points);
            historyData.put("gameType", "OnTimer");
            historyData.put("operation", operation);
            historyData.put("difficulty", gradeLevelId);
            historyData.put("speed_percent", speedPercent);
            historyData.put("timestamp", FieldValue.serverTimestamp());

            collectionRef
                    .whereEqualTo("firstName", firstName)
                    .whereEqualTo("lastName", lastName)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            dismissLoadingDialog();
                            return;
                        }

                        if (!task.getResult().isEmpty()) {
                            DocumentReference docRef = collectionRef.document(task.getResult().getDocuments().get(0).getId());
                            docRef.set(OnTimerData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        docRef.collection("OnTimerHistory")
                                                .whereEqualTo("level", onTimerLevel)
                                                .limit(1)
                                                .get()
                                                .addOnSuccessListener(historyTask -> {
                                                    if (!historyTask.isEmpty()) {
                                                        String existingDocId = historyTask.getDocuments().get(0).getId();
                                                        docRef.collection("OnTimerHistory").document(existingDocId)
                                                                .set(historyData, SetOptions.merge())
                                                                .addOnSuccessListener(v -> {
                                                                    dismissLoadingDialog();
                                                                    Toast.makeText(this, "On Timer Score updated", Toast.LENGTH_SHORT).show();
                                                                });
                                                    } else {
                                                        docRef.collection("OnTimerHistory").add(historyData)
                                                                .addOnSuccessListener(v -> {
                                                                    dismissLoadingDialog();
                                                                    Toast.makeText(this, "On Timer Score added", Toast.LENGTH_SHORT).show();
                                                                });
                                                    }
                                                });
                                    });
                        } else {
                            DocumentReference newDocRef = "Guest".equals(firstName)
                                    ? collectionRef.document(sharedPreferences.getOrCreateGuestId(this))
                                    : collectionRef.document();

                            newDocRef.set(OnTimerData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        newDocRef.collection("OnTimerHistory")
                                                .whereEqualTo("level", onTimerLevel)
                                                .limit(1)
                                                .get()
                                                .addOnSuccessListener(historyTask -> {
                                                    if (!historyTask.isEmpty()) {
                                                        String existingDocId = historyTask.getDocuments().get(0).getId();
                                                        newDocRef.collection("OnTimerHistory").document(existingDocId)
                                                                .set(historyData, SetOptions.merge())
                                                                .addOnSuccessListener(v -> {
                                                                    dismissLoadingDialog();
                                                                    Toast.makeText(this, "On Timer Score updated", Toast.LENGTH_SHORT).show();
                                                                });
                                                    } else {
                                                        newDocRef.collection("OnTimerHistory").add(historyData)
                                                                .addOnSuccessListener(v -> {
                                                                    dismissLoadingDialog();
                                                                    Toast.makeText(this, "New On Timer Score added", Toast.LENGTH_SHORT).show();
                                                                });
                                                    }
                                                });
                                    });
                        }
                    });
        } else if ("Practice".equals(gametype)) {
            collectionRef
                    .whereEqualTo("firstName", firstName)
                    .whereEqualTo("lastName", lastName)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            dismissLoadingDialog();
                            return;
                        }
                        if (!task.getResult().isEmpty()) {
                            DocumentReference docRef = collectionRef.document(task.getResult().getDocuments().get(0).getId());
                            docRef.set(PracticeData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        dismissLoadingDialog();
                                        Toast.makeText(this, "Practice Score updated successfully", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            DocumentReference newDocRef = "Guest".equals(firstName)
                                    ? collectionRef.document(sharedPreferences.getOrCreateGuestId(this))
                                    : collectionRef.document();
                            newDocRef.set(PracticeData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        dismissLoadingDialog();
                                        Toast.makeText(this, "New Practice Score added", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        } else {
            dismissLoadingDialog();
        }
    }


    private void showCustomLoadingDialog(String message) {
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

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
