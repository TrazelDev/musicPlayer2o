package com.example.musicplayer2o.Authentication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthenticator extends Authenticator
{
    // Singleton functionality:
    private FirebaseAuthenticator() { }
    public static Authenticator getFirebaseAuth() { return new FirebaseAuthenticator(); }





    // Identification in application databases:
    @Override
    public String getUserEmail() { return FirebaseAuth.getInstance().getCurrentUser().getEmail(); }

    @Override
    public String getUserId() { return FirebaseAuth.getInstance().getCurrentUser().getUid(); }





    // Login functionality:
    @Override
    public boolean isUserConnected() { return FirebaseAuth.getInstance().getCurrentUser() != null; }

    @Override
    public void createUser(String email, String password, OnAuthenticationAttempt registerAttemptActions)
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> { registerAttemptActions.execute(task); });
    }
    @Override
    public void logIntoAccount(String email, String password, OnAuthenticationAttempt loginAttemptActions)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> { loginAttemptActions.execute(task); });
    }
    @Override
    public void signOut() { FirebaseAuth.getInstance().signOut(); }
}
