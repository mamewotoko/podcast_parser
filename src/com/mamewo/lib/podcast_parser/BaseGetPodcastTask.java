package com.mamewo.lib.podcast_parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import okhttp3.Response;
import okhttp3.Request;
import okhttp3.OkHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class BaseGetPodcastTask
    extends AsyncTask<PodcastInfo, EpisodeInfo, Void>
{
    private Context context_;
    private OkHttpClient client_;
    private int limit_;
    private List<EpisodeInfo> buffer_;
    final static
    private int DEFAULT_BUFFER_SIZE = 10;
    final static
    private EpisodeInfo[] DUMMY_ARRAY = new EpisodeInfo[0];
    private int publishBufferSize_;

    final static
    private String TAG = "podparser";

    private enum TagName {
        TITLE, PUBDATE, LINK, NONE
    }

    /**
     * @param client OkHttpClient cache and timeout is configured
     */
    public BaseGetPodcastTask(Context context,
                              OkHttpClient client,
                              int limit,
                              int publishBufferSize)
    {
        context_ = context;
        limit_ = limit;
        client_ = client;
        buffer_ = new ArrayList<EpisodeInfo>();
        publishBufferSize_ = publishBufferSize;
    }

    public BaseGetPodcastTask(Context context, OkHttpClient client, int limit) {
        this(context, client, limit, DEFAULT_BUFFER_SIZE);
    }

    /**
     * @param podcastInfo podcast to load. podcastInfo.iconURL_ will be updated in this function
     */
    @Override
    protected Void doInBackground(PodcastInfo... podcastInfo) {
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
        }
        catch (XmlPullParserException e1) {
            Log.i(TAG, "cannot get xml parser", e1);
            return null;
        }
        for(int i = 0; i < podcastInfo.length; i++) {
            PodcastInfo pinfo = podcastInfo[i];
            if(isCancelled()){
                break;
            }
            if (!pinfo.enabled_) {
                continue;
            }
            URL url = pinfo.url_;
            Log.d(TAG, "get URL: " + pinfo.url_);
            InputStream is = null;
            try {
                Request request = new Request.Builder()
                    .url(url)
                    .build();
                Response response = client_.newCall(request).execute();
                if(response.code() == 401){
                    //TODO: queue auth request and retry
                    Log.i(TAG, "auth required: "+url);
                    continue;
                }
                if(!response.isSuccessful()){
                    Log.i(TAG, "http error: "+response.message()+", "+url.toString());
                    continue;
                }
                is = response.body().byteStream();
                //exclude UTF-8 bom
                is = new BOMInputStream(is, false);

                XmlPullParser parser = factory.newPullParser();
                //TODO: use reader or give correct encoding
                parser.setInput(is, "UTF-8");
                String title = null;
                String podcastURL = null;
                String iconURL = null;
                String pubdate = "";
                TagName tagName = TagName.NONE;
                int eventType;
                String link = null;
                int episodeCount = 0;

                while((eventType = parser.getEventType()) != XmlPullParser.END_DOCUMENT && !isCancelled()) {
                    if(eventType == XmlPullParser.START_TAG) {
                        String currentName = parser.getName();
                        if("title".equalsIgnoreCase(currentName)) {
                            tagName = TagName.TITLE;
                        }
                        else if("pubdate".equalsIgnoreCase(currentName)) {
                            tagName = TagName.PUBDATE;
                        }
                        else if("link".equalsIgnoreCase(currentName)) {
                            tagName = TagName.LINK;
                        }
                        else if("enclosure".equalsIgnoreCase(currentName)) {
                            //TODO: check type attribute
                            podcastURL = parser.getAttributeValue(null, "url");
                        }
                        else if("itunes:image".equalsIgnoreCase(currentName)) {
                            if(null == iconURL) {
                                iconURL = parser.getAttributeValue(null, "href");
                                pinfo.setIconURL(iconURL);
                            }
                        }
                    }
                    else if(eventType == XmlPullParser.TEXT) {
                        switch(tagName) {
                        case TITLE:
                            title = parser.getText();
                            break;
                        case PUBDATE:
                            //TODO: convert time zone
                            pubdate = parser.getText();
                            break;
                        case LINK:
                            link = parser.getText();
                            break;
                        default:
                            break;
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG) {
                        String currentName = parser.getName();
                        if("item".equalsIgnoreCase(currentName)) {
                            if(podcastURL != null) {
                                if(title == null) {
                                    title = podcastURL;
                                }
                                EpisodeInfo info = new EpisodeInfo(pinfo, podcastURL, title, pubdate, link, i);
                                buffer_.add(info);
                                if (buffer_.size() >= publishBufferSize_) {
                                    Log.d(TAG, "publish: "+podcastURL+" "+title);
                                    publish();
                                }
                            }
                            podcastURL = null;
                            title = null;
                            link = null;
                            episodeCount++;
                            if(0 < limit_ && limit_ <= episodeCount){
                                break;
                            }
                        }
                        else {
                            //always set to NONE, because there is no nested tag for now
                            tagName = TagName.NONE;
                        }
                    }
                    parser.next();
                }
                publish();
                response.close();
            }
            catch (IOException e) {
                Log.i(TAG, "IOException", e);
            }
            catch (XmlPullParserException e) {
                Log.i(TAG, "XmlPullParserException", e);
            }
            finally {
                Log.i(TAG, "finished loading podcast");
                if(null != is) {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        Log.i(TAG, "input stream cannot be close", e);
                    }
                }
            }
        }
        return null;
    }
    
    private void publish() {
        if (buffer_.isEmpty()) {
            return;
        }
        publishProgress(buffer_.toArray(DUMMY_ARRAY));
        buffer_.clear();
    }
}
