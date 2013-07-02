package com.mamewo.lib.podcast_parser;

import java.io.Serializable;
import java.net.URL;

import android.graphics.drawable.BitmapDrawable;

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
	transient public BitmapDrawable icon_;

	public PodcastInfo(String title, URL url, BitmapDrawable icon, boolean enabled) {
		title_ = title;
		url_ = url;
		icon_ = icon;
		enabled_ = enabled;
	}
}
