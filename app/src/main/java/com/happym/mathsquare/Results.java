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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Results extends AppCompatActivity {
    private FirebaseFirestore db;
    private String quizId, quizScore;
    private TextView showScore, showResult, showMotive;
    private List<MathProblem> problemSet = new ArrayList<>();
private MediaPlayer soundEffectPlayer;
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
                   sendScoreResult(getScore,getQuiz);
                }else if (gameType != null){
                   sendScoreResult(getScore,gameType);
                }
               
            }
            
        showMotive.setText("Excellent!");
        break;
    case "Good Job!":
            if(sharedPreferences.StudentIsLoggedIn(this)){
                if(getQuiz != null){
                   sendScoreResult(getScore,getQuiz);
                }else if (gameType != null){
                   sendScoreResult(getScore,gameType);
                }
               
            }
        showMotive.setText("Keep it Up!");
        break;
    case "Nice Try!":
            if(sharedPreferences.StudentIsLoggedIn(this)){
                if(getQuiz != null){
                   sendScoreResult(getScore,getQuiz);
                }else if (gameType != null){
                   sendScoreResult(getScore,gameType);
                }
               
            }
        showMotive.setText("You can do even better!");
        break;
    case "Failed":
            if(sharedPreferences.StudentIsLoggedIn(this)){
                if(getQuiz != null){
                   sendScoreResult(getScore,getQuiz);
                }else if (gameType != null){
                   sendScoreResult(getScore,gameType);
                }
            }
        showMotive.setText("Try Again!");
        break;
}

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
    private void sendScoreResult(int Score, String quizid) {
        String quizname = "Quiz " + quizid;
    String section = sharedPreferences.getSection(this);
    String grade = sharedPreferences.getGrade(this);
    String firstName = sharedPreferences.getFirstN(this);
    String lastName = sharedPreferences.getLastN(this);

    String uuid = UUID.randomUUID().toString(); // Generate a random UUID
    HashMap<String, Object> studentData = new HashMap<>();
    studentData.put("firstName", firstName);
    studentData.put("lastName", lastName);
    studentData.put("section", section);
    studentData.put("grade", grade);
    studentData.put("quizno", quizname);
    studentData.put("quizscore", String.valueOf(Score)); // Convert int score to String

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
                                    .set(studentData) // Replaces the existing document
                                    .addOnSuccessListener(aVoid -> 
                                        Toast.makeText(this, "Quiz updated successfully", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e -> 
                                        Toast.makeText(this, "Error updating quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                        }
                    } else {
                        // Document with quizid = "N/A" doesn't exist, create a new document
                        collectionRef.add(studentData)
                                .addOnSuccessListener(aVoid -> 
                                    Toast.makeText(this, "New quiz record added", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e -> 
                                    Toast.makeText(this, "Error adding quiz: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    }
                } else {
                    Toast.makeText(this, "Error checking student data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error fetching student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
}

}
