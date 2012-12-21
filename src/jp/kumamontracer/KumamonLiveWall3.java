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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class KumamonLiveWall3 extends LiveWallBase {
	private static String Tag ="KumamonLiveWall3";
	private Context context;
	
	@Override
	public Engine onCreateEngine() {
		context = this;
		// 描画用の自作Engineクラスを返す
		return new LiveEngine();
	}
	/** 描画を行うEngineクラス **/
	public class LiveEngine extends Engine {
		// ここに描画用の処理を記述していく
		/** イメージ **/
		private Bitmap image;
		/** 表示状態フラグ **/
		private boolean visible;
		private GestureDetector Detector;
		private int touchSelect = 0;
		private int width;
		private int hight;
		private int displayWidth = 480;
		private int position;
		
		public LiveEngine() {
			image = BitmapFactory.decodeResource(getResources(), R.drawable.kuma6);
			width = image.getWidth();
			hight = image.getHeight();
			position = (width - displayWidth) / 2;
		}

		/** Engine生成時に呼び出される **/
		@Override
		public void onCreate(SurfaceHolder surfaceHolder){
			super.onCreate(surfaceHolder);
			Log.i(Tag, "onCreate");
			// タッチイベントを有効
			setTouchEventsEnabled(true);
			// GestureDetecotorクラスのインスタンス生成
			Detector = new GestureDetector(context,onGestureListener);
			Intent intent = new Intent(context, GPSTraceService.class);
			context.startService(intent);
		}
		/** Engine破棄時に呼び出される **/
		@Override
		public void onDestroy(){
			super.onDestroy();
			Log.i(Tag, "onDestroy");
			if (image != null) {
				// Bitmapデータの解放
				image.recycle();
				image = null;
			}
			Intent in = new Intent(context, GPSTraceService.class);
			context.stopService(in);
		}
		/** 表示状態変更時に呼び出される **/
		@Override
		public void onVisibilityChanged(boolean visible){
			Log.i(Tag, "onVisibilityChanged=" + String.valueOf(visible));
			this.visible = visible;
			if(this.visible){
				drawFrame();
			}
		}
		/** サーフェイス生成時に呼び出される **/
		@Override
		public void onSurfaceCreated(SurfaceHolder surfaceHolder){
			super.onSurfaceCreated(surfaceHolder);
			Log.i(Tag, "onSurfaceCreated");
		}
		/** サーフェイス変更時に呼び出される **/
		@Override
		public void onSurfaceChanged(SurfaceHolder holder,int format, int width , int height){
			super.onSurfaceChanged(holder, format, width, height);
			Log.i(Tag, "onSurfaceChanged");
			drawFrame();
		}
		/** サーフェイス破棄時に呼び出される **/
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder){
			super.onSurfaceDestroyed(holder);
			Log.i(Tag, "onSurfaceDestroyed");
			visible = false;
			if (image != null) {
				// Bitmapデータの解放
				image.recycle();
				image = null;
			}
		}
		/** オフセット変更時に呼び出される **/
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels){
			super.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
			Log.i(Tag, "onOffsetsChanged");
		}
		/** キャンバスで描画を行う **/
		private void drawFrame(){
			final SurfaceHolder holder = getSurfaceHolder();
			
			Canvas c = null;
			try{
				// キャンバスをロック
				c = holder.lockCanvas();
				if(c != null){
					c.drawColor(Color.BLACK);
					_changeImage();
					if(position < 0) position = 0;
					if(position > (width - displayWidth)) position = (width - displayWidth);
					c.drawBitmap(image, new Rect(position, 0, position + displayWidth, hight),
							new Rect(0, 0, displayWidth, hight), null);
					Log.d(Tag, "drawBitmap");
				}
			} catch (Exception e) {
				Log.e(Tag, e.getMessage());
			}finally{
				// Canvas アンロック
				if(c != null){
					holder.unlockCanvasAndPost(c);
				}
			}
			// 次の描画をセット
		}
		private void _changeImage() {
			int[] images = {R.drawable.kuma6,R.drawable.kuma5,};
			image = BitmapFactory.decodeResource(getResources(), images[touchSelect]);
			width = image.getWidth();
			hight = image.getHeight();
			//Log.i("tag1", "imageSelect=" + imageSelect);
		}
		/** タッチイベント **/
		@Override
		public void onTouchEvent(MotionEvent event){
			super.onTouchEvent(event);
			// タッチイベントをGestureDetector#onTouchEventメソッドに
			Detector.onTouchEvent(event);
		}
		// 複雑なタッチイベントを取得
		private final SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent event) {
				Log.d(Tag, "onDoubleTap - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				touchSelect++;
				touchSelect %= 2;
				drawFrame();
				return super.onDoubleTap(event);
			}
			@Override
			public boolean onDoubleTapEvent(MotionEvent event) {
				Log.d(Tag, "onDoubleTapEvent - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onDoubleTapEvent(event);
			}
			@Override
			public boolean onDown(MotionEvent event) {
				Log.d(Tag, "onDown - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onDown(event);
			}
			@Override
			public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
				Log.d(Tag, "onFling - " + String.valueOf(velocityX) + " "+ String.valueOf(velocityY));
				position += (int)(-velocityX / 20.0);
				return super.onFling(event1, event2, velocityX, velocityY);
			}
			@Override
			public void onLongPress(MotionEvent event) {
				Log.d(Tag, "onLongPress - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				super.onLongPress(event);
			}
			@Override
			public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
				Log.d(Tag, "onScroll - " + String.valueOf(distanceX) + " "+ String.valueOf(distanceY));
				position += distanceX;
				drawFrame();
				return super.onScroll(event1, event2, distanceX, distanceY);
			}
			@Override
			public void onShowPress(MotionEvent event) {
				Log.d(Tag, "onShowPress - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				super.onShowPress(event);
			}
			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {
				Log.d(Tag, "onSingleTapConfirmed - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onSingleTapConfirmed(event);
			}
			@Override
			public boolean onSingleTapUp(MotionEvent event) {
				Log.d(Tag, "onSingleTapUp - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onSingleTapUp(event);
			}
		};
	}
}
