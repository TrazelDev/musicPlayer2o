package com.example.musicplayer2o.Authentication;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public abstract class Authenticator
{
    public interface OnAuthenticationAttempt { public void execute(@NonNull Task<AuthResult> loginAttempt); }





    // Singleton functionality:
    private static volatile Authenticator m_instance; // volatile - ensures that everything will be handled correctly with threads
    public static Authenticator getInstance()
    {
        if(m_instance == null) m_instance = FirebaseAuthenticator.getFirebaseAuth();
        return m_instance;
    }





    // Identification in application databases:
    public abstract String getUserEmail();
    public abstract String getUserId();





    // Login functionality:
    public abstract boolean isUserConnected();
    public abstract void createUser(String email, String password, OnAuthenticationAttempt registerAttemptActions);
    public abstract void logIntoAccount(String email, String password, OnAuthenticationAttempt loginAttemptActions);
    public abstract void signOut();
}
