package com.example.groundbooking.Seller.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ProgressBar;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groundbooking.ImageAdapter;
import com.example.groundbooking.ImageInfo;
import com.example.groundbooking.R;
import com.example.groundbooking.UploadImage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements ImageAdapter.OnItemClickListener{

    String UserID;
    FirebaseUser user;

    private RecyclerView mRecycleView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;


    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;

    private ValueEventListener mDbListener;
    private List<ImageInfo> mUploads;
    Button uploadBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();

        mRecycleView = root.findViewById(R.id.recycle_view);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressCircle = root.findViewById(R.id.progress_circle);

        uploadBtn = root.findViewById(R.id.uploadBtn);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadImage.class);
                startActivity(intent);
            }
        });

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(getActivity(),mUploads);

        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(UserID);

        mDbListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    ImageInfo upload = postSnapshot.getValue(ImageInfo.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

        return root;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(),"Normal Click at Position: " +position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(getActivity(),"Whatever Click at Position: " +position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        ImageInfo selectedItem = mUploads.get(position);
        String key = selectedItem.getKey();
        Log.e("Image Url : ",selectedItem.getImageUrl());
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(key).removeValue();
                Toast.makeText(getActivity(),"Item Deleted : ", Toast.LENGTH_SHORT).show();

            }
        });
        //Toast.makeText(this,"Delete Click at Position: " +position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDbListener);
    }
}