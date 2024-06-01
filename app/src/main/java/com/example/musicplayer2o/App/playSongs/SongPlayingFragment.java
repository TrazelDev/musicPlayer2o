package com.example.musicplayer2o.App.playSongs;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer2o.R;
import com.example.musicplayer2o.UriElements.Images.ImageUtils;
import com.example.musicplayer2o.UriElements.Songs.Playlist;
import com.example.musicplayer2o.UriElements.Songs.Song;
import com.example.musicplayer2o.UriElements.Songs.SongPlayer;


public class SongPlayingFragment extends Fragment
{
    public interface BackToPlaylistsCallbackInterface { void execute(); }
    public SongPlayingFragment(Playlist playlist, BackToPlaylistsCallbackInterface backToPlaylistCallback)
    {
        m_songPlayer = new SongPlayer(playlist);
        m_backToPlaylistCallback = backToPlaylistCallback;
        s_firstTimeOnFragment = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);

        setupViewsById(view);
        setupPlayOrPauseListener();
        setupOnUserChangingSongTimePointListener();
        setupBackToPlaylistBtnListener();

        if(!s_firstTimeOnFragment)
        {
            m_songPlayer.forceUpdate();
            updatePauseOrPlayBtn();
            return view;
        }

        s_firstTimeOnFragment = false;
        setupSongImageUpdater();
        setSongCurrTimePassedUpdater();
        setupSongMaxDurationAutomaticUpdater();

        return view;
    }


    private void setupViewsById(View view)
    {
        m_playOrPause = view.findViewById(R.id.play_pause_icon);
        m_songPicture = view.findViewById(R.id.songImage);
        m_songLengthText = view.findViewById(R.id.songCurrLength);
        m_songMaxDuration = view.findViewById(R.id.songMaxLength);
        m_songSeekbarProgress = view.findViewById(R.id.songSeekBar);
        m_backToPlaylist = view.findViewById(R.id.backToPlaylistBtn);
    }
    private void setupPlayOrPauseListener()
    {
        m_playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!m_songPlayer.isSongPlaying()) m_playOrPause.setImageResource(R.drawable.icon_pause);
                else m_playOrPause.setImageResource(R.drawable.icon_play);

                m_songPlayer.playOrPause();
            }
        });
    }

    private void setSongCurrTimePassedUpdater()
    {
        m_songTimeDalayHandler = new Handler();
        m_songCurrPointUpdater = new Runnable()
        {
            @Override
            public void run()
            {
                if (m_songPlayer.isSongPlaying())
                {
                    int songPlayedDurationSeconds = m_songPlayer.getSongDurationPlayedSeconds();
                    int songPlayedPercentage = m_songPlayer.getSongPercentagePassed();
                    m_songLengthText.setText(m_songPlayer.convertSongSecondsWithFormat(songPlayedDurationSeconds));
                    m_songSeekbarProgress.setProgress(songPlayedPercentage);
                }
                m_songTimeDalayHandler.postDelayed(this, 1000);
            }
        };

        m_songTimeDalayHandler.postDelayed(m_songCurrPointUpdater, 1);
    }

    private void setupOnUserChangingSongTimePointListener()
    {
        m_songSeekbarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int songProgressPercentage, boolean fromUser)
            {
                if(fromUser) { m_songPlayer.setupNewSongPlayingTimePoint(songProgressPercentage); }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }


    private void setupSongMaxDurationAutomaticUpdater()
    {
        m_songPlayer.setupSongsMaxDurationUpdater(
                songMaxLength -> { m_songMaxDuration.setText(songMaxLength); }
        );
    }

    private void setupSongImageUpdater()
    {
        m_songPlayer.setupSongPictureUpdater(imgUri -> ImageUtils.loadImageDynamically(requireContext(), m_songPicture, imgUri, R.drawable.default_image));
    }
    private void updatePauseOrPlayBtn()
    {
        if (m_songPlayer.isSongPlaying()) m_playOrPause.setImageResource(R.drawable.icon_pause);
        else m_playOrPause.setImageResource(R.drawable.icon_play);
    }
    private void updateSongPlayedTimeAndSeekBar()
    {
        if (m_songPlayer.isSongPlaying()) return;

        int songPlayedDurationSeconds = m_songPlayer.getSongDurationPlayedSeconds();
        int songPlayedPercentage = m_songPlayer.getSongPercentagePassed();
        m_songLengthText.setText(m_songPlayer.convertSongSecondsWithFormat(songPlayedDurationSeconds));
        m_songSeekbarProgress.setProgress(songPlayedPercentage);
    }
    private void setupBackToPlaylistBtnListener()
    {
        m_backToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                m_backToPlaylistCallback.execute();
                if (m_songPlayer.isSongPlaying()) m_songPlayer.playOrPause();
            }
        });
    }
    private static boolean s_firstTimeOnFragment = true;
    private SongPlayer m_songPlayer;
    private Handler m_songTimeDalayHandler;
    private Runnable m_songCurrPointUpdater;
    private ImageButton m_playOrPause;
    private ImageView m_songPicture;
    private TextView m_songLengthText;
    private TextView m_songMaxDuration;
    private SeekBar m_songSeekbarProgress;
    private ImageButton m_backToPlaylist;
    private BackToPlaylistsCallbackInterface m_backToPlaylistCallback;
}