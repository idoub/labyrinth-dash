package com.labyrinthdash;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Vibrator;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

public class Game extends Activity implements AccelerometerListener 
{
    private static Context CONTEXT;
      
    Panel mainPanel;
    GameMap gameMap;
    GamePlayer player;
    
    private SensorManager mSensorManager;
    private Sensor accelSensor;
    int duration = 0;
    Vibrator v;
	
	//Location
	LocationManager locationManager;
	Location location;
	String locationPos;
	boolean enableGPS = false;

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
       
        // Set full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
        CONTEXT = this;        
        
        player = new GamePlayer(R.drawable.marble);
        
        //Display display = getWindowManager().getDefaultDisplay();
        mainPanel = new Panel(this,player); 
        setContentView(mainPanel);
        
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        //gameMap = new GameMap();
        gameMap = new Map1();
        
        // Get the accelerometer as a Sensor object so we can get information about this version
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        try 
        {
        	accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } 
        catch(Error e) 
        {
        	Toast toast = Toast.makeText(CONTEXT, e.getMessage(), Toast.LENGTH_SHORT);
        	toast.show();
        }
        
        //Location        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        if(enableGPS == true)
        {
        	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else
        {
        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        	
        LocationListener locationListener = new LocationListener() 
        {

			public void onLocationChanged(Location location) 
			{
				// TODO Auto-generated method stub
			}

			public void onProviderDisabled(String provider) 
			{
				// TODO Auto-generated method stub
			}

			public void onProviderEnabled(String provider) 
			{
				// TODO Auto-generated method stub
			}

			public void onStatusChanged(String provider, int status,Bundle extras) 
			{
				// TODO Auto-generated method stub
			}
        	
        } ;
        
        if(enableGPS == true)
        {
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else
        {
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
                        
        if(location != null)
        {
        	sen.longitude = location.getLongitude();
        	sen.latitude = location.getLatitude();
        	
        	locationPos = String.format("Current location \n \n Longitude: %s \n Latitude: %s", location.getLongitude(), location.getLatitude());
        }
        else
        {
        	locationPos = "Location fail";
        }
    }
    
    protected void onResume() 
    {
        super.onResume();
        if (AccelerometerManager.isSupported()) 
        {
            AccelerometerManager.startListening(this);
        }
    }
 
    protected void onDestroy() 
    {
    	//sen.endThread = true;
    	   	
    	super.onDestroy();
    	
        if (AccelerometerManager.isListening()) 
        {
            AccelerometerManager.stopListening();
        }
    
        System.exit(0);
    }
    
    protected void onPause()
	{
    	//Log.d("PAUSE","Back pressed");
    	
		super.onPause();
		
        if (AccelerometerManager.isListening()) 
        {
            AccelerometerManager.stopListening();
        }
        
        System.exit(0);
	}
 
    public static Context getContext() 
    {
        return CONTEXT;
    }
       
    //onAccelerationChanged callback
    public void onAccelerationChanged(float x, float y, float z) 
    {
    	// Normalise the sensor values;
    	float accelRange = accelSensor.getMaximumRange();
    	x = -x/accelRange;	// x seems to be in reverse;
    	y = y/accelRange;
    	
   	
    	if(sen.movePlayer == true)
    	{
    	   	// Let object handle their own movement
    		player.move(x, y);
    	}

    	
    	if(sen.vibrate == true)
    	{
    		v.vibrate(50);
    		sen.vibrate = false;
    	}
    	
    }
 
}
