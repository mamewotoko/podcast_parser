package com.mamewo.lib.podcast_parser;

import java.io.Serializable;
import java.net.URL;
import java.net.MalformedURLException;
import android.util.Log;

public class PodcastInfo 
    implements Serializable
{
    public enum Status {
        UNKNOWN,
        PUBLIC,
        AUTH_REQUIRED_LOCKED,
        AUTH_REQUIRED_UNLOCKED,
        ERROR
    }
    final static
    private String TAG = "podparser";
    
    private static final long serialVersionUID = 7613791894671950703L;
    private String title_;
    private URL url_;
    private boolean enabled_;
    private String iconURL_;

    //TODO: hold in secure area?
    private String username_;
    private String password_;
    private Status lastStatus_;
    
    public PodcastInfo(String title, URL url, String iconURL, boolean enabled, String username, String password, Status status) {
        title_ = title;
        if(null == title_){
            title_ = "";
        }
        url_ = url;
        iconURL_ = iconURL;
        enabled_ = enabled;

        username_ = username;
        password_ = password;
        lastStatus_ = status;
    }

    public PodcastInfo(String title, URL url, String iconURL, boolean enabled) {
        this(title, url, iconURL, enabled, null, null, Status.UNKNOWN);
    }
    
    public String getTitle(){
        return title_;
    }

    public URL getURL(){
        return url_;
    }
    
    public URL getURLWithAuthInfo(){
        if(null != username_ && null != password_){
            //url_.setUserInfo(username_+":"+password_);
            try{
                return new URL(addUserInfo(url_.toString()));
            }
            catch (MalformedURLException e){
                return null;
            }
        }
        return url_;
    }

    public boolean getEnabled(){
        return enabled_;
    }

    public void setEnabled(boolean enabled){
        enabled_ = enabled;
    }
    
    public String getIconURL(){
        return iconURL_;
    }

    public String getIconURLWithAuthInfo(){
        return addUserInfo(iconURL_);
    }

    public void setIconURL(String url){
        iconURL_ = url;
    }

    public String getUsername(){
        return username_;
    }

    public void setUsername(String username){
        username_ = username;
    }

    public String getPassword(){
        return password_;
    }

    public void setPassword(String password){
        password_ = password;
    }

    public String addUserInfo(String url){
        Log.d(TAG, "addUserInfo: " + url + " " + username_ + " " + password_);
        if(null == url || null == username_ || null == password_){
            return url;
        }
        int pos = url.indexOf("://");
        return url.substring(0, pos) + "://" + username_ +":"+password_+"@"+url.substring(pos+3);
    }

    public Status getStatus(){
        return lastStatus_;
    }

    public void setStatus(Status status){
        lastStatus_ = status;
    }
}
