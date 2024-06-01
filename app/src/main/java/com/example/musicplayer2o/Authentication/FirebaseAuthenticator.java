package com.example.musicplayer2o.Authentication;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthenticator extends Authenticator
{
    private FirebaseAuthenticator() { }
    public static Authenticator getFirebaseAuth() { return new FirebaseAuthenticator(); }
    @Override
    public boolean isUserConnected() { return FirebaseAuth.getInstance().getCurrentUser() != null; }

    @Override
    public String getUserEmail() { return FirebaseAuth.getInstance().getCurrentUser().getEmail(); }

    @Override
    public String getUserId() { return FirebaseAuth.getInstance().getCurrentUser().getUid(); }

    @Override
    public void createUser(String email, String password, OnLoginAttempt loginAttemptActions)
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginAttemptActions.onLoginAttempt(task);
                });
    }

    @Override
    public void logIntoAccount(String email, String password, OnLoginAttempt loginAttemptActions)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginAttemptActions.onLoginAttempt(task);
                });
    }

    @Override
    public void signOut() { FirebaseAuth.getInstance().signOut(); }
}
