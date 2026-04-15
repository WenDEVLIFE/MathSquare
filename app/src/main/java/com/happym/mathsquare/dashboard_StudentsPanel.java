package com.happym.mathsquare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.WindowCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.happym.mathsquare.Model.Student;
import com.happym.mathsquare.Service.FirebaseDb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dashboard_StudentsPanel extends AppCompatActivity {
    private TableLayout tableLayout;
    private FirebaseFirestore db;
    private ImageView deleteBtn, checkBtn;

    private SwitchCompat switchquiz1, switchquiz2, switchquiz3, switchquiz4, switchquiz5, switchquiz6;
    private List<TableRow> selectedRows = new ArrayList<>();
    private List<CheckBox> rowCheckBoxes = new ArrayList<>();
    private CheckBox allCheckBox, rowCheckbox;
    private boolean isDeleteButtonSelected = false;
    private boolean isCanceled = false;
    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;

    private LinearLayout quizSwitchesContainer;
    private LinearLayout loadingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.layout_dashboard_students_panel); // your layout file
        tableLayout = findViewById(R.id.tableLayout); // Reference to the TableLayout

        // Initialize Firestore
        db = FirebaseDb.getFirestore();

        deleteBtn = findViewById(R.id.deletesection);
        checkBtn = findViewById(R.id.checkdelete);
        allCheckBox = findViewById(R.id.rowCheckboxSelectAll);


        deleteBtn.setOnClickListener(v -> {
            playSound("click.mp3");
            // Toggle delete button selection state
            isDeleteButtonSelected = !isDeleteButtonSelected;

            if (isDeleteButtonSelected) {
                // First press: show check button and checkboxes
                isCanceled = false;
                allCheckBox.setVisibility(View.VISIBLE);
                checkBtn.setVisibility(View.VISIBLE);


                deleteBtn.setImageResource(R.drawable.ic_cancel);

                if (rowCheckbox != null) {
                    for (CheckBox checkBox : rowCheckBoxes) {
                        checkBox.setVisibility(View.VISIBLE);
                    }

                }
            } else {
                // Second press: cancel and reset
                isCanceled = true;
                allCheckBox.setVisibility(View.GONE);
                checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_green_off));
                checkBtn.setImageResource(R.drawable.ic_check);
                checkBtn.setVisibility(View.GONE);
                deleteBtn.setImageResource(R.drawable.ic_delete);

                if (rowCheckbox != null) {
                    for (CheckBox checkBox : rowCheckBoxes) {
                        checkBox.setVisibility(View.GONE);
                    }
                }
            }
        });

        checkBtn.setOnClickListener(v -> {
            isCanceled = false;
            playSound("click.mp3");
            if (selectedRows.isEmpty()) {
                checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_green_off));
                checkBtn.setImageResource(R.drawable.ic_check);
                Toast.makeText(this, "No data deleted.", Toast.LENGTH_SHORT).show();
            } else {
                checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_condition_create));
                checkBtn.setImageResource(R.drawable.ic_check);

                new AlertDialog.Builder(this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete the selected rows?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                playSound("click.mp3");
                                deleteSelectedRows();
                                if (rowCheckbox != null) {
                                    for (CheckBox checkBox : rowCheckBoxes) {
                                        checkBox.setVisibility(View.GONE);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                playSound("click.mp3");
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });


        allCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playSound("click.mp3");
            toggleAllRows(isChecked);
        });


        if (selectedRows.isEmpty()) {

            checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_green_off));
            checkBtn.setImageResource(R.drawable.ic_check);
        } else {
            checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_condition_create));
            checkBtn.setImageResource(R.drawable.ic_check);
        }
        quizSwitchesContainer = findViewById(R.id.quizSwitchesContainer);
        fetchSectionsAndBuildSwitches();

        fetchStudents("Quiz");
    }

    private void showLoading(boolean show) {
        if (show) {
            if (loadingLayout == null) {
                float scale = getResources().getDisplayMetrics().density;

                loadingLayout = new LinearLayout(this);
                loadingLayout.setOrientation(LinearLayout.VERTICAL);
                loadingLayout.setGravity(Gravity.CENTER);
                loadingLayout.setPadding(0, 50, 0, 50);

                // The Spinner
                ProgressBar progressBar = new ProgressBar(this);
                int size = (int) (60 * scale);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                progressBar.setLayoutParams(lp);
                progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.custom_spinner_ring));

                // The Text
                TextView fetchingText = new TextView(this);
                fetchingText.setText("Fetching Quizzess...");
                fetchingText.setTextColor(Color.WHITE);
                fetchingText.setTextSize(16);
                fetchingText.setGravity(Gravity.CENTER);
                Typeface font = ResourcesCompat.getFont(this, R.font.paytone_one);
                if (font != null) fetchingText.setTypeface(font);

                LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textLp.topMargin = (int) (12 * scale);
                fetchingText.setLayoutParams(textLp);

                loadingLayout.addView(progressBar);
                loadingLayout.addView(fetchingText);
            }

            quizSwitchesContainer.removeAllViews();
            quizSwitchesContainer.addView(loadingLayout);
        } else {
            if (loadingLayout != null) {
                quizSwitchesContainer.removeView(loadingLayout);
            }
        }
    }
    private void fetchSectionsAndBuildSwitches() {
        String currentTeacherUid = sharedPreferences.getEmail(this);

        if (currentTeacherUid == null) {
            Toast.makeText(this, "User session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading(true);
        db.collection("Sections")
                .whereEqualTo("teacherUid", currentTeacherUid)
                .orderBy("Grade_Number", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    showLoading(false);
                    quizSwitchesContainer.removeAllViews();
                    Map<Integer, List<Map<String, Object>>> gradeMap = new java.util.TreeMap<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        int grade = ((Long) doc.get("Grade_Number")).intValue();
                        String section = doc.getString("Section");

                        if (!gradeMap.containsKey(grade)) {
                            gradeMap.put(grade, new ArrayList<>());
                        }
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("section", section);
                        entry.put("grade", grade);
                        gradeMap.get(grade).add(entry);
                    }
                    for (Map.Entry<Integer, List<Map<String, Object>>> gradeEntry : gradeMap.entrySet()) {
                        int grade = gradeEntry.getKey();
                        List<Map<String, Object>> sections = gradeEntry.getValue();
                        TextView gradeHeader = new TextView(this);
                        gradeHeader.setText("Grade " + grade);
                        gradeHeader.setTypeface(ResourcesCompat.getFont(this, R.font.paytone_one));
                        gradeHeader.setTextSize(18);
                        gradeHeader.setTextColor(ContextCompat.getColor(this, R.color.white));
                        gradeHeader.setPadding(16, 24, 16, 8);
                        quizSwitchesContainer.addView(gradeHeader);

                        for (Map<String, Object> sectionData : sections) {
                            String sectionName = (String) sectionData.get("section");
                            buildSectionQuizSwitches(grade, sectionName);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(this, "Failed to load sections: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void buildSectionQuizSwitches(int grade, String sectionName) {
        Typeface paytoneFont = ResourcesCompat.getFont(this, R.font.paytone_one);

        // Section label
        TextView sectionHeader = new TextView(this);
        sectionHeader.setText("Section: " + sectionName);
        if (paytoneFont != null) sectionHeader.setTypeface(paytoneFont);
        sectionHeader.setTextSize(14);
        sectionHeader.setTextColor(ContextCompat.getColor(this, R.color.white));
        sectionHeader.setPadding(16, 12, 16, 4);
        quizSwitchesContainer.addView(sectionHeader);

        // Build Quiz 1–6 rows for this section
        for (int quizNum = 1; quizNum <= 6; quizNum++) {
            String docId = "quiz_" + quizNum + "_grade" + grade + "_section" + sectionName;
            String quizLabel = "Quiz " + quizNum;

            // Outer row
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 8, 0, 0);
            row.setLayoutParams(rowParams);

            // Label box
            LinearLayout labelBox = new LinearLayout(this);
            labelBox.setOrientation(LinearLayout.HORIZONTAL);
            labelBox.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.START);
            labelBox.setBackground(ContextCompat.getDrawable(this, R.drawable.box_create));
            LinearLayout.LayoutParams labelBoxParams = new LinearLayout.LayoutParams(
                    (int) (220 * getResources().getDisplayMetrics().density),
                    (int) (50 * getResources().getDisplayMetrics().density));
            labelBox.setLayoutParams(labelBoxParams);

            // Quiz Name Text
            TextView quizText = new TextView(this);
            quizText.setText(quizLabel);
            if (paytoneFont != null) quizText.setTypeface(paytoneFont);
            quizText.setTextSize(16);
            quizText.setTextColor(ContextCompat.getColor(this, R.color.white));
            quizText.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams quizTextParams = new LinearLayout.LayoutParams(
                    (int) (80 * getResources().getDisplayMetrics().density),
                    LinearLayout.LayoutParams.MATCH_PARENT);
            quizText.setLayoutParams(quizTextParams);

            // Separator Text
            TextView separator = new TextView(this);
            separator.setText("-");
            if (paytoneFont != null) separator.setTypeface(paytoneFont);
            separator.setTextSize(16);
            separator.setTextColor(ContextCompat.getColor(this, R.color.white));
            separator.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                    (int) (20 * getResources().getDisplayMetrics().density),
                    LinearLayout.LayoutParams.MATCH_PARENT);
            separator.setLayoutParams(sepParams);

            // Grade Text
            TextView gradeText = new TextView(this);
            gradeText.setText("Grade " + grade);
            if (paytoneFont != null) gradeText.setTypeface(paytoneFont);
            gradeText.setTextSize(16);
            gradeText.setTextColor(ContextCompat.getColor(this, R.color.white));
            gradeText.setGravity(android.view.Gravity.CENTER);
            gradeText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            labelBox.addView(quizText);
            labelBox.addView(separator);
            labelBox.addView(gradeText);

            // Switch
            SwitchCompat switchCompat = new SwitchCompat(this);
            switchCompat.setThumbDrawable(ContextCompat.getDrawable(this, R.drawable.switch_thumb));
            switchCompat.setTrackDrawable(ContextCompat.getDrawable(this, R.drawable.switch_track));
            switchCompat.setShowText(false);
            LinearLayout.LayoutParams switchParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            switchCompat.setLayoutParams(switchParams);

            row.addView(labelBox);
            row.addView(switchCompat);
            quizSwitchesContainer.addView(row);

            // Setup listener for Firebase updates
            setupSectionSwitchListener(switchCompat, docId, quizNum);
        }
    }
    private void setupSectionSwitchListener(SwitchCompat switchCompat, String docId, int quizNum) {
        // Parent quiz document path: Quizzes > quiz_N > docId
        DocumentReference statusRef = db.collection("Quizzes")
                .document("quiz_" + quizNum)
                .collection("quiz_" + quizNum) // sub-collection matching your structure
                .document(docId);

        // Real-time listener
        statusRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("QuizSwitch", "Listen error for " + docId, e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                String status = snapshot.getString("status");
                // Temporarily remove listener to avoid triggering Firestore write on UI sync
                switchCompat.setOnCheckedChangeListener(null);
                switchCompat.setChecked("open".equalsIgnoreCase(status));
                attachSwitchToggle(switchCompat, statusRef, docId);
            } else {
                // Document doesn't exist yet, default to off
                switchCompat.setOnCheckedChangeListener(null);
                switchCompat.setChecked(false);
                attachSwitchToggle(switchCompat, statusRef, docId);
            }
        });
    }

    private void attachSwitchToggle(SwitchCompat switchCompat, DocumentReference statusRef, String docId) {
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playSound("click.mp3");
            String newStatus = isChecked ? "open" : "closed";
            Map<String, Object> data = new HashMap<>();
            data.put("status", newStatus);

            statusRef.set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid ->
                            Log.d("QuizSwitch", docId + " → " + newStatus))
                    .addOnFailureListener(err ->
                            Log.e("QuizSwitch", "Failed to update " + docId, err));
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

    // Method to set up a real-time listener and toggle Firestore status
    private void setupSwitchListener(SwitchCompat switchCompat, String quizId) {
        DocumentReference statusRef = db.collection("Quizzes").document(quizId);

        // Listen to real-time updates
        statusRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("Quiz", "Error listening to status changes for " + quizId, e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String status = documentSnapshot.getString("status");
                switchCompat.setChecked("open".equalsIgnoreCase(status));
            }
        });

        // Update Firestore when switch is toggled
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playSound("click.mp3");
            String newStatus = isChecked ? "open" : "closed";

            // Use set with merge to create if missing
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("status", newStatus);

            statusRef.set(statusMap, SetOptions.merge())
                    .addOnSuccessListener(aVoid ->
                            Log.d("Quiz", quizId + " status updated to " + newStatus))
                    .addOnFailureListener(error ->
                            Log.e("Quiz", "Failed to update status for " + quizId, error));
        });
    }

    // Call this in onCreate() or appropriate lifecycle method
    private void initializeSwitchListeners() {
        setupSwitchListener(switchquiz1, "quiz_1");
        setupSwitchListener(switchquiz2, "quiz_2");
        setupSwitchListener(switchquiz3, "quiz_3");
        setupSwitchListener(switchquiz4, "quiz_4");
        setupSwitchListener(switchquiz5, "quiz_5");
        setupSwitchListener(switchquiz6, "quiz_6");
    }

    private void fetchStudents(String filter) {
        Log.d("FETCH_STUDENTS", "Starting fetch from QuizHistory...");

        db.collection("Accounts")
                .document("Students")
                .collection("MathSquare")
                .get()
                .addOnSuccessListener(studentDocs -> {

                    List<Student> students = new ArrayList<>();
                    int[] remaining = {studentDocs.size()};

                    if (studentDocs.isEmpty()) {
                        addRowsToTable(students);
                        return;
                    }

                    for (QueryDocumentSnapshot studentDoc : studentDocs) {
                        String studentId = studentDoc.getId();

                        studentDoc.getReference()
                                .collection("QuizHistory")
                                .orderBy("quizno_int", Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(historyDocs -> {

                                    for (QueryDocumentSnapshot histDoc : historyDocs) {
                                        String firstName = histDoc.getString("firstName");
                                        String lastName  = histDoc.getString("lastName");
                                        String section   = histDoc.getString("section");
                                        String grade     = histDoc.getString("grade");
                                        String quizNo    = histDoc.getString("quizno");
                                        String score     = histDoc.getString("quizscore");
                                        String compositeId = studentId + "|" + histDoc.getId();

                                        students.add(new Student(
                                                firstName + " " + lastName,
                                                section,
                                                grade,
                                                quizNo,
                                                score,
                                                compositeId
                                        ));
                                    }

                                    remaining[0]--;
                                    if (remaining[0] == 0) {
                                        students.sort((a, b) -> {
                                            String qa = a.getQuizNo() != null ? a.getQuizNo() : "";
                                            String qb = b.getQuizNo() != null ? b.getQuizNo() : "";
                                            return qa.compareTo(qb);
                                        });
                                        runOnUiThread(() -> addRowsToTable(students));
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    remaining[0]--;
                                    Log.e("FETCH_STUDENTS", "QuizHistory fetch failed: " + e.getMessage());
                                    if (remaining[0] == 0) {
                                        runOnUiThread(() -> addRowsToTable(students));
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FETCH_STUDENTS", "Student fetch failed: " + e.getMessage());
                    Toast.makeText(this, "Error fetching students: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }


    private void addRowsToTable(List<Student> students) {
        try {
            for (Student student : students) {
                TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_student, null);

                rowCheckbox = row.findViewById(R.id.rowCheckbox);
                TextView nameText = row.findViewById(R.id.nameText);
                TextView sectionText = row.findViewById(R.id.sectionText);
                TextView gradeText = row.findViewById(R.id.gradeText);
                TextView quizNoText = row.findViewById(R.id.quizNoText);
                TextView scoreText = row.findViewById(R.id.scoreText);

                nameText.setText(student.getName());
                sectionText.setText(student.getSection());
                gradeText.setText(student.getGrade());
                quizNoText.setText(student.getQuizNo());
                scoreText.setText(student.getScore());

                row.setTag(student.getDocId());

                // Track selected rows and individual checkboxes
                rowCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    playSound("click.mp3");
                    if (isChecked) {
                        selectedRows.add(row);
                        checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_condition_create));
                        checkBtn.setImageResource(R.drawable.ic_delete);
                    } else {
                        selectedRows.remove(row);
                        // Uncheck the "Select All" checkbox if any row is deselected
                        allCheckBox.setOnCheckedChangeListener(null); // Temporarily remove listener
                        allCheckBox.setChecked(false);
                        allCheckBox.setOnCheckedChangeListener((buttonView1, isChecked1) -> {
                            checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_condition_create));
                            checkBtn.setImageResource(R.drawable.ic_delete);
                            toggleAllRows(isChecked1);
                        });
                    }
                });

                rowCheckBoxes.add(rowCheckbox); // Add the checkbox to the list
                tableLayout.addView(row);
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred while adding rows: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // Log the error for debugging purposes
        }
    }

    private void toggleAllRows(boolean isChecked) {
        selectedRows.clear(); // Clear previous selections

        for (int i = 0; i < rowCheckBoxes.size(); i++) {
            CheckBox checkbox = rowCheckBoxes.get(i);

            checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_condition_create));
            checkBtn.setImageResource(R.drawable.ic_delete);

            checkbox.setChecked(isChecked); // Update each checkbox

            // If checked, add the corresponding row to the selected rows
            TableRow row = (TableRow) checkbox.getParent();
            if (isChecked) {
                selectedRows.add(row);
            }
        }
    }


    private void deleteSelectedRows() {
        for (TableRow row : selectedRows) {
            String docId = (String) row.getTag();

            db.collection("Accounts")
                    .document("Students")
                    .collection("MathSquare")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        tableLayout.removeView(row); // Remove row from TableLayout

                        isDeleteButtonSelected = false;
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error deleting row: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }

        selectedRows.clear(); // Clear the list after deletion
        Toast.makeText(this, "Selected rows deleted successfully.", Toast.LENGTH_SHORT).show();
        allCheckBox.setVisibility(View.GONE);
        checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_green_off));
        checkBtn.setImageResource(R.drawable.ic_check);
        checkBtn.setVisibility(View.GONE);
        deleteBtn.setImageResource(R.drawable.ic_delete);
        for (CheckBox checkBox : rowCheckBoxes) {
            checkBox.setVisibility(View.GONE);
        }

    }


}


