package com.happym.mathsquare;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import android.widget.FrameLayout;
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
import java.util.Objects;
import java.util.UUID;

import com.happym.mathsquare.Service.FirebaseDb;
import com.happym.mathsquare.Utils.PasswordUtils;
import com.happym.mathsquare.sharedPreferences;

import androidx.core.view.WindowCompat;

public class studentSignUp extends AppCompatActivity {

    private String selectedSectionId;
    private String selectedTeacherEmail = "";
    private String selectedTeacherUid = "";
    private String selectedSectionName;
    private String selectedGradeLevel;

    private FirebaseFirestore db;

    private FrameLayout loadingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.layoutstudent_sign_up);

        // Firestore instance
        db = FirebaseDb.getFirestore();

        AutoCompleteTextView sectionChooser = findViewById(R.id.SectionChooser);
        TextInputLayout firstNameLayout = findViewById(R.id.first_name_layout);
        TextInputLayout lastNameLayout = findViewById(R.id.last_name_layout);
        TextInputLayout teacherNameLayout = findViewById(R.id.teacher_name_layout);
        AppCompatButton submitButton = findViewById(R.id.btn_submit);
        AutoCompleteTextView numberDropdownPicker = findViewById(R.id.numberDropdownPicker);
        TextView spinnerError = findViewById(R.id.spinnerError);
        TextInputLayout pinLayout = findViewById(R.id.pin_layout);
        TextInputEditText pinEditText = (TextInputEditText) pinLayout.getEditText();
        loadingContainer = findViewById(R.id.loading_container);

        TextInputEditText firstNameEditText = (TextInputEditText) firstNameLayout.getEditText();
        Map<Integer, List<QueryDocumentSnapshot>> preFetchedSectionsByGrade = new HashMap<>();
        Map<String, QueryDocumentSnapshot> currentGradeSectionDocMap = new HashMap<>();

        // Fetch all sections once on startup
        db.collection("Sections").get().addOnSuccessListener(querySnapshot -> {
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Long gradeNumberLong = doc.getLong("Grade_Number");
                if (gradeNumberLong != null) {
                    int gradeNumber = gradeNumberLong.intValue();

                    // Group sections by their grade number
                    if (!preFetchedSectionsByGrade.containsKey(gradeNumber)) {
                        preFetchedSectionsByGrade.put(gradeNumber, new ArrayList<>());
                    }
                    preFetchedSectionsByGrade.get(gradeNumber).add(doc);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load sections in background: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

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
        TextInputEditText teacherNameEditText = (TextInputEditText) teacherNameLayout.getEditText();

        if (lastNameEditText != null) {
            lastNameEditText.setFilters(new InputFilter[]{noSpacesFilter});
        }

        List<String> grades = Arrays.asList("1", "2", "3", "4", "5", "6");

        ArrayAdapter<String> adapterGrades = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grades) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                int padding = dpToPx(16);
                view.setPadding(padding, padding, padding, padding);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                int padding = dpToPx(16);
                view.setPadding(padding, padding, padding, padding);
                return view;
            }

            private int dpToPx(int dp) {
                return Math.round(dp * getContext().getResources().getDisplayMetrics().density);
            }
        };

        adapterGrades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberDropdownPicker.setAdapter(adapterGrades);

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if (firstNameEditText != null) firstNameEditText.setTextColor(Color.WHITE);
            if (lastNameEditText != null) lastNameEditText.setTextColor(Color.WHITE);
            if (teacherNameEditText != null) teacherNameEditText.setTextColor(Color.WHITE);
            if (pinEditText != null) pinEditText.setTextColor(Color.WHITE);

            numberDropdownPicker.setTextColor(Color.WHITE);
            sectionChooser.setTextColor(Color.WHITE);

            pinLayout.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
            firstNameLayout.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
            lastNameLayout.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
            teacherNameLayout.setHintTextColor(ColorStateList.valueOf(Color.WHITE));
        } else {
            if (firstNameEditText != null) firstNameEditText.setTextColor(Color.BLACK);
            if (lastNameEditText != null) lastNameEditText.setTextColor(Color.BLACK);
            if (teacherNameEditText != null) teacherNameEditText.setTextColor(Color.BLACK);
            if (pinEditText != null) pinEditText.setTextColor(Color.BLACK);

            numberDropdownPicker.setTextColor(Color.BLACK);
            sectionChooser.setTextColor(Color.BLACK);

            pinLayout.setHintTextColor(ColorStateList.valueOf(Color.BLACK));
            firstNameLayout.setHintTextColor(ColorStateList.valueOf(Color.BLACK));
            lastNameLayout.setHintTextColor(ColorStateList.valueOf(Color.BLACK));
            teacherNameLayout.setHintTextColor(ColorStateList.valueOf(Color.BLACK));
        }

        ColorStateList whiteStroke = ColorStateList.valueOf(Color.WHITE);
        ColorStateList blackStroke = ColorStateList.valueOf(Color.BLACK);

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            firstNameLayout.setBoxStrokeColorStateList(whiteStroke);
            lastNameLayout.setBoxStrokeColorStateList(whiteStroke);
            teacherNameLayout.setBoxStrokeColorStateList(whiteStroke);
        } else {
            firstNameLayout.setBoxStrokeColorStateList(blackStroke);
            lastNameLayout.setBoxStrokeColorStateList(blackStroke);
            teacherNameLayout.setBoxStrokeColorStateList(blackStroke);
        }

        numberDropdownPicker.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedGrade = adapterGrades.getItem(position);

            if (selectedGrade != null && selectedGrade.matches("[1-6]")) {
                int gradeNumber = Integer.parseInt(selectedGrade);
                sectionChooser.setText("");
                currentGradeSectionDocMap.clear();
                List<String> sectionNames = new ArrayList<>();
                List<QueryDocumentSnapshot> sectionsForGrade = preFetchedSectionsByGrade.get(gradeNumber);

                if (sectionsForGrade != null && !sectionsForGrade.isEmpty()) {
                    for (QueryDocumentSnapshot doc : sectionsForGrade) {
                        String section = doc.getString("Section");
                        if (section != null) {
                            sectionNames.add(section);
                            currentGradeSectionDocMap.put(section, doc);
                        }
                    }

                    ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(studentSignUp.this, android.R.layout.simple_dropdown_item_1line, sectionNames);
                    sectionChooser.setAdapter(sectionAdapter);

                    sectionChooser.setOnItemClickListener((adapterView1, view1, i, l) -> {
                        String selectedSection = sectionAdapter.getItem(i);
                        QueryDocumentSnapshot doc = currentGradeSectionDocMap.get(selectedSection);

                        if (doc != null) {
                            selectedSectionId = doc.getId();
                            selectedSectionName = selectedSection;
                            selectedGradeLevel = selectedGrade;
                            selectedTeacherEmail = doc.getString("teacherEmail");
                            selectedTeacherUid = doc.getString("teacherUid");
                        }
                    });
                } else {
                    sectionChooser.setAdapter(null);
                    Toast.makeText(this, "No sections found for Grade " + gradeNumber, Toast.LENGTH_SHORT).show();
                }
            }
        });
        sectionChooser.setOnClickListener(v -> sectionChooser.showDropDown());

        firstNameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { firstNameLayout.setError(null); }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        assert lastNameLayout.getEditText() != null;
        lastNameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { lastNameLayout.setError(null); }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        assert teacherNameLayout.getEditText() != null;
        teacherNameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { teacherNameLayout.setError(null); }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        assert pinLayout.getEditText() != null;
        pinLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { pinLayout.setError(null); }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        animateButtonFocus(submitButton);

        submitButton.setOnClickListener(v -> {
            boolean hasError = false;
            firstNameLayout.setError(null);
            lastNameLayout.setError(null);
            teacherNameLayout.setError(null);
            sectionChooser.setError(null);
            pinLayout.setError(null);
            spinnerError.setVisibility(View.GONE);
            String firstName = Objects.requireNonNull(((TextInputEditText) firstNameLayout.getEditText()).getText()).toString().trim();
            if (TextUtils.isEmpty(firstName)) {
                firstNameLayout.setError("Student First Name is required");
                animateShakeRotateEditTextErrorAnimation(firstNameLayout);
                hasError = true;
            }

            String lastName = Objects.requireNonNull(((TextInputEditText) lastNameLayout.getEditText()).getText()).toString().trim();
            if (TextUtils.isEmpty(lastName)) {
                lastNameLayout.setError("Student Last Name is required");
                animateShakeRotateEditTextErrorAnimation(lastNameLayout);
                hasError = true;
            }

            String enteredTeacherName = Objects.requireNonNull(((TextInputEditText) teacherNameLayout.getEditText()).getText()).toString().trim();
            if (TextUtils.isEmpty(enteredTeacherName)) {
                teacherNameLayout.setError("Teacher Name is required");
                animateShakeRotateEditTextErrorAnimation(teacherNameLayout);
                hasError = true;
            }

            String section = sectionChooser.getText().toString().trim();
            if (TextUtils.isEmpty(section)) {
                sectionChooser.setError("Student Section is required");
                animateShakeRotateEditTextErrorAnimation(sectionChooser);
                hasError = true;
            }

            String grade = numberDropdownPicker.getText().toString().trim();
            if (TextUtils.isEmpty(grade) || !grade.matches("[1-6]")) {
                spinnerError.setText("Please select your grade");
                spinnerError.setVisibility(View.VISIBLE);
                hasError = true;
            }

            String secretPin = Objects.requireNonNull(pinEditText.getText()).toString().trim();
            if (TextUtils.isEmpty(secretPin) || secretPin.length() != 4) {
                pinLayout.setError("A 4-digit PIN is required");
                animateShakeRotateEditTextErrorAnimation(pinLayout);
                hasError = true;
            }

            // If no errors, proceed
            if (!hasError) {
                showLoading(true, submitButton);

                db.collection("Accounts").document("Teachers")
                        .collection(selectedTeacherEmail)
                        .document("MyProfile")
                        .get()
                        .addOnSuccessListener(teacherDoc -> {
                            if (teacherDoc.exists()) {
                                String officialFirstName = teacherDoc.getString("firstName");
                                String correctedTeacherName = (officialFirstName != null) ? officialFirstName : enteredTeacherName;

                                String studentDocId = (firstName.toLowerCase() + "_" +
                                        lastName.toLowerCase() + "_" +
                                        selectedGradeLevel + "_" +
                                        selectedSectionName.toLowerCase()).replaceAll("\\s+", "");
                                String hashedPin = PasswordUtils.hashPassword(secretPin);

                                HashMap<String, Object> studentData = new HashMap<>();
                                studentData.put("firstName", firstName);
                                studentData.put("lastName", lastName);
                                studentData.put("teacherName", correctedTeacherName);
                                studentData.put("teacherEmail", selectedTeacherEmail);
                                studentData.put("teacherUid", selectedTeacherUid);
                                studentData.put("section", selectedSectionName);
                                studentData.put("grade", selectedGradeLevel);
                                studentData.put("secretPin", hashedPin);
                                studentData.put("quizno", "N/A");
                                studentData.put("timestamp", FieldValue.serverTimestamp());
                                studentData.put("quizscore", "0");
                                String difficultyString = mapGradeToDifficulty(grade);

                                db.collection("Accounts").document("Students")
                                        .collection("MathSquare")
                                        .document(studentDocId)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();

                                                if (document.exists()) {
                                                    // --- LOGIN LOGIC ---
                                                    String storedPin = document.getString("secretPin");
                                                    if (storedPin != null && storedPin.equals(hashedPin)) {
                                                        sharedPreferences.StudentIsSetLoggedIn(studentSignUp.this, true);
                                                        sharedPreferences.notifyGuestAccountStatus(studentSignUp.this, false);
                                                        sharedPreferences.setLoggedIn(studentSignUp.this, false);
                                                        sharedPreferences.saveSection(studentSignUp.this, section);
                                                        sharedPreferences.saveGrade(studentSignUp.this, grade);
                                                        sharedPreferences.saveGradeDifficulty(studentSignUp.this, difficultyString);
                                                        sharedPreferences.saveFirstN(studentSignUp.this, firstName);
                                                        sharedPreferences.saveLastN(studentSignUp.this, lastName);
                                                        sharedPreferences.saveTeacherN(studentSignUp.this, correctedTeacherName);

                                                        navigateToMain("Welcome back Student!");
                                                    } else {
                                                        showLoading(false, submitButton);
                                                        pinLayout.setError("Incorrect PIN for this account");
                                                        animateShakeRotateEditTextErrorAnimation(pinLayout);
                                                        stopButtonFocusAnimation(submitButton);
                                                    }
                                                } else {
                                                    // --- REGISTRATION LOGIC ---
                                                    db.collection("Accounts").document("Students").collection("MathSquare")
                                                            .document(studentDocId)
                                                            .set(studentData)
                                                            .addOnSuccessListener(aVoid -> {
                                                                saveStudentToSection(firstName, lastName, correctedTeacherName, selectedSectionId);

                                                                sharedPreferences.StudentIsSetLoggedIn(studentSignUp.this, true);
                                                                sharedPreferences.setLoggedIn(studentSignUp.this, false);
                                                                sharedPreferences.saveSection(studentSignUp.this, section);
                                                                sharedPreferences.saveGrade(studentSignUp.this, grade);
                                                                sharedPreferences.saveGradeDifficulty(studentSignUp.this, difficultyString);
                                                                sharedPreferences.saveFirstN(studentSignUp.this, firstName);
                                                                sharedPreferences.saveLastN(studentSignUp.this, lastName);
                                                                sharedPreferences.saveTeacherN(studentSignUp.this, correctedTeacherName);

                                                                navigateToMain("Welcome Student!");
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                showLoading(false, submitButton);
                                                                Toast.makeText(studentSignUp.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            });
                                                }
                                            } else {
                                                showLoading(false, submitButton);
                                                Toast.makeText(studentSignUp.this, "Error checking data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                showLoading(false, submitButton);
                                teacherNameLayout.setError("Teacher not found for this section");
                                animateShakeRotateEditTextErrorAnimation(teacherNameLayout);
                            }
                        })
                        .addOnFailureListener(e -> {
                            showLoading(false, submitButton);
                            Toast.makeText(studentSignUp.this, "Teacher verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    private String mapGradeToDifficulty(String gradeNumber) {
        switch (gradeNumber) {
            case "1": return "grade_one";
            case "2": return "grade_two";
            case "3": return "grade_three";
            case "4": return "grade_four";
            case "5": return "grade_five";
            case "6": return "grade_six";
            default: return "grade_one"; // Safe fallback
        }
    }

    private void navigateToMain(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    private void showLoading(boolean isLoading, AppCompatButton button) {
        if (isLoading) {
            button.setEnabled(false);
            button.setAlpha(0.5f);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            if (button.getBackground() != null) {
                button.getBackground().setColorFilter(filter);
            }
            button.setTextColor(Color.LTGRAY);
            loadingContainer.setVisibility(View.VISIBLE);

        } else {
            button.setEnabled(true);
            button.setAlpha(1.0f);
            if (button.getBackground() != null) {
                button.getBackground().clearColorFilter();
            }
            button.setTextColor(Color.WHITE);
            loadingContainer.setVisibility(View.GONE);
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void saveStudentToSection(String firstName, String lastName, String teacherName, String sectionDocId) {

        CollectionReference studentsCollection = db.collection("Sections")
                .document(sectionDocId)
                .collection("Students");

        //Student data map.
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("firstName", firstName);
        studentData.put("lastName", lastName);
        studentData.put("section", selectedSectionName);
        studentData.put("grade", selectedGradeLevel);
        studentData.put("teacherName", teacherName);
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
