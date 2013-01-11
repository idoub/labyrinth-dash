package com.labyrinthdash;

import com.labyrinthdash.R;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * GamePlayer is the sphere which the player will be controlling.
 * 
 * @author Isaac Doub
 */
public class GamePlayer extends GameObject 
{
	/** A reference to the map */
	public GameMap mapReference;
	/** Velocity of the player */
	public Vector2D velocity;
	/** Position of the player in next frame if the current velocity is added */
	public Vector2D nextPosition;
	/** Position of player at last collision */
	public Vector2D lastCollision;
	/** Time of last collision as a percentage of time in between frames */
	public double timeOfCollision;
	/** Radius of the sphere representing the player */
	public double radius;
	
	/** Cell the player is currently in */
	public GameCell currentCell;
	/** Cell the player was in in the last frame */
	public GameCell lastCell = currentCell;
	
	/** Whether the player is currently in the air */
	public boolean jumping = false;
	/** How many frames the jump should last for */
	public int jumpLength = 0;
	/** The highest point of the jump */
	public int apex = 0;

	/** If the player has finished the map */
	public boolean finshed = false;
	/** How many times the player falls off */
	public int penalty = 0;
	
	/**
	 * Creates the player by calling the parent constructor of GameObject and
	 * then setting the other values to default.
	 * @param png
	 */
	public GamePlayer(int png) {
		super(png);
		velocity = new Vector2D();
		nextPosition = new Vector2D();
		lastCollision = null;
		radius = img.getWidth() * 0.5;
		timeOfCollision = Double.MAX_VALUE;
	}
	
	/** X position setter */
	public void setX(float x)
	{
		this.position.x = x;
	}
	
	/** Y position setter */
	public void setY(float y)
	{
		this.position.y = y;
	}
	
	/** X position getter */
	public double getX()
	{
		return this.position.x;
	}
	
	/** Y position getter */
	public double getY()
	{
		return this.position.y;
	}
	
	/**
	 * Handles the movement of the player each frame.
	 * <p>
	 * Starts by getting the current acceleration from the accelerometer in the
	 * phone and then adding that to the velocity. Then calculates what the
	 * next position of the player would be at the new velocity and calls
	 * checkCollision(). Finally checks whether the player is in a cell it can
	 * fall through and if so falls; otherwise it adds the velocity to the
	 * position.
	 * 
	 * @param accelX
	 * @param accelY
	 */
	public void move(float accelX, float accelY) {
		try {
			Vector2D acceleration = new Vector2D(accelX, accelY);
			// Adjust velocity of the ball according to slope
			if (!jumping)
				velocity = velocity.add(acceleration);
			else
				jump();
			// Find what it's next position would be
			nextPosition = position.add(velocity);
			// Check if it would intersect or collide at that position
			checkCollision();
			if (currentCell != mapReference.getCellContaining(position)) {
				if (currentCell != null) {
					if (currentCell.support)
						lastCell = currentCell;
				}
				currentCell = mapReference.getCellContaining(position);
			}
			currentCell.react(this);
			// If the cell doesn't hold the player go to next position
			position = position.add(velocity);
		} catch (NullPointerException e) {
			//Log.e("ERROR", "Map has not yet been created");
		}
	}
	
	private void jump() {
		if(jumpLength == 0) {
			jumping = false;
		} else if(apex < jumpLength) {
			apex++;
			img = Bitmap.createScaledBitmap(img, (int)(img.getWidth()*1.05), (int)(img.getHeight()*1.05), false);			
		} else {
			jumpLength--;
			img = Bitmap.createScaledBitmap(img, (int)(img.getWidth()*0.95), (int)(img.getHeight()*0.95), false);
		}
	}
	
	/**
	 * Checks whether the player has collided with anything and adjusts it's
	 * position and velocity accordingly.
	 * <p>
	 * It starts by looping through the cells that are surrounding the current
	 * cell and for each cell calling the cell's hasCollided() method. If there
	 * is a collision, the position and time of the collision are recorded by
	 * segmentCollision() and the loop is broken.
	 * <p>
	 * The method then calculates the reaction vector using the point of
	 * collision before resetting values. It also uses separate checks for
	 * collision with the edge of the screen.
	 */
	private void checkCollision() {
		boolean hasCollided = false;
		for(GameCell cell : mapReference.getCellsInProximity(nextPosition)) {
			if(cell.checkCollision(this)) {
				hasCollided = true;
			}
			if(hasCollided)	break;
		}
		
		// Calculate the position at collision and collision reaction vector (if we have collided)
		if(hasCollided) {
			// Get position
			Vector2D collisionPosition = this.position.add(velocity.mul(timeOfCollision));
			this.position = collisionPosition;
			
			// Get unit vector from point of collision to centre of sphere
			Vector2D collisionNormal = collisionPosition.sub(lastCollision).unit();
			
			// Reflect the velocity in direction of Normal
			double v_dot_n = velocity.dot(collisionNormal);
            Vector2D twoN_v_dot_n = collisionNormal.mul(2).mul(v_dot_n);
            velocity = (velocity.sub(twoN_v_dot_n.mul(0.8)));				// 0.8 is loss of momentum on collision
		}
		
    	timeOfCollision = Double.MAX_VALUE;									// Reset the soonest collision time
    	lastCollision = null;												// Reset the point of collision
		
		if(nextPosition.x > (sen.surfaceWidth - this.width*0.5)) {
			position.x = sen.surfaceWidth - this.width*0.5;					// Set the position against the edge - eliminates jitter
            velocity.x = (velocity.x * 0.8);
		}
		if(nextPosition.x < this.width*0.5) {
			position.x = this.width*0.5;									// Set the position against the edge - eliminates jitter
            velocity.x = (velocity.x * 0.8);
		}
		if(nextPosition.y > (sen.surfaceHeight - this.height*0.5)) {
			position.y = sen.surfaceHeight - this.height*0.5;				// Set the position against the edge - eliminates jitter
            velocity.y = (velocity.y * 0.8);
		}
		if(nextPosition.y < this.height*0.5) {
			position.y = this.height*0.5;									// Set the position against the edge - eliminates jitter
            velocity.y = (velocity.y * 0.8);
		}
	}
	
	public void reset() {
		makeImage(R.drawable.marble);
		velocity = new Vector2D();
		nextPosition = new Vector2D();
		lastCollision = null;
		radius = img.getWidth() * 0.5;
		timeOfCollision = Double.MAX_VALUE;
		nextPosition = new Vector2D();
		lastCollision = new Vector2D();
		timeOfCollision = 0.0;
		lastCell = currentCell;
		jumping = false;
		jumpLength = 0;
		apex = 0;
		finshed = false;
		penalty = 0;
	}
}
