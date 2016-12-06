package com.example.eventcamp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    //Firebase
    FirebaseAuth userAuth;
    FirebaseUser firebaseUser;
    DatabaseReference userDate;

    TextView username, emailid, branch, year;
    String Username, Branch, Year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        //Firebase references
        userAuth = FirebaseAuth.getInstance();
        firebaseUser = userAuth.getCurrentUser();
        userDate = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid());

        //UI elements
        username = (TextView) findViewById(R.id.profile_username);
        emailid = (TextView) findViewById(R.id.profile_emailid);
        branch = (TextView) findViewById(R.id.profile_branch);
        year = (TextView) findViewById(R.id.profile_year);
        emailid.setText(firebaseUser.getEmail());
        username.setText(firebaseUser.getDisplayName());
        userDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("Username").getValue().toString());
                branch.setText(dataSnapshot.child("Branch").getValue().toString());
                year.setText(dataSnapshot.child("Year").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
