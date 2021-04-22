package com.example.userbooking;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplaySeller extends AppCompatActivity {

    ImageSlider imageSlider;
    TextView dispGroundName,dispTiming,dispDayPrice,dispNightPrice;
    DatabaseReference databaseRef;
    Button book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_seller);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        databaseRef = FirebaseDatabase.getInstance().getReference("SellerDetails");

        book = findViewById(R.id.booking);

        dispGroundName = findViewById(R.id.dispGroundName);
        dispTiming = findViewById(R.id.dispTiming);
        dispDayPrice = findViewById(R.id.dispDayPrice);
        dispNightPrice = findViewById(R.id.dispNightPrice);

        imageSlider = findViewById(R.id.image_slider);
        final List<SlideModel> remoteImages = new ArrayList<>();


            final String sellerId = getIntent().getStringExtra("SellerId");

        FirebaseDatabase.getInstance().getReference("uploads").child(sellerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data:dataSnapshot.getChildren()){
                            remoteImages.add(new SlideModel(data.child("imageUrl").getValue().toString(), ScaleTypes.FIT));
                        }
                        imageSlider.setImageList(remoteImages,ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot data:snapshot.getChildren()){
//                    SellerDetails seller = data.getValue(SellerDetails.class);
//                    if(sellerId.equals(seller.getUserId())) {
//
//                        String groundName = seller.getGroundName();
//                        String prices = "The Price at day time is " + seller.getDayPrice() + " and Price at night time is " + seller.getNightPrice();
//                        dispGroundName.setText(groundName);
//                        dispPrices.setText(prices);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        FirebaseDatabase.getInstance().getReference("SellerDetails").child(sellerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String groundName = dataSnapshot.child("groundName").getValue().toString();
                        String timing = dataSnapshot.child("dayTimeStart").getValue().toString()+" to "+dataSnapshot.child("nightTimeEnd").getValue().toString();
                        String dayPrice = dataSnapshot.child("dayPrice").getValue().toString();
                        String nightPrice = dataSnapshot.child("nightPrice").getValue().toString();
                        //String prices = "The Price at day time is "+dataSnapshot.child("dayPrice").getValue().toString()+" and Price at night time is "+dataSnapshot.child("nightPrice").getValue().toString();
                        dispGroundName.setText(groundName);
                        // dispPrices.setText(prices);
                        dispTiming.setText(timing);
                        dispDayPrice.setText(dayPrice);
                        dispNightPrice.setText(nightPrice);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplaySeller.this,BookingSlot.class);
                intent.putExtra("SellerId" , sellerId);
                startActivity(intent);
            }
        });

    }
}