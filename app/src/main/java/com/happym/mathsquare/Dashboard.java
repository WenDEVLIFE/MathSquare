package com.happym.mathsquare;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.WindowCompat;

import com.google.firebase.FirebaseApp;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.happym.mathsquare.dashboard_StudentsPanel;
import com.happym.mathsquare.dashboard_SectionPanel;
import com.happym.mathsquare.dialog.CreateSection;
import java.io.IOException;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.BounceInterpolator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import com.happym.mathsquare.Model.Sections;
import com.happym.mathsquare.Model.Student;

public class Dashboard extends AppCompatActivity {
    private FirebaseFirestore db;
    private CreateSection createSectionDialog;
    private String teacherFirstName; // This should be initialized with the teacher's first name
    private RelativeLayout quizhistory_panel, sections_panel;
    private SwitchCompat switchquiz1,switchquiz2;
    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;
    private TextView firstSection, firstGrade;
    private ListenerRegistration sectionsListener;
    private String teacherEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.layout_teacher_dashboard);
        FirebaseApp.initializeApp(this);
        
       LinearLayout btnLogOut = findViewById(R.id.btn_logout);
        
        
       animateButtonFocus(btnLogOut);
        
        // Firestore instance
        db = FirebaseFirestore.getInstance();
        
         // Replace this with actual logic to get the teacher's email
        teacherEmail = sharedPreferences.getEmail(this);
        
        switchquiz1 = findViewById(R.id.switch_quiz1);
         switchquiz2 = findViewById(R.id.switch_quiz2);
        
        firstSection = findViewById(R.id.first_grade);
         firstGrade = findViewById(R.id.first_section);
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
        
        btnLogOut.setOnClickListener(view -> {
               animateButtonClick(btnLogOut);
               Intent intent = new Intent(this, signInUp.class);
                                sharedPreferences.StudentIsSetLoggedIn(this, false);
                                            sharedPreferences.setLoggedIn(this, false);
sharedPreferences.clearSection(this);
                    sharedPreferences.clearGrade(this);
                    sharedPreferences.clearFirstName(this);
                    sharedPreferences.clearLastName(this);
               playSound("click.mp3");
                                startActivity(intent);
                finish();
                                Toast.makeText(this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                                stopButtonFocusAnimation(btnLogOut);
                                animateButtonFocus(btnLogOut);
                
        });
     
    }
    
   @Override
protected void onStart() {
    super.onStart();
    listenToTeacherSections(teacherEmail,firstSection,firstGrade); // Replace with actual email
}
    
    private void listenToTeacherSections(String teacherEmail, TextView gradeTextView, TextView sectionTextView) {
    sectionsListener = db.collection("Accounts")
            .document("Teachers")
            .collection(teacherEmail)
            .document("MathSquare")
            .collection("MySections")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Ensure documents are ordered by creation time
            .limit(1) // Fetch only the latest document
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot latestDocument = queryDocumentSnapshots.getDocuments().get(0);

                    // Retrieve grade and section fields
                    String grade = latestDocument.getString("Grade");
                    String section = latestDocument.getString("Section");

                    // Update TextViews if data exists
                    if (grade != null) {
                        gradeTextView.setText(grade);
                    }
                    if (section != null) {
                        sectionTextView.setText(section);
                    }
                } else {
                    Toast.makeText(this, "No sections found for this teacher.", Toast.LENGTH_SHORT).show();
                }
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
    
    
}
