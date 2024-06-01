package com.example.musicplayer2o.App;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;


import com.example.musicplayer2o.App.Profile.UserProfileFragment;
import com.example.musicplayer2o.App.UsersNewSongs.NewSongsFragment;
import com.example.musicplayer2o.App.playSongs.SongPlayListFragment;
import com.example.musicplayer2o.App.playSongs.SongPlayingFragment;
import com.example.musicplayer2o.Database.RealtimeDB.RealtimeDB;
import com.example.musicplayer2o.R;
import com.example.musicplayer2o.UriElements.Songs.Playlist;
import com.example.musicplayer2o.UriElements.Songs.SongPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainAppActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        setupFragments();
        setupBottomBar();
    }

    void setupFragments()
    {
        if (m_songPlaylistFragment == null) m_songPlaylistFragment = new SongPlayListFragment((playlist) -> { transitionToPlayingSong(playlist); });
        if (m_newSongsFragment == null) m_newSongsFragment = new NewSongsFragment();
        if (m_userProfileFragment == null) m_userProfileFragment = new UserProfileFragment();

        loadFragment(m_songPlaylistFragment);
    }

    void setupBottomBar()
    {
        m_bottomNavigationView = findViewById(R.id.bottom_navigation);
        m_bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if (item.getItemId() == R.id.navigation_play)
                {
                    if(m_songPlayingFragment != null) loadFragment(m_songPlayingFragment);
                    else loadFragment(m_songPlaylistFragment);
                }
                else if (item.getItemId() == R.id.navigation_addSongs) loadFragment(m_newSongsFragment);
                else if (item.getItemId() == R.id.navigation_profile) loadFragment(m_userProfileFragment);
                else return false;

                return true;
            }
        });
    }


    private void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        // Adding the transaction to the backstack which make it so the fragment state is being state and the functions
        // of onCreate and onCreateView are not recalled when the fragment is switched to again:
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void transitionToPlayingSong(Playlist playlist)
    {
        m_songPlayingFragment = new SongPlayingFragment(playlist, () -> transactionToPlaylistFragment());
        loadFragment(m_songPlayingFragment);
    }

    public void transactionToPlaylistFragment()
    {
        m_songPlayingFragment = null;
        loadFragment(m_songPlaylistFragment);
    }
    private UserProfileFragment m_userProfileFragment = null;
    private NewSongsFragment m_newSongsFragment = null;
    private SongPlayListFragment m_songPlaylistFragment = null;
    private SongPlayingFragment m_songPlayingFragment = null;
    private BottomNavigationView m_bottomNavigationView;
}