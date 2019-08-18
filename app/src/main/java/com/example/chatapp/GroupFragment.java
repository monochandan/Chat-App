package com.example.chatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

     private ListView list;
     private View group_fregment_view;
     private FirebaseAuth mAuth;
     private DatabaseReference groupRef;
     private ArrayAdapter<String>array_adapter;
     private ArrayList<String> list_of_groups = new ArrayList<>();
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        group_fregment_view = inflater.inflate(R.layout.fragment_group, container, false);
        groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        Initialize();
        RetriveAndDisplayGroups();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupeName = parent.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupeName);
                startActivity(groupChatIntent);
            }
        });
        return group_fregment_view;
    }



    private void Initialize() {
        list = group_fregment_view.findViewById(R.id.list_view);
        array_adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        list.setAdapter(array_adapter);
    }

    private void RetriveAndDisplayGroups() {

          groupRef.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  Set<String> set = new HashSet<>();
                  Iterator iterator = dataSnapshot.getChildren().iterator();

                  while(iterator.hasNext())
                  {
                                 set.add(((DataSnapshot)iterator.next()).getKey());
                  }

                  list_of_groups.clear();
                  list_of_groups.addAll(set);
                  array_adapter.notifyDataSetChanged();
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });

    }

}
