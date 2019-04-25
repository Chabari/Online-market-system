package com.example.uoksmartsoko;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyUserDetails extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private String user_id,phone,fname,lname,id,email;
    private CircleImageView mImageProfile;
    private EditText mFname,mLname,mPhone,mID;
    private Button mSubmit;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_user_details);

        //init firebase
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = auth.getCurrentUser().getUid();
        email = auth.getCurrentUser().getEmail();

        //init view
        mFname = (EditText)findViewById(R.id.edt_fname);
        mLname = (EditText)findViewById(R.id.edt_lname);
        mPhone = (EditText)findViewById(R.id.edt_phone);
        mID = (EditText)findViewById(R.id.edt_ID);
        mImageProfile = (CircleImageView)findViewById(R.id.profile_image);
        progressDialog = new ProgressDialog(this);
        mSubmit = (Button)findViewById(R.id.btn_submit);
        //initializing details
        firebaseFirestore.collection("Users").document(user_id).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String imageUrl = task.getResult().getString("imageUrl");
                                String fname = task.getResult().getString("fname");
                                String lname = task.getResult().getString("lname");
                                String phone = task.getResult().getString("phone");
                                String id = task.getResult().getString("id");
                                //setting user image
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();
                                Glide.with(MyUserDetails.this)
                                        .applyDefaultRequestOptions(requestOptions)
                                        .load(imageUrl).into(mImageProfile);

                                //setting other values
                                mFname.setText(fname);
                                mLname.setText(lname);
                                mPhone.setText(phone);
                                mID.setText(id);
                            }
                        }
                    }
                });

        //picking the image profile from gallery
        mImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MyUserDetails.this);
            }
        });

        //uploading them details
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = mFname.getText().toString().trim();
                lname = mLname.getText().toString().trim();
                phone = mPhone.getText().toString().trim();
                id = mID.getText().toString().trim();

                //checking them values
                if (TextUtils.isEmpty(fname)){
                    mFname.setError("Enter First Name..!");
                }else if (TextUtils.isEmpty(lname)){
                    mLname.setError("Enter Last Name..!");
                }else if (TextUtils.isEmpty(phone)){
                    mPhone.setError("Enter Phone Number..!");
                }else if (phone.length()< 10 || phone.length() >10){
                    mPhone.setError("Enter a valid phone number 10 digits");
                }else if (id.length() >8){
                    mID.setError("Enter a valid id number");
                }else {

                    if (imageUri != null){
                        //showing progress here
                        progressDialog.setMessage("Uploading your details...");
                        progressDialog.show();


                        //uploading the image to storage
                        String randomFileName = UUID.randomUUID().toString();
                        final StorageReference reference = storageReference.child(user_id+"Profile Images").child(randomFileName+".jpg");
                        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           //getting the dowload image url
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String image_url = uri.toString();

                                        //calling the uploading method
                                        uploadDetails(auth.getCurrentUser().getEmail().toString(),fname,lname,phone,id,image_url);

                                    }
                                });
                            }
                        });
                    }else
                    {
                        Toast.makeText(MyUserDetails.this, "Please pick profile image..!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                mImageProfile.setImageURI(imageUri);//setting the actual image to the imageView item
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadDetails(String email,String fname,String lname,String phone,String id,String image_url){
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("email",email);
        objectMap.put("fname",fname);
        objectMap.put("lname",lname);
        objectMap.put("phone",phone);
        objectMap.put("id",id);
        objectMap.put("imageUrl",image_url);

        //to db now
        firebaseFirestore.collection("Users").document(user_id).set(objectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MyUserDetails.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else {
                    String error = task.getException().toString();
                    Toast.makeText(MyUserDetails.this, "Something went wrong\n"+error, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
