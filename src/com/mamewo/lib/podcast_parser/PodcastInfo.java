package com.mamewo.lib.podcast_parser;

import java.io.Serializable;
import java.net.URL;

public class PodcastInfo 
	implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7613791894671950703L;
	public String title_;
	public URL url_;
	public boolean enabled_;
    public String iconURL_;

    public PodcastInfo(String title, URL url, String iconURL, boolean enabled) {
		title_ = title;
		url_ = url;
		iconURL_ = iconURL;
		enabled_ = enabled;
	}

    public String getTitle(){
        return title_;
    }

    public URL getURL(){
        return url_;
    }

    public boolean getEnabled(){
        return enabled_;
    }

    public String getIconURL(){
        return iconURL_;
    }

    public void setIconURL(String url){
        iconURL_ = url;
    }
}
