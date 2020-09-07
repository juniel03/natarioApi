package com.example.natarioapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewUploads extends AppCompatActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    private VideoAdapter adapter;
    private List<Vid> vidList;
    String name, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploads);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vidList = new ArrayList<>();
        adapter = new VideoAdapter(this, vidList);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();

        String title = intent.getExtras().getString("name");

        Query query = FirebaseDatabase.getInstance().getReference("Video")
                .orderByChild("uname")
                .equalTo(title);

        query.addListenerForSingleValueEvent(valueEventListener);
//        Log.d("tag", "main: " + userId);
    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            vidList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vid vids = snapshot.getValue(Vid.class);
                    vidList.add(vids);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}