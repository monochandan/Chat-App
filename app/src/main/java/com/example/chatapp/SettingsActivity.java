package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText username,status;
    private CircleImageView circle;
    private String currentUserId,downlodeInageUrl;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private StorageReference userProfileImgRef;

    private ProgressDialog lodingBar;

    private Uri Imguri;

    private static final int request = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("User Profile Image");

        Initialized();
        username.setVisibility(View.INVISIBLE);
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        RetriveUserInfo();

        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,request);
            }
        });


    }




    private void Initialized() {
        updateAccountSettings = findViewById(R.id.update_settings_button);
        username = findViewById(R.id.set_user_name);
        status = findViewById(R.id.set_profile_status);
        circle = findViewById(R.id.set_profile_image);
        lodingBar = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == request && resultCode == RESULT_OK && data != null)
        {
            Imguri = data.getData();
            //circle.setImageURI(Imguri);
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

             if (resultCode == RESULT_OK)
             {
                 lodingBar.setTitle("Upload Image");
                 lodingBar.setMessage("Image Udating.Please Wait...");
                 lodingBar.setCanceledOnTouchOutside(false);
                 lodingBar.show();
                  Uri resultUri = result.getUri();
                  final StorageReference filePath = userProfileImgRef.child(currentUserId + ".jpg");//stoaring in firebase storage

//                 final UploadTask uploadTask = filePath.putFile(resultUri);
//
//                 uploadTask.addOnFailureListener(new OnFailureListener() {
//                     @Override
//                     public void onFailure(@NonNull Exception e) {
//                         String mssg = e.toString();
//                         Toast.makeText(SettingsActivity.this, "Error: " + mssg, Toast.LENGTH_SHORT).show();
//                     }
//                 }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                     @Override
//                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                         Toast.makeText(SettingsActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                         Task<Uri>urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                             @Override
//                             public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                if(!task.isSuccessful())
//                                {
//                                    throw  task.getException();
//                                }
//
//                                downlodeInageUrl = filePath.getDownloadUrl().toString();
//
//                                return filePath.getDownloadUrl();
//                             }
//                         }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                             @Override
//                             public void onComplete(@NonNull Task<Uri> task) {
//                                 if(task.isSuccessful())
//                                 {
//                                     Toast.makeText(SettingsActivity.this, "getting image url successfully", Toast.LENGTH_SHORT).show();
//                                 }
//                             }
//                         });
//                     }
//                 });

                  filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                          if(task.isSuccessful())
                          {
                              Toast.makeText(SettingsActivity.this, "profile image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                               final String downlodeUrl = task.getResult().getDownloadUrl().toString();

                               rootRef.child("Users").child(currentUserId).child("image")
                                       .setValue(downlodeUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful())
                                       {
                                           Toast.makeText(SettingsActivity.this, "image add to database Successfully", Toast.LENGTH_SHORT).show();
                                           lodingBar.dismiss();
                                       }
                                       else
                                       {
                                           String mssg = task.getException().toString();
                                           Toast.makeText(SettingsActivity.this, "Error: " + mssg, Toast.LENGTH_SHORT).show();
                                           lodingBar.dismiss();

                                       }
                                   }
                               });
                          }
                          else
                          {
                              String mssg = task.getException().toString();
                              Toast.makeText(SettingsActivity.this, "Error: " + mssg, Toast.LENGTH_SHORT).show();
                              lodingBar.dismiss();
                          }
                      }
                  });
             }
             else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
             {
                  Exception error = result.getError();
                  String excp = error.toString();

                 Toast.makeText(this, " Croop Error: " + excp, Toast.LENGTH_SHORT).show();
             }
        }

    }

    //profile update
    private void updateSettings() {
        String user = username.getText().toString();
        String stat = status.getText().toString();

        if(TextUtils.isEmpty(user))
        {
            Toast.makeText(this, "empty field", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(stat))
        {
            Toast.makeText(this, "empty field", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,String>ProfileMap = new HashMap<>();
            ProfileMap.put("uid",currentUserId);
            ProfileMap.put("name",user);
            ProfileMap.put("status",stat);

            rootRef.child("Users").child(currentUserId).setValue(ProfileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String msg = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error: "+msg, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    private void sendUserToMainActivity() {

        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
        startActivity(intent);
        finish();
    }

    private void RetriveUserInfo() {
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")))
                {
                    String retriveusername = dataSnapshot.child("name").getValue().toString();
                    String retrivestatus = dataSnapshot.child("status").getValue().toString();
                    String  retriveimage = dataSnapshot.child("image").getValue().toString();

                    username.setText(retriveusername);
                    status.setText(retrivestatus);
                    Toast.makeText(SettingsActivity.this, retriveimage, Toast.LENGTH_SHORT).show();
                    //Picasso.get().load(retriveimage).into(circle);
                    Picasso.get().load(retriveimage).into(circle);

                }
                else if(dataSnapshot.exists() && (dataSnapshot.hasChild("name")))
                {
                    String retriveusername = dataSnapshot.child("name").getValue().toString();
                    String retrivestatus = dataSnapshot.child("status").getValue().toString();

                    username.setText(retriveusername);
                    status.setText(retrivestatus);
                }
                else
                {
                    username.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please Set and Update Your profile Info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
