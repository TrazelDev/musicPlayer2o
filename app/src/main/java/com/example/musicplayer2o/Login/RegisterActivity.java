package com.example.musicplayer2o.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer2o.Authentication.Authenticator;
import com.example.musicplayer2o.Database.RealtimeDB.RealtimeDB;
import com.example.musicplayer2o.R;
import com.example.musicplayer2o.App.MainAppActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class RegisterActivity extends AppCompatActivity
{
    // Setup of listeners and settings of buttons and so on:
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupViewsById();
        setupListenerForSwitchToLoggingIn();
        setupRegisterListener();
    }
    public void setupViewsById()
    {
        m_editTextEmail = findViewById(R.id.email);
        m_editTextPassword = findViewById(R.id.password);
        m_registerBtn = findViewById(R.id.btnRegister);
        m_switchToLoginBtn = findViewById(R.id.loginScreenSwitch);
    }
    public void setupListenerForSwitchToLoggingIn()
    {
        m_switchToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
    public void setupRegisterListener()
    {
        m_registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!areRegisterFieldsFilled()) return;
                String email = String.valueOf(m_editTextEmail.getText());
                String password = String.valueOf(m_editTextPassword.getText());


                Authenticator.getInstance().createUser(email, password, task -> onRegisterAttempt(task));
            }
        });
    }





    // Register helpers:
    public boolean areRegisterFieldsFilled()
    {
        String email = String.valueOf(m_editTextEmail.getText());
        String password = String.valueOf(m_editTextPassword.getText());

        if(TextUtils.isEmpty(email)) Toast.makeText(RegisterActivity.this, "email missing", Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(password)) Toast.makeText(RegisterActivity.this, "password missing", Toast.LENGTH_SHORT).show();

        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(password));
    }
    public void onRegisterAttempt(@NonNull Task<AuthResult> registerAttempt)
    {
        if (!registerAttempt.isSuccessful())
        {
            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            return;
        }


        RealtimeDB.getInstance().createNewUser();
        Intent intent = new Intent(getApplicationContext(), MainAppActivity.class);
        startActivity(intent);
        finish();
    }





    // UI elements:
    TextInputEditText m_editTextEmail, m_editTextPassword;
    Button m_registerBtn;
    TextView m_switchToLoginBtn;
}