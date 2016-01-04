package com.can.DAO;

import java.io.Serializable;

/**
 * The class Zone is bind to each peer which includes coordinates of their
 * respective zones.
 * 
 * @author chetan
 *
 */
public class Zone implements Serializable {

	public Point lowerLeft, lowerRight, upperLeft, upperRight;

	public Zone() {
		lowerLeft = new Point(0.0, 0.0);
		lowerRight = new Point(10.0, 0.0);
		upperLeft = new Point(0.0, 10.0);
		upperRight = new Point(10.0, 10.0);
	}

	public Zone(Point p1, Point p2, Point p3, Point p4) {
		this.lowerLeft = p1;
		this.lowerRight = p2;
		this.upperLeft = p3;
		this.upperRight = p4;
	}

	public void setPoints(Point p1, Point p2, Point p3, Point p4) {
		this.lowerLeft = p1;
		this.lowerRight = p2;
		this.upperLeft = p3;
		this.upperRight = p4;
	}

	public String toString() {
		return lowerLeft.toString() + lowerRight.toString() + upperLeft.toString() + upperRight.toString();
	}
}
