package com.example.duy.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by Duy on 5/17/2015.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
}
