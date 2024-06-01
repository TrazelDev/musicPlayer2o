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

public class LoginActivity extends AppCompatActivity
{
    // Automatic Login if user is already logged:
    public void onStart()
    {
        super.onStart();
        if(Authenticator.getInstance().isUserConnected()) switchToMainApp();
    }
    private void switchToMainApp()
    {
        RealtimeDB.getInstance().loginUser();
        Intent registerIntent = new Intent(getApplicationContext(), MainAppActivity.class);
        startActivity(registerIntent);
        finish();
    }





    // Setup of listeners and settings of buttons and so on:
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_login);

        setupViewsById();
        setupListenerForSwitchToRegistering();
        setupLoginListener();
    }
    private void setupViewsById()
    {
        m_editTextEmail = findViewById(R.id.email);
        m_editTextPassword = findViewById(R.id.password);
        m_loginBtn = findViewById(R.id.btnLogin);
        m_switchToRegisterBtn = findViewById(R.id.registerScreenSwitch);
    }
    private void setupListenerForSwitchToRegistering()
    {
        m_switchToRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });
    }
    private void setupLoginListener()
    {
        m_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!areLoginFieldsFilled()) return;
                String email = String.valueOf(m_editTextEmail.getText());
                String password = String.valueOf(m_editTextPassword.getText());

                Authenticator.getInstance().logIntoAccount(email, password, task -> onLoginAttempt(task));
            }
        });
    }





    // Log in helpers:
    private boolean areLoginFieldsFilled()
    {
        String email = String.valueOf(m_editTextEmail.getText());
        String password = String.valueOf(m_editTextPassword.getText());

        if(TextUtils.isEmpty(email)) Toast.makeText(LoginActivity.this, "email missing", Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(password)) Toast.makeText(LoginActivity.this, "password missing", Toast.LENGTH_SHORT).show();

        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(password));
    }
    private void onLoginAttempt(@NonNull Task<AuthResult> loginAttempt)
    {
        if (loginAttempt.isSuccessful())
        {
            switchToMainApp();
            return;
        }
        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
    }





    // UI elements:
    TextInputEditText m_editTextEmail, m_editTextPassword;
    Button m_loginBtn;
    TextView m_switchToRegisterBtn;
}