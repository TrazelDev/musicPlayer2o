package com.example.musicplayer2o.App.playSongs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.musicplayer2o.R;
import com.example.musicplayer2o.UriElements.Images.ImageUtils;
import com.example.musicplayer2o.UriElements.Songs.Playlist;
import com.example.musicplayer2o.UriElements.Songs.SongPlayer.AbstractSongPlayerService;
import com.example.musicplayer2o.UriElements.Songs.SongPlayer.MediaSongPlayerService;
import com.example.musicplayer2o.UriElements.Songs.SongPlayerUpdateCallbacks;

public class SongPlayingFragment extends Fragment implements SongPlayerUpdateCallbacks
{
    // Basic setup
    public SongPlayingFragment(Playlist playlist, SongPlayingFragment.BackToPlaylistsCallbackInterface backToPlaylistCallback)
    {
        m_serviceConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                AbstractSongPlayerService.LocalBinder binder = (AbstractSongPlayerService.LocalBinder)service;
                m_songPlayerService = binder.getService();
                m_songPlayerService.setPlaylist(playlist);
                // m_songPlayerService.addNewUiCallback(SongPlayingFragment.this);
                m_boundToService = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) { m_boundToService = false; }
        };


        m_backToPlaylistCallback = backToPlaylistCallback;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(getActivity(), MediaSongPlayerService.class);
        getActivity().bindService(serviceIntent, m_serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);

        setupViewsById(view);
        setupPlayOrPauseListener();
        setupOnUserChangingSongTimePointListener();
        setupGoBackToPlaylistBtn();
        // if(m_boundToService) m_songPlayerService.forceUiUpdate();

        return view;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        // if(m_boundToService) m_songPlayerService.forceUiUpdate();
    }
    private void setupViewsById(View view)
    {
        m_playOrPause = view.findViewById(R.id.playPauseIcon);
        m_backBtn = view.findViewById(R.id.backToPlaylistBtn);
        m_songPicture = view.findViewById(R.id.songImage);
        m_songCurrDuration = view.findViewById(R.id.songCurrLength);
        m_songMaxDuration = view.findViewById(R.id.songMaxLength);
        m_songSeekbarProgress = view.findViewById(R.id.songSeekBar);
    }
    private void setupPlayOrPauseListener()
    {
        m_playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { m_songPlayerService.playOrPause(); }
        });
    }
    private void setupOnUserChangingSongTimePointListener()
    {
        m_songSeekbarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int songProgressPercentage, boolean fromUser)
            {
                // if(fromUser) { m_songPlayerService.changeSongPlayingPoint(songProgressPercentage); }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
    public void setupGoBackToPlaylistBtn()
    {
        m_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getActivity().unbindService(m_serviceConnection);
                m_backToPlaylistCallback.execute();
            }
        });
    }




    // Update callbacks for song playing service:
    @Override
    public void setMaxDuration(String formattedMaxDuration) { m_songMaxDuration.setText(formattedMaxDuration); }
    @Override
    public void setSongDurationPassed(String formattedSongDurationPassed) { m_songCurrDuration.setText(formattedSongDurationPassed); }
    @Override
    public void setSongImage(Uri imageUri) { ImageUtils.loadImageDynamically(requireContext(), m_songPicture, imageUri, R.drawable.default_image); }
    @Override
    public void setSongPercentagePassed(int songPercentagePassed) { m_songSeekbarProgress.setProgress(songPercentagePassed); }
    @Override
    public void setIsSongPlaying(boolean isSongPlaying)
    {
        if (isSongPlaying) m_playOrPause.setImageResource(R.drawable.icon_pause);
        else m_playOrPause.setImageResource(R.drawable.icon_play);
    }





    private ImageButton m_playOrPause;
    private ImageButton m_backBtn;
    private ImageView m_songPicture;
    private TextView m_songMaxDuration;
    private TextView m_songCurrDuration;
    private SeekBar m_songSeekbarProgress;
    private BackToPlaylistsCallbackInterface m_backToPlaylistCallback;
    private boolean m_boundToService = false;
    private AbstractSongPlayerService m_songPlayerService;
    private ServiceConnection m_serviceConnection;
    public interface BackToPlaylistsCallbackInterface { void execute(); }
}
