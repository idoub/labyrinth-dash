package com.labyrinthdash;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

/**
 * Root class for any object in the game that has an image, height, width, and
 * position.
 * 
 * @author Isaac Doub
 */
public class GameObject 
{
	/**
	 * Height and width of the object. If an image is provided, they are taken
	 * from the image dimensions, otherwise they are set to 0.
	 */
	protected int height, width;
	/** Position of the object */
	protected Vector2D position;
	/** Object image */
	Bitmap img;
	
	/**
	 * Constructs a new object with a null image, 0 height and width, and a
	 * postion of (0,0).
	 */
	public GameObject() {
		img = null;
		height = 0;
		width = 0;
		position = new Vector2D();
	}
	
	/**
	 * Constructs a new object with the provided image, a height and width equal
	 * to that of the image, and a position of (0,0).
	 * @param png
	 */
	public GameObject(int png) {
		makeImage(png);
		height = img.getHeight();
		width= img.getWidth();
		position = new Vector2D();
	}
	
	/**
	 * Attaches an image to the object.
	 * @param png
	 */
	protected void makeImage(int png) {
		Context context = Game.getContext();
		
		Resources res = context.getResources();
		DisplayMetrics metrics = new DisplayMetrics();
		
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inDensity = metrics.densityDpi;
		
		img = BitmapFactory.decodeResource(res, png, o);
	}
}
