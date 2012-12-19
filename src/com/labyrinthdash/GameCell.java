package com.labyrinthdash;

public abstract class GameCell extends GameObject {
	private Vector2D seg;
	private Vector2D vel;
	private Vector2D normal;
	
	private double D1;
	private double D2;
	private double t;
	
	private Vector2D collisionPosition;
	private Vector2D intersectionPoint;
	
	protected boolean space = true;
	
	public GameCell(int png) {
		// Call the GameObject constructor so the image is created.
		super(png);
	}
	
	public String toString() {
		return position.toString();
	}
	
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
	
	/* 
	 * Each cell should cycle through it's segments, using segmentCollision()
	 * to check for collision and modifying the player's velocity accordingly
	*/
	protected abstract boolean hasCollided(GamePlayer player);
}

class EmptyCell extends GameCell {
	public EmptyCell() {
		super(R.drawable.spaceblack);
	}
	
	public EmptyCell(double newX, double newY) {
		super(R.drawable.spaceblack);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
	}

	@Override
	protected boolean hasCollided(GamePlayer player) {
		return false;
	}
}

/*class CellWall_H extends GameCell {
	Vector2D p1;
	Vector2D p2;
	
	public CellWall_H(double newX, double newY) {
		super(R.drawable.wall_h);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
		p1 = new Vector2D(this.position.x, this.position.y + (this.img.getHeight() * 0.5));
		p2 = new Vector2D(this.position.x + this.img.getWidth(), this.position.y + (this.img.getHeight() * 0.5));
	}

	@Override
	protected boolean hasCollided(GamePlayer player) {
		return this.segmentCollision(p1, p2, player);
	}
}*/

/*class CellWall_V extends GameCell {
	Vector2D p1;
	Vector2D p2;
	
	public CellWall_V(double newX, double newY) {
		super(R.drawable.wall_v);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
		p1 = new Vector2D(this.position.x + (this.img.getWidth() * 0.5), this.position.y);
		p2 = new Vector2D(this.position.x + (this.img.getWidth() * 0.5), this.position.y + this.img.getHeight());
	}

	@Override
	protected boolean hasCollided(GamePlayer player) {
		return  this.segmentCollision(p1, p2, player);
	}
}*/

class Platform extends GameCell {
	public Platform(double newX, double newY) {
		super(R.drawable.metalplatform);
		position = new Vector2D(newX*img.getWidth(), newY*img.getHeight());
		space = false;
	}

	@Override
	protected boolean hasCollided(GamePlayer player) {
		// TODO Auto-generated method stub
		return false;
	}
		
}