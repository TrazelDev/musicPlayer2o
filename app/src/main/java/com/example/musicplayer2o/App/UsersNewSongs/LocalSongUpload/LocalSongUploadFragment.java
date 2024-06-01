package com.example.musicplayer2o.App.UsersNewSongs.LocalSongUpload;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer2o.UriElements.Images.ImageUtils;
import com.example.musicplayer2o.UriElements.Songs.Song;
import com.example.musicplayer2o.R;
import com.google.android.material.textfield.TextInputEditText;

public class LocalSongUploadFragment extends Fragment
{
    public LocalSongUploadFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_local_song_upload, container, false);

        setupViewsById(view);
        setupImageProvider();
        setupSongProvider();
        setSongUploadListener();

        return view;
    }

    private void setupViewsById(View view)
    {
        m_currSongUploaded = view.findViewById(R.id.currentSongUploaded);
        m_uploadPictureBtn = view.findViewById(R.id.uploadPicture);
        m_uploadSongBtn = view.findViewById(R.id.uploadSong);
        m_sendSongBtn = view.findViewById(R.id.sendSong);
        m_noImageBox = view.findViewById(R.id.songNoWithNoPic);
        m_songName = view.findViewById(R.id.songName);
        m_userSongImageDisplay = view.findViewById(R.id.songImage);
    }
    private void setupImageProvider()
    {
        m_ImageProvider = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri ->
                {
                    if (!didUserChooseFile(uri)) return;

                    m_imageUri = uri;
                    ImageUtils.loadImageDynamically(requireContext(), m_userSongImageDisplay, m_imageUri, R.drawable.default_image);
                }
        );

        m_uploadPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { m_ImageProvider.launch(IMAGE_UPLOAD_FORMAT); }
        });
    }
    private void setupSongProvider()
    {
        m_SongProvider = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri ->
                {
                    if (!didUserChooseFile(uri)) return;

                    m_songUri = uri;
                    m_currSongUploaded.setText("song uploaded: " + getFileNameFromUri(uri));
                }
        );

        m_uploadSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { m_SongProvider.launch(SONG_UPLOAD_FORMAT); }
        });
    }
    private boolean didUserChooseFile(Uri uri) { return uri != null; }
    private String getFileNameFromUri(Uri uri)
    {
        return uri.getLastPathSegment();
    }

    private boolean isAllInformationForSongUploadAvailable()
    {
        String songsName = String.valueOf(m_songName.getText());

        if(m_imageUri == null && !m_noImageBox.isChecked())
            Toast.makeText(requireContext(), "pls upload an image or check the box for no image", Toast.LENGTH_SHORT).show();
        else if(m_songUri == null) Toast.makeText(requireContext(), "pls upload a song", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(songsName)) Toast.makeText(requireContext(), "pls enter the song name", Toast.LENGTH_SHORT).show();
        else return true;

        return false;
    }
    private void setSongUploadListener()
    {
        m_sendSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!isAllInformationForSongUploadAvailable()) return;

                Song song = new Song(m_songUri, m_songName.getText().toString(), !m_noImageBox.isChecked(), m_imageUri);
                Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show();
                song.uploadSong();
            }
        });

    }


    // UI elements:
    private Button m_uploadPictureBtn;
    private Button m_uploadSongBtn;
    private Button m_sendSongBtn;
    private CheckBox m_noImageBox;
    private TextView m_currSongUploaded;
    private TextInputEditText m_songName;
    private ImageView m_userSongImageDisplay;

    // Pictures and song ids, URI - Uniform Resource Identifier
    private Uri m_imageUri;
    private Uri m_songUri;

    // Activity for result ( opening an activity for taking photos and songs from the user )
    // It generates URI which are kind of like computer paths with the following structure: scheme:[//authority]path[?query][#fragment]
    private ActivityResultLauncher<String> m_ImageProvider;
    private ActivityResultLauncher<String> m_SongProvider;
    static final String IMAGE_UPLOAD_FORMAT = "image/*";
    static final String SONG_UPLOAD_FORMAT = "audio/*";
}