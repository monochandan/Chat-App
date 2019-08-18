package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhonLoginActivity extends AppCompatActivity {
  private Button send_verification,verify_button;
  private EditText phon_number,verificatin_code;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks Callbacks;
  private String mVerificationId;
  private PhoneAuthProvider.ForceResendingToken mResendToken;
  private FirebaseAuth mAuth;
  private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phon_login);
        //initialize();
        mAuth = FirebaseAuth.getInstance();

        send_verification = findViewById(R.id.send_verif_code_button);
        verify_button = findViewById(R.id.verify_button);
        phon_number = findViewById(R.id.phon_number_input);
        verificatin_code = findViewById(R.id.verification_code_here);
        loadingbar = new ProgressDialog(this);




        send_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber = phon_number.getText().toString();

                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhonLoginActivity.this, "Need Phone Number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("Please Wait...");
                    loadingbar.setCanceledOnTouchOutside(true);
                    loadingbar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhonLoginActivity.this,               // Activity (for callback binding)
                            Callbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
                }
            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_verification.setVisibility(View.INVISIBLE);
                phon_number.setVisibility(View.INVISIBLE);


                String  verificatincode = verificatin_code.getText().toString();

                if(TextUtils.isEmpty(verificatincode))
                {
                    Toast.makeText(PhonLoginActivity.this, "Please Write First", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Verifying Code");
                    loadingbar.setMessage("Please Wait...");
                    loadingbar.setCanceledOnTouchOutside(true);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificatincode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        Callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                loadingbar.dismiss();
                Toast.makeText(PhonLoginActivity.this, "Invalied!! Try Again", Toast.LENGTH_SHORT).show();
                send_verification.setVisibility(View.VISIBLE);
                phon_number.setVisibility(View.VISIBLE);

                verify_button.setVisibility(View.INVISIBLE);
                verificatin_code.setVisibility(View.INVISIBLE);

            }
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingbar.dismiss();

                Toast.makeText(PhonLoginActivity.this, "Code has been sent", Toast.LENGTH_SHORT).show();
                send_verification.setVisibility(View.INVISIBLE);
                phon_number.setVisibility(View.INVISIBLE);

                verify_button.setVisibility(View.VISIBLE);
                verificatin_code.setVisibility(View.VISIBLE);
                // ...
            }
        };

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                             loadingbar.dismiss();
                            Toast.makeText(PhonLoginActivity.this, "Congratulation", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        }
                        else
                            {
                                  String mssg = task.getException().toString();
                                Toast.makeText(PhonLoginActivity.this, "Error: "+ mssg, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void sendUserToMainActivity() {

        Intent intent = new Intent(PhonLoginActivity.this,MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
        startActivity(intent);
        //finish();
    }

}

