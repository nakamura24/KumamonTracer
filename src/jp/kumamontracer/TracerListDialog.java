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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class TracerListDialog extends Activity {
	private static final String TAG = "TracerListDialog";
	private static final String TraceFileName = "Trace.txt";

	private class ListGPSDataAdapter extends ArrayAdapter<GPSData> {
		private LayoutInflater layoutInflater_;
		private Context context;

		public ListGPSDataAdapter(Context context, int textViewResourceId, List<GPSData> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GPSData data = (GPSData) getItem(position);
			if (null == convertView) {
				 convertView = layoutInflater_.inflate(R.layout.tracer_list_item, null);
			}
			TextView date = (TextView)convertView.findViewById(R.id.text1);
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/d H:mm");
			date.setText(sdf1.format(new Date(data.Date)));
			TextView title = (TextView)convertView.findViewById(R.id.text2);
			Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
			try {
				List<Address> list_address = 
						geocoder.getFromLocation(data.Latitude, data.Longitude, 3);	//引数末尾は返す検索結果数
				if (!list_address.isEmpty()){
					Address address = list_address.get(0);
					StringBuffer strbuf = new StringBuffer();
					//adressをStringへ
					String buf;
					for (int i = 1; (buf = address.getAddressLine(i)) != null; i++){
						strbuf.append(buf);
					}
					String string = strbuf.toString();
					title.setText(string);				
				} else {
			    	Log.v(TAG, "getView - isEmpty");
					title.setText("東経="+String.valueOf((float)data.Longitude) + "北緯="+String.valueOf((float)data.Latitude));									
				}
			} catch (Exception e) {
		    	Log.v(TAG, "getView - Exception");
				title.setText("東経="+String.valueOf((float)data.Longitude) + "北緯="+String.valueOf((float)data.Latitude));									
			}
			return convertView;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onEnabled");

		ArrayList<GPSData> mGPSDatas = getTrace();
		
		setContentView(R.layout.tracer_list_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(new ListGPSDataAdapter(this, 0, mGPSDatas));
	}

	// Button の onClick で実装
	public void onOKButtonClick(View v){
		Log.i(TAG, "onOKButtonClick");
		finish();		
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