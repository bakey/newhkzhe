package com.hkzhe.wwtt.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	static public boolean isNetworkAvailable( Context c ) {
	    ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	       // if no network is available networkInfo 	will be null
	     // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	    	        return true;
	    }
	    return false;
	} 
	static public String readStream(InputStream in) {
		BufferedReader reader = null;
		String json_content = "";
		try {
		    reader = new BufferedReader(new InputStreamReader(in));
		    String line = "";
		    while ((line = reader.readLine()) != null) {
		    	json_content += line;
		    }
		    return json_content;
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    if (reader != null) {
		      try {
		        reader.close();
		      } catch (IOException e) {
		        e.printStackTrace();
		        }
		    }
		}
		return json_content;
	} 

}
