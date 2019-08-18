package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager myViewpager;
    private TabLayout myTablayout;
    private TabAccessorAdapter myTabAccessorAdapter;
     private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatApp");


        myViewpager  = (ViewPager)findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        myViewpager.setAdapter(myTabAccessorAdapter);

        myTablayout = (TabLayout)findViewById(R.id.main_tabs);
        myTablayout.setupWithViewPager(myViewpager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null)
        {
            sendUserToLoginActivity();
        }
        else
        {
            verifyUserExisence();
        }
    }

    private void verifyUserExisence() {
        String CurrentuserId = mAuth.getCurrentUser().getUid();

        rootRef.child("Users").child(CurrentuserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {

                        sendUserToSettingsActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.main_logout_option)
         {
             mAuth.signOut();
             sendUserToLoginActivity();
         }
         if(item.getItemId() == R.id.main_settings_option)
         {

               sendUserToSettingsActivity();

         }
        if(item.getItemId() == R.id.main_create_group_option)
        {
               RequestNewGrpup();
        }
         if(item.getItemId() == R.id.main_find_friends_option)
         {
               sendUserToFindFriendActivity();
         }

         return true;
    }

    private void RequestNewGrpup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Groupe Name");

        final EditText groupname = new EditText(MainActivity.this);

        groupname.setHint("e.g : coding cafe");

        builder.setView(groupname);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String grpName = groupname.getText().toString();
                if(TextUtils.isEmpty(grpName))
                {
                    Toast.makeText(MainActivity.this, "Please Wait Groupe Name...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroupe(grpName);
                }
            }
        });

        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               dialog.cancel();
            }
        });

        builder.show();

    }

    private void CreateNewGroupe(final String groupName) {
        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName+" created Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToSettingsActivity(){
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
        startActivity(intent);
        finish();
    }

    private void sendUserToFindFriendActivity(){
        Intent intent = new Intent(MainActivity.this,FIndFriendsActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//cannot go back by backButton
        startActivity(intent);
        //finish();
    }
}
