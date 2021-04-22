package com.example.groundbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.service.controls.templates.TemperatureControlTemplate;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class UploadImage extends AppCompatActivity {

    String UserID;
    FirebaseUser user;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage,mButtonUpload;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask  mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        user = FirebaseAuth.getInstance().getCurrentUser();
        UserID = user.getUid();

        mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);
        mButtonUpload = (Button) findViewById(R.id.button_upload);


        mImageView = (ImageView) findViewById(R.id.image_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(UserID);

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(UploadImage.this,"Upload in Progress" , Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();
                }
            }
        });

    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();

          //  Picasso.with(this).load(mImageUri).into(mImageView);
            Glide.with(this).load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if(mImageUri != null){
            final long[] fileName = {System.currentTimeMillis()};
            StorageReference fileReference = mStorageRef.child(fileName[0] +"."+getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(UploadImage.this,"Picture Upload Successful" , Toast.LENGTH_SHORT).show();
                            String url = getString(R.string.storageLink)+ fileName[0] +"."+getFileExtension(mImageUri)+"?alt=media";
                            ImageInfo upload = new ImageInfo(UserID,fileName[0] +"."+getFileExtension(mImageUri),url);
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(UploadImage.this,"Failed to Upload" , Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }else{
            Toast.makeText(this,"Picture Not Selected" , Toast.LENGTH_SHORT).show();
        }

    }

}