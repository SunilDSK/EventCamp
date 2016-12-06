package com.example.eventcamp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class Homescreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //For Firebase
    private DatabaseReference ReceiveData, mDatabaseRegister, userDataReference;
    private DatabaseReference mRegisteredEventDetails;
    FirebaseAuth firebaseuser;
    FirebaseUser user;

    //For card
    private boolean mProcessClick = false;
    private String userName, EmailId;
    private RecyclerView eventList;
    private TextView username, useremail;
    private String[] userDatalocla = new String[3];

    //For shared preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor ed;

    private ShareActionProvider mShareActionProvider;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        //initializing firebase auth object
        firebaseuser = FirebaseAuth.getInstance();
        mRegisteredEventDetails = FirebaseDatabase.getInstance().getReference().child("Registered_EventDetails");
        mDatabaseRegister = FirebaseDatabase.getInstance().getReference().child("Users_Registered");
        mDatabaseRegister.keepSynced(true);
        mRegisteredEventDetails.keepSynced(true);

        //Navigation drawer menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);


        user = firebaseuser.getCurrentUser();

        userDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        ReceiveData = FirebaseDatabase.getInstance().getReference().child("Events");
        ReceiveData.keepSynced(true);

        username = (TextView) header.findViewById(R.id.profile_username);
        useremail = (TextView) header.findViewById(R.id.profile_emailid);

        //Get the recyclerView references
        eventList = (RecyclerView) findViewById(R.id.homescreen_recyclerview);
        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(this));


        if (getIntent().getExtras() != null) {
            //Get the user details from previous activity
            Bundle extras = getIntent().getExtras();
            userName = extras.getString("Name");
            EmailId = extras.getString("Email");
            username.setText(userName);
            useremail.setText(EmailId);
            updateUserProfile();
        } else {
            userName = user.getDisplayName();
            EmailId = user.getEmail();
            username.setText(userName);
            useremail.setText(EmailId);
        }


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
        collapsingToolbar.setTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        //Configure the FirebaseRecyclerAdapter
        FirebaseRecyclerAdapter<EventDetails, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EventDetails, EventViewHolder>(
                EventDetails.class,
                R.layout.card1,
                EventViewHolder.class,
                ReceiveData
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, final EventDetails model, int position) {
                final String event_key = getRef(position).getKey();
                viewHolder.setTitle(model.getEventTitle());
                viewHolder.setSubTitle(model.getEventSubtitle());
                viewHolder.setDate(model.getEventDate());
                viewHolder.setPoster(getApplicationContext(), model.getEventPoster());
                viewHolder.mRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!model.getUserId().equals(user.getUid())) {

                            //Add users to registered list
                            userDataReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot innerDataSnapshot) {
                                    userDatalocla[0] = innerDataSnapshot.child("Username").getValue().toString();
                                    userDatalocla[1] = innerDataSnapshot.child("Branch").getValue().toString();
                                    userDatalocla[2] = innerDataSnapshot.child("Year").getValue().toString();
                                    if (!mProcessClick) {

                                        mDatabaseRegister.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.child(event_key).hasChild(user.getUid())) {
                                                    mDatabaseRegister.child(event_key)
                                                            .child(user.getUid())
                                                            .child("Username")
                                                            .setValue(userDatalocla[0]);
                                                    mDatabaseRegister.child(event_key)
                                                            .child(user.getUid())
                                                            .child("Branch")
                                                            .setValue(userDatalocla[1]);
                                                    mDatabaseRegister.child(event_key)
                                                            .child(user.getUid())
                                                            .child("Year")
                                                            .setValue(userDatalocla[2]);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        mProcessClick = true;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Unable to the  user data", Toast.LENGTH_LONG).show();
                                }
                            });
                            //Add event to users registration list
                            mRegisteredEventDetails.child(user.getUid())
                                    .child(event_key)
                                    .child("eventTitle")
                                    .setValue(model.getEventTitle());
                            mRegisteredEventDetails.child(user.getUid())
                                    .child(event_key)
                                    .child("eventSubtitle")
                                    .setValue(model.getEventSubtitle());
                            mRegisteredEventDetails.child(user.getUid())
                                    .child(event_key)
                                    .child("eventDate")
                                    .setValue(model.getEventDate());
                            mRegisteredEventDetails.child(user.getUid())
                                    .child(event_key)
                                    .child("eventPoster")
                                    .setValue(model.getEventPoster());
                        }else {
                            Toast.makeText(getApplicationContext(),"You cannot register:\n " +
                                    "This event is organized by you.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                viewHolder.mKnowmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent eventDetails = new Intent(getApplicationContext(),Event_Details.class);
                        eventDetails.putExtra("EventID",event_key);
                        eventDetails.putExtra("parentActivity","Register");
                        startActivity(eventDetails);
                    }
                });
            }
        };

        //Set the adapter
        eventList.setAdapter(firebaseRecyclerAdapter);
    }


    private void updateUserProfile() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("Profile", "User profile updated.");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent profile = new Intent(this, Profile.class);
            startActivity(profile);
        } else if (id == R.id.nav_addevent) {
            Intent registration = new Intent(this, Myevents.class);
            startActivity(registration);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://firebasestorage.googleapis.com/v0/b/eventcamp-54ef3." +
                            "appspot.com/o/Events%2FEvents%2Fimage%3A51924?alt=media&token=6d49f703-9857-4355-8a8b-10496d3c2f7f");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_sendfeedback) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, "sunillangtad54@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "EventCamp Feedback");
            intent.putExtra(Intent.EXTRA_TEXT, "The following content describes the user " +
                    "experience and user's concern regarding the EventCamp App:");

            startActivity(Intent.createChooser(intent, "Send Email"));

        } else if (id == R.id.nav_about) {
            Intent about = new Intent(this,about.class);
            startActivity(about);
        } else if (id == R.id.nav_logout) {
            firebaseuser.signOut();
            //closing activity
            finish();
            SharedPreferences mysharedpref =
                    getSharedPreferences("EventCampInfo", Context.MODE_PRIVATE);
            mysharedpref.edit().clear().commit();
            //starting login activity
            startActivity(new Intent(this, Login.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        Button mRegister,mKnowmore;

        public EventViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mRegister = (Button) itemView.findViewById(R.id.card_eventRegister);
            mKnowmore = (Button) itemView.findViewById(R.id.card_eventKnow_more);
        }

        public void setTitle(String title) {
            TextView eventTitle = (TextView) itemView.findViewById(R.id.card_eventTitle);
            eventTitle.setText(title);
        }

        public void setSubTitle(String subTitle) {
            TextView eventSubtitle = (TextView) itemView.findViewById(R.id.card_eventSubTitle);
            eventSubtitle.setText(subTitle);
        }

        public void setDate(String date) {
            TextView eventDate = (TextView) itemView.findViewById(R.id.card_eventDate);
            eventDate.setText(date);
        }

        public void setPoster(Context ctx, String imageURL) {
            Log.i("Image_URL", imageURL);
            ImageView poster = (ImageView) itemView.findViewById(R.id.card_eventPoster);
            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(ctx).load(imageURL).into(poster);
            Log.i("Image_URL", imageURL);

        }
    }
}
