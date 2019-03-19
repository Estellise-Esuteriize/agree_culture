package com.capstone.agree_culture;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    // UI references.
    private TextView sign_up_link;

    private EditText sign_in_email;
    private EditText sign_in_password;

    private Button sign_in_button;

    private View progress_bar;

    private FirebaseAuth mAuth;

    private Context cont;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Toast.makeText(this, getResources().getString(R.string.user_exists), Toast.LENGTH_SHORT).show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, Toast.LENGTH_SHORT + 100);


        }

        cont = this;


        /**
         * Toolbar
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Set up the login form.

        sign_in_email = (EditText) findViewById(R.id.email);
        sign_in_password = (EditText) findViewById(R.id.password);
        sign_in_button = (Button) findViewById(R.id.sign_in_button);
        sign_up_link = (TextView) findViewById(R.id.sign_up_link);

        progress_bar = findViewById(R.id.progress_bar);

        sign_up_link.setOnClickListener(new SignUpLink());

        sign_in_button.setOnClickListener(new SignIn());

    }


    private void signIn(){

        final String email = sign_in_email.getText().toString();
        final String password = sign_in_password.getText().toString();

        progress_bar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(cont, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
                else{

                    progress_bar.setVisibility(View.GONE);

                    try{
                        throw task.getException();
                    }
                    catch (Exception ex){
                        Toast.makeText(cont, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    class SignIn implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            signIn();
        }
    }

    class SignUpLink implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(cont, SignUpActivity.class);
            startActivity(intent);
        }
    }


}

