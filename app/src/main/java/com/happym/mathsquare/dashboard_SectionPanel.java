package com.happym.mathsquare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.happym.mathsquare.Model.Sections;
import com.happym.mathsquare.Model.Student;

import com.happym.mathsquare.dialog.CreateSection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import  com.happym.mathsquare.sharedPreferences;
import java.util.Map;

public class dashboard_SectionPanel extends AppCompatActivity implements   CreateSection.SectionDialogListener  {

    private TableLayout tableLayout;
    private ImageView deleteBtn,addBtn,checkBtn;
    private CreateSection createSectionDialog;
    private FirebaseFirestore db;
    private String teacherEmail; // Replace with the method to retrieve the email
    
    private SwitchCompat switchquiz1,switchquiz2,switchquiz3,switchquiz4, switchquiz5, switchquiz6;
    private List<TableRow> selectedRows = new ArrayList<>();
    private List<CheckBox> rowCheckBoxes = new ArrayList<>();
    private CheckBox allCheckBox, rowCheckbox;
    private boolean isDeleteButtonSelected = false;
    private boolean isCanceled = false;
   private List<Sections> existingSections = new ArrayList<>(); // Local model
private ListenerRegistration sectionsListener;
private MediaPlayer bgMediaPlayer;
    private MediaPlayer soundEffectPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.layout_dashboard_section_panel);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Replace this with actual logic to get the teacher's email
        teacherEmail = sharedPreferences.getEmail(this);

        // Reference to the TableLayout
        tableLayout = findViewById(R.id.tableLayout);
        createSectionDialog = new CreateSection();
        
        listenToTeacherSections(teacherEmail);
        
        deleteBtn = findViewById(R.id.deletesection);
        checkBtn = findViewById(R.id.checkdelete);
        addBtn = findViewById(R.id.createsection);
        allCheckBox =  findViewById(R.id.rowCheckboxSelectAll);
        addBtn.setOnClickListener(v -> {
            playSound("click.mp3");
                        createSectionDialog.setListener(dashboard_SectionPanel.this);
                        createSectionDialog.show(
                                getSupportFragmentManager(), "PauseDialog");
        });
        deleteBtn.setOnClickListener(v -> {
    // Toggle delete button selection state
    isDeleteButtonSelected = !isDeleteButtonSelected;
    playSound("click.mp3");
    if (isDeleteButtonSelected) {
        // First press: show check button and checkboxes
        isCanceled = false;
        allCheckBox.setVisibility(View.VISIBLE);
        checkBtn.setVisibility(View.VISIBLE);
        deleteBtn.setImageResource(R.drawable.ic_cancel);
        for (CheckBox checkBox : rowCheckBoxes) {
    checkBox.setVisibility(View.VISIBLE);
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
        
        
        
    }
    
   @Override
    public void onUpdateSection(boolean resumeGame) {
        if (resumeGame) {
            recreate();
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
    

private void listenToTeacherSections(String teacherEmail) {
    sectionsListener = db.collection("Accounts")
            .document("Teachers")
            .collection(teacherEmail)
            .document("MathSquare")
            .collection("MySections")
       .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    List<Sections> newSections = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String grade = documentSnapshot.getString("Grade");
                        String section = documentSnapshot.getString("Section");
                        String documentId = documentSnapshot.getString("DocumentKey");

                        if (grade != null && section != null && documentId != null) {
                            Sections newSection = new Sections(section, grade, documentId);

                            // Check if it already exists in the local model
                            if (!existingSections.contains(newSection)) { 
                                existingSections.add(newSection); // Add to local model
                                newSections.add(newSection); // Add to table
                            }
                        }
                    }
                   addRowsToTable(newSections);
                } else {
                    Toast.makeText(this, "No sections found for this teacher.", Toast.LENGTH_SHORT).show();
                }
            });
}


    
private void addRowsToTable(List<Sections> students) {
    List<TableRow> rows = new ArrayList<>(); // Store rows temporarily

    for (Sections student : students) {
        // Inflate a new row instance for each student
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_grade, tableLayout, false);

        // Assign the class-level rowCheckbox to the row's checkbox
        rowCheckbox = row.findViewById(R.id.rowCheckbox);
        TextView sectionText = row.findViewById(R.id.sectionText);
        TextView gradeText = row.findViewById(R.id.gradeText);

        sectionText.setText(student.getSection());
        gradeText.setText(student.getGrade());
        row.setTag(student.getDocId());
               
        // Store the row to process it later
        rows.add(row);
        rowCheckBoxes.add(rowCheckbox);  // Keep track of checkboxes

        // Add checkbox behavior for selection tracking
        rowCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playSound("click.mp3");
            if (isChecked) {
                selectedRows.add(row);
            } else {
                selectedRows.remove(row);

                // Uncheck "Select All" if any row is deselected
                allCheckBox.setOnCheckedChangeListener(null); // Temporarily remove listener
                allCheckBox.setChecked(false);
                allCheckBox.setOnCheckedChangeListener((buttonView1, isChecked1) -> {
                    toggleAllRows(isChecked1);
                });
            }
        });

        // Add row to the table but WITHOUT setting the tag yet
        tableLayout.addView(row);
    }

    // Second loop to ensure each row gets its correct documentId
    for (int i = 0; i < students.size(); i++) {
        TableRow row = rows.get(i);
        Sections student = students.get(i);

        row.setTag(student.getDocId()); // Set unique document ID for each row
        Log.d("RowTag", "Set tag for row: " + student.getDocId());
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
    playSound("click.mp3");
    
    List<TableRow> rowsToRemove = new ArrayList<>(selectedRows); // Create a copy to avoid modification errors
    selectedRows.clear(); // Clear selections after deletion

    for (TableRow row : rowsToRemove) {
        String docId = (String) row.getTag();

        db.collection("Accounts")
            .document("Teachers")
            .collection(teacherEmail)
            .document("MathSquare")
            .collection("MySections")
            .document(docId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String selectedGrade = documentSnapshot.getString("Grade");
                    String sectionName = documentSnapshot.getString("Section");

                    // Reference for Student_Sections sub-collection
                    DocumentReference sectionDocRef = db.collection("Sections")
                        .document(selectedGrade)
                        .collection("Student_Sections")
                        .document(sectionName);

                    // Step 1: Delete the document from MySections
                    db.collection("Accounts")
                        .document("Teachers")
                        .collection(teacherEmail)
                        .document("MathSquare")
                        .collection("MySections")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Remove from UI
                            tableLayout.removeView(row);
                            rowCheckBoxes.remove(row.findViewById(R.id.rowCheckbox));

                            // Step 2: Remove section from the grade's sections list
                            db.collection("Sections")
                                .document(selectedGrade)
                                .get()
                                .addOnSuccessListener(gradeDoc -> {
                                    if (gradeDoc.exists()) {
                                        Map<String, Object> existingData = gradeDoc.getData();
                                        if (existingData != null && existingData.containsKey("sections")) {
                                            List<String> existingSections = (List<String>) existingData.get("sections");

                                            if (existingSections != null && existingSections.contains(sectionName)) {
                                                existingSections.remove(sectionName); // Remove the section

                                                if (existingSections.isEmpty()) {
                                                    // If no more sections exist, delete the entire grade document
                                                    db.collection("Sections")
                                                        .document(selectedGrade)
                                                        .delete()
                                                        .addOnSuccessListener(aVoid3 -> {
                                                            Log.d("Delete", "Grade removed as it had no more sections.");
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(this, "Error deleting empty grade: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                        });
                                                } else {
                                                    // Update Firestore with the new sections list
                                                    db.collection("Sections")
                                                        .document(selectedGrade)
                                                        .update("sections", existingSections)
                                                        .addOnSuccessListener(aVoid2 -> {
                                                            // Step 4: Delete the section from Student_Sections sub-collection
                                                            sectionDocRef.delete()
                                                                .addOnSuccessListener(aVoid3 -> {
                                                                    // Step 5: Delete students under this grade and section
                                                                    deleteStudents(selectedGrade, sectionName);
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(this, "Error deleting section from Student_Sections: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                });
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(this, "Error updating sections: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                        });
                                                }
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error retrieving grade sections: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error deleting row: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error retrieving section data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    // Reset UI state
    isDeleteButtonSelected = false;
    allCheckBox.setVisibility(View.GONE);
    checkBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_green_off));
    checkBtn.setImageResource(R.drawable.ic_check);
    checkBtn.setVisibility(View.GONE);
    deleteBtn.setImageResource(R.drawable.ic_delete);

    // Remove checkboxes from rows
    for (TableRow row : rowsToRemove) {
        CheckBox rowCheckbox = row.findViewById(R.id.rowCheckbox);
        if (rowCheckbox != null) {
            rowCheckbox.setVisibility(View.GONE);
        }
    }

    Log.d("Delete", "Selected rows deleted and UI updated.");
}

    
private void deleteStudents(String grade, String section) {
    db.collection("Accounts")
        .document("Students")
        .collection("MathSquare")
        .whereEqualTo("grade", grade)
        .whereEqualTo("section", section)
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String studentId = document.getId(); // Get UUID (document ID)
                String firstName = document.getString("firstName");
                String lastName = document.getString("lastName");

                // Step 1: Delete student document from "Accounts/Students/MathSquare/{uuid}"
                db.collection("Accounts")
                    .document("Students")
                    .collection("MathSquare")
                    .document(studentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Step 2: Remove student from "studentIndexes" array in "Sections/{grade}/Student_Sections/{section}"
                        DocumentReference sectionDocRef = db.collection("Sections")
                            .document(grade)
                            .collection("Student_Sections")
                            .document(section);

                        Map<String, Object> student = new HashMap<>();
                        student.put("firstName", firstName);
                        student.put("lastName", lastName);

                        sectionDocRef.update("studentIndexes", FieldValue.arrayRemove(student))
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "Deleted student: " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error removing student from studentIndexes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error deleting student document: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            }
        })
        .addOnFailureListener(e -> {
            Toast.makeText(this, "Error retrieving students: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
}

    
}
