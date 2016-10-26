package com.mamewo.lib.podcast_parser;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Comparator;
import android.util.Log;

public class EpisodeInfo
    implements Serializable
{
    //read
    static final String DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss Z";
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN, Locale.US);

    static final
    String TAG = "podparser";
    
    private static final long serialVersionUID = 1L;
    final public String url_;
    final public String title_;
    final public String pubdate_;
    public Date pubdateobj_;
    final public String link_;
    final public int index_;

    public EpisodeInfo(String url, String title, String pubdate, String link, int index) {
        url_ = url;
        title_ = title;
        pubdate_ = pubdate;
        try {
            synchronized(DATE_FORMAT) {
                pubdateobj_ = DATE_FORMAT.parse(pubdate);
            }
        }
        catch (ParseException e) {
            pubdateobj_ = null;
            Log.d(TAG, "parse error: " + pubdate);
        }
        link_ = link;
        index_ = index;
    }
        
    public String getPubdateString(){
        if(null != pubdateobj_) {
            //return pubdateobj_.toLocaleString();
            synchronized(DATE_FORMAT){
                return DATE_FORMAT.format(pubdateobj_);
            }
        }
        return pubdate_;
    }

    public boolean equalEpisode(EpisodeInfo other){
        return url_.equals(other.url_) && pubdate_.equals(other.pubdate_);
    }

    // static
    // public class PubdateComparator
    //     implements Serializable,
    //                Comparator<EpisodeInfo>                   
    // {
    //     @Override
    //     public int compare(EpisodeInfo o1, EpisodeInfo o2){
    //         if(null == o1.pubdateobj_ && null == o1.pubdateobj_){
    //             //use occurence order or compare date string
    //             return 0;
    //         }
    //         if(null == o1.pubdateobj_){
    //             return -1;
    //         }
    //         if(null == o2.pubdateobj_){
    //             return 1;
    //         }
    //         return o1.pubdateobj_.compareTo(o2.pubdateobj_);
    //     }
    // }
    
    // static
    // public OccurenceOrderComparator
    //     extends Compator<EpisodeInfo>
    //     implements Serializable
    // {
    //     @Override
    //     public int compare(EpisodeInfo o1, EpisodeInfo o2){
    //         if(o1.index_ != o2.index_){
    //             return o1.index_ - o2.index_;
    //         }
    //         //
    //     }
    // }
}
