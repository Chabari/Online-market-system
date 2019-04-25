package com.example.uoksmartsoko;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Upload extends AppCompatActivity {

    private static final  int PICK_IMAGE_REQUEST =1;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String user_id,phone,price,pricebefore,desc,title,category,fname,lname;

    private Spinner mSpinner;
    private Button mUpload;
    private EditText mTitle,mDesc,mPrice,mPriceBefore;
    private ImageView mImageToPost;
    private ProgressDialog progressDialog;

    private Uri mImageUri = null;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //initializing firebase
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //initializing item view
        mTitle = (EditText)findViewById(R.id.edt_title_to_post);
        mDesc = (EditText)findViewById(R.id.edt_desc_to_post);
        mPrice = (EditText)findViewById(R.id.edt_price_to_post);
        mPriceBefore= (EditText)findViewById(R.id.edt_price_to_post_before);
        mSpinner = (Spinner)findViewById(R.id.sp_category);
        mImageToPost = (ImageView)findViewById(R.id.image_to_post);
        progressDialog = new ProgressDialog(this);
        mUpload = (Button)findViewById(R.id.btn_upload);

        //setting initial value to spinner category
        mSpinner.setSelection(0);

        //picking the image from Gallery
        mImageToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(mImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Upload.this);
            }
        });

        //Uploading on click
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the phone number and fullname from db for upload
                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                mUpload.setEnabled(false);//disabling the button to prevent double uploads
                                fname = task.getResult().getString("fname");
                                lname = task.getResult().getString("lname");
                                phone = task.getResult().getString("phone");

                                String fullname = fname+" "+lname;
                                sendingUploads(phone,fullname);

                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mImageToPost.setImageURI(mImageUri);//setting the actual image to the imageView item
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void sendingUploads(final String phone,final String fullname) {

        title = mTitle.getText().toString().trim();
        desc = mDesc.getText().toString().trim();
        price = mPrice.getText().toString().trim();
        pricebefore = mPriceBefore.getText().toString().trim();
        category = mSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(title)) {
            mTitle.setError("Please enter title..!");
        } else if (TextUtils.isEmpty(desc)) {
            mDesc.setError("Please enter description..!");
        } else if (TextUtils.isEmpty(price)) {
            mPrice.setError("Please enter your final price..!");
        } else if (TextUtils.isEmpty(pricebefore)) {
            mPriceBefore.setError("Please enter your initial price..!");
        } else if (mSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select category..!", Toast.LENGTH_SHORT).show();
        } else {


            if (mImageUri != null) {
                //showing uploading progress
                progressDialog.setMessage("Uploading your item to smartSoko...");
                progressDialog.show();

                String randomName = UUID.randomUUID().toString();
                final StorageReference reference = mStorageRef.child("SmartSokoPics").child(randomName + ".jpg");
                reference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Uri dowloadUri = uri;

                                //UPLOADING THE FILE TO DATABSE SECTION
                                Map<String, Object> objectMap = new HashMap<>();
                                objectMap.put("imageUrl", dowloadUri.toString());
                                objectMap.put("user_id", user_id);
                                objectMap.put("title",title);
                                objectMap.put("desc",desc);
                                objectMap.put("phone",phone);
                                objectMap.put("fullname",fullname);
                                objectMap.put("category",category);
                                objectMap.put("price",price);
                                objectMap.put("pricebefore",pricebefore);
                                objectMap.put("timeStamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("TopDeals").add(objectMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(getApplicationContext(), "Uploaded successfully..", Toast.LENGTH_SHORT).show();
                                            onBackPressed();//going back to the parent activity
                                        } else {
                                            String error = task.getException().toString();
                                            Toast.makeText(Upload.this, "Something went wrong\n" + error, Toast.LENGTH_SHORT).show();
                                        }

                                        progressDialog.dismiss();//finished upload

                                    }
                                });
                            }
                        });
                    }
                });

            } else {
                Toast.makeText(this, "Please select image file..!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
