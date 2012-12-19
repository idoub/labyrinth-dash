package com.labyrinthdash;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

public class GameObject 
{
	protected int height, width;
	protected Vector2D position;
	Bitmap img;
	
	public GameObject() {
		img = null;
		height = 0;
		width = 0;
		position = new Vector2D();
	}
	
	public GameObject(int png) {
		makeImage(png);
		height = img.getHeight();
		width= img.getWidth();
		position = new Vector2D();
	}
	
	public void makeImage(int png) {
		Context context = Game.getContext();
		
		Resources res = context.getResources();
		DisplayMetrics metrics = new DisplayMetrics();
		
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inDensity = metrics.densityDpi;
		
		img = BitmapFactory.decodeResource(res, png, o);
	}
}
