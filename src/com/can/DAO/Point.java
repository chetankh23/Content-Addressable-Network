package com.can.DAO;

import java.io.Serializable;

/**
 * The class Point is used to maintain coordinates of virtual 2-dimensional
 * space.
 * 
 * @author chetan
 *
 */
public class Point implements Serializable {

	public double x, y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
		this.x = 0.0;
		this.y = 0.0;
	}

	void setX(double x) {
		this.x = x;
	}

	void setY(double y) {
		this.y = y;
	}

	double getX() {
		return x;
	}

	double getY() {
		return y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public boolean equals(Point p) {
		if (this.x == p.x && this.y == p.y)
			return true;
		else
			return false;
	}
}
