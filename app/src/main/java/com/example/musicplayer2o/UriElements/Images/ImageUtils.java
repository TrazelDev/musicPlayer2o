package com.example.musicplayer2o.UriElements.Images;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.musicplayer2o.R;

import java.util.Map;
import java.util.WeakHashMap;


public class ImageUtils
{
    public static void loadImageDynamically(Context context, ImageView imageView, Uri imageUri, int defaultDrawableImageResource)
    {
        Glide.with(context)
                .load(imageUri)
                .placeholder(defaultDrawableImageResource)
                .error(defaultDrawableImageResource)
                .into(imageView);
    }
}
