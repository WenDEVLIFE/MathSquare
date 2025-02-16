package com.happym.mathsquare;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
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
import java.util.HashMap;
import java.util.List;
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

public class Results extends AppCompatActivity {
    private FirebaseFirestore db;
    private String quizId, quizScore;
    private TextView showScore, showResult, showMotive;
    private List<MathProblem> problemSet = new ArrayList<>();
private MediaPlayer soundEffectPlayer;
    
    private FrameLayout numberContainer,backgroundFrame;
    private final Random random = new Random();
    private final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final int numberCount = 3; // Number of numbers per side
    String completedLevelsField = "";
                        String worldCompletedField = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
// Firestore instance
        db = FirebaseFirestore.getInstance();
        ImageButton imageButton = findViewById(R.id.imgBtn_home);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   playSound("click.mp3");
                Intent intent = new Intent(Results.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                    
                   finish();
            }
        });
        ImageButton imageButton_pause = findViewById(R.id.imgBtn_retry);
imageButton_pause.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
                    playSound("click.mp3");
        // Get current data
        String getOperationText = getIntent().getStringExtra("EXTRA_OPERATIONTEXT");
        String getDifficulty = getIntent().getStringExtra("EXTRA_DIFFICULTY");
        ArrayList<MathProblem> answeredQuestions = getIntent().getParcelableArrayListExtra("EXTRA_ANSWERED_QUESTIONS");

        // Create an intent to return to MultipleChoicePage
        Intent resultIntent = new Intent(Results.this, MultipleChoicePage.class);
        resultIntent.putExtra("operation", getOperationText);
        resultIntent.putExtra("difficulty", getDifficulty);

        startActivity(resultIntent);
                    finish();
    }
});


        TextView textView = findViewById(R.id.textViewResults);
textView.setText("");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playSound("click.mp3");
                Intent intent = new Intent(Results.this, Difficulty.class);
                startActivity(intent);
            }
        });

        String getResult = getIntent().getStringExtra("EXTRA_RESULT");
        String worldType = getIntent().getStringExtra("passing_world_type");
        String levelType = getIntent().getStringExtra("level_type");
        String difficulty = getIntent().getStringExtra("EXTRA_DIFFICULTY");
       String levelNext = getIntent().getStringExtra("passing_level_next");
        String getQuiz = getIntent().getStringExtra("quizid");
            String gameType = getIntent().getStringExtra("game_type");
int getScore = getIntent().getIntExtra("EXTRA_SCORE", 0);
int getTotal = getIntent().getIntExtra("EXTRA_TOTAL", 0);

showResult = findViewById(R.id.textViewResult);
showScore = findViewById(R.id.textViewScore);
showMotive = findViewById(R.id.textViewMotive);

// Result and score text
showResult.setText(getResult);
String scoreDisplay = getScore + "/" + getTotal;
showScore.setText(scoreDisplay);

// Display Motivational message based on the result
switch (getResult) {
    case "Congratulations!":
            
            if(sharedPreferences.StudentIsLoggedIn(this)){
                if(getQuiz != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType, levelNext, worldType, difficulty);
                }else if (gameType != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType,levelNext,worldType,difficulty);
                }
               
            }
            
        showMotive.setText("Excellent!");
        break;
    case "Good Job!":
            if(sharedPreferences.StudentIsLoggedIn(this)){
                if(getQuiz != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType, levelNext, worldType, difficulty);
                }else if (gameType != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType,levelNext,worldType,difficulty);
                }
               
            }
        showMotive.setText("Keep it Up!");
        break;
    case "Nice Try!":
            if(sharedPreferences.StudentIsLoggedIn(this)){
               if(getQuiz != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType, levelNext, worldType, difficulty);
                }else if (gameType != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType,levelNext,worldType,difficulty);
                }
               
               
            }
        showMotive.setText("You can do even better!");
        break;
    case "Failed":
            if(sharedPreferences.StudentIsLoggedIn(this)){
                if(getQuiz != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType, levelNext, worldType, difficulty);
                }else if (gameType != null){
                   sendScoreResult(getScore,getQuiz,gameType,levelType,levelNext,worldType,difficulty);
                }
               
            }
        showMotive.setText("Try Again!");
        break;
}

   backgroundFrame = findViewById(R.id.main);
        numberContainer = findViewById(R.id.number_container); // Get FrameLayout from XML

        startNumberAnimationLoop();
        
backgroundFrame.post(this::applyVignetteEffect);
        
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
    
   private void sendScoreResult(int Score, String quizid, String gametype, String levelNum, String nextlevel, String worldType, String OnTimerDifficulty) {
       String section = sharedPreferences.getSection(this);
       String grade = sharedPreferences.getGrade(this);
       String firstName = sharedPreferences.getFirstN(this);
       String lastName = sharedPreferences.getLastN(this);
       String quizname = "Quiz " + quizid;
       String uuid = UUID.randomUUID().toString(); // Generate a random UUID

       HashMap<String, Object> studentDataQuiz = new HashMap<>();
       studentDataQuiz.put("firstName", firstName);
       studentDataQuiz.put("lastName", lastName);
       studentDataQuiz.put("section", section);
       studentDataQuiz.put("grade", grade);
       studentDataQuiz.put("quizno", quizname);
       studentDataQuiz.put("quizscore", String.valueOf(Score));

       HashMap<String, Object> OnTimerData = new HashMap<>();
       OnTimerData.put("firstName", firstName);
       OnTimerData.put("lastName", lastName);
       OnTimerData.put("section", section);
       OnTimerData.put("grade", grade);
       OnTimerData.put("ontimer_difficulty", OnTimerDifficulty);
       OnTimerData.put("ontimer_score", String.valueOf(Score));

       HashMap<String, Object> PracticeData = new HashMap<>();
       PracticeData.put("firstName", firstName);
       PracticeData.put("lastName", lastName);
       PracticeData.put("section", section);
       PracticeData.put("grade", grade);
       PracticeData.put("practice_difficulty", OnTimerDifficulty);
       PracticeData.put("practice_score", String.valueOf(Score));

       HashMap<String, Object> passingData = new HashMap<>();
       passingData.put("firstName", firstName);
       passingData.put("lastName", lastName);
       passingData.put("section", section);
       passingData.put("grade", grade);
       passingData.put("passing_level_must_complete", nextlevel);
       passingData.put("passing_level", levelNum);
       passingData.put("passing_" + levelNum + "_score", String.valueOf(Score));


       if (Score >= 1 && Score <= 4) {
           passingData.put("passing_" + levelNum + "_" + worldType, String.valueOf(Score));
           passingData.put("passing_" + levelNum, "1 Star");
       } else if (Score >= 5 && Score <= 9) {
           passingData.put("passing_" + levelNum + "_" + worldType, String.valueOf(Score));
           passingData.put("passing_" + levelNum, "2 Stars");
       } else if (Score > 10) {
           passingData.put("passing_" + levelNum + "_" + worldType, String.valueOf(Score));
           passingData.put("passing_" + levelNum, "3 Stars");
       }

       if (gametype == "passing_level") {


           CollectionReference collectionRef = db.collection("Accounts")
                   .document("Students")
                   .collection("MathSquare");

           collectionRef.whereEqualTo("firstName", firstName)
                   .whereEqualTo("lastName", lastName)
                   .whereEqualTo("passing_level_must_complete", nextlevel)
                   .get()
                   .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           if (!task.getResult().isEmpty()) {
                               for (DocumentSnapshot document : task.getResult()) {
                                   DocumentReference docRef = collectionRef.document(document.getId());

                                   // Add levelNum to the correct world array without overwriting


                                   if (worldType.equals("world_one")) {
                                       completedLevelsField = "passing_completed_world_one_levels";
                                       worldCompletedField = "passing_world_one_completed";
                                   } else if (worldType.equals("world_two")) {
                                       completedLevelsField = "passing_completed_world_two_levels";
                                       worldCompletedField = "passing_world_two_completed";
                                   } else if (worldType.equals("world_three")) {
                                       completedLevelsField = "passing_completed_world_three_levels";
                                       worldCompletedField = "passing_world_three_completed";
                                   } else if (worldType.equals("world_four")) {
                                       completedLevelsField = "passing_completed_world_four_levels";
                                       worldCompletedField = "passing_world_four_completed";
                                   } else if (worldType.equals("world_five")) {
                                       completedLevelsField = "passing_completed_world_five_levels";
                                       worldCompletedField = "passing_world_five_completed";
                                   }

                                   if (!completedLevelsField.isEmpty()) {
                                       docRef.get().addOnSuccessListener(snapshot -> {
                                           if (snapshot.exists()) {
                                               // Check if array exists
                                               List<String> completedLevels = (List<String>) snapshot.get(completedLevelsField);
                                               if (completedLevels == null) {
                                                   // If field does not exist, create it
                                                   docRef.update(completedLevelsField, Arrays.asList(levelNum))
                                                           .addOnSuccessListener(aVoid ->
                                                                   Toast.makeText(this, "New level tracking created", Toast.LENGTH_SHORT).show()
                                                           )
                                                           .addOnFailureListener(e ->
                                                                   Toast.makeText(this, "Error updating: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                                           );
                                               } else {
                                                   // If field exists, add new level without overwriting
                                                   docRef.update(completedLevelsField, FieldValue.arrayUnion(levelNum))
                                                           .addOnSuccessListener(aVoid ->
                                                                   Toast.makeText(this, "Level updated successfully", Toast.LENGTH_SHORT).show()
                                                           )
                                                           .addOnFailureListener(e ->
                                                                   Toast.makeText(this, "Error updating level: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                                           );
                                               }
                                           }
                                       });
                                   }

                                   // Set next world if levelNum is "level_10"
                                   if (levelNum.equals("level_10")) {
                                       docRef.update(worldCompletedField, worldType)
                                               .addOnSuccessListener(aVoid ->
                                                       Toast.makeText(this, worldType + " completed!", Toast.LENGTH_SHORT).show()
                                               )
                                               .addOnFailureListener(e ->
                                                       Toast.makeText(this, "Error updating world: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                               );

                                       String nextWorld = "";
                                       if (worldType.equals("world_one")) nextWorld = "world_two";
                                       else if (worldType.equals("world_two"))
                                           nextWorld = "world_three";
                                       else if (worldType.equals("world_three"))
                                           nextWorld = "world_four";
                                       else if (worldType.equals("world_four"))
                                           nextWorld = "world_five";
                                       else if (worldType.equals("world_five"))
                                           nextWorld = "world_soon";

                                       docRef.update("passing_next_world", nextWorld);
                                   }
                               }
                           } else {
                               // If no document exists, create a new one
                               collectionRef.add(passingData)
                                       .addOnSuccessListener(aVoid ->
                                               Toast.makeText(this, "New record added", Toast.LENGTH_SHORT).show()
                                       )
                                       .addOnFailureListener(e ->
                                               Toast.makeText(this, "Error adding record: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                       );
                           }
                       }
                   })
                   .addOnFailureListener(e -> {
                       Toast.makeText(this, "Error fetching student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                   });


       } else if (gametype == "quiz") {
           CollectionReference collectionRef = db.collection("Accounts")
                   .document("Students")
                   .collection("MathSquare");

           // Query to check if a document with the same firstName, lastName, and quizid = "N/A" exists
           collectionRef.whereEqualTo("firstName", firstName)
                   .whereEqualTo("lastName", lastName)
                   .whereEqualTo("quizno", "N/A")
                   .get()
                   .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           if (!task.getResult().isEmpty()) {
                               // Document with quizid = "N/A" exists, replace it
                               for (DocumentSnapshot document : task.getResult()) {
                                   collectionRef.document(document.getId())
                                           .set(passingData) // Replaces the existing document
                                           .addOnSuccessListener(aVoid ->
                                                   Toast.makeText(this, "Quiz updated successfully", Toast.LENGTH_SHORT).show()
                                           )
                                           .addOnFailureListener(e ->
                                                   Toast.makeText(this, "Error updating quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                           );
                               }
                           } else {

                           }
                       } else {
                           Toast.makeText(this, "Error checking student data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                           // Document with quizid = "N/A" doesn't exist, create a new document
                           collectionRef.add(passingData)
                                   .addOnSuccessListener(aVoid ->
                                           Toast.makeText(this, "New quiz record added", Toast.LENGTH_SHORT).show()
                                   )
                                   .addOnFailureListener(e ->
                                           Toast.makeText(this, "Error adding quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                   );
                       }
                   })
                   .addOnFailureListener(e -> {
                       Toast.makeText(this, "Error fetching student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                   });
       } else if (gametype == "OnTimer") {
           CollectionReference collectionRef = db.collection("Accounts")
                   .document("Students")
                   .collection("MathSquare");

           // Query to check if a document with the same firstName, lastName, and quizid = "N/A" exists
           collectionRef.whereEqualTo("firstName", firstName)
                   .whereEqualTo("lastName", lastName)
                   .whereEqualTo("ontimer_difficulty", OnTimerDifficulty)
                   .get()
                   .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           if (!task.getResult().isEmpty()) {
                               // Document with quizid = "N/A" exists, replace it
                               for (DocumentSnapshot document : task.getResult()) {
                                   collectionRef.document(document.getId())
                                           .set(OnTimerData) // Replaces the existing document
                                           .addOnSuccessListener(aVoid ->
                                                   Toast.makeText(this, "Quiz updated successfully", Toast.LENGTH_SHORT).show()
                                           )
                                           .addOnFailureListener(e ->
                                                   Toast.makeText(this, "Error updating quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                           );
                               }
                           } else {

                           }
                       } else {
                           Toast.makeText(this, "Error checking student data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                           // Document with quizid = "N/A" doesn't exist, create a new document
                           collectionRef.add(OnTimerData)
                                   .addOnSuccessListener(aVoid ->
                                           Toast.makeText(this, "New quiz record added", Toast.LENGTH_SHORT).show()
                                   )
                                   .addOnFailureListener(e ->
                                           Toast.makeText(this, "Error adding quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                   );
                       }
                   })
                   .addOnFailureListener(e -> {
                       Toast.makeText(this, "Error fetching student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                   });
       } else if (gametype == "Practice") {
           CollectionReference collectionRef = db.collection("Accounts")
                   .document("Students")
                   .collection("MathSquare");

           // Query to check if a document with the same firstName, lastName, and quizid = "N/A" exists
           collectionRef.whereEqualTo("firstName", firstName)
                   .whereEqualTo("lastName", lastName)
                   .whereEqualTo("practice_score", "None")
                   .get()
                   .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           if (!task.getResult().isEmpty()) {
                               // Document with quizid = "N/A" exists, replace it
                               for (DocumentSnapshot document : task.getResult()) {
                                   collectionRef.document(document.getId())
                                           .set(PracticeData) // Replaces the existing document
                                           .addOnSuccessListener(aVoid ->
                                                   Toast.makeText(this, "Quiz updated successfully", Toast.LENGTH_SHORT).show()
                                           )
                                           .addOnFailureListener(e ->
                                                   Toast.makeText(this, "Error updating quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                           );
                               }
                           } else {

                           }
                       } else {
                           Toast.makeText(this, "Error checking student data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                           // Document with quizid = "N/A" doesn't exist, create a new document
                           collectionRef.add(PracticeData)
                                   .addOnSuccessListener(aVoid ->
                                           Toast.makeText(this, "New quiz record added", Toast.LENGTH_SHORT).show()
                                   )
                                   .addOnFailureListener(e ->
                                           Toast.makeText(this, "Error adding quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                   );
                       }
                   })
                   .addOnFailureListener(e -> {
                       Toast.makeText(this, "Error fetching student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                   });
       }

   }

}
