package com.example.natarioapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FullScreen extends AppCompatActivity {

    DatabaseReference dbVdideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        Intent intent = getIntent();

        String title = intent.getExtras().getString("name");
        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();

        dbVdideo = FirebaseDatabase.getInstance().getReference("Video");

        Query query = FirebaseDatabase.getInstance().getReference("Video")
                .orderByChild("videoUrl")
                .equalTo(title);
    }
}