package com.mamewo.lib.podcast_parser;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.net.PasswordAuthentication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class SimpleAuthenticator {
    static
    private Map<String, AuthInfo> authCache_ = null;

	final static
	private String TAG = "podparser";
    
    final static
    private String AUTH_FILENAME = "auth.json";

    static
    public class AuthInfo
    {
        public String user_;
        public String password_;
        
        public AuthInfo(String user, String password){
            user_ = user;
            password_ = password;
        }
    }
    
    // root_url,user,password,realm
    static
	public void initAuthCache(Context context) {
        authCache_ = new HashMap<String, AuthInfo>();
        File configFile = context.getFileStreamPath(AUTH_FILENAME);
        if(configFile.exists()){
            //load
            FileInputStream fis = null;
            BufferedReader reader = null;
            try {
                fis = context.openFileInput(AUTH_FILENAME);
                StringBuffer sb = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(fis));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                String json = sb.toString();
                List<PodcastInfo> list = new ArrayList<PodcastInfo>();
                JSONTokener tokener = new JSONTokener(json);
                JSONArray jsonArray = (JSONArray) tokener.nextValue();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject value = jsonArray.getJSONObject(i);
                    //TODO: check key existance
                    String rootURL  = value.getString("root_url");
                    String user = value.getString("user");
                    String password = value.getString("password");

                    long lastPassedTime = 0;
                    authCache_.put(rootURL, new AuthInfo(user, password));
                }
			}
			catch (IOException e) {
				Log.d(TAG, "IOException", e);
			}
            catch (JSONException e3) {
				Log.d(TAG, "JSONException", e3);
            }
            finally {
                try{
                    if(null != fis){
                        fis.close();
                    }
                    if(null != reader){
                        reader.close();
                    }
                }
                catch(IOException e2){
                    Log.d(TAG, "IOException", e2);
                }
            }
        }
    }

    static
	public void writeAuthCache(Context context) {
        JSONArray array = new JSONArray();

        try{
            for(Map.Entry<String, AuthInfo> entry: authCache_.entrySet()){
                String user = entry.getValue().user_;
                String password = entry.getValue().password_;
                String url = entry.getKey();
                
                JSONObject jsonValue = (new JSONObject())
                    .accumulate("root_url", url)
                    .accumulate("user", user)
                    .accumulate("password", password);
                array.put(jsonValue);
            }
        }
        catch(JSONException e){
            array = new JSONArray();
        }
		String json = array.toString();
		//Log.d(TAG, "JSON: " + json);
		FileOutputStream fos = null;
		try{
            fos = context.openFileOutput(AUTH_FILENAME, Context.MODE_PRIVATE);
			fos.write(json.getBytes());
		}
        catch(IOException e){
            Log.d(TAG, "IOException", e);
        }
		finally {
            try{
                if(null != fos){
                    fos.close();
                }
            }
            catch(IOException e2){
            }
		}
    }
   
}
