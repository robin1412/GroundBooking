package com.example.groundbooking.Seller.home;

import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.groundbooking.R;
import com.example.groundbooking.SellerDetails;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


public class HomeFragment extends Fragment {

    int thr1,thr4,tmin=0;
    private static final int PICK_IMAGE_REQUEST = 1;
    String UserID;
    FirebaseUser user;
    EditText eGroundName,eDayPrice,eNightPrice;
    Button save;
    TextView eDayTimeStart,eNightTimeEnd,editProfilePic;
    private Uri mImageUri;
    ImageView profilePic;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private FirebaseStorage firebaseStorage;
    ProgressBar progressBar;

    DatabaseReference databaseRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();

        databaseRef = FirebaseDatabase.getInstance().getReference("SellerDetails");
        mStorageRef = FirebaseStorage.getInstance().getReference("ProfilePics");
        firebaseStorage = FirebaseStorage.getInstance();

        profilePic = root.findViewById(R.id.profilePic);

        editProfilePic = root.findViewById(R.id.editProfilePic);
        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        eGroundName = root.findViewById(R.id.groundNamex);
        eDayPrice = root.findViewById(R.id.dayPricex);
        eNightPrice = root.findViewById(R.id.nightPricex);

        progressBar = root.findViewById(R.id.progressBar3);

        eDayTimeStart = root.findViewById(R.id.dayTimeStartx);
        eDayTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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


        eNightTimeEnd = root.findViewById(R.id.nightTimeEndx);
        eNightTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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

        final String[] profURL = new String[1];
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SellerDetails seller = snapshot.getValue(SellerDetails.class);
                    if(UserID.equals(seller.getUserId())){
                        eDayPrice.setText(seller.getDayPrice());
                        eNightPrice.setText(seller.getNightPrice());
                        eGroundName.setText(seller.getGroundName());
                        eDayTimeStart.setText(seller.getDayTimeStart());
                        eNightTimeEnd.setText(seller.getNightTimeEnd());
                        profURL[0] = seller.getProfilePicLink();
                        if(!(seller.getProfilePicLink().equals(""))) {
                            Glide.with(getContext())
                                    .load(seller.getProfilePicLink()).centerCrop().placeholder(R.mipmap.ic_launcher).into(profilePic);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        save = root.findViewById(R.id.saveDetails);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(getActivity(),"Upload in Progress" , Toast.LENGTH_SHORT).show();
                }else if(mImageUri != null){
                    uploadFile(profURL[0]);
                }else{
                    uploadDataOnly(profURL[0]);
                }

            }
        });

        return root;
    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null){
             mImageUri = data.getData();
            //Picasso.with(getActivity()).load(mImageUri).into(profilePic);
            Glide.with(getActivity()).load(mImageUri).into(profilePic);
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadDataOnly(String url){
        String groundName,dayPrice,nightPrice,dayTimeStart,nightTimeEnd;
        groundName = eGroundName.getText().toString();
        dayPrice = eDayPrice.getText().toString();
        nightPrice = eNightPrice.getText().toString();
        dayTimeStart = eDayTimeStart.getText().toString();
        nightTimeEnd = eNightTimeEnd.getText().toString();
        SellerDetails seller = new SellerDetails(groundName, dayTimeStart, nightTimeEnd, dayPrice, nightPrice,UserID,url);
        databaseRef.child(UserID).setValue(seller);
    }

    private void uploadFile(String url) {
            deleteCurrentProfile(url);
            final long[] fileName = {System.currentTimeMillis()};
            StorageReference fileReference = mStorageRef.child(fileName[0] +"."+getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getActivity(),"Picture Upload Successful" , Toast.LENGTH_SHORT).show();
                            String url = getString(R.string.profileStrorageLink)+ fileName[0] +"."+getFileExtension(mImageUri)+"?alt=media";

                            String groundName,dayPrice,nightPrice,dayTimeStart,nightTimeEnd;
                            groundName = eGroundName.getText().toString();
                            dayPrice = eDayPrice.getText().toString();
                            nightPrice = eNightPrice.getText().toString();
                            dayTimeStart = eDayTimeStart.getText().toString();
                            nightTimeEnd = eNightTimeEnd.getText().toString();
                            SellerDetails seller = new SellerDetails(groundName, dayTimeStart, nightTimeEnd, dayPrice, nightPrice,UserID,url);
                            databaseRef.child(UserID).setValue(seller);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(),"Failed to Upload" , Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        }
                    });
    }

    private void deleteCurrentProfile(String url){
        Log.e("","link : "+url);
        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(url);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getActivity(),"Item Deleted : ", Toast.LENGTH_SHORT).show();
            }
        });
    }


}