package com.example.userbooking;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserHome extends AppCompatActivity implements HomeAdapter.OnItemClickListener {

    private RecyclerView mRecycleView;
    private HomeAdapter mAdapter;
    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;

    private ValueEventListener mDbListener;
    private List<SellerDetails> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        mRecycleView = findViewById(R.id.recycle_view2);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mAdapter = new HomeAdapter(this,mUploads);

        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SellerDetails");

        mDbListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    SellerDetails upload = postSnapshot.getValue(SellerDetails.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserHome.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        SellerDetails selectedItem = mUploads.get(position);
        Intent intent = new Intent(UserHome.this,DisplaySeller.class);
        intent.putExtra("SellerId" , selectedItem.getUserId());
        startActivity(intent);
        //Toast.makeText(UserHome.this,"Ground Name is : " +selectedItem.getGroundName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDbListener);
    }

}