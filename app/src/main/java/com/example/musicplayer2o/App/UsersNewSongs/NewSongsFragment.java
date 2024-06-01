package com.example.musicplayer2o.App.UsersNewSongs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicplayer2o.App.UsersNewSongs.LocalSongUpload.LocalSongUploadFragment;
import com.example.musicplayer2o.App.UsersNewSongs.RemoteSongUpload.AppPoolSongUploadFragment;
import com.example.musicplayer2o.R;

public class NewSongsFragment extends Fragment
{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_new_songs, container, false);
        setupViewsById(view);
        setupFragmentsTransactions();

        return view;
    }

    void setupViewsById(View view)
    {
        m_uploadSongScreen = view.findViewById(R.id.uploadSongButton);
        m_viewAppPoolScreenUpload = view.findViewById(R.id.viewSongs);
        m_screenTitle = view.findViewById(R.id.frameSpecifierText);
    }

    void setupFragmentsTransactions()
    {
        if (m_appSongPoolUploadFragment == null) m_appSongPoolUploadFragment = new AppPoolSongUploadFragment();
        if (m_localSongUploadFragment == null) m_localSongUploadFragment = new LocalSongUploadFragment();

        m_uploadSongScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSongUploadMethod(m_localSongUploadFragment);
            }
        });

        m_viewAppPoolScreenUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSongUploadMethod(m_appSongPoolUploadFragment);
            }
        });
        setSongUploadMethod(m_localSongUploadFragment);
    }
    private void setSongUploadMethod(Fragment fragment)
    {
        if(fragment == m_localSongUploadFragment) m_screenTitle.setText("Upload Songs:");
        if(fragment == m_appSongPoolUploadFragment) m_screenTitle.setText("View App's Songs:");

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.uploadSongMethodId, fragment);

        // Adding the transaction to the backstack which make it so the fragment state is being stored and the functions
        // of onCreate and onCreateView are not recalled when the fragment is switched to again:
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private AppPoolSongUploadFragment m_appSongPoolUploadFragment = null;
    private LocalSongUploadFragment m_localSongUploadFragment = null;
    private ImageButton m_uploadSongScreen;
    private ImageButton m_viewAppPoolScreenUpload;

    private TextView m_screenTitle;
}
