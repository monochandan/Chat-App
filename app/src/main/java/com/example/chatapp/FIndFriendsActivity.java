package com.example.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


public class FIndFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        viewList = findViewById(R.id.find_friends_recycleView);
        viewList.setLayoutManager(new LinearLayoutManager(this));


        toolbar = findViewById(R.id.find_friends_toolbar);
         setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setDisplayShowHomeEnabled(true);
         getSupportActionBar().setTitle("Find Friends");

    }


}
