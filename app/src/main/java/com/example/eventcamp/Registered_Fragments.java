package com.example.eventcamp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by Sunilkumar on 07-11-2016.
 */

public class Registered_Fragments extends Fragment {

    //Firebase
    private DatabaseReference eventsRegistered;
    private FirebaseAuth autheUser;
    private FirebaseUser firebaseUser;

    private RecyclerView listOfEvents;
    private String eventID;

    public Registered_Fragments(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registered_fragment, container, false);

        //Connect to firebase references
        autheUser = FirebaseAuth.getInstance();
        firebaseUser = autheUser.getCurrentUser();
        eventsRegistered = FirebaseDatabase.getInstance()
                                           .getReference()
                                           .child("Registered_EventDetails")
                                           .child(firebaseUser.getUid());
        eventsRegistered.keepSynced(true);

        //RecyclerView References
        listOfEvents = (RecyclerView) view.findViewById(R.id.registered_recyclerview);
        listOfEvents.setHasFixedSize(true);
        listOfEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfEvents.smoothScrollToPosition(10);

        Log.i("From_RegisterOnCreate","OnCreate First");

        //Inflate the recycler view
        inflateRecyclerView();
        return view;
    }

    private void inflateRecyclerView() {
        FirebaseRecyclerAdapter<RetrievingEventDetails,registerEventListHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<RetrievingEventDetails, registerEventListHolder>(
                RetrievingEventDetails.class,
                R.layout.eventlist,
                registerEventListHolder.class,
                eventsRegistered

        ) {
            @Override
            protected void populateViewHolder(registerEventListHolder viewHolder, RetrievingEventDetails model, int position) {
                eventID = getRef(position).getKey();
                viewHolder.setTitle(model.getEventTitle());
                viewHolder.setSubTitle(model.getEventSubtitle());
                viewHolder.setDate(model.getEventDate());
                viewHolder.setPoster(getActivity(), model.getEventPoster());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayEventDetails();
                    }
                });
            }
        };
        listOfEvents.setAdapter(firebaseRecyclerAdapter);
    }

    private void displayEventDetails() {
        Intent eventDetails = new Intent(getActivity(),Event_Details.class);
        eventDetails.putExtra("EventID",eventID);
        eventDetails.putExtra("parentActivity","Register");
        startActivity(eventDetails);
    }

    public static class registerEventListHolder extends RecyclerView.ViewHolder{

        View itemView;

        public registerEventListHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }


        public void setTitle(String title) {
            TextView eventTitle = (TextView) itemView.findViewById(R.id.eventList_title);
            eventTitle.setText(title);
        }

        public void setSubTitle(String subTitle) {
            TextView eventSubtitle = (TextView) itemView.findViewById(R.id.eventList_subtitle);
            eventSubtitle.setText(subTitle);
        }

        public void setDate(String date) {
            TextView eventDate = (TextView) itemView.findViewById(R.id.eventList_date);
            eventDate.setText(date);
        }

        public void setPoster(Context ctx, String imageURL) {
            ImageView poster = (ImageView) itemView.findViewById(R.id.eventList_poster);
            Picasso.with(ctx).load(imageURL).into(poster);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("From_RegisterOnStart","OnStart First");
    }
}
