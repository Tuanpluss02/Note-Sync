package com.stormx.notesync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText emailController, passwordController;
    ProgressBar progressBar;
    Button loginButton;
    LinearLayout createAccountButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailController = findViewById(R.id.email);
        passwordController = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);
        loginButton = findViewById(R.id.login_button);
        createAccountButtonView = findViewById(R.id.create_account_btn_view);
        loginButton.setOnClickListener(view -> firebaseLogin());
        createAccountButtonView.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, CreateAccount.class)));
    }

    private void firebaseLogin() {
        String email = emailController.getText().toString();
        String password = passwordController.getText().toString();
        boolean isValid = validateInput(email, password);
        if (!isValid) return;
        loginToFirebase(email, password);
    }

    private void loginToFirebase(String email, String password) {
        isLoading(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    isLoading(false);
                    if (task.isSuccessful()) {
                        Utils.showToast(LoginActivity.this, "Login successful");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Utils.showToast(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                });
    }

    private void isLoading(boolean b) {
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            loginButton.setEnabled(false);
            createAccountButtonView.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInput(String email, String password) {
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
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailController.setError("Please provide valid email");
            emailController.requestFocus();
            return false;
        }
        return true;
    }
}