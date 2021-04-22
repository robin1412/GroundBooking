package com.example.groundbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    EditText emailx,passx;
    TextView register,forgetpass;
    Button login;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(this, Main2Activity.class);
                startActivity(intent);
            }
        }

        progressBar = findViewById(R.id.progressBar2);

        emailx = findViewById(R.id.emailx);
        passx = findViewById(R.id.passx);
        login = findViewById(R.id.loginx);
        login.setOnClickListener(this);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        forgetpass = findViewById(R.id.forgetpass);
        forgetpass.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register:
                startActivity(new Intent(this,Register.class));
                break;
            case R.id.loginx:
                userLogin();
                break;
            case R.id.forgetpass:
                startActivity(new Intent(this,ForgetPassword.class));
        }
    }

    private void userLogin() {
        String e = emailx.getText().toString().trim();
        String p = passx.getText().toString().trim();
        if(e.isEmpty()){
            emailx.setError("Email is required!");
            emailx.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
            emailx.setError("Enter valid email!");
            emailx.requestFocus();
            return;
        }
        if(p.isEmpty()){
            passx.setError("Password is required!");
            passx.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        //direct to
                        startActivity(new Intent(MainActivity.this,Main2Activity.class));
                        Toast.makeText(MainActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify account!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this, "Failed to login! Check credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}