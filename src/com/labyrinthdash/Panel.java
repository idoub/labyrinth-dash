package com.labyrinthdash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Panel extends SurfaceView implements SurfaceHolder.Callback
{
	// Surface and threads
	private SurfaceHolder holder;
	private appThread surfaceThread;
	private boolean running;
	private boolean multiConnect = false;
	private Paint myPaint;
	Panel _panel;
	Paint paint = new Paint();
	private static final String TAG = "Labyrinth";

	// Threads
	AsyncTask<Void,Void,Void> appTask;
	boolean runMainTask = true;
	
	//Screen
	int touchX, touchY = 0;
	int previousX, previousY = 0;
	int stage, previousStage = 0;
	
	// Bitmaps
	Bitmap bmpBackground, bmpAndroid;
	Bitmap bmpMetal1, bmpMetal2;
	Bitmap bmpSingle, bmpMulti;
	Bitmap bmpHelp, bmpAbout;
	Bitmap bmpOriginalLogo, bmpLogo;
	Bitmap bmpBackButtonLeft, bmpBorder;
	Bitmap bmpLevel1, bmpLevel2, bmpLevel3, bmpLevel4, bmpLevel5;
	
	boolean scaleImages = true;
	boolean scaleInitialImages = true;
	boolean validPress = false;
	boolean loadImages = true;
	boolean showPlayerName = true;
	boolean openMenu = false;
	Rect src;
	Rect dst;
	
	//Intro positions
	int leftPanel, rightPanel = 0;
	int logoY, logoX, logoSize = 0;
	int buttonX, levelButtonX, levelButtonX2, backButtonX = 0;
	boolean initialMove = true;
	
	// Player object
	GamePlayer player;
	String playerName;
	
	//Zooming
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	private static float MIN_ZOOM = 1f;
	private static float MAX_ZOOM = 2f;
	private int dm = 0;
	private boolean zoom = true;
	
	// Dialogs
	private AlertDialog.Builder chooseNameDialog;
	private AlertDialog.Builder multiplayDialog;
	private AlertDialog.Builder infoDialog;
	private AlertDialog.Builder helpDialog;
	private boolean nameChosen = false;
	
	// Score keeping
	private int score = 3000;
	private long start;
	private long end;
	
	public Panel(final Context context, GamePlayer p)
	{
		super(context);
		
		player = p;		
		_panel = this;
		running = false;
		getHolder().addCallback(this);
		holder = getHolder();
		surfaceThread = new appThread(holder);
		
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		
		// Adjust zoom level to DPI
		dm = getResources().getDisplayMetrics().densityDpi;
		switch(dm){
	     case DisplayMetrics.DENSITY_LOW:
	    	 mScaleFactor = 1f;
	         break;
	     case DisplayMetrics.DENSITY_MEDIUM:
	    	 mScaleFactor = 1f;
	         break;
	     case DisplayMetrics.DENSITY_HIGH:
	    	 mScaleFactor = 1f;
	    	 break;
	     case DisplayMetrics.DENSITY_XHIGH:
	    	 mScaleFactor = 1f;
	    	 break;
		}
		
		// Prepare Dialogs
		multiplayDialog = new AlertDialog.Builder(getContext());
		multiplayDialog.setTitle("Info");
		multiplayDialog.setMessage("Multiplayer is currently unavailable");
		multiplayDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              // Canceled.
            	openMenu = false;
            }
          });
		
		helpDialog = new AlertDialog.Builder(getContext());
		helpDialog.setTitle("Help");
		helpDialog.setMessage("\nIf you need help for this game then you should consider not reproducing\n");
		helpDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              // Canceled.
            	openMenu = false;
            }
          });
		
		infoDialog = new AlertDialog.Builder(getContext());
		infoDialog.setTitle("About");
		infoDialog.setMessage("App developed by: \n\n  Isaac Doub, \n  Pham Minh Nhat,  \n  Matthew Woodacre\n");
		infoDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              // Canceled.
            	openMenu = false;
            }
          });
		
		chooseNameDialog = new AlertDialog.Builder(getContext());
		chooseNameDialog.setTitle("Welcome!");
		chooseNameDialog.setMessage("Put your name in the textbox:");
		
		/*
		 * Listener for any touch on the screen
		 * Calls dealTouch() to decide what to do with user interaction
		 */
		setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				dealTouch(event);
				
				return true;
			}
		});
	}

	/*
	 * Implements listener to detect gestures and change player view accordingly
	 */
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener 
	{
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) 
	    {
	        mScaleFactor *= detector.getScaleFactor();

	        // Limit zoom factor
	        mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
	        invalidate();
	        return true;
	    }
	}
	
	/*
	 * Called when the screen is touched
	 */
	private void dealTouch(MotionEvent event)
	{
		// Get touch position
		touchX = (int) event.getX();
		touchY = (int) event.getY();
		
		Log.d(TAG, "touchX: " + touchX);
		Log.d(TAG, "touchY: " + touchY);

		// Check if 'valid' touch 
		/*
		 * In order to stop any button bounce the next touch must 
		 * be at least a set distance from the previous touch
		 *
		 */
		if((Math.abs(touchX-previousX) > 6) && (Math.abs(touchY-previousY) > 6))
		{
			Log.d(TAG, "Valid press");
			validPress = true;
		}
		else
		{
			Log.d(TAG, "Invalid press");
			validPress = false;
		}
		
		/*
		 *  Test if position is near ball (debug only), allows ball overide movement
		 */
		/*if(((touchX - player.position.x) < (player.width*1.5)) && ((touchX - player.position.x) > 0))
		{
			if(((touchY - player.position.y) < (player.height*1.5)) && ((touchY - player.position.y) > 0))
			{
				player.position.y = touchY - (player.width/2);
				player.position.x = touchX - (player.height/2);
			}
		}*/
		
		/*
		 * If the touch is valid then depending on the current 
		 * menu affects touch effect 
		 */
		if(validPress == true)
		{
			if(stage == 0)
			{
				//stage = 1;
			}
					
			if(stage == 2)
			{
				Log.d(TAG, "In stage 2");
				
				if((touchY > (sen.surfaceHeight/2)) && (touchY < ((sen.surfaceHeight/2)+(sen.surfaceHeight/10))) )
				{
					// Check is there is a saved name in memory
					 SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

					// Get the player name
				    playerName = app_preferences.getString("playerName", "noName");
				    				    
				    if(openMenu == false)
				    {
				    	openMenu = true;
				    	
					    // Player has never set name
					    if(playerName.equals("noName"))
					    {
					    	nameChosen = false;
					    }		
					    // Player has saved a name
					    else
					    {
					    	nameChosen = true;
					    	
					    	if(showPlayerName == true)
					    	{
					    		// Welcome player
						    	Toast toast = Toast.makeText(getContext(), "Welcome back " + playerName, Toast.LENGTH_SHORT);
		                      	toast.show();
		                      	showPlayerName = false;
					    	}
					    }
						
						// If player hasn't picked a name
						if(!nameChosen) 
						{
			                final EditText input = new EditText(getContext());
			                chooseNameDialog.setView(input);
			                chooseNameDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			                {   
			                	public void onClick(DialogInterface dialog, int whichButton) 
			                    {
			                        playerName = input.getText().toString();
			                     
			                        if(playerName.equals(""))
			                        {
			                        	// Player has no name :(
				                      	Toast toast = Toast.makeText(getContext(), "No name entered", Toast.LENGTH_SHORT);
				                      	toast.show();
			                        }
			                        else
			                        {
				                        // Welcome the player to the game
				                      	Toast toast = Toast.makeText(getContext(), "Player's name set to " + playerName, Toast.LENGTH_SHORT);
				                      	toast.show();
				                      	
				                      	// Save player name to the device memory
				                      	SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
				                      	SharedPreferences.Editor editor = app_preferences.edit();
				                      	
				                      	editor.putString("playerName", playerName);
				                      	editor.commit();     	
				                      	nameChosen = true;
			                        }
			                        openMenu = false;
			                        stage = 3;
				        			previousStage = 2;	
			        				Log.d(TAG, "Moving to stage 3");		        				
			                      }
			                });
			                
			                chooseNameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			                {
			                    public void onClick(DialogInterface dialog, int whichButton) 
			                    {
			                    	// Cancelled.
			                    	openMenu = false;
			                    }
			                });

		                	chooseNameDialog.show();
		                	sen.vibrate = true;
						} 
						else 
						{
							//If name is chosen, skip to level selection
							openMenu = false;
							stage = 3;
							previousStage = 2;
							Log.d(TAG, "Moving to stage 3");
							sen.vibrate = true;
						}
				    }
				}
				
				// Multiplayer button
				if((touchY > ((sen.surfaceHeight/100)*62)) && (touchY < (((sen.surfaceHeight/100)*62)+(sen.surfaceHeight/10))) )
				{
					if((touchX > buttonX) && (touchX < (buttonX + (sen.surfaceWidth/2))))
					{
						if(openMenu == false)
						{
							sen.vibrate = true;
							multiplayDialog.show();
							openMenu = true;
						}
					}
				}
				
				// Help button
				if((touchY > ((sen.surfaceHeight/100)*74)) && (touchY < (((sen.surfaceHeight/100)*74)+(sen.surfaceHeight/10))) )
				{
					if((touchX > buttonX) && (touchX < (buttonX + (sen.surfaceWidth/2))))
					{
						if(openMenu == false)
						{
							sen.vibrate = true;
							helpDialog.show();
							openMenu = true;
						}
					}
				}
	
				// Information button
				if((touchY > ((sen.surfaceHeight/100)*86)) && (touchY < (((sen.surfaceHeight/100)*86)+(sen.surfaceHeight/10))) )
				{
					if((touchX > buttonX) && (touchX < (buttonX + (sen.surfaceWidth/2))))
					{
						if(openMenu == false)
						{
							sen.vibrate = true;
							infoDialog.show();
							openMenu = true;
						}
					}
				}	
			}
			// Displaying level options screen
			else if(stage == 4)
			{
				Log.d(TAG, "In stage 4");
				
				// Return arrow button
				if((touchX < (sen.surfaceWidth/4)) && (touchY < (sen.surfaceWidth/4)))
				{
					stage = 3;
					previousStage = 4;
					Log.d(TAG, "Moving to stage 2");
					sen.vibrate = true;
				}
				else
				{
					/* TODO
					 * Isaac, this is where the levels are selected  
					 * 
					 * Currently there is only the code for the level 1 button press, however, when
					 * we have different maps the code will be added to detect presses of the other
					 * buttons
					 * 
					 */
					
					// Level 1 button
					if((touchX > levelButtonX) && (touchY > ((sen.surfaceHeight/100)*25)))
					{
						if((touchX < (levelButtonX+(sen.surfaceWidth/5))) && (touchY < (((sen.surfaceHeight/100)*25))+(sen.surfaceWidth/5)))
						{
							// TODO: Select map 1 here
							
							stage = 5;
							score = 3000;
							previousStage = 4;
							Log.d(TAG, "Moving to stage 5");
							sen.vibrate = true;
						}
					}
					
					// Level 2 button
					
					// Level 3 button
					
				}
				previousStage = 4;
			}
			
			if(stage == 5)
			{
				if((touchX < (sen.surfaceWidth/8)) && (touchY < (sen.surfaceWidth/8)))
				{
					stage = 4;
					sen.vibrate = true;
				}
			}
			
			previousX = touchX;
			previousY = touchY;
		}
		
		// Let the ScaleGestureDetector inspect all events.
		if(zoom){
			mScaleDetector.onTouchEvent(event);
		}
	}

	/*
	 * Initialse player position and screen size information
	 */
	
	public void initialise()
	{
		player.position = GameMap.startCell.position.add(GameMap.startCell.width/2, GameMap.startCell.height/2);
		
		src = new Rect(0, 0, 610, 458);
		dst = new Rect(0, 0, sen.surfaceWidth, sen.surfaceHeight);
		
		if(multiConnect == true)
		{
			Log.d(TAG, "about to start connection");
			
			// No choice in partner
			new InitialConnect(player, player, sen.surfaceHeight, sen.surfaceWidth, playerName).start();
			
			Log.d(TAG, "connection process started");
		}
	}
	
		
	public void resume() 
	{
		if (surfaceThread == null)
		{
			surfaceThread = new appThread(holder);
		}

		if (running)
		{
			surfaceThread.start();
		}
	}

	//@SuppressWarnings("deprecation")
	public void onPause()
	{
		if (surfaceThread != null)
		{
			surfaceThread.finish();
			surfaceThread = null;
			//System.runFinalizersOnExit(true);
		}
		
		System.exit(0);
		
		//super.onPause();
		//onDestroy();
	}
	
	/*
	 * Handles all displaying of graphics on the device screen
	 * Each stage number designates a different application screen to be displayed
	 *  
	 */
	@Override
	public void onDraw(Canvas canvas)
	{		
		super.onDraw(canvas);
		
		canvas.save();
		
		// Intial start screen
		if(stage == 0)
		{
			if(logoX > (sen.surfaceWidth/12))
			{
				logoX--;
			}
			if(logoSize < (sen.surfaceWidth)-(sen.surfaceWidth/6))
			{
				logoSize += 2;
				bmpLogo = resizeImage(bmpOriginalLogo, (sen.surfaceHeight/4), logoSize);
			}
			
			canvas.drawBitmap(bmpMetal2, leftPanel, 0, myPaint);
			canvas.drawBitmap(bmpMetal1, rightPanel, 0, myPaint);
			
			canvas.drawBitmap(bmpLogo, logoX, logoY, myPaint);
		}
		
		// Transition from start screen to main menu
		// Timing is computed in mainThread async thread
		if(stage == 1)
		{			
			canvas.drawBitmap(bmpBackground, src, dst, myPaint);
			
			for(int i = (sen.surfaceWidth/20); i < sen.surfaceHeight; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, (-(sen.surfaceWidth/20)), i, myPaint);
			}
			
			for(int i = (sen.surfaceWidth/20); i < sen.surfaceHeight; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, (sen.surfaceWidth-(sen.surfaceWidth/20)), i, myPaint);
			}
			
			for(int i = -(sen.surfaceWidth/20); i < sen.surfaceWidth; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, i, (-(sen.surfaceWidth/20)), myPaint);
			}
			
			for(int i = -(sen.surfaceWidth/20); i < sen.surfaceWidth; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, i, (sen.surfaceHeight-(sen.surfaceWidth/20)), myPaint);
			}	
			
			canvas.drawBitmap(bmpSingle, (sen.surfaceWidth/4), ((sen.surfaceHeight/100)*50), myPaint);
			canvas.drawBitmap(bmpMulti, (sen.surfaceWidth/4), ((sen.surfaceHeight/100)*62), myPaint);
			canvas.drawBitmap(bmpHelp, (sen.surfaceWidth/4), ((sen.surfaceHeight/100)*74), myPaint);
			canvas.drawBitmap(bmpAbout, (sen.surfaceWidth/4), ((sen.surfaceHeight/100)*86), myPaint);
			
			canvas.drawBitmap(bmpMetal2, leftPanel, 0, myPaint);
			canvas.drawBitmap(bmpMetal1, rightPanel, 0, myPaint);
			
			canvas.drawBitmap(bmpLogo, logoX, logoY, myPaint);
		}		
		
		// Main menu
		if(stage == 2)
		{						
			canvas.drawBitmap(bmpBackground, src, dst, myPaint);	
			canvas.drawBitmap(bmpLogo, logoX, logoY, myPaint);
			canvas.drawBitmap(bmpSingle, buttonX, ((sen.surfaceHeight/100)*50), myPaint);
			canvas.drawBitmap(bmpMulti, buttonX, ((sen.surfaceHeight/100)*62), myPaint);
			canvas.drawBitmap(bmpHelp, buttonX, ((sen.surfaceHeight/100)*74), myPaint);
			canvas.drawBitmap(bmpAbout, buttonX, ((sen.surfaceHeight/100)*86), myPaint);
						
			//canvas.drawBitmap(bmpAndroid, ((sen.surfaceWidth)-(sen.surfaceWidth/7)), ((sen.surfaceHeight)-(sen.surfaceWidth/7)), myPaint);
		}
		
		// Transition from main menu to single player menu
		// Changing of bitmap position computed in mainTask async thread 
		if(stage == 3)
		{			
			canvas.drawBitmap(bmpBackground, src, dst, myPaint);
			
			canvas.drawBitmap(bmpLogo, logoX, logoY, myPaint);
			canvas.drawBitmap(bmpSingle, buttonX, ((sen.surfaceHeight/100)*50), myPaint);
			canvas.drawBitmap(bmpMulti, buttonX, ((sen.surfaceHeight/100)*62), myPaint);
			canvas.drawBitmap(bmpHelp, buttonX, ((sen.surfaceHeight/100)*74), myPaint);
			canvas.drawBitmap(bmpAbout, buttonX, ((sen.surfaceHeight/100)*86), myPaint);

			canvas.drawBitmap(bmpLevel1, levelButtonX, ((sen.surfaceHeight/100)*25), myPaint);
			canvas.drawBitmap(bmpLevel2, levelButtonX2, ((sen.surfaceHeight/100)*40), myPaint);
			canvas.drawBitmap(bmpLevel3, levelButtonX, ((sen.surfaceHeight/100)*55), myPaint);
			canvas.drawBitmap(bmpLevel4, levelButtonX2, ((sen.surfaceHeight/100)*70), myPaint);
			canvas.drawBitmap(bmpLevel5, levelButtonX, ((sen.surfaceHeight/100)*85), myPaint);	
			canvas.drawBitmap(bmpBackButtonLeft, backButtonX, ((sen.surfaceHeight/100)*5), myPaint);
		}
		
		// Choose single player level
		if(stage == 4)
		{
			canvas.drawBitmap(bmpBackground, src, dst, myPaint);
			
			canvas.drawBitmap(bmpLevel1, levelButtonX, ((sen.surfaceHeight/100)*25), myPaint);
			canvas.drawBitmap(bmpLevel2, levelButtonX2, ((sen.surfaceHeight/100)*40), myPaint);
			canvas.drawBitmap(bmpLevel3, levelButtonX, ((sen.surfaceHeight/100)*55), myPaint);
			canvas.drawBitmap(bmpLevel4, levelButtonX2, ((sen.surfaceHeight/100)*70), myPaint);
			canvas.drawBitmap(bmpLevel5, levelButtonX, ((sen.surfaceHeight/100)*85), myPaint);	
			
			canvas.drawBitmap(bmpBackButtonLeft, backButtonX, ((sen.surfaceHeight/100)*5), myPaint);
		}
		
		// Draw menu boundary platforms 
		if((stage == 2) || (stage == 3) || (stage == 4))
		{
			for(int i = (sen.surfaceWidth/20); i < sen.surfaceHeight; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, (-(sen.surfaceWidth/20)), i, myPaint);
			}
			
			for(int i = (sen.surfaceWidth/20); i < sen.surfaceHeight; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, (sen.surfaceWidth-(sen.surfaceWidth/20)), i, myPaint);
			}
			
			for(int i = -(sen.surfaceWidth/20); i < sen.surfaceWidth; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, i, (-(sen.surfaceWidth/20)), myPaint);
			}
			
			for(int i = -(sen.surfaceWidth/20); i < sen.surfaceWidth; i += (sen.surfaceWidth/10))
			{
				canvas.drawBitmap(bmpBorder, i, (sen.surfaceHeight-(sen.surfaceWidth/20)), myPaint);
			}
		}
		
		
		// Game play
		if(stage == 5)
		{			
			// Zooming in or out
			canvas.scale(mScaleFactor, mScaleFactor, (float)player.getX(), (float)player.getY());
			
			for(GameCell col[] : GameMap.Map) 
			{
				for(GameCell cell : col) 
				{
					canvas.drawBitmap(cell.img, (float)cell.position.x, (float)cell.position.y, myPaint);
				}
			}
			canvas.drawBitmap(player.img, (float)(player.position.x - player.img.getWidth()*0.5), (float)(player.position.y - player.img.getHeight()*0.5), myPaint);
		
			canvas.drawBitmap(bmpBackButtonLeft, 0, 0, myPaint);
		}		
		
		canvas.restore();
	}

	
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (sen.surfaceHeight == 0 && getHeight() != 0)
		{
			sen.surfaceHeight = getHeight();
		}
		if (sen.surfaceWidth == 0 && getWidth() != 0)
		{
			sen.surfaceWidth = getWidth();
		}				
					
		//Initialise player, obstacle etc.
		initialise();
		
		// Load and Scale primary images
		if(scaleInitialImages == true)
		{
			// Load images
			bmpOriginalLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
			bmpMetal1 = BitmapFactory.decodeResource(getResources(), R.drawable.metal_left);
			bmpMetal2 = BitmapFactory.decodeResource(getResources(), R.drawable.metal_right);
			
			logoSize = 2;

			// Scale Images
			bmpMetal1 = resizeImage(bmpMetal1, sen.surfaceHeight, (sen.surfaceWidth/2));
			bmpMetal2 = resizeImage(bmpMetal2, sen.surfaceHeight, (sen.surfaceWidth/2));
			bmpLogo = resizeImage(bmpOriginalLogo, (sen.surfaceHeight/4), logoSize);
			
			// Image related data
			buttonX = (sen.surfaceWidth/4);
			rightPanel = sen.surfaceWidth/2;
			logoY = (sen.surfaceHeight/4);
			logoX = sen.surfaceWidth/2;
			levelButtonX = (sen.surfaceWidth) + (sen.surfaceWidth/10);
			levelButtonX2 = (sen.surfaceWidth) + ((sen.surfaceWidth/10)*5);
			backButtonX = (sen.surfaceWidth);
			
			scaleInitialImages = false;
		}
		
		running = true;

		// Begin surface thread
		if (surfaceThread != null)
		{
			surfaceThread.start();
		}
		
		// Run thread (used for timing)
		appTask = new mainTask().execute();	
	}
	
	public boolean loadSecondaryImages()
	{
		// Scale secondary images
			
		bmpBackground = BitmapFactory.decodeResource(getResources(),R.drawable.space);
		bmpSingle = BitmapFactory.decodeResource(getResources(), R.drawable.single_button);
		bmpMulti = BitmapFactory.decodeResource(getResources(), R.drawable.multi_button);
		bmpBorder = BitmapFactory.decodeResource(getResources(), R.drawable.metalplatform);
		bmpHelp = BitmapFactory.decodeResource(getResources(), R.drawable.help_button);
		bmpAbout = BitmapFactory.decodeResource(getResources(), R.drawable.about_button);		
		bmpBackButtonLeft = BitmapFactory.decodeResource(getResources(), R.drawable.back_button);
		bmpAndroid = BitmapFactory.decodeResource(getResources(), R.drawable.android);
					
		bmpLevel1 = BitmapFactory.decodeResource(getResources(), R.drawable.marble1);	
		bmpLevel2 = BitmapFactory.decodeResource(getResources(), R.drawable.marble2);		
		bmpLevel3 = BitmapFactory.decodeResource(getResources(), R.drawable.marble3);		
		bmpLevel4 = BitmapFactory.decodeResource(getResources(), R.drawable.marble4);		
		bmpLevel5 = BitmapFactory.decodeResource(getResources(), R.drawable.marble5);		
					
		bmpSingle = resizeImage(bmpSingle, (sen.surfaceHeight/14), (sen.surfaceWidth)-(sen.surfaceWidth/2));
		bmpMulti = resizeImage(bmpMulti, (sen.surfaceHeight/14), (sen.surfaceWidth)-(sen.surfaceWidth/2));
		bmpHelp = resizeImage(bmpHelp, (sen.surfaceHeight/14), (sen.surfaceWidth)-(sen.surfaceWidth/2));
		bmpAbout = resizeImage(bmpAbout, (sen.surfaceHeight/14), (sen.surfaceWidth)-(sen.surfaceWidth/2));
		bmpBackButtonLeft = resizeImage(bmpBackButtonLeft, (sen.surfaceWidth/8), (sen.surfaceWidth/8));
		bmpAndroid = resizeImage(bmpAndroid, (sen.surfaceWidth/6), (sen.surfaceWidth/6));
					
		bmpLevel1 = resizeImage(bmpLevel1, (sen.surfaceWidth/5), (sen.surfaceWidth/5));
		bmpLevel2 = resizeImage(bmpLevel2, (sen.surfaceWidth/5), (sen.surfaceWidth/5));
		bmpLevel3 = resizeImage(bmpLevel3, (sen.surfaceWidth/5), (sen.surfaceWidth/5));
		bmpLevel4 = resizeImage(bmpLevel4, (sen.surfaceWidth/5), (sen.surfaceWidth/5));
		bmpLevel5 = resizeImage(bmpLevel5, (sen.surfaceWidth/5), (sen.surfaceWidth/5));	
				
		bmpBorder = resizeImage(bmpBorder, (sen.surfaceWidth/10), (sen.surfaceWidth/10));
		
		return true;
	}

	/*
	 *  Function used to  create a new resized image for every element
	 */
	public Bitmap resizeImage(Bitmap inputImage, int newHeight, int newWidth) 
	{
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();

		matrix.postScale((float)scaleWidth, (float)scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(inputImage, 0, 0, width, height,	matrix, false);
		return resizedBitmap;
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		running = false;
		
		onPause();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) 
	{
		// Deal with orientation change
	}

	class appThread extends Thread
	{
		private boolean threadStart;
		SurfaceHolder appHolder;

		public appThread(SurfaceHolder _holder) 
		{
			super();
			appHolder = _holder;
			threadStart = false;
		}

		@Override
		public void run()
		{
			Canvas appCanvas;

			while (!threadStart)
            {
				appCanvas = null;
				try
				{
					appCanvas = appHolder.lockCanvas(null);
					_panel.onDraw(appCanvas);
				} 
				finally 
				{
					if (appCanvas != null)
					{
						appHolder.unlockCanvasAndPost(appCanvas);
					}
				}

			}
		}

		public void finish() 
		{
			threadStart = true;
			try
			{
				join();
			}
			catch (Exception e)
			{
				// Deal with it
			}
		}
	}

	private class mainTask extends AsyncTask<Void, Void, Void> 
	{
		protected Void doInBackground(Void... urls)
		{
			while (runMainTask) 
			{				
				// Run any synchronous animations etc.
							
				if(stage == 0)
				{										
					if((logoX == sen.surfaceWidth/12) && (logoSize == (sen.surfaceWidth)-(sen.surfaceWidth/6)))
					{
						try 
						{
							Thread.sleep(200);
						} 
						catch (Exception e)
						{
							Log.d(TAG, "Thread sleep fail");
						}
						
						if(scaleImages == true)
						{
							boolean safe = loadSecondaryImages();
							
							if(safe != true)
							{
								// Really bad day
							}
							
							scaleImages = false;
						}
						
						try 
						{
							Thread.sleep(200);
						} 
						catch (Exception e)
						{
							Log.d(TAG, "Thread sleep fail");
						}
						
						stage = 1;
					}
				}
				
				if(stage == 1)
				{
					if(initialMove == true)
					{
						try 
						{
							Thread.sleep(100);
						} 
						catch (Exception e)
						{
							Log.d(TAG, "Thread sleep fail");
						}
						
						initialMove = false;
					}
					
					leftPanel -= (sen.surfaceWidth/200);
					rightPanel += (sen.surfaceWidth/200);
					
					if(logoY > sen.surfaceHeight/15)
					{
						logoY -= (sen.surfaceWidth/200);
					}
					
					if(rightPanel >= sen.surfaceWidth)
					{
						stage = 2;
					}
				}
				
				if(stage == 3)
				{
					if(previousStage == 2)
					{
						// Moving left
						
						if(levelButtonX > (sen.surfaceWidth/4))
						{
							buttonX -= (sen.surfaceWidth/10);
							logoX -= (sen.surfaceWidth/10);
							
							levelButtonX -= (sen.surfaceWidth/10);
							levelButtonX2 -= (sen.surfaceWidth/10);
							backButtonX -= (sen.surfaceWidth/10);
						}
						else
						{
							stage = 4;
						}
					}
					else
					{
						// Moving right
						if(buttonX < (sen.surfaceWidth/4))
						{
							buttonX += (sen.surfaceWidth/10);
							logoX += (sen.surfaceWidth/10);
							
							levelButtonX += (sen.surfaceWidth/10);
							levelButtonX2 += (sen.surfaceWidth/10);
							backButtonX += (sen.surfaceWidth/10);
						}
						else
						{
							stage = 2;
						}	
					}
				}
		
				if (stage == 5) {
					score--;
					if(score == 0) {
						stage = 4;
					}
				}
				
				try 
				{
					Thread.sleep(20);
				} 
				catch (Exception e)
				{
					Log.d(TAG, "Thread sleep fail");
				}
			}
			return null;
		}
	}
}

/*
 *  Public class to share key information between different classes
 */
class sen
{
	public static double longitude;
	public static double latitude;
	
	public static boolean vibrate = false;
	
	public static int surfaceWidth = 0;
	public static int surfaceHeight = 0;
	
	public static boolean endThread = false;
}
