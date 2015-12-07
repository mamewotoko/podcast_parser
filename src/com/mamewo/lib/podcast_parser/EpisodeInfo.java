package com.mamewo.lib.podcast_parser;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

public class EpisodeInfo
	implements Serializable
{
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
			return pubdateobj_.toLocaleString();
		}
		return pubdate_;
	}

	public boolean equalEpisode(EpisodeInfo other){
		return url_.equals(other.url_) && pubdate_.equals(other.pubdate_);
	}
}
