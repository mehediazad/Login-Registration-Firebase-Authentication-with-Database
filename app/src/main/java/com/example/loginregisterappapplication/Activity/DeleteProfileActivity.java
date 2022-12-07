package com.example.loginregisterappapplication.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.loginregisterappapplication.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DeleteProfileActivity extends AppCompatActivity {
    private EditText editText_delete_user_pwd;
    private ImageView imageView_show_hide_pwd;
    private TextView button_delete_user_authenticate;
    private TextView textView_delete_user_authenticated;
    private TextView button_delete_user;
    private ProgressBar progressBar;
    private String userPwd;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private static final String TAG = "DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAction_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //

        initView();
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

// disable Delete user Button until user is Authenticated
        button_delete_user.setEnabled(false);

        if (firebaseUser.equals("")) {
            Toast.makeText(DeleteProfileActivity.this, "Something went wrong!" + "User details are not available at the moment", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();

        } else {
            reAuthenticateUser(firebaseUser);
        }

    }

    // reAuthenticateUser user before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {

        button_delete_user_authenticate.setOnClickListener(v -> {
            userPwd = editText_delete_user_pwd.getText().toString();

            if (TextUtils.isEmpty(userPwd)) {
                Toast.makeText(DeleteProfileActivity.this, "Password is needed.", Toast.LENGTH_SHORT).show();
                editText_delete_user_pwd.setError("Please Enter Your Current Password to Authenticate");
                editText_delete_user_pwd.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), userPwd);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        progressBar.setVisibility(View.GONE);

                        // Disable editText for  Password.
                        editText_delete_user_pwd.setEnabled(false);

                        // Enable delete user btn. disable AUTHENTICATE btn
                        button_delete_user_authenticate.setEnabled(false);
                        button_delete_user.setEnabled(true);

                        textView_delete_user_authenticated.setText("You are Authenticate/Verified." + "You can delete Your profile and related data now");
                        Toast.makeText(DeleteProfileActivity.this, "Password has been Verified" +
                                "You can delete your profile now Be careful, this action is irreversible.", Toast.LENGTH_SHORT).show();

                        // update color of change password btn
                        button_delete_user.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this, R.color.green));

                        button_delete_user.setOnClickListener(v1 -> showAlertDialog());
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
        imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
        imageView_show_hide_pwd.setOnClickListener(v -> {
            if (editText_delete_user_pwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                editText_delete_user_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageView_show_hide_pwd.setImageResource(R.drawable.hide_pwd);
            } else {
                editText_delete_user_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageView_show_hide_pwd.setImageResource(R.drawable.eye_24);
            }

        });
    }

    // Delete All data of user
    private void deleteUserData(FirebaseUser firebaseUser) {
        // Delete Display pic.  Also check if the user has uploaded any pic before deleting.
        if (firebaseUser.getPhotoUrl() != null){
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(unused -> Log.d(TAG,"OnSuccess: Photo Deleted")).addOnFailureListener(e -> {
                Log.d(TAG,e.getMessage());
                Toast.makeText(DeleteProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            });
        }


        // Delete data from realtime database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered User");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(unused -> {
            Log.d(TAG,"OnSuccess: User Data Deleted");

            // Finally delete the user after deleting the real time database
            deleteUser();
        }).addOnFailureListener(e -> {
            Log.d(TAG,e.getMessage());
            Toast.makeText(DeleteProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        });

    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete User and Related Data?");
        builder.setMessage("Do you really want to delete your profile and related data?");
        builder.setPositiveButton("Continue", (dialog, which) -> deleteUserData(firebaseUser));
        // Return to user profile Activity if user presses Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        });

        //crete the AlertDialog box
        AlertDialog alertDialog = builder.create();

        //Change the button color of Continue
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red)));
        alertDialog.show();

    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(this::onComplete);
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
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_settings) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(DeleteProfileActivity.this, PasswordChangeActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(DeleteProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_Logout) {
            authProfile.signOut();
            Toast.makeText(DeleteProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(DeleteProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);


    }

    private void initView() {
        editText_delete_user_pwd = findViewById(R.id.editText_delete_user_pwd);
        imageView_show_hide_pwd = findViewById(R.id.imageView_show_hide_pwd);
        button_delete_user_authenticate = findViewById(R.id.button_delete_user_authenticate);
        textView_delete_user_authenticated = findViewById(R.id.textView_delete_user_authenticated);
        button_delete_user = findViewById(R.id.button_delete_user);
        progressBar = findViewById(R.id.progressBar);
    }

    private void onComplete(Task<Void> task) {
        if (task.isSuccessful()) {
            authProfile.signOut();
            Toast.makeText(DeleteProfileActivity.this, "User has been deleted!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            try {
                throw Objects.requireNonNull(task.getException());
            } catch (Exception e) {
                Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        progressBar.setVisibility(View.GONE);
    }
}