package com.labyrinthdash;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.widget.Toast;

/**
 * GameCell is the parent class for all map cells in the game. It's primary
 * purpose is to provide structure to it's children classes and to provide a
 * universal segment collision function for the children to use. All but one of
 * the global variables in this class are solely used in segmentCollision to
 * minimize the creation of variables for each iteration of the function.
 * 
 * @author Isaac Doub
 */
public abstract class GameCell extends GameObject {
	/** Current segment that will be tested for collision. */
	private Vector2D seg;
	/** Velocity of the player */
	private Vector2D vel;
	/** Normal to the segment */
	private Vector2D normal;
	/** Distance from player to segment in current frame. */
	private double D1;
	/** Distance from player to segment in next frame. */
	private double D2;
	/** Time of collision. */
	private double t;
	/** Position of player at time of collision. */
	private Vector2D collisionPosition;
	/** Point of intersection between player and segment. */
	private Vector2D intersectionPoint;
	
	/** Whether the current cell supports the player or not */
	public boolean support = true;
	
	/**
	 * GameCell constructor. Takes the cell image and creates a GameObject with
	 * the image.
	 * 
	 * @param png An integer reference to the image.
	 */
	public GameCell(int png) {
		// Call the GameObject constructor so the image is created.
		super(png);
	}
	
	/**
	 * Returns a string representing the position of the cell.
	 */
	public String toString() {
		return position.toString();
	}
	
	/**
	 * Calculates whether the player will collide with a segment in between
	 * this frame and the next.
	 * <p>
	 * This class is quite complicated and uses a lot of vector trigonometry in
	 * it's calculations. It's core functionality is based on Seb Lee-Delisle's
	 * post at http://seb.ly/2010/01/predicting-circle-line-collisions/ with a
	 * lot of modifications to make it work and to optimize it.
	 * 
	 * @param P1 First segment point
	 * @param P2 Second segment point
	 * @param player GamePlayer to check collision with
	 * @return true if it collided, false if it didn't
	 */
	protected boolean segmentCollision(Vector2D P1, Vector2D P2, GamePlayer player) {
		// Prepare variables that will be used commonly.
		seg = P2.sub(P1);
		vel = player.velocity;
		normal = seg.normal();
		if(vel.dot(normal) > 0) {
			normal = normal.reverse();
		}
		
		// Quickly check if the velocity intersects the line segment at all
		if(vel.cross(seg) != 0) {														// If 0 then the circle is moving parallel to the line 
			double t = (P1.sub(player.position).cross(seg)) / (vel.cross(seg));
			if(t < 0) return false;														// If t is negative, the circle is travelling away from the segment
		} else {
			return false;
		}
		
		// Do actual collision calculations
		D1 = Math.abs(player.position.sub(P1).dot(normal));								// Get current distance from line
		D2 = Math.abs(player.nextPosition.sub(P1).dot(normal));							// Get next distance from line

		// If you consider the distances as a linear function that varies over time between the current frame and the next, we can treat them as
		// two points and the change in distance as a line between the two. Collision occurs when the distance is equal to the radius of the circle
		// hence the equation for the line becomes "r = P + tV" where P is the origin point of the vector, V is the direction, and t the length.
		// Substituting and rearranging gives us "t = (r - D1) / (D2 - D1)" where D1 and D2 are the current and next distances.
		if(D1 == D2) return false;														// Caveat to prevent division by 0
		t = (player.radius - D1) / (D2 - D1);
		
		// If "t" is less than 0 the collision has happened in the past (should never happen, but just in case)
		// If "t" is greater than 1 then the collision won't happen in this frame but maybe in a following frame.
		if(t < 0 || t > 1) return false;
		
		// If it does happen in this frame, make sure there's no object closer that it should collide with instead
		if(t >= player.timeOfCollision) {
			return false;
		}
		
		collisionPosition = player.position.add(vel.mul(t));							// The position of the circle at the moment of collision
		intersectionPoint = collisionPosition.sub(normal.mul(player.radius));			// The point on the line where the circle touches
		
		// Now cause we're working with a segment and not an infinite line, we need to check that the point of intersection actually lies on the
		// segment or clips the end points.
		if(intersectionPoint.between(P1, P2)) {
			player.lastCollision = intersectionPoint;
			player.timeOfCollision = t;													// Set the current closest collision to this one
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Abstract class to force standard cell reaction when the player enters the
	 * cell.
	 * @param player
	 */
	protected abstract void react(GamePlayer player);
	
	/**
	 * Abstract class to force standard collision.
	 * If the cell has collision boundaries it should cycle through it's
	 * segments, using segmentCollision() to check for collision and modifying
	 * the player's velocity accordingly.
	 * @param player
	 */
	protected abstract boolean checkCollision(GamePlayer player);
}

/**
 * EmptyCell is simply the default/background cell that gets tiled across each
 * map and then replaced by the platform cells.
 * 
 * @author Isaac Doub
 */
class EmptyCell extends GameCell {	
	/**
	 * Creates a new cell with a provided X and Y position.
	 * 
	 * @param newX Desired X position of the cell.
	 * @param newY Desired Y position of the cell.
	 */
	public EmptyCell(int png, double newX, double newY) {
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
		support = false;
	}

	/**
	 * Protected function for reacting with the player. In the empty cell the
	 * player simply falls so this function iteratively scales the bitmap to
	 * simulate the falling animation until the image is 5 pixels, then resets
	 * the players position to the last good cell it was on.
	 */
	@Override
	protected void react(GamePlayer player) {
		if(!player.jumping) {
			if(player.img.getHeight() > 5) {
				player.velocity.zero();
				player.img = Bitmap.createScaledBitmap(player.img, (int)(player.img.getWidth()*0.75), (int)(player.img.getHeight()*0.75), false);
			} else {
				player.makeImage(R.drawable.marble);
				player.velocity = new Vector2D();
				player.position = player.lastCell.position.add(player.lastCell.width/2, player.lastCell.height/2);
				player.penalty++;
			}
		}
	}

	/**
	 * Function to check if there's a colision within this cell. Always returns
	 * false as it's the empty cell.
	 * @return false
	 */
	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}
}

/**
 * Platform is the default tile the player can navigate on.
 * 
 * @author Isaac Doub
 */
class Platform extends GameCell {
	/**
	 * Creates a new platform cell with the specified X and Y coordinates. The
	 * default constructor requires X and Y coordinates so the platforms do not
	 * pile up and mess up collisions.
	 * 
	 * @param newX
	 * @param newY
	 */
	public Platform(int png, double newX, double newY) {
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	/**
	 * Protected function for reacting with the player.
	 */
	@Override
	protected void react(GamePlayer player) {
	}

	/**
	 * Function to check if there's a colision within this cell. Always returns
	 * false.
	 * @return false
	 */
	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}

}

class PlatformEnd extends GameCell {
	
	public PlatformEnd(int png, double newX, double newY) {
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	@Override
	protected void react(GamePlayer player) {
		if(!player.jumping) {
			player.finshed = true;
			player.velocity.zero();
		}
	}

	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}
}

class BoostRight extends GameCell {
	
	public BoostRight(int png, double newX, double newY) {
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	@Override
	protected void react(GamePlayer player) {
		player.velocity = new Vector2D(10, 0);
	}

	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}
	
}

class BoostLeft extends GameCell {
	public BoostLeft(int png, double newX, double newY) {
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	@Override
	protected void react(GamePlayer player) {
		player.velocity = new Vector2D(-10, 0);
	}

	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}
}

class BoostDown extends GameCell {
	public BoostDown(int png, double newX, double newY) {
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	@Override
	protected void react(GamePlayer player) {
		player.velocity = new Vector2D(0, 10);
	}

	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}
}

class BoostUp extends GameCell {
	public BoostUp(int png, double newX, double newY) {
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	@Override
	protected void react(GamePlayer player) {
		player.velocity = new Vector2D(0, -10);
	}

	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}
}

class Jump extends GameCell {
	
	public Jump(int png, double newX, double newY)	{
		super(png);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	@Override
	protected void react(GamePlayer player) {
		if(!player.jumping) {
			player.jumping = true;
			player.jumpLength = 20;
			player.apex = 0;
		}
	}

	@Override
	protected boolean checkCollision(GamePlayer player) {
		return false;
	}
}	