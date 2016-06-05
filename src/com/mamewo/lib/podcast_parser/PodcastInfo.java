package com.mamewo.lib.podcast_parser;

import java.io.Serializable;
import java.net.URL;

//import android.graphics.drawable.BitmapDrawable;

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
	//transient public BitmapDrawable icon_;
    public URL iconURL_;

	// public PodcastInfo(String title, URL url, BitmapDrawable icon, boolean enabled) {
	// 	title_ = title;
	// 	url_ = url;
	// 	icon_ = icon;
	// 	enabled_ = enabled;
	// }

    public PodcastInfo(String title, URL url, URL iconURL, boolean enabled) {
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

    //null
    // public BitmapDrawable getIcon(){
    //     return icon_;
    // }

    public URL getIconURL(){
        return iconURL_;
    }

    public void setIconURL(URL url){
        iconURL_ = url;
    }

    public String getIconURLString(){
        if(null == iconURL_){
            return null;
        }
        return iconURL_.toString();
    }
}
