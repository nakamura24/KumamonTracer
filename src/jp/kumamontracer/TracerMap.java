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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class TracerMap extends MapActivityBase {
	private static final String TAG ="TracerMap";
	private static final String TraceFileName = "Trace.txt";
	private static final String TRACELISTDIALOG = "jp.kumamontracer.DIALOG";
	private MapView mMapView;
	private MapController mMapController;
	private PinItemizedOverlay mPinItemizedOverlay;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.i(TAG, "onCreate");

    	try{
            setContentView(R.layout.tracer_map);
            mMapView = (MapView)findViewById( R.id.trace_view);
            mMapView.setEnabled(true);
            mMapView.setClickable(true);
            mMapView.setBuiltInZoomControls(true);
            mMapController = mMapView.getController();
            mMapController.setZoom(14);
            //mMapController.setCenter(new GeoPoint((int)(35.681111 * 1E6), (int)(139.766667 * 1E6)));
     
            Drawable pin = getResources().getDrawable( R.drawable.pin_green_l);
            mPinItemizedOverlay = new PinItemizedOverlay(pin);
            mMapView.getOverlays().add(mPinItemizedOverlay);
            
            ArrayList<GPSData> data = getTrace();
            for(int i=0;i<data.size();i++) {
                setPin(data.get(i).Longitude, data.get(i).Latitude);       	
            }
            if(data.size() > 0) {
            	mMapController.setCenter(new GeoPoint((int)(data.get(0).Latitude * 1E6), (int)(data.get(0).Longitude * 1E6)));
            }
            //setPin(130.710,32.810);
            //setPin(131.040,32.940);
            //setPin(130.030,32.200);
            //setPin(130.750,32.220);
            //setPin(130.686200,32.768425);
    	}catch(Exception e){
    		ExceptionLog.Log(TAG, e);
    	}
    }
    
    @Override
     protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    	Log.i(TAG, "onDestroy");
    }
    
	// Button の onClick で実装
	public void onLoglistButtonClick(View v){
		try {		
			Log.i(TAG, "onLoglistButtonClick");
			Intent intent = new Intent(this, TracerListDialog.class);
			intent.setAction(TRACELISTDIALOG);
			startActivity(intent);
		}catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}
    
	// Button の onClick で実装
	public void onLogclearButtonClick(View v){
		try {		
			Log.i(TAG, "onLogclearButtonClick");
			//deleteFile(TraceFileName);
        	/*FileOutputStream file = openFileOutput(TraceFileName, Context.MODE_PRIVATE);
    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(file,"UTF-8"));
    		out.write("");
    		out.flush();
    		out.close();*/
		}catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

    private void setPin(double longitude, double latitude) {
    	Log.d(TAG, "setPin - " + String.valueOf(longitude) + " " + String.valueOf(latitude));
		try {
	        GeoPoint point	= new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
	        mPinItemizedOverlay.add(point);    	
		} catch (Exception e) {
    		ExceptionLog.Log(TAG, e);
		}
    }
	
    private ArrayList<GPSData> getTrace() {
		ArrayList<GPSData> mGPSDatas = new ArrayList<GPSData>();
		try {
			FileInputStream file = openFileInput(TraceFileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(file,"UTF-8"));
			String trace = in.readLine();
			while(trace != null) {
				String[] split = trace.split(",");
            	GPSData data = new GPSData();
            	data.Date = Long.parseLong(split[0]);
            	data.Longitude = Double.parseDouble(split[1]);
            	data.Latitude = Double.parseDouble(split[2]);
            	data.Altitude = Double.parseDouble(split[3]);
            	mGPSDatas.add(data);
				trace = in.readLine();
			}
	    	Log.d(TAG, "getTrace - " + String.valueOf(mGPSDatas.size()));
			in.close();
			file.close();
		} catch (Exception e) {
    		ExceptionLog.Log(TAG, e);
		}
		return mGPSDatas;
	}
}
