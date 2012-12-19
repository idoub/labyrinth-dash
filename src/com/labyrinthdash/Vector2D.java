package com.labyrinthdash;

/**
 * Vector2D provides a structure for points and vectors and is a fundamental
 * class in the physics of Labyrinth Dash. It provides a host of methods for
 * manipulating vectors and comparing them.
 * <p>
 * A single class can be used for points and vectors as point can simply be
 * represented as a vector from the origin in a specific direction.
 * <p>
 * It is important to note that none of the functions in this class modify the
 * values of the class. Rather each function returns a new vector where
 * appropriate.
 * 
 * @author Isaac Doub
 */
public class Vector2D {
	/** X and Y of the vector */
	public double x,y;
	
	/** Zero vector constructor */
	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Creates a Vector2D with the specified x and y
	 * 
	 * @param x
	 * @param y
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param vector
	 */
	public Vector2D(Vector2D vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	/**
	 * String representation of the vector
	 * 
	 * @return String in the form "(x,y)".
	 */
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	/**
	 * Returns a new <code>Vector2D</code> with the specified values added to
	 * it's X and Y.
	 * @param x Value to add to the X direction
	 * @param y Value to add to the Y direction
	 * @return A new <code>Vector2D</code> with the values added
	 */
	public Vector2D add(double x, double y) {return new Vector2D(this.x + x, this.y + y);}
	
	/**
	 * Returns a new <code>Vector2D</code> which is the addition of two vectors.
	 * @param vector Vector to add to the current one
	 * @return A new <code>Vector2D</code> with the provided vector added to it
	 */
	public Vector2D add(Vector2D vector) {return new Vector2D(this.x + vector.x, this.y + vector.y);}

	/**
	 * Returns a new <code>Vector2D</code> with the specified values subtracted
	 * from it's X and Y.
	 * @param x Value to subtract from the X direction
	 * @param y Value to subtract from the Y direction
	 * @return A new <code>Vector2D</code> with the values subtracted
	 */
	public Vector2D sub(double x, double y) {return new Vector2D(this.x - x, this.y - y);}
	
	/**
	 * Returns a new <code>Vector2D</code> which is the subtraction of one
	 * vector from the current.
	 * @param vector The vector to subtract from this one
	 * @return A new <code>Vector2D</code> with the provided vector subtracted
	 */
	public Vector2D sub(Vector2D vector) {return new Vector2D(this.x - vector.x, this.y - vector.y);}
	
	/**
	 * Returns a new <code>Vector2D</code> which is the multiplication of a
	 * vector by a scalar value.
	 * @param v Value to multiply by
	 * @return A new <code>Vector2D</code> scaled by a value
	 */
	public Vector2D mul(double v) {return new Vector2D(this.x * v, this.y * v);}
	
	/**
	 * Returns a new <code>Vector2D</code> which is the multiplication of one
	 * vector's X and Y components by another vector's X and Y components.
	 * @param vector Vector to be multiplied by
	 * @return A new <code>Vector2D</code> which is the multiplication of the
	 * separate values of two vectors
	 */
	public Vector2D mul(Vector2D vector) {return new Vector2D(this.x * vector.x, this.y * vector.y);}

	/**
	 * Returns a new <code>Vector2D</code> which is the division of a vector by
	 * a scalar value.
	 * @param v Value to divide by
	 * @return A new <code>Vector2D</code> scaled negatively by a value
	 */
	public Vector2D div(double v) {return new Vector2D(this.x / v, this.y / v);}
	/**
	 * Returns a new <code>Vector2D</code> which is the division of one vector's
	 * X and Y components by another vector's X and Y components.
	 * @param vector The vector this one will be divided by
	 * @return A new <code>Vector2D</code> that is the current vector divided by
	 * the provided vector.
	 */
	public Vector2D div(Vector2D vector) {return new Vector2D(this.x / vector.x, this.y / vector.y);}
	
	/**
	 * Returns the scalar magnitude of the vector.
	 * @return The magnitude of the vector as a <code>double</code>
	 */
	public double mag() {return Math.sqrt(x * x + y * y);}
	
	/**
	 * Returns a new <vode>Vector2D</code> which is the unit vector in the same
	 * direction as the current vector.
	 * @return A new <code>Vector2D</code> of unit length in the same direction
	 * as the current
	 */
	public Vector2D unit() {return new Vector2D(this.div(this.mag()));}
	
	/**
	 * Returns a new <code>Vector2D</code> that is the normal to the current
	 * vector.
	 * @return A new <code>Vector2D</code> that is the normal to the current
	 */
	public Vector2D normal() {return this.turnRight().unit();}
	
	/**
	 * Returns a new <code>Vector2D</code> which is in the same direction as the
	 * current vector but has the specified magnitude/length.
	 * @param l The desired length of the vector
	 * @return A new <code>Vector2D</code> in the same direction that is of
	 * length <i>l</i>
	 */
	public Vector2D limit(double l) {return new Vector2D(this.unit().mul(l));}
	
	/**
	 * Returns the angle <i>theta</i> from the conversion of rectangular
	 * coordinates (x, y) to polar coordinates (r, <i>theta</i>) as a <code>
	 * double</code>
	 * @return <i>theta</i> as <code>double</code>
	 */
	public double angle() {return Math.atan2(this.y, this.x);}
	
	/**
	 * Returns the scalar distance between two vectors as a <code>double</code>.
	 * This makes more sense when the class is representing points than vectors
	 * but can also be applicaple for vectors.
	 * @param vector The vector to find the distance to
	 * @return The distance between vectors as <code>double</code>
	 */
	public double distance(Vector2D vector) {return vector.sub(this).mag();}
	
	/**
	 * Returns the scalar dot product of two vectors as a <code>double</code>.
	 * @param vector The vector to be dotted with.
	 * @return The dot product as a <code>double</code>
	 */
	public double dot(Vector2D vector) {return (this.x * vector.x) + (this.y * vector.y);}
	
	/**
	 * Returns the vector cross product of two vectors as a <code>double</code>
	 * calculated using the 'determinant' rule. The vector cross product is not
	 * defined for 2D space. In this case it is a representation of the
	 * orientation of one vector against another, similar to the dot product.
	 * @param vector The vector to be crossed with.
	 * @return The corss product as a <code>double</code>
	 */
	public double cross(Vector2D vector) {return (this.x * vector.y) - (this.y * vector.x);}
	
	/**
	 * Returns a <code>Vector2D</code> which is the current vector rotated
	 * 90&deg; counter-clockwise.
	 * @return A <code>Vector2d</code> which is the current vector rotated
	 * 90&deg; counter-clockwise.
	 */
	public Vector2D turnLeft() {return new Vector2D(-this.y, this.x);}
	
	/**
	 * Returns a <code>Vector2D</code> which is the current vector rotated
	 * 90&deg; clockwise.
	 * @return A <code>Vector2D</code> which is the current vector rotated
	 * 90&deg; clockwise.
	 */
	public Vector2D turnRight() {return new Vector2D(this.y, -this.x);}
	
	/**
	 * Returns a <code>Vector2D</code> which is the current vector rotated by a
	 * specified angle.
	 * @param angle The angle of rotation
	 * @return A <code>Vector2D</code> which is the current vector rotated by a
	 * <code>angle</code>
	 */
	public Vector2D rotate(double angle) {return new Vector2D((this.x * Math.cos(angle)) - (this.y * Math.sin(angle)),(this.x * Math.sin(angle)) + (this.y * Math.cos(angle)));}
	
	/**
	 * Returns the current vector pointing in the opposite direction.
	 * @return A <code>Vector2D</code> which is the current vector rotated by
	 * 180&deg;.
	 */
	public Vector2D reverse() {return new Vector2D(-this.x, -this.y);}
	
	/**
	 * Tests whether a this point lies on the line formed by the provided two
	 * points.
	 * @param point1 First point on the line
	 * @param point2 Second point on the line
	 * @return True if the three points are in a line, false if not
	 */
	public boolean between(Vector2D point1, Vector2D point2) {
		if(this.x >= point1.x && this.x <= point2.x || this.x >= point2.x && this.x <= point1.x) {
			if(this.y >= point1.y && this.y <= point2.y || this.y >= point2.y && this.y <= point1.y) {
				return true;
			}
			return false;
		}
		return false;
	}
}
