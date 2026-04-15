package com.happym.mathsquare;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.happym.mathsquare.Model.LeaderboardEntry;
import com.happym.mathsquare.Service.FirebaseDb;

import java.io.IOException;
import java.util.List;

public class StudentProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private ImageView profileImage;

    private MediaPlayer soundEffectPlayer;
    private TextView firstNameText, lastNameText;
    private LinearLayout badgesContainer;
    private TextView leaderboardRankText;
    private TextView gradeText, sectionText, teacherText;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);

        MusicManager.playBGGame(this, "music.mp3");

        backButton = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        firstNameText = findViewById(R.id.first_name);
        lastNameText = findViewById(R.id.last_name);
        badgesContainer = findViewById(R.id.badges_container);
        leaderboardRankText = findViewById(R.id.leaderboard_rank);
        gradeText = findViewById(R.id.student_grade);
        sectionText = findViewById(R.id.student_section);
        teacherText = findViewById(R.id.student_teacher);
        TextView totalPointsTextView = findViewById(R.id.my_total_points);

        backButton.setOnClickListener(v -> {
            playSound("click");
            getOnBackPressedDispatcher().onBackPressed();
        });

        FirebaseApp.initializeApp(this);
        db = FirebaseDb.getFirestore();

        // Load student info from SharedPreferences or other storage
        String firstName = sharedPreferences.getFirstN(this);
        String lastName = sharedPreferences.getLastN(this);
        String grade = sharedPreferences.getGrade(this);
        String section = sharedPreferences.getSection(this);
        String teacher = sharedPreferences.getTeacherN(this);

        firstNameText.setText(firstName);
        lastNameText.setText(lastName);
        gradeText.setText("Grade: " + grade);
        sectionText.setText("Section: " + section);
        teacherText.setText("Teacher: " + teacher);

        // Reference student document
        CollectionReference studentCollection = db
                .collection("Accounts")
                .document("Students")
                .collection("MathSquare");

        Query studentQuery = studentCollection
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("section", section)
                .whereEqualTo("grade", grade)
                .whereGreaterThan("total_points", 0);

        studentQuery.get().addOnSuccessListener(querySnapshots -> {
            for (DocumentSnapshot document : querySnapshots) {
                if (document.exists()) {
                    Log.d("FirestoreDebug", "Document found: " + document.getId());

                    // Load displayed badges
                    List<String> displayedBadges = (List<String>) document.get("displayed_achievement_screen");
                    if (displayedBadges != null) {
                        Log.d("FirestoreDebug", "Displayed badges: " + displayedBadges.toString());
                        badgesContainer.removeAllViews();
                        int badgesPerRow = 5;

                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        int screenWidth = metrics.widthPixels;
                        int totalMargin = (badgesPerRow + 1) * dpToPx(8);
                        int badgeSize = (screenWidth - totalMargin) / badgesPerRow;

                        for (String badgeName : displayedBadges) {
                            Log.d("FirestoreDebug", "Adding badge: " + badgeName);
                            ImageView badgeView = new ImageView(this);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(badgeSize, badgeSize);
                            params.setMargins(dpToPx(8), 0, dpToPx(8), 0);
                            badgeView.setLayoutParams(params);

                            int drawableRes = getBadgeDrawable(badgeName);
                            badgeView.setImageResource(drawableRes);
                            badgeView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            badgesContainer.addView(badgeView);
                        }
                    } else {
                        Log.d("FirestoreDebug", "No badges to display for this document.");
                    }

                    // Get total points safely (handles Long, Double, Integer)
                    Object totalPointsObj = document.get("total_points");
                    int pointsInt;
                    if (totalPointsObj instanceof Number) {
                        pointsInt = ((Number) totalPointsObj).intValue();
                        Log.d("FirestoreDebug", "Total points fetched: " + pointsInt);
                    } else {
                        pointsInt = 0;
                        Log.d("FirestoreDebug", "total_points field missing or not a number, defaulting to 0");
                    }

                    totalPointsTextView.setText("My Total Points: " + pointsInt);


                    CollectionReference leaderboardRef = db.collection("Leaderboards");
                    leaderboardRef
                            .whereEqualTo("firstName", firstName)
                            .whereEqualTo("lastName", lastName)
                            .whereEqualTo("section", section)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    Log.d("FirestoreDebug", "Student found in leaderboard, updating points...");
                                    for (DocumentSnapshot snapshot : querySnapshot) {
                                        Log.d("FirestoreDebug", "Updating leaderboard doc: " + snapshot.getId() + " with points: " + pointsInt);
                                        snapshot.getReference().update("points", pointsInt)
                                                .addOnFailureListener(e -> Log.e("FirestoreDebug", "Failed to update points", e));
                                    }
                                } else {
                                    Log.d("FirestoreDebug", "Student not found in leaderboard, creating new entry...");
                                    String uuid = java.util.UUID.randomUUID().toString();
                                    LeaderboardEntry newEntry = new LeaderboardEntry(firstName, lastName, section, grade, pointsInt);

                                    leaderboardRef.document(uuid)
                                            .set(newEntry)
                                            .addOnFailureListener(e -> Log.e("FirestoreDebug", "Failed to create leaderboard entry", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.e("FirestoreDebug", "Failed to fetch leaderboard", e));
                }

            }
        }).addOnFailureListener(Throwable::printStackTrace);

        // Calculate and display leaderboard rank
        db.collection("Leaderboards")
                .orderBy("points", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    int rank = 1;
                    for (DocumentSnapshot doc : querySnapshots) {
                        String fn = doc.getString("firstName");
                        String ln = doc.getString("lastName");
                        String sec = doc.getString("section");

                        assert fn != null;
                        if (fn.equals(firstName)) {
                            assert ln != null;
                            if (ln.equals(lastName)) {
                                assert sec != null;
                                if (sec.equals(section)) {
                                    leaderboardRankText.setText("Leaderboard Rank: #" + rank);
                                    break;
                                }
                            }
                        }
                        rank++;
                    }
                });
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

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
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

    private int getBadgeDrawable(String badgeName) {
        switch (badgeName) {
            case "Addition Master": return R.drawable.ic_addition_master;
            case "Math Explorer": return R.drawable.ic_math_explorer;
            case "Quiz Whiz": return R.drawable.ic_quiz_whiz;
            case "Speed Demon": return R.drawable.ic_speed_demon;
            default: return R.drawable.ic_math_explorer;
        }
    }

}