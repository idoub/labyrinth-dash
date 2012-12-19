package com.labyrinthdash;

import com.labyrinthdash.R;

import android.graphics.Bitmap;

public class GamePlayer extends GameObject 
{
	Vector2D velocity;
	Vector2D nextPosition;
	Vector2D lastCollision;
	double radius;
	double timeOfCollision;
	GameCell currentCell;
	GameCell lastCell = currentCell;
	
	public GamePlayer(int png) {
		super(png);
		velocity = new Vector2D();
		nextPosition = new Vector2D();
		lastCollision = null;
		radius = img.getWidth() * 0.5;
		timeOfCollision = Double.MAX_VALUE;
	}
	
	// For multiplayer
	public void setX(float x)
	{
		this.position.x = x;
	}
	
	public void setY(float y)
	{
		this.position.y = y;
	}
	
	public double getX()
	{
		return this.position.x;
	}
	
	public double getY()
	{
		return this.position.y;
	}
	
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
