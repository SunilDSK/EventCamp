package com.example.eventcamp;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static com.example.eventcamp.R.id.new_eventname;

public class AddNewEvent extends AppCompatActivity {

    //Ui elements
    private EditText eventName,eventSubtitle,eventDate,eventVenue,eventDescription;
    private TextInputLayout input_EventName,input_EventSubtitle,input_EventVenu,input_EventDescription;
    private Button submit;
    private ImageButton imageButton;
    private String eveName,eveSubtitle,eveDate,eveVenue,eveDesc,eventKey;
    private String imageUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressDialog progressDialog;

    //Firebase items
    private Uri uri;
    private DatabaseReference firebaseDatabase;
    private FirebaseUser firebaseUser;
    private StorageReference firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnewevent);

        //Progress bar
        progressDialog = new ProgressDialog(this);

        //Layout
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.registration);
        FrameLayout posterLayout = (FrameLayout) mainLayout.findViewById(R.id.posterFrame);

        //Firebase references
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        firebaseStorage = FirebaseStorage.getInstance().getReference().child("Events");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //All edit text references
        imageButton = (ImageButton) posterLayout.findViewById(R.id.imgButton);

        submit = (Button) findViewById(R.id.newevents_create);
        imageButton.setTag("R.drawable.ic_photo_size_select_actual_24dp");

        //All text Views
        eventName = (EditText) mainLayout.findViewById(new_eventname);
        eventSubtitle = (EditText) mainLayout.findViewById(R.id.new_eventsubtitle);
        eventDate = (EditText) mainLayout.findViewById(R.id.new_event_date);
        eventVenue = (EditText) mainLayout.findViewById(R.id.new_eventvenue);
        eventDescription = (EditText) mainLayout.findViewById(R.id.new_eventDescription);

        //All the text in put layout
        input_EventName = (TextInputLayout) mainLayout.findViewById(R.id.input_layout_EventName);
        input_EventSubtitle = (TextInputLayout) mainLayout.findViewById(R.id.input_layout_EventSubtitle);
        input_EventVenu = (TextInputLayout) mainLayout.findViewById(R.id.input_layout_EventVenu);
        input_EventDescription = (TextInputLayout) mainLayout.findViewById(R.id.input_layout_EventDescription);




        //This listener will add event details to database
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventDetails();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



    }

    private void selectImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Poster"), PICK_IMAGE_REQUEST);
    }

    private void saveEventDetails() {

        if(submitItems() && isPoster()){
            progressDialog.setMessage("CREATING EVENT");
            progressDialog.show();
            //Get the text from all the edit text
            eveName = eventName.getText().toString().trim();
            eveSubtitle = eventSubtitle.getText().toString().trim();
            eveDate = eventDate.getText().toString().trim();
            eveVenue = eventVenue.getText().toString().trim();
            eveDesc = eventDescription.getText().toString().trim();

            //Add event poster to firebase
            StorageReference myFile =  firebaseStorage.child("Events").child(uri.getLastPathSegment());
            myFile.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Get the image url
                    imageUrl = taskSnapshot.getDownloadUrl().toString().trim();
                    String userid = firebaseUser.getUid().toString().trim();

                    EventDetails eventDetailsData = new EventDetails(eveName,eveDate,
                                            eveVenue,eveDesc,imageUrl,eveSubtitle,userid);

                    //Add user info to real time db
                    DatabaseReference oneEvent = firebaseDatabase.push();
                    eventKey = oneEvent.getKey();
                    oneEvent.setValue(eventDetailsData);
                    progressDialog.dismiss();
                    eventOrganized();
                    //Event created successfully
                    AddNewEvent.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof FirebaseNetworkException)
                        Toast.makeText(getApplicationContext(),"Please check your " +
                                "network connection",Toast.LENGTH_LONG).show();

                }
            });

        }else{
            Toast.makeText(this,"Please fill all the text fields and " +
                    "select the event poster",Toast.LENGTH_LONG).show();
        }
    }


    //This method wil check if all the Edit text fields are not empty
    private boolean submitItems() {
        //Get the text from all the edit text
        eveName = eventName.getText().toString().trim();
        eveSubtitle = eventSubtitle.getText().toString().trim();
        eveDate = eventDate.getText().toString().trim();
        eveVenue = eventVenue.getText().toString().trim();
        eveDesc = eventDescription.getText().toString().trim();
        if(eveName.isEmpty()){
            input_EventName.setError("Event name is required");
            eventName.requestFocus();
            return false;
        }else {
            input_EventName.setErrorEnabled(true);
        }
        if(eveSubtitle.isEmpty()){
            input_EventSubtitle.setError("Event subtitle is required");
            eventSubtitle.requestFocus();
            return false;
        }else {
            input_EventSubtitle.setErrorEnabled(true);
        }
        if(eveDate.isEmpty()){
            eventDate.setError("Event date is required");
            eventDate.requestFocus();
            return false;
        }
        if (eveVenue.isEmpty()){
            input_EventVenu.setError("Event venue is required");
            eventVenue.requestFocus();
            return false;
        }else {
            input_EventVenu.setErrorEnabled(true);
        }
        if(eveDesc.isEmpty()){
            input_EventDescription.setError("Event description is required");
            eventDescription.requestFocus();
            return false;
        }else {
            input_EventDescription.setErrorEnabled(true);
        }
        return true;
    }

    private boolean isPoster() {
        String imageTag = (String) imageButton.getTag();
        if(imageTag.equals("R.drawable.ic_photo_size_select_actual_24dp")){
            Toast.makeText(this,"Please upload the poster of the event",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void onStart() {
        super.onStart();
        final TextView editText = (TextView) findViewById(R.id.new_event_date);
        editText.setFocusable(true);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    DateDialog dialog = new DateDialog(view);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                TextView imageText = (TextView) findViewById(R.id.new_eventImageMessage);
                imageText.setVisibility(View.GONE);
                imageButton.setImageBitmap(bitmap);
                imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageButton.setTag("NewImage");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void eventOrganized() {
        DatabaseReference eventOrganized = FirebaseDatabase.getInstance().getReference().child("Organized_EventDetails");
        eventOrganized.child(firebaseUser.getUid())
                .child(eventKey)
                .child("eventTitle")
                .setValue(eveName);
        eventOrganized.child(firebaseUser.getUid())
                .child(eventKey)
                .child("eventSubtitle")
                .setValue(eveSubtitle);
        eventOrganized.child(firebaseUser.getUid())
                .child(eventKey)
                .child("eventDate")
                .setValue(eveDate);
        eventOrganized.child(firebaseUser.getUid())
                .child(eventKey)
                .child("eventPoster")
                .setValue(imageUrl);

    }
}
