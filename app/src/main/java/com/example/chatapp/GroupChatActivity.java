package com.example.chatapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
private Toolbar toolbar;
private ImageButton send_mesg;
private EditText message;
private TextView displayMessage;
private ScrollView myScrollview;
private String CurentGroupName,currentUserID,currentUserName,currentDate,currentTime;
private FirebaseAuth mAuth;
private DatabaseReference userRef,groupnameRef,groupmessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        mAuth = FirebaseAuth.getInstance();

        CurentGroupName = getIntent().getExtras().get("groupName").toString();
        currentUserID = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupnameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(CurentGroupName);

        Toast.makeText(GroupChatActivity.this, CurentGroupName, Toast.LENGTH_SHORT).show();

        Initialize();
        getUserInfo();

        send_mesg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabase();

                message.setText("");

                myScrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupnameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    displayMessage(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void Initialize() {
        toolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(CurentGroupName);
        send_mesg = findViewById(R.id.send_message_button);
        message = findViewById(R.id.input_group_message);
        displayMessage = findViewById(R.id.display_message);
        myScrollview = findViewById(R.id.my_scroll_view);

    }

    private void getUserInfo()
    {
                userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                currentUserName = dataSnapshot.child("name").getValue().toString();
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void saveMessageInfoToDatabase() {

        String mssg = message.getText().toString();
        String messageKey = groupnameRef.push().getKey();

        if(TextUtils.isEmpty(mssg))
        {
            Toast.makeText(this, "empty field", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calenForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormate = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormate.format(calenForDate.getTime());

            Calendar calenForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormate = new SimpleDateFormat("hh:mm a");
            currentTime  = currentTimeFormate.format(calenForTime.getTime());

//            HashMap<String,Object> groupMessageKey = new HashMap<>();
//            groupnameRef.updateChildren(groupMessageKey);

            groupmessageKeyRef = groupnameRef.child(messageKey);

            HashMap<String,Object> mssginfoMap = new HashMap<>();

            mssginfoMap.put("name",currentUserName);
            mssginfoMap.put("message",mssg);
            mssginfoMap.put("time",currentTime);
            mssginfoMap.put("date",currentDate);

            groupmessageKeyRef.updateChildren(mssginfoMap);

        }

    }
    private void displayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())
        {
            String chatDate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String)((DataSnapshot)iterator.next()).getValue();
            String chatName = (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String)((DataSnapshot)iterator.next()).getValue();

            displayMessage.append(chatName +" :\n"+chatMessage +"\n"+chatTime + "    " +chatDate +"\n\n\n");
            myScrollview.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

}
