package com.happym.mathsquare;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.happym.mathsquare.Animation.VignetteEffect;
import com.happym.mathsquare.Service.FirebaseDb;

import org.json.JSONException;
import org.json.JSONObject;

public class UserFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;
    private TextView tvTotalStudents, tvTotalTeachers, tvTotalSections, tvTotalAdmins;
    private LinearLayout btnStudentLeaderboards, btnAddAdmin, btnAddTeacher, btnRemoveTeacher;
    private ImageView btnLogout;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        db = FirebaseDb.getFirestore();

        tvTotalStudents = view.findViewById(R.id.tv_total_students);
        tvTotalTeachers = view.findViewById(R.id.tv_total_teachers);
        tvTotalSections = view.findViewById(R.id.tv_total_sections);
        tvTotalAdmins = view.findViewById(R.id.tv_total_admins);

        btnStudentLeaderboards = view.findViewById(R.id.btn_student_leaderboards);
        btnAddTeacher = view.findViewById(R.id.btn_add_teacher);
        btnRemoveTeacher = view.findViewById(R.id.btn_remove_teacher);
        btnAddAdmin = view.findViewById(R.id.btn_add_admin);
        btnLogout = view.findViewById(R.id.btn_logout);

        fetchDashboardTotalsRealtime();
        setupClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start or Restart animations every time the fragment becomes visible
        animateButtonFocus(btnStudentLeaderboards);
        animateButtonFocus(btnAddTeacher);
        animateButtonFocus(btnRemoveTeacher);
        animateButtonFocus(btnAddAdmin);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop animations when leaving the screen to prevent memory leaks and UI glitches
        stopButtonFocusAnimation(btnStudentLeaderboards);
        stopButtonFocusAnimation(btnAddTeacher);
        stopButtonFocusAnimation(btnRemoveTeacher);
        stopButtonFocusAnimation(btnAddAdmin);
    }

    private void fetchDashboardTotalsRealtime() {
        db.collection("Accounts")
                .document("Students")
                .collection("MathSquare")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("UserFragment", "Error fetching students", error);
                        return;
                    }
                    if (value != null && isAdded()) {
                        tvTotalStudents.setText(String.valueOf(value.size()));
                    }
                });

        db.collectionGroup("MyProfile")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("UserFragment", "Error fetching teachers", error);
                        return;
                    }
                    if (value != null && isAdded()) {
                        tvTotalTeachers.setText(String.valueOf(value.size()));
                    }
                });

        db.collection("Sections")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("UserFragment", "Error fetching sections", error);
                        return;
                    }
                    if (value != null && isAdded()) {
                        tvTotalSections.setText(String.valueOf(value.size()));
                    }
                });

        db.collection("Admin")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("UserFragment", "Error fetching admins", error);
                        return;
                    }
                    if (value != null && isAdded()) {
                        tvTotalAdmins.setText(String.valueOf(value.size()));
                    }
                });
    }

    private void setupClickListeners() {
        btnAddAdmin.setOnClickListener(v -> {
            stopButtonFocusAnimation(btnAddAdmin); // Pause idle animation before clicking
            animateButtonClick(btnAddAdmin);

            // Added a slight delay so the user actually sees the click animation
            // before the activity jumps to the next screen.
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(getContext(), AddAdminActivity.class);
                startActivity(intent);
            }, 300);
        });

        btnAddTeacher.setOnClickListener(v -> {
            stopButtonFocusAnimation(btnAddTeacher);
            animateButtonClick(btnAddTeacher);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(getContext(), teacherSignUp.class);
                startActivity(intent);
            }, 300);
        });

        btnRemoveTeacher.setOnClickListener(v -> {
            animateButtonClick(btnRemoveTeacher);
            showRemoveTeacherDialog();
        });

        btnStudentLeaderboards.setOnClickListener(v -> {
            stopButtonFocusAnimation(btnStudentLeaderboards);
            animateButtonClick(btnStudentLeaderboards);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(getContext(), LeaderboardsActivity.class);
                startActivity(intent);
            }, 300);
        });

        btnLogout.setOnClickListener(v -> {
            animateButtonClick(btnLogout);
            showLogoutDialog();
        });
    }

    // --- REMOVE TEACHER DIALOG ---
    private void showRemoveTeacherDialog() {
        Dialog removeDialog = new Dialog(requireContext());
        removeDialog.setContentView(R.layout.layout_dialog_remove_teacher);

        LinearLayout dialogContainer = removeDialog.findViewById(R.id.dialog_container);
        EditText etEmail = removeDialog.findViewById(R.id.et_teacher_email);
        LinearLayout btnCancel = removeDialog.findViewById(R.id.btn_dialog_cancel);
        FrameLayout btnRemove = removeDialog.findViewById(R.id.btn_dialog_remove);
        TextView tvRemoveText = removeDialog.findViewById(R.id.tv_remove_text);
        ProgressBar pbRemoveLoading = removeDialog.findViewById(R.id.pb_remove_loading);

        if (dialogContainer != null) {
            VignetteEffect.apply(requireContext(), dialogContainer, 24f);
        }

        if (removeDialog.getWindow() != null) {
            removeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            removeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            removeDialog.getWindow().setGravity(Gravity.CENTER);
            removeDialog.getWindow().setDimAmount(0.7f);
        }

        removeDialog.setCancelable(false);

        btnCancel.setOnClickListener(v -> {
            animateButtonClick(btnCancel);
            removeDialog.dismiss();
        });
        btnRemove.setOnClickListener(v -> {
            animateButtonClick(btnRemove);
            String targetEmail = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(targetEmail)) {
                etEmail.setError("Please enter an email");
                return;
            }

            // Start UI Loading State
            tvRemoveText.setVisibility(View.GONE);
            pbRemoveLoading.setVisibility(View.VISIBLE);
            btnCancel.setEnabled(false);
            btnRemove.setEnabled(false);

            com.google.android.gms.tasks.Task<com.google.firebase.firestore.QuerySnapshot> adminCheck =
                    db.collection("Admin").whereEqualTo("email", targetEmail).get();

            com.google.android.gms.tasks.Task<com.google.firebase.firestore.DocumentSnapshot> teacherCheck =
                    db.collection("Accounts").document("Teachers").collection(targetEmail).document("MyProfile").get();

            com.google.android.gms.tasks.Tasks.whenAllComplete(adminCheck, teacherCheck)
                    .addOnCompleteListener(allTask -> {
                        boolean isAdminFound = adminCheck.isSuccessful() && !adminCheck.getResult().isEmpty();
                        boolean isTeacherFound = teacherCheck.isSuccessful() && teacherCheck.getResult().exists();
                        if (!isAdminFound && !isTeacherFound) {
                            tvRemoveText.setVisibility(View.VISIBLE);
                            pbRemoveLoading.setVisibility(View.GONE);
                            btnCancel.setEnabled(true);
                            btnRemove.setEnabled(true);

                            etEmail.setError("This email does not belong to an Admin or Teacher");
                            Toast.makeText(requireContext(), "Account not found.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isAdminFound) {
                            // Delete Admin document
                            adminCheck.getResult().getDocuments().get(0).getReference().delete();
                        }

                        if (isTeacherFound) {
                            // Delete Teacher profile
                            teacherCheck.getResult().getReference().delete();
                        }

                        callDeleteAuthFunction(targetEmail, removeDialog);
                    });
        });
        removeDialog.show();
    }

    private void callDeleteAuthFunction(String email, Dialog dialog) {
        String functionUrl = "https://deleteauthuserprofiledeletemathsquare-zb5tghmu5q-uc.a.run.app";

        org.json.JSONObject jsonBody = new org.json.JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonBody.toString());

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(functionUrl)
                .post(body)
                .build();

        new okhttp3.OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Firestore wiped, but Auth cleanup failed to trigger.", Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Account and Auth fully removed.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Auth cleanup returned an error.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // --- LOGOUT DIALOG ---
    private void showLogoutDialog() {
        Dialog logoutDialog = new Dialog(requireContext());
        logoutDialog.setContentView(R.layout.layout_dialog_logout);

        LinearLayout dialogContainer = logoutDialog.findViewById(R.id.dialog_container);
        LinearLayout btnNo = logoutDialog.findViewById(R.id.btn_dialog_no);
        FrameLayout btnYes = logoutDialog.findViewById(R.id.btn_dialog_yes);
        TextView tvYesText = logoutDialog.findViewById(R.id.tv_yes_text);
        ProgressBar pbYesLoading = logoutDialog.findViewById(R.id.pb_yes_loading);

        if (dialogContainer != null) {
            VignetteEffect.apply(requireContext(), dialogContainer, 24f);
        }

        if (logoutDialog.getWindow() != null) {
            logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            logoutDialog.getWindow().setGravity(Gravity.CENTER);
            logoutDialog.getWindow().setDimAmount(0.7f);
        }

        logoutDialog.setCancelable(false);

        btnNo.setOnClickListener(v -> {
            animateButtonClick(btnNo);
            logoutDialog.dismiss();
        });

        btnYes.setOnClickListener(v -> {
            animateButtonClick(btnYes);

            tvYesText.setVisibility(View.GONE);
            pbYesLoading.setVisibility(View.VISIBLE);

            btnNo.setEnabled(false);
            btnYes.setEnabled(false);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                logoutDialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                sharedPreferences.setAdminLoggedIn(requireContext(), false);
                Intent intent = new Intent(requireContext(), signInUp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                requireActivity().finish();

                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
            }, 1200);
        });

        logoutDialog.show();
    }

    // --- GAME BUTTON ANIMATIONS ---
    private void animateButtonClick(View button) {
        if (button == null) return;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.6f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.6f, 1.1f, 1f);

        scaleX.setDuration(1000);
        scaleY.setDuration(1000);

        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(2f);
        scaleX.setInterpolator(overshootInterpolator);
        scaleY.setInterpolator(overshootInterpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }

    private void animateButtonFocus(View button) {
        if (button == null) return;
        stopButtonFocusAnimation(button); // Critical: Prevent duplicate animators stacking

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.06f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.06f, 1f);

        scaleX.setDuration(2000);
        scaleY.setDuration(2000);

        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        scaleX.setInterpolator(interpolator);
        scaleY.setInterpolator(interpolator);

        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);

        button.setTag(animatorSet);
        animatorSet.start();
    }

    private void stopButtonFocusAnimation(View button) {
        if (button == null) return;

        AnimatorSet animatorSet = (AnimatorSet) button.getTag();
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.end();
            animatorSet.cancel();
            button.setScaleX(1f);
            button.setScaleY(1f);
            button.setTag(null);
        }
    }
}