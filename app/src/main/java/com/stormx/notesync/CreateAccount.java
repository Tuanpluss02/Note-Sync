package com.stormx.notesync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CreateAccount extends AppCompatActivity {

    EditText emailController, passwordController, confirmPasswordController;
    Button createAccountButton;
    ProgressBar progressBar;
    LinearLayout loginButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailController = findViewById(R.id.email);
        passwordController = findViewById(R.id.password);
        confirmPasswordController = findViewById(R.id.confirm_password);
        createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progress_bar);
        loginButtonView = findViewById(R.id.login_btn_view);
        createAccountButton.setOnClickListener(view -> createAccount());
        loginButtonView.setOnClickListener(view -> startActivity(new Intent(CreateAccount.this, LoginActivity.class)));
    }

    private void createAccount() {
        String email = emailController.getText().toString();
        String password = passwordController.getText().toString();
        String confirmPassword = confirmPasswordController.getText().toString();
        boolean isValid = valdateInput(email, password, confirmPassword);
        if (!isValid) return;
        createAccountFirebase(email, password);
    }

    private void createAccountFirebase(String email, String password) {
        isLoading(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(CreateAccount.this, task -> {
                    isLoading(false);
                    if (task.isSuccessful()) {
                        Utils.showToast(CreateAccount.this, "Account created successfully");
                        firebaseAuth.signOut();
                        finish();
                    } else {
                        Utils.showToast(CreateAccount.this, Objects.requireNonNull(task.getException()).getLocalizedMessage() );
                    }
                });


    }

    private void isLoading(boolean b) {
        if (b) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            createAccountButton.setVisibility(Button.GONE);
            emailController.setEnabled(false);
            passwordController.setEnabled(false);
            confirmPasswordController.setEnabled(false);
            createAccountButton.setEnabled(false);
            loginButtonView.setEnabled(false);
        } else {
            createAccountButton.setVisibility(Button.VISIBLE);
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }

    boolean valdateInput(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            emailController.setError("Email is required");
            emailController.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordController.setError("Password is required");
            passwordController.requestFocus();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordController.setError("Confirm Password is required");
            confirmPasswordController.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailController.setError("Please provide valid email");
            emailController.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            passwordController.setError("Password must be at least 6 characters");
            passwordController.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordController.setError("Password is not matched");
            confirmPasswordController.requestFocus();
            return false;
        }
        return true;
    }
}