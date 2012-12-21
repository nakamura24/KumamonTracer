/*
 * Copyright (C) 2012 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */

package jp.kumamontracer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTraceService extends Service {
	private static final String TAG = "GPSTraceService";
	private static final String TraceFileName = "Trace.txt";
	private static final long gps_minTime = 60 * 60 * 1000;
	private static final long gps_minDistance = 50;
	//private static final long gps_interval = 15 * 60 * 1000;

	@Override
	public IBinder onBind(Intent intent) {
	    return null;
	}

	@Override
	public void onCreate() {
	    super.onCreate();
	    Log.i(TAG, "onCreate");
    	try{
    		// Acquire a reference to the system Location Manager
    		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		// Register the listener with the Location Manager to receive location updates
    		locationManager.requestLocationUpdates(
            		LocationManager.GPS_PROVIDER, gps_minTime, gps_minDistance, mLocationListener);
    	}catch(Exception e){
    		ExceptionLog.Log(TAG, e);
    	}
	}
	  
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Log.i(TAG, "onDestroy");
    	try{
    		// Acquire a reference to the system Location Manager
    		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		// Remove the listener with the Location Manager to receive location updates
    		locationManager.removeUpdates(mLocationListener);
    	}catch(Exception e){
    		ExceptionLog.Log(TAG, e);
    	}
	}
	  
	private LocationListener mLocationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
			Log.i(TAG, "onLocationChanged");
        	try{
                if(location != null) {
                	double Longitude= location.getLongitude();
                	double Latitude	= location.getLatitude();
                	double Altitude	= location.getAltitude();
                	Date date = new Date();
                	String trace = String.valueOf(date.getTime()) +"," + String.valueOf(Longitude) +"," + 
                			String.valueOf(Latitude) +"," + String.valueOf(Altitude) + "\n";
                	FileOutputStream file = openFileOutput(TraceFileName, Context.MODE_PRIVATE|Context.MODE_APPEND);
            		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(file,"UTF-8"));
            		out.write(trace);
            		out.flush();
            		out.close();
        			file.close();
                	Log.d(TAG, "onLocationChanged - " + String.valueOf(Longitude) + " " + String.valueOf(Latitude) + " " + String.valueOf(Altitude));
                }
        	}catch(Exception e){
        		ExceptionLog.Log(TAG, e);
        	}
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    	Log.i(TAG, "onStatusChanged");
	    }

	    public void onProviderEnabled(String provider) {
	    	Log.i(TAG, "onProviderEnabled");
	    }

	    public void onProviderDisabled(String provider) {			
	    	Log.i(TAG, "onProviderDisabled");
	    }
	};
}
