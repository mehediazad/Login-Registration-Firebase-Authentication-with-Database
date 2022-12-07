package com.example.loginregisterappapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregisterappapplication.Model.ReadWriteUserDetails;
import com.example.loginregisterappapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UpdateProfileActivity extends AppCompatActivity {
    private EditText editText_update_profile_name;
    private EditText editText_update_profile_dob;
    private ImageView imageView_date_picker;
    private RadioGroup radio_group_update_profile_gender;
    private RadioButton radioButtonUpdateGenderSelected;
    private EditText editText_update_profile_mobile;
    private TextView button_update_profile;
    private ProgressBar progressBar;
    private TextView textView_profile_update_email;
    private TextView textView_profile_upload_pic;
    private FirebaseAuth authProfile;

    private String txtFullName, txtDoB, txtGender, txtMobile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAction_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //

        initView();
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        showProfile(firebaseUser);

        // Upload profile pic
        textView_profile_upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Upload Email
        textView_profile_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Edit text date piker
        imageView_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For showing previous date
                String txtPvsDoB[] = txtDoB.split("/");

                int day = Integer.parseInt(txtPvsDoB[0]);
                int month = Integer.parseInt(txtPvsDoB[1]) - 1;
                int year = Integer.parseInt(txtPvsDoB[2]);

                DatePickerDialog picker;

                // date picker Dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editText_update_profile_dob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

                    }
                }, year, month, day);
                picker.show();
            }

        });

        // update profile
        button_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }
    // update profile
    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radio_group_update_profile_gender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        // Validate Mobile Number using Matcher and patten (Regular Expression)
        String mobileRegex = "(01)[3-9][0-9]{8}"; // First no. can be {01} and rest 11 num, can be any number;
        Matcher mobileMatcher;
        Pattern mobilePatten = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePatten.matcher(txtMobile);


        if (TextUtils.isEmpty(txtFullName)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Full Name", Toast.LENGTH_SHORT).show();
            editText_update_profile_name.setError("Full Name is required");
            editText_update_profile_name.requestFocus();

        }  else if (TextUtils.isEmpty(txtDoB)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Date Of Birth", Toast.LENGTH_SHORT).show();
            editText_update_profile_dob.setError("Date of Birth is required");
            editText_update_profile_dob.requestFocus();
        }
        else if ((TextUtils.isEmpty(txtMobile))) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
            editText_update_profile_mobile.setError("Phone Number is required");
            editText_update_profile_mobile.requestFocus();
        } else if (txtMobile.length() != 11) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter Your Phone Number", Toast.LENGTH_SHORT).show();
            editText_update_profile_mobile.setError("Phone Number should be 11 digits");
            editText_update_profile_mobile.requestFocus();
        }else if (!mobileMatcher.find()){
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter Your Phone Number", Toast.LENGTH_SHORT).show();
            editText_update_profile_mobile.setError("Phone Number is not valid");
            editText_update_profile_mobile.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Gender", Toast.LENGTH_SHORT).show();
            radioButtonUpdateGenderSelected.setError("Password is required");
            radioButtonUpdateGenderSelected.requestFocus();
        } else {
            txtFullName = editText_update_profile_name.getText().toString();
            txtDoB = editText_update_profile_dob.getText().toString();
            txtGender = radioButtonUpdateGenderSelected.getText().toString();
            txtMobile = editText_update_profile_mobile.getText().toString();


            // Enter use data into the firebase RealTime database
            ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(txtDoB,txtGender,txtMobile);

            //Extract User reference from database for "Registered User"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");

            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        // Setting new display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(txtFullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this,"Update Successful!",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }

    // fetch data from Firebase and display;
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null) {
                    txtFullName = firebaseUser.getDisplayName();
                    txtDoB = readWriteUserDetails.doB;
                    txtGender = readWriteUserDetails.gender;
                    txtMobile = readWriteUserDetails.mobile;

                    editText_update_profile_name.setText(txtFullName);
                    editText_update_profile_dob.setText(txtDoB);
                    editText_update_profile_mobile.setText(txtMobile);

                    // show gender though radio button
                    if (txtGender.equals("Male")) {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });



    }
    // Create Action Bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }
        else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_update_settings) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfileActivity.this, PasswordChangeActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_Logout) {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        editText_update_profile_name = findViewById(R.id.editText_update_profile_name);
        editText_update_profile_dob = findViewById(R.id.editText_update_profile_dob);
        imageView_date_picker = findViewById(R.id.imageView_date_picker);
        radio_group_update_profile_gender = findViewById(R.id.radio_group_update_profile_gender);
        editText_update_profile_mobile = findViewById(R.id.editText_update_profile_mobile);
        button_update_profile = findViewById(R.id.button_update_profile);
        progressBar = findViewById(R.id.progressBar);
        textView_profile_update_email = findViewById(R.id.textView_profile_update_email);
        textView_profile_upload_pic = findViewById(R.id.textView_profile_upload_pic);
    }
}