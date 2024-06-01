package com.example.musicplayer2o.Authentication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface OnLoginAttempt
{
    public void onLoginAttempt(@NonNull Task<AuthResult> loginAttempt);
}
