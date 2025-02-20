package com.happym.mathsquare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.happym.mathsquare.Model.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class dashboard_StudentsPanel extends AppCompatActivity {
    private TableLayout tableLayout;
    private FirebaseFirestore db;
    private ImageView deleteBtn,checkBtn;
    
    private SwitchCompat switchquiz1,switchquiz2,switchquiz3,switchquiz4, switchquiz5, switchquiz6;
    private List<TableRow> selectedRows = new ArrayList<>();
    private List<CheckBox> rowCheckBoxes = new ArrayList<>();
    private CheckBox allCheckBox, rowCheckbox;
    private boolean isDeleteButtonSelected = false;
    private boolean isCanceled = false;
    private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.layout_dashboard_students_panel); // your layout file
        tableLayout = findViewById(R.id.tableLayout); // Reference to the TableLayout
        
         // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        
        deleteBtn = findViewById(R.id.deletesection);
        checkBtn = findViewById(R.id.checkdelete);
        allCheckBox =  findViewById(R.id.rowCheckboxSelectAll);
        
        
        
        
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
            }else{
                checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_condition_create));
 checkBtn.setImageResource(R.drawable.ic_check);
            }
        
        
        switchquiz1 = findViewById(R.id.switch_quiz1);
         switchquiz2 = findViewById(R.id.switch_quiz2);
         switchquiz3 = findViewById(R.id.switch_quiz3);
         switchquiz4 = findViewById(R.id.switch_quiz4);
        switchquiz5 = findViewById(R.id.switch_quiz5);
         switchquiz6 = findViewById(R.id.switch_quiz6);
        

      /*  // Sample student data
        List<Student> students = new ArrayList<>();
        students.add(new Student("Cristopher", "Butter", "1", "1", "Nara"));
        students.add(new Student("James", "Smith", "2", "2", "Leo"));
        students.add(new Student("Alice", "Cooper", "3", "3", "Milo"));

        // Add rows dynamically
        addRowsToTable(students);
        
        */
        
        fetchStudents();
        initializeSwitchListeners();
        
         
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
               playSound("click.mp3");
        String newStatus = isChecked ? "open" : "closed";
        db.collection("Quizzes").document("Status").collection(quizId)
            .document("status")
            .update("status", newStatus)
            .addOnSuccessListener(aVoid -> Log.d("Quiz", quizId + " status updated to " + newStatus))
            .addOnFailureListener(error -> Log.e("Quiz", "Failed to update status for " + quizId, error));
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

    private void fetchStudents() {
    try {
        
            db.collection("Accounts")
                .document("Students")
                .collection("MathSquare") // Access the "MathSquare" collection
           .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Student> students = new ArrayList<>();

                            // Loop through each document in the "MathSquare" collection
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                try {
                                    String firstName = doc.getString("firstName");
                                    String lastName = doc.getString("lastName");
                                    String section = doc.getString("section");
                                    String grade = doc.getString("grade");
                                    String quizNo =doc.getString("quizno");
                                    String score = doc.getString("quizscore");
                                    
                                    String documentId = doc.getId();

                                    // Add parsed student to the list
                                    students.add(new Student(
                                            firstName + " " + lastName, // Full name
                                            section,
                                            grade,
                                            quizNo,
                                            score,
                                            documentId
                                    ));
                                } catch (Exception e) {
                                    Toast.makeText(this, "Error processing document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            // Update UI with the fetched student data
                            addRowsToTable(students);
                        } else {
                            Toast.makeText(this, "No student data found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error processing student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    try {
                        Toast.makeText(this, "Error fetching students: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Toast.makeText(this, "Unexpected error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    } catch (Exception e) {
        Toast.makeText(this, "Error initializing fetch: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
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
                } else {
                    selectedRows.remove(row);
                    // Uncheck the "Select All" checkbox if any row is deselected
                    allCheckBox.setOnCheckedChangeListener(null); // Temporarily remove listener
                    allCheckBox.setChecked(false);
                    allCheckBox.setOnCheckedChangeListener((buttonView1, isChecked1) -> {
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


