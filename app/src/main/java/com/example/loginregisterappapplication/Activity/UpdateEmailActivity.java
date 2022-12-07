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
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregisterappapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.Authenticator;

public class UpdateEmailActivity extends AppCompatActivity {
    private TextView textView_update_email_old;
    private EditText editText_update_email_verify_password;
    private TextView button_authenticate_user;
    private EditText editText_update_email_new;
    private TextView button_update_email;
    private TextView textView_update_email_authenticated;
    private ImageView imageView_show_hide_pwd;
    private ProgressBar progressBar;
    private String userOldEmail, userNewEmail, userPwd;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

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

        InitView();

        editText_update_email_new.setEnabled(false);
        button_update_email.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        // set old email id on TextView
        userOldEmail = firebaseUser.getEmail();
        textView_update_email_old.setText(userOldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(UpdateEmailActivity.this, "Something Went Wrong! User's details not available", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticate(firebaseUser);
        }

    }

    // Verify User before updating email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        TextView button_authenticate_user = findViewById(R.id.button_authenticate_user);
        button_authenticate_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtain password for Authentication
                userPwd = editText_update_email_verify_password.getText().toString();

                if (TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(UpdateEmailActivity.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                    editText_update_email_verify_password.setError("Please Enter Your Password for authentication");
                    editText_update_email_verify_password.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this, "Password has been verified" + "You can Update Email now", Toast.LENGTH_SHORT).show();

                                // set TextView to show that authentication
                                textView_update_email_authenticated.setText("You are Authenticated. You can update your email now");

                                // Disable Edit for Password, button to verify user and enable editText for new email and update email button.
                                editText_update_email_new.setEnabled(true);
                                editText_update_email_verify_password.setEnabled(false);
                                button_authenticate_user.setEnabled(false);
                                button_update_email.setEnabled(true);

                                // Change color of Update Email button
                                button_update_email.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this, R.color.green));

                                button_update_email.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = editText_update_email_new.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New Email is requied", Toast.LENGTH_SHORT).show();
                                            editText_update_email_new.setError("Please Enter the new Email");
                                            editText_update_email_new.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please Enter vaild email", Toast.LENGTH_SHORT).show();
                                            editText_update_email_new.setError("Please Provide valid email");
                                            editText_update_email_new.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New Email can be same old email", Toast.LENGTH_SHORT).show();
                                            editText_update_email_new.setError("Please enter the new  email");
                                            editText_update_email_new.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        // Show hide password using eye Icon
        imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_update_email_verify_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editText_update_email_verify_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
                } else {
                    editText_update_email_verify_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView_show_hide_pwd.setImageResource(R.drawable.eye_24);
                }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isComplete()) {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "Email has been Updated.Please verify your new email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

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
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_settings) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateEmailActivity.this, PasswordChangeActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menu_Logout) {
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateEmailActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void InitView() {
        textView_update_email_old = findViewById(R.id.textView_update_email_old);
        editText_update_email_verify_password = findViewById(R.id.editText_update_email_verify_password);
        button_authenticate_user = findViewById(R.id.button_authenticate_user);
        editText_update_email_new = findViewById(R.id.editText_update_email_new);
        button_update_email = findViewById(R.id.button_update_email);
        textView_update_email_authenticated = findViewById(R.id.textView_update_email_authenticated);
        progressBar = findViewById(R.id.progressBar);
        imageView_show_hide_pwd = findViewById(R.id.imageView_show_hide_pwd);
    }

}