package com.example.eventcamp;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Event_Details extends AppCompatActivity {

    //Firebase
    FirebaseAuth userAuth;
    FirebaseUser firebaseUser;
    DatabaseReference eventData,registrationDetails;

    //UI
    private String eventID, eventTitle,posterUrl, parentActivity;
    private ImageView eventPoster;
    private FrameLayout eventPosterLayout;
    private TextView event_Desc,event_Venue,event_Date,register;
    private ListView registeredUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event__details);

        //Firebase
        userAuth = FirebaseAuth.getInstance();
        firebaseUser = userAuth.getCurrentUser();

        //Get the user details from previous activity
        Bundle extras = getIntent().getExtras();
        eventID = extras.getString("EventID");
        parentActivity = extras.getString("parentActivity");

        registrationDetails = FirebaseDatabase.getInstance()
                                              .getReference()
                                              .child("Users_Registered")
                                              .child(eventID);

        //UI References
        ScrollView scroller = (ScrollView) findViewById(R.id.aboutevent_userslist);
        eventPosterLayout = (FrameLayout) findViewById(R.id.eventdetails_eventposterLayout);
        eventPoster = (ImageView) eventPosterLayout.findViewById(R.id.eventdetails_eventposter);
        event_Desc = (TextView) findViewById(R.id.aboutevent_eventdesc);
        event_Venue = (TextView) findViewById(R.id.aboutevent_eventvenue);
        event_Date = (TextView) findViewById(R.id.aboutevent_eventdate);
        register = (TextView) findViewById(R.id.aboutevent_users);
        registeredUsers = (ListView) scroller.findViewById(R.id.aboutevent_usersdetails);

        if(parentActivity.equals("Register")){
            register.setVisibility(View.GONE);
            scroller.setVisibility(View.GONE);
            //registeredUsers.setVisibility(View.GONE);
        }else{
            FirebaseListAdapter<UserInformation> userList = new FirebaseListAdapter<UserInformation>(
                    this,
                    UserInformation.class,
                    R.layout.userdetails_listitem,
                    registrationDetails
            ) {
                @Override
                protected void populateView(View view, UserInformation model, int position) {
                    //UI elements references
                    TextView userName = (TextView) view.findViewById(R.id.eventdetails_userdetails_username);
                    TextView Branch = (TextView) view.findViewById(R.id.eventdetails_userdetails_branch);
                    TextView Year = (TextView) view.findViewById(R.id.eventdetails_userdetails_year);
                    //Set the data to user details
                    userName.setText(model.getUsername());
                    Branch.setText(model.getBranch());
                    Year.setText(model.getYear());
                }
            };
            registeredUsers.setAdapter(userList);
        }

        eventData = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("Events")
                                    .child(eventID);

        eventData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posterUrl = dataSnapshot.child("eventPoster").getValue().toString();
                eventTitle = (String) dataSnapshot.child("eventTitle").getValue();
                event_Date.setText(dataSnapshot.child("eventDate").getValue().toString());
                event_Venue.setText(dataSnapshot.child("eventVenue").getValue().toString());
                event_Desc.setText("\t"+dataSnapshot.child("eventDescription").getValue().toString());
                Picasso.with(getApplicationContext()).load(posterUrl).into(eventPoster);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Unable to read event details",Toast.LENGTH_LONG).show();
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(eventTitle);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.event_collapse_toolbar);
        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
        collapsingToolbar.setTitle(eventTitle);


    }
}
