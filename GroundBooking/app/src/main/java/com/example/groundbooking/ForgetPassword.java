package com.example.groundbooking;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
    private Button resetbtn;
    private EditText emailrst;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        emailrst = findViewById(R.id.emailrst);
        resetbtn = findViewById(R.id.resetbtn);
        auth = FirebaseAuth.getInstance();
        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetpassword();
            }
        });

    }

    private void resetpassword() {
        String email = emailrst.getText().toString().trim();
        if (email.isEmpty()) {
            emailrst.setError("Email required");
            emailrst.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailrst.setError("Enter valid email!");
            emailrst.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPassword.this, "Check your email to change your password", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgetPassword.this, MainActivity.class));
                } else {
                    Toast.makeText(ForgetPassword.this, "Try again! Something wrong happen.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}