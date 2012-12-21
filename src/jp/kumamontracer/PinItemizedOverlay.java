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

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PinItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
	 
	public PinItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	@Override
	protected OverlayItem createItem(int i) {
	    return items.get(i);
	}
	@Override
	public int size() {
	    return items.size();
	}
	public void add(GeoPoint point) {
	    items.add(new OverlayItem(point, "", ""));
	    populate();
	}
	public void clear() {
		items.clear();
		populate();
	}
}
