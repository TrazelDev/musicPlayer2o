package com.example.musicplayer2o.UriElements.Songs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicplayer2o.UriElements.Images.ImageUtils;
import com.example.musicplayer2o.R;

import java.util.ArrayList;


public class GeneralSongListAdapter extends ArrayAdapter<Song>
{
    public interface EverySongAdditionalActions { public void execute(Song song); }
    public GeneralSongListAdapter(Context context, ArrayList<Song> songs, String songBtnActionInstruction, int songBtnActionIconResource,
                                  EverySongAdditionalActions additinalActions)
    {
        super(context, R.layout.song_list_element,songs);
        m_context = context;
        m_songs = songs;
        m_songBtnActionInstruction = songBtnActionInstruction;
        m_songBtnActionIconResource = songBtnActionIconResource;
        m_additionalActions = additinalActions;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View listItemView = convertView;
        if (listItemView == null) listItemView = LayoutInflater.from(m_context).inflate(R.layout.song_list_element, parent, false);

        Song currentSong = m_songs.get(position);
        setupSongName(listItemView, currentSong);
        setupSongImage(listItemView, currentSong);
        setupSongActionListener(listItemView);
        setupSongActionBtnInstructionText(listItemView);
        setupSongActionBtnIconResource(listItemView);
        m_additionalActions.execute(currentSong);

        return listItemView;
    }
    public void updateData(ArrayList<Song> newSongs)
    {
        m_songs.clear();
        m_songs.addAll(newSongs);
        notifyDataSetChanged();
    }
    public ArrayList<Song> getSongs() { return m_songs; }
    private void setupSongName(View songItemView, Song currSong)
    {
        TextView songNameTextView = songItemView.findViewById(R.id.itemSongName);
        songNameTextView.setText(currSong.getSongName());
    }

    private void setupSongImage(View songItemView, Song currSong)
    {
        ImageView songImageView = songItemView.findViewById(R.id.itemSongImage);

        if(currSong.hasPicture())
        {
            currSong.setOnRetrieveImageAction(songImageUri ->
            {
                ImageUtils.loadImageDynamically(m_context, songImageView, songImageUri, R.drawable.default_image);
            });
            return;
        }

         songImageView.setImageResource(R.drawable.default_image);
    }

    private void setupSongActionListener(View songItemView)
    {
        ImageButton actionItemIcon = songItemView.findViewById(R.id.actionBtn);
        actionItemIcon.setOnClickListener(m_onSaveSongListener);
    }
    private void setupSongActionBtnInstructionText(View songItemView)
    {
        TextView actionBtnDescription = songItemView.findViewById(R.id.actionBtnDescription);
        actionBtnDescription.setText(m_songBtnActionInstruction);
    }
    private void setupSongActionBtnIconResource(View songItemView)
    {
        ImageButton itemPlayIcon = songItemView.findViewById(R.id.actionBtn);
        itemPlayIcon.setImageResource(m_songBtnActionIconResource);
    }
    public void uploadOnSongActionListener(View.OnClickListener listener) { this.m_onSaveSongListener = listener; }
    private final Context m_context;
    private final ArrayList<Song> m_songs;
    private View.OnClickListener m_onSaveSongListener;
    private String m_songBtnActionInstruction;
    private int m_songBtnActionIconResource;
    private EverySongAdditionalActions m_additionalActions;
}
