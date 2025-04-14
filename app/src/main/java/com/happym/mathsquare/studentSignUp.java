package com.happym.mathsquare;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.view.ViewGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.happym.mathsquare.MainActivity;

import java.util.Map;
import java.util.UUID;
import com.happym.mathsquare.sharedPreferences;
import androidx.core.view.WindowCompat;
public class studentSignUp extends AppCompatActivity {
    
    private String selectedSectionId;
    private String selectedSectionName;
    private String selectedGradeLevel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        FirebaseApp.initializeApp(this);
        
        setContentView(R.layout.layoutstudent_sign_up);
        
        // Firestore instance
FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        AutoCompleteTextView sectionChooser = findViewById(R.id.SectionChooser);
        TextInputLayout firstNameLayout = findViewById(R.id.first_name_layout);
        TextInputLayout lastNameLayout = findViewById(R.id.last_name_layout);
        AppCompatButton submitButton = findViewById(R.id.btn_submit);
        AutoCompleteTextView numberDropdownPicker = findViewById(R.id.numberDropdownPicker);
        TextView spinnerError = findViewById(R.id.spinnerError);

TextInputEditText firstNameEditText = (TextInputEditText) firstNameLayout.getEditText();

InputFilter noSpacesFilter = (source, start, end, dest, dstart, dend) -> {
    if (source.toString().contains(" ")) {
        return "";
    }
    return source;
};

if (firstNameEditText != null) {
    firstNameEditText.setFilters(new InputFilter[]{noSpacesFilter});
}

       TextInputEditText lastNameEditText = (TextInputEditText) lastNameLayout.getEditText();

if (lastNameEditText != null) {
    lastNameEditText.setFilters(new InputFilter[]{noSpacesFilter});
}
        
// Set up Dropdown with list of grades using default styling
List<String> grades = Arrays.asList("1", "2", "3", "4", "5", "6");

ArrayAdapter<String> adapterGrades = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades);
adapterGrades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterGrades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberDropdownPicker.setAdapter(adapterGrades);


        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            
            
        } else {
            

        }
        
numberDropdownPicker.setOnItemClickListener((adapterView, view, position, id) -> {
    String selectedGrade = adapterGrades.getItem(position);

    if (selectedGrade.matches("[1-6]")) {
        int gradeNumber = Integer.parseInt(selectedGrade);

        db.collection("Sections")
            .whereEqualTo("Grade_Number", gradeNumber)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<String> sectionNames = new ArrayList<>();
                Map<String, String> sectionIdMap = new HashMap<>();

                for (QueryDocumentSnapshot doc : querySnapshot) {
                    String section = doc.getString("Section");
                    if (section != null) {
                        sectionNames.add(section);
                        sectionIdMap.put(section, doc.getId());
                    }
                }

                if (!sectionNames.isEmpty()) {
                    ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(studentSignUp.this, android.R.layout.simple_dropdown_item_1line, sectionNames);
                    sectionChooser.setAdapter(sectionAdapter);

                    sectionChooser.setOnItemClickListener((adapterView1, view1, i, l) -> {
                        String selectedSection = sectionAdapter.getItem(i);
                        String documentId = sectionIdMap.get(selectedSection);

                        selectedSectionId = documentId;
                        selectedSectionName = selectedSection;
                        selectedGradeLevel = selectedGrade;
                    });
                } else {
                    Toast.makeText(studentSignUp.this, "No sections found for grade " + selectedGrade, Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> Toast.makeText(studentSignUp.this, "Error fetching sections: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
});



// Set up SectionChooser to show dropdown when clicked
sectionChooser.setOnClickListener(v -> sectionChooser.showDropDown());


        // Clear errors on text change for firstName and lastName
        ((TextInputEditText) firstNameLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firstNameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        ((TextInputEditText) lastNameLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastNameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        animateButtonFocus(submitButton);



        submitButton.setOnClickListener(v -> {
            boolean hasError = false;

            // Reset errors and hide spinner error initially
            firstNameLayout.setError(null);
            lastNameLayout.setError(null);
            sectionChooser.setError(null);
            spinnerError.setVisibility(View.GONE);

            // Validate First Name
            String firstName = ((TextInputEditText) firstNameLayout.getEditText()).getText().toString().trim();
            if (TextUtils.isEmpty(firstName)) {
                firstNameLayout.setError("Student First Name is required");
                animateShakeRotateEditTextErrorAnimation(firstNameLayout);
                hasError = true;
            }

            // Validate Last Name
            String lastName = ((TextInputEditText) lastNameLayout.getEditText()).getText().toString().trim();
            if (TextUtils.isEmpty(lastName)) {
                lastNameLayout.setError("Student Last Name is required");
                animateShakeRotateEditTextErrorAnimation(lastNameLayout);
                hasError = true;
            }

            // Validate Section
            String section = sectionChooser.getText().toString().trim();
            if (TextUtils.isEmpty(section)) {
                sectionChooser.setError("Student Section is required");
                animateShakeRotateEditTextErrorAnimation(sectionChooser);
                hasError = true;
            }

            // Validate Spinner selection (Grade)
            String grade = numberDropdownPicker.getText().toString().trim();
if (TextUtils.isEmpty(grade) || !grade.matches("[1-6]")) {
    spinnerError.setText("Please select your grade");
    spinnerError.setVisibility(View.VISIBLE);
    hasError = true;
}

            // If no errors, proceed with saving the data
            if (!hasError) {
                // Prepare data for Firestore (Student Data)
                String uuid = UUID.randomUUID().toString(); // Generate a random UUID
                HashMap<String, Object> studentData = new HashMap<>();
                studentData.put("firstName", firstName);
                studentData.put("lastName", lastName);
                studentData.put("section", selectedSectionName);
                studentData.put("grade", selectedGradeLevel);
                studentData.put("quizno", "N/A");
                studentData.put("timestamp", FieldValue.serverTimestamp());           
                studentData.put("quizscore", "0");    
                    

               // Query to check if a document with the same firstName and lastName already exists in "Accounts/Students/MathSqure"
db.collection("Accounts").document("Students")
        .collection("MathSquare")
        .whereEqualTo("firstName", firstName) // Match firstName
        .whereEqualTo("lastName", lastName)   // Match lastName
        .get() // Execute query
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // A student with the same firstName and lastName already exists
                    animateButtonPushDowm(submitButton);
                                
                                sharedPreferences.StudentIsSetLoggedIn(studentSignUp.this, true);
                                            sharedPreferences.setLoggedIn(studentSignUp.this, false);
sharedPreferences.saveSection(studentSignUp.this, section);
                    sharedPreferences.saveGrade(studentSignUp.this, grade);
                    sharedPreferences.saveFirstN(studentSignUp.this, firstName);
                    sharedPreferences.saveLastN(studentSignUp.this, lastName);
                                    
                                Intent intent = new Intent(this, MainActivity.class);   
                                startActivity(intent);
                                  finish(); 
                                Toast.makeText(this, "Welcome Student!", Toast.LENGTH_SHORT).show();
                                stopButtonFocusAnimation(submitButton);
                                animateButtonFocus(submitButton);
                                          
                                    
                } else {
                    // Student doesn't exist, proceed to save new data with UUID
                    db.collection("Accounts").document("Students").collection("MathSquare")
                            .document(uuid)         // Using UUID as document ID
                            .set(studentData)       // Save the student data
                            .addOnSuccessListener(documentReference -> {
                                // Save student data successfully, now handle Sections collection
                                saveStudentToSection(firstName, lastName, selectedSectionId);  // Save to Sections

                                // Proceed with UI actions (navigate to MainActivity)
                                animateButtonPushDowm(submitButton);
                                
                                sharedPreferences.StudentIsSetLoggedIn(studentSignUp.this, true);
                                            sharedPreferences.setLoggedIn(studentSignUp.this, false);
sharedPreferences.saveSection(studentSignUp.this, section);
                    sharedPreferences.saveGrade(studentSignUp.this, grade);
                    sharedPreferences.saveFirstN(studentSignUp.this, firstName);
                    sharedPreferences.saveLastN(studentSignUp.this, lastName);
                                            
                                Intent intent = new Intent(this, MainActivity.class);           
                                startActivity(intent);
                                 finish();          
                                Toast.makeText(this, "Welcome Student!", Toast.LENGTH_SHORT).show();
                                stopButtonFocusAnimation(submitButton);
                                animateButtonFocus(submitButton);
                                            
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure in saving data
                                Toast.makeText(this, "Failed to save student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
            } else {
                // Handle error in querying the Firestore collection
                Toast.makeText(this, "Error checking student data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        .addOnFailureListener(e -> {
            // Handle general failure in the query
            Toast.makeText(this, "Error checking student data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });

            }
        });
    }
    
    
    private void saveStudentToSection(String firstName, String lastName, String sectionDocId) {
   
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    CollectionReference studentsCollection = db.collection("Sections")
        .document(sectionDocId)
        .collection("Students");

    //Student data map.
    Map<String, Object> studentData = new HashMap<>();
    studentData.put("firstName", firstName);
    studentData.put("lastName", lastName);
    studentData.put("section", selectedSectionName);
    studentData.put("grade", selectedGradeLevel);
    studentData.put("timestamp", FieldValue.serverTimestamp());

    studentsCollection.add(studentData)
        .addOnSuccessListener(documentReference -> {
            Log.d("Firestore", "Student added with ID: " + documentReference.getId());
            Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show();
        })
        .addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to save student: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }


    // Shake and rotate animation for error fields
    private void animateShakeRotateEditTextErrorAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();

        // Shake animation
        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, 20f, -20f, 20f, -20f, 0);
        shakeAnimator.setDuration(350);

        // Rotate animation
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 5f, -5f, 5f, -5f, 0);
        rotateAnimator.setDuration(350);

        // Play both animations together
        animatorSet.playTogether(shakeAnimator, rotateAnimator);
        animatorSet.start();
    }
    
private void animateButtonClick(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.6f, 1.1f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.6f, 1.1f, 1f);

    // Set duration for the animations
    scaleX.setDuration(3000);
    scaleY.setDuration(3000);

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
    scaleX.setDuration(4000);
    scaleY.setDuration(4000);

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

    private void animateButtonPushDowm(View button) {
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f);  // Scale down slightly
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f);  // Scale down slightly

    // Set shorter duration for a quick push effect
    scaleX.setDuration(200);
    scaleY.setDuration(200);

    // Use a smooth interpolator
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    scaleX.setInterpolator(interpolator);
    scaleY.setInterpolator(interpolator);

    // Combine the animations into an AnimatorSet
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(scaleX, scaleY);
    
    // Start the animation
    animatorSet.start();
}

// Stop Focus Animation
private void stopButtonFocusAnimation(View button) {
    AnimatorSet animatorSet = (AnimatorSet) button.getTag();
    if (animatorSet != null) {
        animatorSet.cancel();  // Stop the animation when focus is lost
    }
}
    
}
