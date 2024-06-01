package com.example.musicplayer2o.App.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.musicplayer2o.Authentication.Authenticator;
import com.example.musicplayer2o.Login.LoginActivity;
import com.example.musicplayer2o.R;

public class UserProfileFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        setupViewsById(view);
        setupLogoutListener();

        return view;
    }

    void setupViewsById(View view)
    {
        m_logout = view.findViewById(R.id.logout);
        m_emailDisplay = view.findViewById(R.id.user_details);
        m_emailDisplay.setText(Authenticator.getInstance().getUserEmail());
    }

    void setupLogoutListener()
    {
        m_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Authenticator.getInstance().signOut();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        });
    }
    Button m_logout;
    TextView m_emailDisplay;
}