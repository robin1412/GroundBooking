package com.example.groundbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class Register extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    Button reg;
    private EditText email,pass;
    EditText eGroundName,eDayPrice,eNightPrice;
    TextView eDayTimeStart,eNightTimeEnd;
    int thr1,thr4,tmin=0;
    private DatabaseReference databaseRef;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg = findViewById(R.id.reg);
        reg.setOnClickListener(this);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();

        databaseRef = FirebaseDatabase.getInstance().getReference("SellerDetails");

        eGroundName = findViewById(R.id.groundName);
        eDayPrice = findViewById(R.id.dayPrice);
        eNightPrice = findViewById(R.id.nightPrice);

        progressBar = findViewById(R.id.progressBar);

        eDayTimeStart = findViewById(R.id.dayTimeStart);
        eDayTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        Register.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                thr1 = hourOfDay;
                                eDayTimeStart.setText(Integer.toString(thr1));
                            }
                        },12,0,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(thr1,tmin);
                timePickerDialog.show();
            }
        });



        eNightTimeEnd = findViewById(R.id.nightTimeEnd);
        eNightTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        Register.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                thr4 = hourOfDay;
                                eNightTimeEnd.setText(Integer.toString(thr4));
                            }
                        },12,0,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(thr4,tmin);
                timePickerDialog.show();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.reg:
                registerUser();
        }
    }
    private void registerUser() {
        String e = email.getText().toString().trim();
        String p = pass.getText().toString().trim();
        String groundName,dayPrice,nightPrice,dayTimeStart,dayTimeEnd,nightTimeStart,nightTimeEnd;
        groundName = eGroundName.getText().toString();
        dayPrice = eDayPrice.getText().toString();
        nightPrice = eNightPrice.getText().toString();
        dayTimeStart = eDayTimeStart.getText().toString();
        nightTimeEnd = eNightTimeEnd.getText().toString();
        if(e.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if(p.isEmpty()){
            pass.setError("password is required");
            pass.requestFocus();
            return;
        }
        if(groundName.isEmpty()){
            eGroundName.setError("Enter GroundName");
            eGroundName.requestFocus();
            return;
        }
        if(dayPrice.isEmpty()){
            eDayPrice.setError("Enter Day Price");
            eDayPrice.requestFocus();
            return;
        }
        if(nightPrice.isEmpty()){
            eNightPrice.setError("Enter Night Price");
            eNightPrice.requestFocus();
            return;
        }
        if(dayTimeStart.isEmpty()){
            eDayTimeStart.setError("Choose Day Starting Time");
            eDayTimeStart.requestFocus();
            return;
        }
        if(nightTimeEnd.isEmpty()){
            eNightTimeEnd.setError("Choose Night Ending Time");
            eNightTimeEnd.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
            email.setError("Please provide valid email");
            email.requestFocus();
            return;
        }
        if(p.length()<6){
            pass.setError("Min password length should be 6 characters!");
            pass.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(e,p)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            SellerDetails seller = new SellerDetails(groundName, dayTimeStart, nightTimeEnd, dayPrice, nightPrice,FirebaseAuth.getInstance().getCurrentUser().getUid(),"");
                            databaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(seller);
                            FirebaseDatabase.getInstance().getReference("SellerDetails")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Register.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Register.this,MainActivity.class));

                                    }else{
                                        Toast.makeText(Register.this, "Failed to register! Please try again.", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }else{
                            Toast.makeText(Register.this, "Failed to register! Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}