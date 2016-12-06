package com.example.eventcamp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private Button login, singup;
    private EditText emailid, password;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private String login_emailid, login_password;
    SharedPreferences pref;
    SharedPreferences.Editor ed;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loging);

        //Button references
        login = (Button) findViewById(R.id.login);
        singup = (Button) findViewById(R.id.signup);

        //EditText references
        emailid = (EditText) findViewById(R.id.emailid);
        password = (EditText) findViewById(R.id.password);

        //TextInputLayout references
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_emailid);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        pref = getSharedPreferences("EventCampInfo", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false)) {
            Intent homeScreen = new Intent(this, Homescreen.class);
            startActivity(homeScreen);
            finish();
        }
    }

    public void firstTimeLogin() {
        ed = pref.edit();
        ed.putBoolean("activity_executed", true);
        ed.commit();
    }

    public void onClickLogin(View view) {
        if (submitForm()) {
            //logging in the user
            login_emailid = emailid.getText().toString().trim();
            login_password = password.getText().toString().trim();
            firebaseAuth.signInWithEmailAndPassword(login_emailid, login_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            //if the task is successfull
                            if (task.isSuccessful()) {
                                //start the profile activity
                                firstTimeLogin();
                                Toast.makeText(getApplicationContext(), "Welcome back", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), Homescreen.class));
                            } else {

                                if (task.getException().toString().contains("FirebaseNetworkException"))
                                    Toast.makeText(getApplicationContext(), "Please check the " +
                                            "network connection", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Please check your emailId " +
                                            "and password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void onClickSignup(View view) {
        Intent homeScreen = new Intent(this, Signup.class);
        startActivity(homeScreen);
    }

    private boolean submitForm() {
        if ((!validateEmail()) || (!validatePassword()))
            return false;

        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean validateEmail() {
        String email = emailid.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError("Invalid Email Id");
            requestFocus(emailid);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        String innerPassword = password.getText().toString().trim();
        if (innerPassword.isEmpty()||innerPassword.length()<8) {
            inputLayoutPassword.setError("Invalid Password: Password should be more than 8 characters.");
            requestFocus(password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    //Check for email_id format
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
