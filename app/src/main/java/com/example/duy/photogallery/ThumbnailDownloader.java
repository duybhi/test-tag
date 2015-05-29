package com.example.duy.photogallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by Duy on 5/26/2015.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    public ThumbnailDownloader() {
        super(TAG);
    }

    public void queueThumbnail(Token token, String url){
        Log.i(TAG, "Got an URL: " + url);
    }
}
