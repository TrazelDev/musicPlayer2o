package com.example.musicplayer2o.Authentication;


public abstract class Authenticator
{
    public static Authenticator getInstance()
    {
        if(m_instance == null) m_instance = FirebaseAuthenticator.getFirebaseAuth();
        return m_instance;
    }
    public abstract boolean isUserConnected();
    public abstract String getUserEmail();
    public abstract String getUserId();

    // login:
    public abstract void createUser(String email, String password, OnLoginAttempt loginAttemptActions);
    public abstract void logIntoAccount(String email, String password, OnLoginAttempt loginAttemptActions);
    public abstract void signOut();


    private static volatile Authenticator m_instance; // volatile - ensures that everything will be handled correctly with threads
}
