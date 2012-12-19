package com.labyrinthdash;

import com.labyrinthdash.R;

import android.graphics.Bitmap;

/**
 * GamePlayer is the sphere which the player will be controlling.
 * 
 * @author Isaac Doub
 */
public class GamePlayer extends GameObject 
{
	/** Velocity of the player */
	Vector2D velocity;
	/** Position of the player in next frame if the current velocity is added */
	Vector2D nextPosition;
	/** Position of player at last collision */
	Vector2D lastCollision;
	/** Time of last collision as a percentage of time in between frames */
	double timeOfCollision;
	/** Radius of the sphere representing the player */
	double radius;
	/** Cell the player is currently in */
	GameCell currentCell;
	/** Cell the player was in in the last frame */
	GameCell lastCell = currentCell;
	
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
		Vector2D acceleration = new Vector2D(accelX, accelY);
    	
    	// Adjust velocity of the ball according to slope
    	velocity = velocity.add(acceleration);
    	// Find what it's next position would be
    	nextPosition = position.add(velocity);
    	// Check if it would intersect or collide at that position
    	checkCollision();
    	if(currentCell != GameMap.getCellContaining(position)) {
    		lastCell = currentCell;
    		currentCell = GameMap.getCellContaining(position);
    	}
    	if(currentCell.space) fall();
    	// Set to new position
    	else position = position.add(velocity);
	}
	
	/**
	 * Iteratively scales the bitmap to simulate falling animation until the
	 * image is 5 pixels, then resets the players position to the last good cell
	 * it was on.
	 */
	private void fall() {
		// TODO: Animate fall, then reset
		if(img.getHeight() > 5) {
			img = Bitmap.createScaledBitmap(img, (int)(img.getWidth()*0.75), (int)(img.getHeight()*0.75), false);
		} else {
			this.makeImage(R.drawable.marble);
			this.velocity = new Vector2D();
			this.position = lastCell.position.add(lastCell.width/2, lastCell.height/2);
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
		for(GameCell cell : GameMap.getCellsInProximity(nextPosition)) {
			if(cell.hasCollided(this)) {
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
}
