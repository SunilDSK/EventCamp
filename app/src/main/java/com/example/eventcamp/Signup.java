package com.example.eventcamp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends AppCompatActivity {

    private EditText name,emailid,password,confirm_password;
    private TextInputLayout inputName,inputEmailid,inputpassword,inputconfirm_password;
    private Button details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        //EditText references
        name = (EditText) findViewById(R.id.signup_username);
        emailid = (EditText) findViewById(R.id.signup_email);
        password = (EditText) findViewById(R.id.signup_password);
        confirm_password = (EditText) findViewById(R.id.signup_confirmpassword);

        //TextInputLayout references
        inputName = (TextInputLayout) findViewById(R.id.input_layout_signup_username);
        inputEmailid = (TextInputLayout) findViewById(R.id.input_layout_signup_email);
        inputpassword = (TextInputLayout) findViewById(R.id.input_layout_signup_password);
        inputconfirm_password = (TextInputLayout) findViewById(R.id.input_layout_signup_confirmpassword);


    }

    public void onClickSignup(View view){
        if (submitForm()) {
            Intent details = new Intent(this,Signup_detail.class);
            details.putExtra("name",name.getText().toString().trim());
            details.putExtra("emailid",emailid.getText().toString().trim());
            details.putExtra("Password",password.getText().toString().trim());
            startActivity(details);
        }

    }

    private boolean submitForm() {
        String confirmPassword = confirm_password.getText().toString().trim();
        String passwor = password.getText().toString().trim();
        if ((!validateEmail()) || (!validatePassword()))
            return false;
        if(!passwor.equals(confirmPassword)){
            inputconfirm_password.setError("Password did not match");
            inputpassword.setError("Password did not match");
            requestFocus(confirm_password);
            return  false;
        }else {
            inputconfirm_password.setErrorEnabled(false);
            inputpassword.setErrorEnabled(false);
        }

        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean validateEmail() {
        String email = emailid.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmailid.setError("Invalid Email Id");
            requestFocus(emailid);
            return false;
        } else {
            inputEmailid.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        String passwordstr = password.getText().toString().trim();
        if ( passwordstr.isEmpty() || passwordstr.length()<8 ) {
            inputpassword.setError("Please enter password of length 8 or more characters.");
            requestFocus(emailid);
            return false;
        } else {
            inputpassword.setErrorEnabled(false);
        }
        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
