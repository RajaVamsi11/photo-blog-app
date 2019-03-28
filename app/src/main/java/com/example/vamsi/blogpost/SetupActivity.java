package com.example.vamsi.blogpost;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private EditText setupName;
    private Button setupBtn;
    private String user_id;
    private boolean isChanged = false;
    private ProgressBar setupProgress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();

        setupImage = findViewById(R.id.setup_image);

        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);
        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().exists()) {
                     String name = task.getResult().getString("name");
                     String image = task.getResult().getString("image");
                     mainImageURI = Uri.parse(image);

                     setupName.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                     Glide.with(SetupActivity.this).
                             setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "firestore Retrieval Error "+error, Toast.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString().trim();

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {
                    setupProgress.setVisibility(View.VISIBLE);


                    if(isChanged){



                        user_id = firebaseAuth.getCurrentUser().getUid();


                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    storeFirestore(task,user_name);
                                } else {
                                    setupProgress.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SetupActivity.this, " Image Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });


                    }

                    else{
                        storeFirestore(null,user_name);
                    }
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(SetupActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {

                        BringImagePicker();
                    }
                } else {

                    BringImagePicker();

                }


            }
        });

    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task,String user_name) {
        Uri download_uri;
        if(task != null){
            download_uri = task.getResult().getDownloadUrl();
        }else {
            download_uri = mainImageURI;
        }



        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(SetupActivity.this, "User Settings Updated", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }else{
                    Toast.makeText(SetupActivity.this, "Fire base Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);
                isChanged=true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
