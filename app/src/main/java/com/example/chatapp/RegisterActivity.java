package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
  private TextView havAccount;
  private EditText emailET,passwordET;
  private Button createAccuont;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        Initialize();


        havAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });

        createAccuont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "enter email first", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "enter Password first", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Please Wait...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        
                        String currentUID = mAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUID).setValue("");

                        sendUserToMainActivity();
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(RegisterActivity.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    } else {
                        String mssg = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "error: "+mssg, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }


    }

    private void sendToLogin() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void sendUserToMainActivity() {

        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
        startActivity(intent);
        finish();
    }

    private void Initialize() {

        havAccount = findViewById(R.id.reg_already_account);
        createAccuont = findViewById(R.id.reg_button);
        emailET = findViewById(R.id.reg_email);
        passwordET = findViewById(R.id.reg_password);
        loadingbar = new ProgressDialog(this);
    }
}
