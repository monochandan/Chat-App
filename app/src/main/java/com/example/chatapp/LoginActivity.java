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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
  private TextView needAccount;
  private Button login,login_phone;
  private EditText email,password;
//  private FirebaseUser  currentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();

        Initialize();

        needAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegistration();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogIn();
            }
        });
        login_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,PhonLoginActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
                startActivity(intent);
                //finish();
            }
        });
    }

    private void userLogIn() {
        String eml = email.getText().toString();
        String pass = password.getText().toString();
        if(TextUtils.isEmpty(eml))
        {
            Toast.makeText(this, "enter email first", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(this, "enter Password first", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Logging in");
            loadingbar.setMessage("Please Wait...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();
            mAuth.signInWithEmailAndPassword(eml,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        sendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else
                    {
                        String mssg = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "error: "+mssg, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }

                }
            });

        }
    }

    private void sendToRegistration() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    private void Initialize() {

        needAccount = findViewById(R.id.new_account);
        login_phone = findViewById(R.id.login_by_phone);
        login = findViewById(R.id.login_button);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loadingbar = new ProgressDialog(this);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(currentUser != null)
//        {
//            sendUserToMainActivity();
//        }
//    }

    private void sendUserToMainActivity() {

        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
        startActivity(intent);
        finish();
    }
}
