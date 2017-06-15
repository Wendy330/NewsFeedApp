package com.yqian.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnLinkToRegisterScreen;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLinkToRegisterScreen = (Button)findViewById(R.id.btnLinkToRegisterScreen);

        firebaseAuth = FirebaseAuth.getInstance();

        // If the user is already logged in
        if (firebaseAuth.getCurrentUser() != null){
            finish();

            // Start new activity
        }

        EventHandle eventHandle = new EventHandle();

        btnLogin.setOnClickListener(eventHandle);
        btnLinkToRegisterScreen.setOnClickListener(eventHandle);
    }

    public void userLogin(){
        String email = String.valueOf(etEmail.getText());
        String password = String.valueOf(etPassword.getText());

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // start new activity
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(Login.this, "Log in fail", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    class EventHandle implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnLogin:
                    userLogin();
                    break;
                case R.id.btnLinkToRegisterScreen:
                    Intent i = new Intent(getApplicationContext(), Register.class);
                    startActivity(i);
                    finish();
                    break;
            }
        }
    }
}
