package com.example.eventcamp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Signup_detail extends AppCompatActivity {

    private Spinner spinner_year, spinner_branch;
    private String userName, EmailId, Password, Branch, Year;


    private ProgressDialog progressDialog;

    //Defining firebase auth object
    private FirebaseAuth firebaseAuth;


    //For shared preferences
    SharedPreferences pref;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singup_details);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        pref = getSharedPreferences("EventCampInfo", Context.MODE_PRIVATE);

        Button signupbutton = (Button) findViewById(R.id.signup_b);

        //Get the user details from previous activity
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("name");
        EmailId = bundle.getString("emailid");
        Password = bundle.getString("Password");


        spinner_year = (Spinner) findViewById(R.id.spinner_year);
        spinner_branch = (Spinner) findViewById(R.id.spinner_branch);
        inflaterSpinners();

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser() {
        Branch = spinner_branch.getSelectedItem().toString();
        Year = spinner_year.getSelectedItem().toString();
        //displaying a progress dialog
        progressDialog.setMessage("Registering User please wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //checking if success
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            UserInformation userInformation = new UserInformation(userName, Branch, Year);

                            //Add user info to real time db
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                            databaseReference.child(user.getUid()).setValue(userInformation);

                            //Move to next activity
                            Intent home = new Intent(getApplicationContext(), Homescreen.class);
                            home.putExtra("Name", userName);
                            home.putExtra("Email", EmailId);
                            //This will clear the previous activities from the stack
                            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            //Save the activity_executed as true
                            ed = pref.edit();
                            ed.putBoolean("activity_executed", true);
                            ed.commit();
                            startActivity(home);
                            finish();
                        } else {

                            Toast.makeText(getApplicationContext(), "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
        Log.i("Encrypted_password",md5(Password));
    }

    private void inflaterSpinners() {

        //Inflate branch spinner
        ArrayAdapter<CharSequence> adapter_branch = ArrayAdapter.createFromResource(this,
                R.array.branchs, android.R.layout.simple_spinner_item);
        adapter_branch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(adapter_branch);

        //Inflate year spinner
        ArrayAdapter<CharSequence> adapter_year = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(adapter_year);
    }

    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }
}
