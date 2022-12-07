package com.example.loginregisterappapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregisterappapplication.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChangeActivity extends AppCompatActivity {
    private EditText editText_change_pwd_current;
    private ImageView imageView_show_hide_curr_pwd;
    private TextView button_change_pwd_authenticate;
    private EditText editText_change_pwd_new;
    private ImageView imageView_show_hide_new_pwd;
    private TextView button_change_pwd;
    private EditText editText_conform_change_pwd_new;
    private TextView textView_change_pwd_authenticated;
    private ImageView imageView_show_hide_new_conform_pwd;
    private ProgressBar progressBar;
    private String userPwdCur;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

/*
        imageView_show_hide_curr_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_curr_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_change_pwd_current.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editText_change_pwd_current.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView_show_hide_curr_pwd.setImageResource(R.drawable.hide_pwd);
                }else {
                    editText_change_pwd_current.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView_show_hide_curr_pwd.setImageResource(R.drawable.eye_24);
                }

            }
        });
        imageView_show_hide_new_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_new_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_change_pwd_new.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editText_change_pwd_new.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView_show_hide_new_pwd.setImageResource(R.drawable.hide_pwd);
                }else {
                    editText_change_pwd_new.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView_show_hide_new_pwd.setImageResource(R.drawable.eye_24);
                }
            }
        });

        imageView_show_hide_new_conform_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_new_conform_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_conform_change_pwd_new.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editText_conform_change_pwd_new.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView_show_hide_new_pwd.setImageResource(R.drawable.hide_pwd);
                }else {
                    editText_conform_change_pwd_new.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView_show_hide_new_conform_pwd.setImageResource(R.drawable.eye_24);
                }
            }
        });
        */

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAction_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //
        initView();

        editText_change_pwd_new.setEnabled(false);
        editText_conform_change_pwd_new.setEnabled(false);
        button_change_pwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(PasswordChangeActivity.this, "Something went wrong! User's details not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PasswordChangeActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

    }


    // reAuthenticateUser user before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        button_change_pwd_authenticate.setOnClickListener(v -> {
            userPwdCur = editText_change_pwd_current.getText().toString();

            if (TextUtils.isEmpty(userPwdCur)){
                Toast.makeText(PasswordChangeActivity.this,"Password is needed.",Toast.LENGTH_SHORT).show();
                editText_change_pwd_current.setError("Please Enter Your Current Password to Authenticate");
                editText_change_pwd_current.requestFocus();
            }else {
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwdCur);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);

                        // Disable editText view the Current Password. Enable editText view the new Password and conform password.
                        editText_change_pwd_current.setEnabled(false);
                        editText_change_pwd_new.setEnabled(true);
                        editText_conform_change_pwd_new.setEnabled(true);

                        // Enable Change pwd btn. disable AUTHENTICATE btn
                        button_change_pwd_authenticate.setEnabled(false);
                        button_change_pwd.setEnabled(true);

                        textView_change_pwd_authenticated.setText("You are Authenticate/Verified."+"You can change password now.");
                        Toast.makeText(PasswordChangeActivity.this,"Password has been Verified"+"Change Password now.",Toast.LENGTH_SHORT).show();

                        // update color of change password btn
                        button_change_pwd.setBackgroundTintList(ContextCompat.getColorStateList(PasswordChangeActivity.this,R.color.green));

                        button_change_pwd.setOnClickListener(v1 -> changePwd(firebaseUser));
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(PasswordChangeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });


        imageView_show_hide_curr_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_curr_pwd.setOnClickListener(v -> {
            if (editText_change_pwd_current.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                editText_change_pwd_current.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageView_show_hide_curr_pwd.setImageResource(R.drawable.hide_pwd);
            }else {
                editText_change_pwd_current.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageView_show_hide_curr_pwd.setImageResource(R.drawable.eye_24);
            }

        });
        imageView_show_hide_new_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_new_pwd.setOnClickListener(v -> {
            if (editText_change_pwd_new.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                editText_change_pwd_new.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageView_show_hide_new_pwd.setImageResource(R.drawable.hide_pwd);
            }else {
                editText_change_pwd_new.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageView_show_hide_new_pwd.setImageResource(R.drawable.eye_24);
            }
        });

        imageView_show_hide_new_conform_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_new_conform_pwd.setOnClickListener(v -> {
            if (editText_conform_change_pwd_new.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                editText_conform_change_pwd_new.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageView_show_hide_new_pwd.setImageResource(R.drawable.hide_pwd);
            }else {
                editText_conform_change_pwd_new.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageView_show_hide_new_conform_pwd.setImageResource(R.drawable.eye_24);
            }
        });



    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPassNew = editText_change_pwd_new.getText().toString();
        String userPassConfirmNew = editText_conform_change_pwd_new.getText().toString();

        if (TextUtils.isEmpty(userPassNew)){
            Toast.makeText(PasswordChangeActivity.this,"New Password Needed",Toast.LENGTH_SHORT).show();
            editText_change_pwd_new.setError("Please enter your new password");
            editText_change_pwd_new.requestFocus();
        }else if (TextUtils.isEmpty(userPassConfirmNew)){
            Toast.makeText(PasswordChangeActivity.this,"Please Confirm Your New Password Needed",Toast.LENGTH_SHORT).show();
            editText_conform_change_pwd_new.setError("Please re-enter your new password");
            editText_conform_change_pwd_new.requestFocus();
        }else if (!userPassNew.matches(userPassConfirmNew)){
            Toast.makeText(PasswordChangeActivity.this,"Password doesn't match",Toast.LENGTH_SHORT).show();
            editText_conform_change_pwd_new.setError("Please re-enter same password");
            editText_conform_change_pwd_new.requestFocus();
        }else if (userPwdCur.matches(userPassNew)){
            Toast.makeText(PasswordChangeActivity.this,"New Password same the old password",Toast.LENGTH_SHORT).show();
            editText_change_pwd_new.setError("Please enter your new password");
            editText_change_pwd_new.requestFocus();

        }else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPassNew).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(PasswordChangeActivity.this,"Password has been Change",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PasswordChangeActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(PasswordChangeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });


        }


    }

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
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(PasswordChangeActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(PasswordChangeActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_settings) {
            Intent intent = new Intent(PasswordChangeActivity.this, UpdateSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(PasswordChangeActivity.this, PasswordChangeActivity.class);
            startActivity(intent);
        }
         else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(PasswordChangeActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_Logout) {
            authProfile.signOut();
            Toast.makeText(PasswordChangeActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PasswordChangeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(PasswordChangeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);


    }


    private void initView() {
        editText_change_pwd_current = findViewById(R.id.editText_change_pwd_current);
        imageView_show_hide_curr_pwd = findViewById(R.id.imageView_show_hide_curr_pwd);
        button_change_pwd_authenticate = findViewById(R.id.button_change_pwd_authenticate);
        editText_change_pwd_new = findViewById(R.id.editText_change_pwd_new);
        imageView_show_hide_new_pwd = findViewById(R.id.imageView_show_hide_new_pwd);
        button_change_pwd = findViewById(R.id.button_change_pwd);
        editText_conform_change_pwd_new = findViewById(R.id.editText_conform_change_pwd_new);
        imageView_show_hide_new_conform_pwd = findViewById(R.id.imageView_show_hide_new_conform_pwd);
        textView_change_pwd_authenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
    }
}