package com.example.userbooking;

import java.util.UUID;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;


public class BookingSlot extends AppCompatActivity implements PaymentResultListener {

    RadioGroup radioGroup;
    EditText username,email,mobileNo;
    TextView bookingDate;
    Spinner spinner;
    Button makePayment;
    DatabaseReference databaseReference,databaseReference2;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_slot);

        final String sellerId = getIntent().getStringExtra("SellerId");

        databaseReference = FirebaseDatabase.getInstance().getReference("BookingDetails").child(sellerId);
        databaseReference2 = FirebaseDatabase.getInstance().getReference("SellerDetails").child(sellerId);

        makePayment=findViewById(R.id.makePayment);

        spinner = findViewById(R.id.userTimeSeleced);

        radioGroup = findViewById(R.id.radioGroup);

        username = findViewById(R.id.userName);
        email = findViewById(R.id.userEmail);
        mobileNo = findViewById(R.id.userPhoneNo);
        bookingDate = findViewById(R.id.userBookingDate);

        Calendar c1 = Calendar.getInstance();
        final int year = c1.get(Calendar.YEAR);
        final int month = c1.get(Calendar.MONTH);
        final int day = c1.get(Calendar.DAY_OF_MONTH);


        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                try {
                    razorpayPayment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                //BookingDetails details = new BookingDetails(username1,email1,mobileNo1,bookingDate1,bookingTime);
                //databaseReference.child(bookingDate1).child(UUID.randomUUID().toString()).setValue(details);
                //Toast.makeText(BookingSlot.this, "Done", Toast.LENGTH_SHORT).show();

            }
        });


        bookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        BookingSlot.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = String.format("%02d", month)+"-"+String.format("%02d", dayOfMonth)+"-"+year;
                        bookingDate.setText(date);
                        try {
                            dateSelect(sellerId,bookingDate.getText().toString());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },year,month,day);
                datePickerDialog.show();

            }

        });

    }

    void razorpayPayment() throws InterruptedException {
        String username1,email1,mobileNo1,gameType,bookingDate1,bookingTime;
        email1 = email.getText().toString().trim();
        username1 = username.getText().toString();
        int i = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton =  findViewById(i);
        try{
            gameType = radioButton.getText().toString();
            Log.e("radio value",gameType);
        }catch(NullPointerException e){
            Toast.makeText(BookingSlot.this, "Select the Game type!", Toast.LENGTH_SHORT).show();
            radioGroup.requestFocus();
            return;
        }
        bookingDate1 = bookingDate.getText().toString();


        mobileNo1=mobileNo.getText().toString();

        if(username1.isEmpty()){
            username.setError("Name is required");
            username.requestFocus();
            return;
        }
        if(email1.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            email.setError("Please provide valid email");
            email.requestFocus();
            return;
        }
        if(mobileNo1.isEmpty()){
            mobileNo.setError("Mobile No. is required");
            mobileNo.requestFocus();
            return;
        }
        if(!Patterns.PHONE.matcher(mobileNo1).matches()){
            mobileNo.setError("Please provide valid Mobile No.");
            mobileNo.requestFocus();
            return;
        }
        if(bookingDate1.isEmpty()){
            bookingDate.setError("Enter Day Price");
            bookingDate.requestFocus();
            return;
        }

        try{
            bookingTime = spinner.getSelectedItem().toString();
            Log.e("dropDown ",bookingTime);
        }catch (NullPointerException e){
            Toast.makeText(BookingSlot.this, "Select Booking Time!", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
            return;
        }

//        BookingDetails details = new BookingDetails(username1,email1,mobileNo1,bookingDate1,bookingTime);
//        databaseReference.child(bookingDate1).child(UUID.randomUUID().toString()).setValue(details);


        final String[] sAmount = new String[1];

        new Thread(){
            public void run(){
                try {
                    getPriceFromDatabase(bookingTime,sAmount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            public void run(){razorPayment(sAmount);}
        }.start();


//        int amount = Math.round(Float.parseFloat(sAmount[0])*100);
//        Log.e("Final price",amount+"");
//
//        Checkout checkout = new Checkout();
//
//        checkout.setKeyID("rzp_test_xzI9CTs3LCMRYv");
//        checkout.setImage(R.drawable.rzp_logo);
//        JSONObject object = new JSONObject();
//        try {
//
//            object.put("name",username1);
//            object.put("description",gameType);
//            object.put("theme.color","#0093DD");
//            object.put("currency","INR");
//            object.put("amount",amount);
//            object.put("prefill.contact",mobileNo1);
//            object.put("prefill.email",email1);
//            checkout.open(BookingSlot.this,object);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    synchronized void razorPayment(String[] sAmount){
    try {
        if(sAmount[0].equals(null)){
            try{wait();}catch(Exception e){}
        }
    }catch (NullPointerException e){
        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
    }


        int amount = Math.round(Float.parseFloat(sAmount[0])*100);
        Log.e("Final price",amount+"");

        String username1,email1,mobileNo1,gameType;
        email1 = email.getText().toString().trim();
        username1 = username.getText().toString();
        int i = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton =  findViewById(i);
        gameType = radioButton.getText().toString();

        mobileNo1=mobileNo.getText().toString();

        Checkout checkout = new Checkout();

        checkout.setKeyID("rzp_test_xzI9CTs3LCMRYv");
        checkout.setImage(R.drawable.rzp_logo);
        JSONObject object = new JSONObject();
        try {

            object.put("name",username1);
            object.put("description",gameType);
            object.put("theme.color","#0093DD");
            object.put("currency","INR");
            object.put("amount",amount);
            object.put("prefill.contact",mobileNo1);
            object.put("prefill.email",email1);
            checkout.open(BookingSlot.this,object);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

    }

    synchronized void getPriceFromDatabase(String bookingTime,String[] sAmount) throws InterruptedException {
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int bookTime = Integer.parseInt(bookingTime.substring(0,2).trim());
                Log.e("Booking Time",bookTime+"");

                if(bookTime>=19){
                    sAmount[0] = snapshot.child("nightPrice").getValue().toString();
                    Log.e("price", sAmount[0]);
                }
                else{
                    sAmount[0] = snapshot.child("dayPrice").getValue().toString();
                    Log.e("price", sAmount[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Thread.sleep(1000);

        while (sAmount[0].isEmpty()){

        }
        notify();
    }


    void dateSelect(String sellerId,String date) throws InterruptedException {

        ArrayList<String> timeBooked = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("BookingDetails").child(sellerId).child(date)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        timeBooked.clear();
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                            BookingDetails bookingDetails = postSnapshot.getValue(BookingDetails.class);

                            if(bookingDetails.getBookingDate().equals(date)){
                                Log.e("booked database",bookingDetails.getBookingTime());
                                timeBooked.add(bookingDetails.getBookingTime());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Thread.sleep(500);

        ArrayList<String> totalTime = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("SellerDetails").child(sellerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int startTime = Integer.parseInt(dataSnapshot.child("dayTimeStart").getValue().toString());
                        int endingTime = Integer.parseInt(dataSnapshot.child("nightTimeEnd").getValue().toString());

                        totalTime.clear();
                        for(int i=startTime;i<endingTime;i++){
                            Boolean timeAvailable;
                            String time;

                            time=Integer.toString(i)+" to "+Integer.toString(i+1);
                            //timeAvailable=checkTimeExisted(time,sellerId,date);
                            timeAvailable=checkTimeExisted(time,timeBooked);
                            if(timeAvailable.equals(false)){
                                Log.e("total time ",time);
                                totalTime.add(time);
                            }

                        }

                        spinner.setAdapter(new ArrayAdapter<>(BookingSlot.this,android.R.layout.simple_spinner_dropdown_item,totalTime));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

//    Boolean checkTimeExisted(String time,String sellerId,String date){
//        final Boolean[] returnVal = new Boolean[1];
//        returnVal[0]=false;
//        FirebaseDatabase.getInstance().getReference("BookingDetails").child(sellerId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                            //Log.e("postsnapshot",postSnapshot.getKey().toString());
//                            //Log.e("postsnapshot2",postSnapshot.toString());
//                            BookingDetails bookingDetails = postSnapshot.getValue(BookingDetails.class);
//                            Log.e("database",bookingDetails.getBookingTime());
//                            Log.e("time",time);
//                            if(bookingDetails.getBookingTime().equals(time)){
//                                returnVal[0] =true;
//                                break;
//                            }
//                            else{
//                                returnVal[0]=false;
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        return returnVal[0];
//    }

    Boolean checkTimeExisted(String time,ArrayList<String> timeBooked){
        Boolean returnVal=false;
        for(int i=0; i < timeBooked.size(); i++){
            Log.e("total function time ",timeBooked.get(i));
           if(timeBooked.get(i).equals(time)){
               returnVal=true;
               break;
           }
           else{
               returnVal=false;
           }
        }
        return returnVal;
    }


    @Override
    public void onPaymentSuccess(String s) {
        String username1,email1,mobileNo1,gameType,bookingDate1,bookingTime;
        email1 = email.getText().toString().trim();
        username1 = username.getText().toString();
        int i = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton =  findViewById(i);
        gameType = radioButton.getText().toString();
        bookingDate1 = bookingDate.getText().toString();
        bookingTime = spinner.getSelectedItem().toString();
        mobileNo1=mobileNo.getText().toString();
        

        BookingDetails details = new BookingDetails(username1,email1,mobileNo1,bookingDate1,bookingTime);
        databaseReference.child(bookingDate1).child(UUID.randomUUID().toString()).setValue(details);

        String message = "Hello "+username1+", Your booking for "+gameType+" on "+bookingDate1+" and timing is "+bookingTime+" is done.";
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,email1,"Booking Successful",message);

        javaMailAPI.execute();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Fail please try again!", Toast.LENGTH_SHORT).show();
    }
}