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

public class Register extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Button btnRegister, btnLinkToLoginScreen;
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnLinkToLoginScreen = (Button)findViewById(R.id.btnLinkToLoginScreen);
        etEmail = (EditText)findViewById(R.id.etRegisterEmail);
        etPassword = (EditText)findViewById(R.id.etRegisterPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            // means user is already logged in
            // so close this activity
            finish();

            // can open another activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        EventHandler eventHandler = new EventHandler();

        btnLinkToLoginScreen.setOnClickListener(eventHandler);
        btnRegister.setOnClickListener(eventHandler);
    }

    private void registerUser(){
        // getting email and password from et
        String email = String.valueOf(etEmail.getText());
        String password = String.valueOf(etPassword.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // checking if success
                    if (task.isSuccessful()){

                        // could start other activity
                        Toast.makeText(Register.this, "Register Successfully", Toast.LENGTH_LONG).show();

                        // can open another activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                    else{
                        Toast.makeText(Register.this, "Registration Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    class EventHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btnRegister:
                    registerUser();
                    break;
                case R.id.btnLinkToLoginScreen:
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    break;
            }
        }
    }
}
