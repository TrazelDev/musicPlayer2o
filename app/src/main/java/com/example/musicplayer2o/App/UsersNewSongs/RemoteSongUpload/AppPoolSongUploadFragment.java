package com.example.musicplayer2o.App.UsersNewSongs.RemoteSongUpload;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.musicplayer2o.Database.RealtimeDB.RealtimeDB;
import com.example.musicplayer2o.R;
import com.example.musicplayer2o.UriElements.Songs.GeneralSongListAdapter;
import com.example.musicplayer2o.UriElements.Songs.Song;

import java.util.ArrayList;


public class AppPoolSongUploadFragment extends Fragment
{
    public AppPoolSongUploadFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_app_pool_song_upload, container, false);

        setupViewsById(view);
        setupSongListAdapter();
        setupSongList();

        return view;
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        destroySongDataListeners();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        setupSongList();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        destroySongDataListeners();
    }
    private void setupViewsById(View view) { m_listView = view.findViewById(R.id.songListView); }

    private void setupSongList()
    {
        m_userSongsIds = new ArrayList<>();
        m_allSongs = new ArrayList<>();

        RealtimeDB.getInstance().setupOnUsersSongsChangedCallback(
                userSongsIds ->
                {
                    m_userSongsIds = userSongsIds;
                    m_allNonUserSongs = Song.generateNonUserSongList(m_allSongs, m_userSongsIds);
                    m_adapter.updateData(m_allNonUserSongs);
                }
        );

        RealtimeDB.getInstance().setupOnSongsChangedCallback(
                appSongs ->
                {
                    m_allSongs = appSongs;
                    m_allNonUserSongs = Song.generateNonUserSongList(m_allSongs, m_userSongsIds);
                    m_adapter.updateData(m_allNonUserSongs);
                }
        );
    }

    private void setupSongListAdapter()
    {
        m_allNonUserSongs = new ArrayList<>();
        m_adapter = new GeneralSongListAdapter(requireContext(), m_allNonUserSongs,
                "Add to your songs:", R.drawable.icon_plus, (Song) -> { });

        m_listView.setAdapter(m_adapter);
        m_adapter.uploadOnSongActionListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int position = m_listView.getPositionForView(view);
                if (position == ListView.INVALID_POSITION) return;

                Song song = m_adapter.getItem(position);
                song.registerReferenceSongToUser();
            }
        });
    }
    // this is used so a certain code will not run while the fragment is paused and it will crush the programm
    private void destroySongDataListeners()
    {
        RealtimeDB.getInstance().removeUserSongsListener();
        RealtimeDB.getInstance().removeSongListener();
    }
    private ListView m_listView;
    private GeneralSongListAdapter m_adapter;

    // Handling a list of all the songs in the app that the user does not have:
    private ArrayList<String> m_userSongsIds;
    private ArrayList<Song> m_allSongs;
    private ArrayList<Song> m_allNonUserSongs;
}