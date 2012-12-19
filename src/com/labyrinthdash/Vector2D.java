package com.labyrinthdash;

public class Vector2D {
	public double x,y;
	
	/** CONSTRUCTORS **/
	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	// Copy constructor
	public Vector2D(Vector2D vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	/** OTHER USEFUL FUNCTIONS **/
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	/** MATHEMATICAL METHODS **/
	//ADD
	public Vector2D add(double x, double y) {return new Vector2D(this.x + x, this.y + y);}
	public Vector2D add(Vector2D vector) {return new Vector2D(this.x + vector.x, this.y + vector.y);}

	//SUBTRACT
	public Vector2D sub(double x, double y) {return new Vector2D(this.x - x, this.y - y);}
	public Vector2D sub(Vector2D vector) {return new Vector2D(this.x - vector.x, this.y - vector.y);}
	
	//MULTIPLY
	public Vector2D mul(double v) {return new Vector2D(this.x * v, this.y * v);}
	public Vector2D mul(Vector2D vector) {return new Vector2D(this.x * vector.x, this.y * vector.y);}

	//DIVIDE
	public Vector2D div(double v) {return new Vector2D(this.x / v, this.y / v);}
	public Vector2D div(Vector2D vector) {return new Vector2D(this.x / vector.x, this.y / vector.y);}
	
	//MAGNITUDE
	public double mag() {return Math.sqrt(x * x + y * y);}
	
	//UNIT
	public Vector2D unit() {return new Vector2D(this.div(this.mag()));}
	
	//NORMAL
	public Vector2D normal() {return this.turnRight().unit();}
	
	//LIMIT
	public Vector2D limit(double l) {return new Vector2D(this.unit().mul(l));}
	
	//ANGLE
	public double angle() {return Math.atan2(this.y, this.x);}
	
	//DISTANCE
	public double distance(Vector2D vector) {return vector.sub(this).mag();}
	
	//DOT
	public double dot(Vector2D vector) {return (this.x * vector.x) + (this.y * vector.y);}
	
	//CROSS
	public double cross(Vector2D vector) {return (this.x * vector.y) - (this.y * vector.x);}
	
	//TURN LEFT
	public Vector2D turnLeft() {return new Vector2D(-this.y, this.x);}
	
	//TURN RIGHT
	public Vector2D turnRight() {return new Vector2D(this.y, -this.x);}
	
	//ROTATE
	public Vector2D rotate(double angle) {return new Vector2D((this.x * Math.cos(angle)) - (this.y * Math.sin(angle)),(this.x * Math.sin(angle)) + (this.y * Math.cos(angle)));}
	
	//REVERSE
	public Vector2D reverse() {return new Vector2D(-this.x, -this.y);}
	
	//BETWEEN
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
