package com.happym.mathsquare;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.happym.mathsquare.Animation.NumBGAnimation;
import com.happym.mathsquare.Animation.VignetteEffect;
import com.happym.mathsquare.Model.LeaderboardEntry;
import com.happym.mathsquare.Service.FirebaseDb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noLeaderboardsText;

    private FirebaseFirestore db;
    private LeaderboardAdapter adapter;
    private List<LeaderboardEntry> leaderboardList;

    private MediaPlayer soundEffectPlayer;
    private FrameLayout backgroundFrame;

    private View loadingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        FirebaseApp.initializeApp(this);
        db = FirebaseDb.getFirestore();
        MusicManager.playLeaderboard(this, "leaderboards.mp3");

        backgroundFrame = findViewById(R.id.main);
        backgroundFrame.post(() -> VignetteEffect.apply(this, backgroundFrame));

        recyclerView        = findViewById(R.id.leaderboard_recycler);
        noLeaderboardsText  = findViewById(R.id.text_no_leaderboards);
        loadingContainer    = findViewById(R.id.loading_container);
        ImageView btnHome   = findViewById(R.id.btn_home);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardList = new ArrayList<>();
        adapter = new LeaderboardAdapter(leaderboardList);
        recyclerView.setAdapter(adapter);

        btnHome.setOnClickListener(v -> {
            playSound("click");
            getOnBackPressedDispatcher().onBackPressed();
        });

        loadLeaderboards();
    }

    public void loadLeaderboards() {
        loadingContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noLeaderboardsText.setVisibility(View.GONE);

        CollectionReference leaderboardRef = db.collection("Leaderboards");

        leaderboardRef.get().addOnCompleteListener(task -> {
            loadingContainer.setVisibility(View.GONE);

            if (task.isSuccessful() && task.getResult() != null) {
                leaderboardList.clear();

                for (DocumentSnapshot document : task.getResult()) {
                    if (!document.contains("firstName") || document.contains("a")) {
                        continue;
                    }

                    String firstName = document.getString("firstName");
                    String lastName  = document.getString("lastName");
                    String section   = document.getString("section");
                    String grade     = document.getString("grade");

                    Number pointsNum = (Number) document.get("points");
                    int points = (pointsNum != null) ? pointsNum.intValue() : 0;

                    if (firstName == null) firstName = "Anonymous";
                    if (lastName == null)  lastName  = "";
                    if (section == null)   section   = "N/A";
                    if (grade == null)     grade     = "N/A";

                    leaderboardList.add(new LeaderboardEntry(
                            firstName, lastName, section, grade, points));
                }

                leaderboardList.sort((o1, o2) ->
                        Integer.compare(o2.getPoints(), o1.getPoints()));

                if (leaderboardList.isEmpty()) {
                    noLeaderboardsText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noLeaderboardsText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Exception e = task.getException();
                if (e != null) e.printStackTrace();
                Toast.makeText(this, "Failed to load leaderboards.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.stopLeaderboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.playLeaderboard(this, "leaderboard_ambient.mp3");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.stopLeaderboard();
    }
}