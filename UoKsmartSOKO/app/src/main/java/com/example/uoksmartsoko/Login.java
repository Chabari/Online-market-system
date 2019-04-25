package com.example.uoksmartsoko;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private Button buttonLogin;
    private EditText etEmail,etPassword;
    private TextView tvSignUp;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //Home activity here
            startActivity(new Intent(getApplicationContext(), Home.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonLogin = findViewById(R.id.buttonLogin);
        etEmail = findViewById(R.id.etL_email);
        etPassword = findViewById(R.id.etL_password);
        tvSignUp = findViewById(R.id.textViewSignUp);


        buttonLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

    }
    private void userLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this,"email is empty", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this,"password is empty", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        //if the email and password are not empty
        //display a progressDialog
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //Start Home activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else{
                            String error = task.getException().toString();
                            Toast.makeText(Login.this, "Something went wrong\n"+error, Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });



    }

        @Override
        public void onClick(View view) {
        if(view == buttonLogin){
            userLogin();

        }
        if(view == tvSignUp){
            finish();
            startActivity(new Intent(this, SignUp.class));

        }

        }


}
