package com.example.duy.photogallery;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Duy on 5/17/2015.
 */
public class FlickrFetchr {
	/* this is a fucking tag v1.0 */
    public static final String TAG = "FlickrFetchr";
    private static final String ENDPOINT = "http://api.flickr.com/services/rest/";
    private static final String API_KEY = "yourApiKeyHere";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String PARAM_EXTRAS = "extras";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String XML_PHOTO = "photo";

    byte[] getUrlBytes(String urlSpec) throws IOException{
        /* lay raw data tu link ve duoi dang array of byte */
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // tao connect toi web

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while((byteRead = in.read(buffer)) > 0){
                out.write(buffer, 0, byteRead);
            }
            out.close();

            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<GalleryItem> fetchItem(){
        ArrayList<GalleryItem> items = new ArrayList<>();
        try {
            String url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GET_RECENT)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                    .build().toString();
            String xmlString = getUrl(url);
            Log.d(TAG, "receive Xml: " + xmlString);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            parseItem(items, parser);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "failed to receive xml");
        } catch (XmlPullParserException e) {
            Log.d(TAG, "failed to parse items");
        }
        return items;
    }

    public void parseItem(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException{
        int eventType = parser.next();
        while(eventType != XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())){
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);

                GalleryItem item = new GalleryItem();
                item.setmUrl(smallUrl);
                item.setmCaption(caption);
                item.setmId(id);
            }

            eventType = parser.next();
        }
    }
}
