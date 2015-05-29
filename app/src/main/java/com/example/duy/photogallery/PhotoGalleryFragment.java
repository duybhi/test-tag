package com.example.duy.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Duy on 5/17/2015.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    GridView mGridView;
    ArrayList<GalleryItem> mItems;
    ThumbnailDownloader<ImageView> mThumbnailThread;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItem().execute();

        mThumbnailThread = new ThumbnailDownloader();
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        setupAdapter();
        return v;
    }

    private class FetchItem extends AsyncTask <Void, Void, ArrayList<GalleryItem>>{

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... voids) {
            return new FlickrFetchr().fetchItem();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items){
            mItems = items;
            setupAdapter();
        }

    }

    private void setupAdapter(){
        if(getActivity() == null && mGridView == null){
            return;
        }
        if(mItems != null){
//            mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(), android.R.layout.simple_gallery_item, mItems));
            mGridView.setAdapter(new GalleryItemAdapter(mItems));

            if(mGridView.getCount() > 0){
                Toast.makeText(getActivity(), "co count: " + mGridView.getCount(), Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getActivity(), "Eo co ti child nao luon ", Toast.LENGTH_LONG).show();
            }

        } else {
            mGridView.setAdapter(null);
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem>{

        public GalleryItemAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }
            ImageView img = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            img.setImageResource(R.drawable.brian_up_close);
            GalleryItem item = getItem(position);
            mThumbnailThread.queueThumbnail(img, item.getmUrl());
            return convertView;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbnailThread.quit();
        Log.d(TAG, "bacground thread quit");
    }
}
