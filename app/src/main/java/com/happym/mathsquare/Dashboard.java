package com.happym.mathsquare;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.WindowCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;
import java.io.IOException;


public class Dashboard extends AppCompatActivity {
    private FirebaseFirestore db;
    private CreateSection createSectionDialog;
    private String teacherFirstName; // This should be initialized with the teacher's first name
    private RelativeLayout quizhistory_panel, sections_panel;
    private SwitchCompat switchquiz1,switchquiz2;
    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.layout_teacher_dashboard);
        FirebaseApp.initializeApp(this);
        
        
        
        // Firestore instance
        db = FirebaseFirestore.getInstance();
        
        switchquiz1 = findViewById(R.id.switch_quiz1);
         switchquiz2 = findViewById(R.id.switch_quiz2);
        initializeSwitchListeners();
        
        ImageView createsection = findViewById(R.id.createsection);
        quizhistory_panel = findViewById(R.id.quizhistory_panel);
        sections_panel = findViewById(R.id.sections_panel);
        
        createSectionDialog = new CreateSection();
        playSound("click.mp3");
        createsection.setOnClickListener(v -> {
            
                createSectionDialog.show(getSupportFragmentManager(), "PauseDialog"); // Show the dialog
        });
        
        quizhistory_panel.setOnClickListener(v -> {
                playSound("click.mp3");
    Intent intent = new Intent(this, dashboard_StudentsPanel.class);
    startActivity(intent);
});
        sections_panel.setOnClickListener(v -> {
                playSound("click.mp3");
            Intent intent = new Intent(this, dashboard_SectionPanel.class);
            startActivity(intent);
        });
     
    }
    
    // Method to set up a real-time listener and toggle Firestore status
private void setupSwitchListener(SwitchCompat switchCompat, String quizId) {
    // Real-time listener to sync status from Firestore
    db.collection("Quizzes").document("Status").collection(quizId)
        .document("status")
        .addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("Quiz", "Error listening to status changes for " + quizId, e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String status = documentSnapshot.getString("status");
                if ("open".equalsIgnoreCase(status)) {
                    switchCompat.setChecked(true);
                } else if ("closed".equalsIgnoreCase(status)) {
                    switchCompat.setChecked(false);
                }
            }
        });

    // Switch toggle listener to update Firestore
    switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
        String newStatus = isChecked ? "open" : "closed";
               playSound("click.mp3");
        db.collection("Quizzes").document("Status").collection(quizId)
            .document("status")
            .update("status", newStatus)
            .addOnSuccessListener(aVoid -> Log.d("Quiz", quizId + " status updated to " + newStatus))
            .addOnFailureListener(error -> Log.e("Quiz", "Failed to update status for " + quizId, error));
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
// Call this in onCreate() or appropriate lifecycle method
private void initializeSwitchListeners() {
    setupSwitchListener(switchquiz1, "quiz_1");
    setupSwitchListener(switchquiz2, "quiz_2");
}
    
}
