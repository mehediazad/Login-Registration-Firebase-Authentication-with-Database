package com.example.loginregisterappapplication.Activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.loginregisterappapplication.Model.ReadWriteUserDetails;
import com.example.loginregisterappapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editText_register_full_name, editText_register_email, editText_register_dob, editText_register_mobile, editText_register_password, editText_register_ConfirmPassword;
    private RadioGroup radio_group_register_gender;
    private RadioButton radio_group_register_gender_seleted;
    private TextView button_register;
    private ImageView imageView_date_picker, imageView_show_hide_pwd, imageView_show_hide_ConfirmPwd;
    private ProgressBar progressBar;
    private static final String Tag = "RegisterActivity";
    private DatePickerDialog picker;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAction_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedgenderId = radio_group_register_gender.getCheckedRadioButtonId();
                radio_group_register_gender_seleted = findViewById(selectedgenderId);

                //obtain the entered data
                String textFullName = editText_register_full_name.getText().toString();
                String textEmail = editText_register_email.getText().toString();
                String textDoB = editText_register_dob.getText().toString();
                String textMobile = editText_register_mobile.getText().toString();
                String textPassword = editText_register_password.getText().toString();
                String textConformPassword = editText_register_ConfirmPassword.getText().toString();
                String textGender; // can't obtain the value before verifying if any button was selected or not

                // Validate Mobile Number using Matcher and patten (Regular Expression)
                String mobileRegex = "(01)[3-9][0-9]{8}"; // First no. can be {01} and rest 11 num, can be any number;
                Matcher mobileMatcher;
                Pattern mobilePatten = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePatten.matcher(textMobile);


                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Full Name", Toast.LENGTH_SHORT).show();
                    editText_register_full_name.setError("Full Name is required");
                    editText_register_full_name.requestFocus();

                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your email", Toast.LENGTH_SHORT).show();
                    editText_register_email.setError("Email is required");
                    editText_register_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter Your email", Toast.LENGTH_SHORT).show();
                    editText_register_email.setError("Vaild email is required");
                    editText_register_email.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Date Of Birth", Toast.LENGTH_SHORT).show();
                    editText_register_dob.setError("Date of Birth is required");
                    editText_register_dob.requestFocus();
                }
                else if ((TextUtils.isEmpty(textMobile))) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
                    editText_register_mobile.setError("Phone Number is required");
                    editText_register_mobile.requestFocus();
                } else if (textMobile.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter Your Phone Number", Toast.LENGTH_SHORT).show();
                    editText_register_mobile.setError("Phone Number should be 11 digits");
                    editText_register_mobile.requestFocus();
                }else if (!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter Your Phone Number", Toast.LENGTH_SHORT).show();
                    editText_register_mobile.setError("Phone Number is not valid");
                    editText_register_mobile.requestFocus();
                }
                else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
                    editText_register_password.setError("Password is required");
                    editText_register_password.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password Should be at lest 6 digits", Toast.LENGTH_SHORT).show();
                    editText_register_password.setError("Password Confermation is required");
                    editText_register_password.requestFocus();
                } else if (TextUtils.isEmpty(textConformPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Conform Your Password", Toast.LENGTH_SHORT).show();
                    editText_register_ConfirmPassword.setError("Password Confermation is required");
                    editText_register_ConfirmPassword.requestFocus();
                } else if (!textPassword.equals(textConformPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Same Password", Toast.LENGTH_SHORT).show();
                    editText_register_ConfirmPassword.setError("Password Confermation is required");
                    editText_register_ConfirmPassword.requestFocus();

                    // Clean the enter password
                    editText_register_password.clearComposingText();
                    editText_register_ConfirmPassword.clearComposingText();
                } else if (radio_group_register_gender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Gender", Toast.LENGTH_SHORT).show();
                    radio_group_register_gender_seleted.setError("Password is required");
                    radio_group_register_gender_seleted.requestFocus();
                } else {
                    textGender = radio_group_register_gender_seleted.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPassword);
                }
            }

            private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPassword) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        //update display name of user
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(userProfileChangeRequest);

                        // Enter User data into the firebase Realtime data
                        ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobile);

                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
                        referenceProfile.child(firebaseUser.getUid()).setValue(readWriteUserDetails).addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                // send Verification mail
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "User Registered Successfully. Please Verify Your email", Toast.LENGTH_SHORT).show();

                               //  Open User Profile after successful registration
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();



                            } else {
                                Toast.makeText(RegisterActivity.this, "User Registered failed, Please Try again", Toast.LENGTH_SHORT).show();

                            }
                            progressBar.setVisibility(View.GONE);
                        });

                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            editText_register_password.setError("Your Password is too weak. Kindly use a max of alphabets,numbers and special characters");
                            editText_register_password.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editText_register_password.setError("Your email is invalid or already in use.Kindly re-enter");
                            editText_register_password.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            editText_register_password.setError("User is already registered with this email. Use anther email.");
                            editText_register_password.requestFocus();
                        } catch (Exception e) {
                            Log.e(Tag, e.getMessage());
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });

        // Edit text date piker
        imageView_date_picker.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            // date picker Dialog
            picker = new DatePickerDialog(RegisterActivity.this, (view, year1, month1, dayOfMonth) -> editText_register_dob.setText(dayOfMonth + "/" + (month1 +1)+"/"+ year1),year,month, day);
            picker.show();

        });
        imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_pwd.setOnClickListener(v -> {
            if (editText_register_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                editText_register_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
            }else {
                editText_register_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageView_show_hide_pwd.setImageResource(R.drawable.eye_24);
            }

        });
        imageView_show_hide_ConfirmPwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_ConfirmPwd.setOnClickListener(v -> {
            if (editText_register_ConfirmPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                editText_register_ConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageView_show_hide_ConfirmPwd.setImageResource(R.drawable.hide_pwd);
            }else {
                editText_register_ConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageView_show_hide_ConfirmPwd.setImageResource(R.drawable.eye_24);
            }
        });

    }

    private void initView() {
        editText_register_full_name = findViewById(R.id.editText_register_full_name);
        editText_register_email = findViewById(R.id.editText_register_email);
        editText_register_dob = findViewById(R.id.editText_register_dob);
        editText_register_mobile = findViewById(R.id.editText_register_mobile);
        editText_register_password = findViewById(R.id.editText_register_password);
        editText_register_ConfirmPassword = findViewById(R.id.editText_register_ConfirmPassword);
        // Radio Botton for Gender
        radio_group_register_gender = findViewById(R.id.radio_group_register_gender);
        radio_group_register_gender.clearCheck();

        CheckBox checkBox_terms_conditions = findViewById(R.id.checkBox_terms_conditions);
        button_register = findViewById(R.id.button_register);
        imageView_date_picker = findViewById(R.id.imageView_date_picker);
        imageView_show_hide_pwd = findViewById(R.id.imageView_show_hide_pwd);
        imageView_show_hide_ConfirmPwd = findViewById(R.id.imageView_show_hide_ConfirmPwd);
        progressBar = findViewById(R.id.progressBar);

    }
}