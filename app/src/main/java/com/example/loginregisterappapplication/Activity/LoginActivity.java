package com.example.loginregisterappapplication.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregisterappapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editText_login_email, editText_login_pwd;
    private ImageView imageView_show_hide_pwd;
    private TextView button_login;
    private TextView textView_register_link, textView_forgot_password_link;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String Tag = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAction_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //
        authProfile = FirebaseAuth.getInstance();

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textloginemail = editText_login_email.getText().toString();
                String textloginpwd = editText_login_pwd.getText().toString();

                if (TextUtils.isEmpty(textloginemail)) {
                    Toast.makeText(LoginActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
                    editText_login_email.setError("Emaile is requied");
                    editText_login_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textloginemail).matches()) {
                    Toast.makeText(LoginActivity.this, " Please re-enter Your Email", Toast.LENGTH_SHORT).show();
                    editText_login_email.setError("Valid Email is requied");
                    editText_login_email.requestFocus();
                } else if (TextUtils.isEmpty(textloginpwd)) {
                    Toast.makeText(LoginActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
                    editText_login_pwd.setText("Password is requied");
                    editText_login_pwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textloginemail, textloginpwd);
                }
            }

            private void loginUser(String loginemail, String loginpwd) {
                authProfile.signInWithEmailAndPassword(loginemail, loginpwd).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = authProfile.getCurrentUser();

                        if (firebaseUser.isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Your are Logged in now", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            firebaseUser.sendEmailVerification();
                            authProfile.signOut();
                            showAlertdialog();
                        }

                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            editText_login_email.setError("User does not exits or is no longer valid. Please register Again");
                            editText_login_email.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editText_login_email.setError("Invalid credentials. Kindly, check and re-enter");
                            editText_login_email.requestFocus();
                        } catch (Exception e) {
                            Log.e(Tag, e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
// Show hide password using eye Icon
        imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_pwd.setOnClickListener(v -> {
            if (editText_login_pwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                editText_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
            } else {
                editText_login_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageView_show_hide_pwd.setImageResource(R.drawable.eye_24);
            }

        });

    }

    private void showAlertdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email Now. You can not login without email verification");
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });
        //crete the AlertDialog box
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "Already Logged In!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "You can Login In!", Toast.LENGTH_SHORT).show();
        }
// Reset/Forget Password
        textView_forgot_password_link.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "You can reset your password now", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        textView_register_link.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initView() {
        editText_login_email = findViewById(R.id.editText_login_email);
        editText_login_pwd = findViewById(R.id.editText_login_pwd);
        imageView_show_hide_pwd = findViewById(R.id.imageView_show_hide_pwd);
        button_login = findViewById(R.id.button_login);
        textView_register_link = findViewById(R.id.textView_register_link);
        textView_forgot_password_link = findViewById(R.id.textView_forgot_password_link);
        progressBar = findViewById(R.id.progressBar);
    }
}