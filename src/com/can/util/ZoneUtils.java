package com.can.util;

import java.rmi.RemoteException;

import com.can.DAO.Point;
import com.can.DAO.Zone;

/**
 * The class ZoneUtils includes zone operations such as splitting a given zone
 * equally, verifying if the point is lying in given zone, etc.
 * 
 * @author chetan
 *
 */
public class ZoneUtils {

	public static double genRandomCoordinate(double max, double min)
			throws RemoteException {
		
		double r = Math.random();
		
		if (r < 0.5) {
			
			return ((1 - Math.random()) * (max - min) + min);
		}
		
		return (Math.random() * (max - min) + min);
	}
	
	
	
	public static double getDistance(Point a, Point b)
	{
		double dist_side1=a.x-b.x;
		
		double dist_side2=a.y-b.y;
		
		return Math.sqrt((dist_side1*dist_side1)+(dist_side2*dist_side2));
	}
	
	public static double findMinDistance(Point randomP, Zone zone) {
		
		double minX=Math.min(getDistance(randomP, zone.lowerLeft), getDistance(randomP, zone.lowerRight));
		
		double minY=Math.min(getDistance(randomP, zone.upperLeft), getDistance(randomP, zone.upperRight));
		
		return Math.min(minX, minY);
	}
	
	
	
	public static boolean checkPointInZone(double x, double y, Zone zone) {
		
		if((x>=zone.lowerLeft.x && x<=zone.lowerRight.x) && (y>=zone.lowerLeft.y && y<=zone.upperLeft.y))
			return true;
		
		return false;

	}
	
	
	public static boolean checkPointInZone(Point p,Zone currentZone) {
		if ((currentZone.lowerLeft.x <= p.x) && (p.x <= currentZone.lowerRight.x)
				&& (currentZone.lowerLeft.y <= p.y) && (p.y <= currentZone.upperLeft.y)) {
			return true;
		} else
			return false;
	}
	
	
	public static Zone splitZone(Zone myZone) {
		
		Zone newOccupantZone=null;
		
		if (((myZone.lowerRight.x - myZone.lowerLeft.x) == (myZone.upperLeft.y - myZone.lowerLeft.y)) || ((myZone.lowerRight.x - myZone.lowerLeft.x) > (myZone.upperLeft.y - myZone.lowerLeft.y))) {
			
			double newXValue = (myZone.lowerLeft.x + myZone.lowerRight.x) / 2;
			
			double oldXValue = myZone.lowerRight.x;
			
			myZone.lowerRight.x = newXValue;
			
			myZone.upperRight.x = newXValue;
			
			newOccupantZone = new Zone(new Point(newXValue, myZone.lowerRight.y), new Point(oldXValue, myZone.lowerRight.y),
					new Point(newXValue, myZone.upperRight.y), new Point(oldXValue, myZone.upperRight.y));

		} else if ((myZone.lowerRight.x - myZone.lowerLeft.x) < (myZone.upperRight.y - myZone.lowerRight.y)) {
			
			double oldYValue = myZone.upperLeft.y;
			
			double newYValue = (myZone.lowerLeft.y + myZone.upperLeft.y) / 2;
			
			myZone.upperLeft.y = newYValue;
			
			myZone.upperRight.y = newYValue;
			
			newOccupantZone = new Zone(new Point(myZone.upperLeft.x, newYValue), new Point(myZone.upperRight.x, newYValue),
					new Point(myZone.upperLeft.x, oldYValue), new Point(myZone.upperRight.x, oldYValue));
		}
		
		return newOccupantZone;
	}
	
	
	public static boolean hasCommonEdge(Zone z1, Zone z2) {
		
		boolean commonWidth = (z1.lowerLeft.x == z2.upperLeft.x
				&& (z1.lowerRight.x > z1.lowerLeft.x && z2.upperRight.x > z1.lowerLeft.x) && z1.lowerLeft.y == z2.upperLeft.y)
				|| (z1.upperLeft.x == z2.lowerLeft.x
						&& (z1.upperRight.x > z1.upperLeft.x && z2.lowerRight.x > z1.upperLeft.x) && z1.upperLeft.y == z2.lowerLeft.y)
				
				|| ((z1.lowerRight.x == z2.upperRight.x) && (z1.lowerRight.y == z2.upperRight.y) && ((z1.lowerLeft.x < z1.lowerRight.x) && (z2.upperLeft.x < z2.upperRight.x)))
				|| ((z1.upperRight.x == z2.lowerRight.x) && (z1.upperRight.y == z2.lowerRight.y) && ((z1.upperLeft.x < z1.upperRight.x) && (z2.lowerLeft.x < z1.upperRight.x)))
				
				
				|| (z1.upperLeft.x == z2.lowerLeft.x && z1.upperLeft.y == z2.lowerLeft.y && ((z1.upperRight.x > z1.upperLeft.x) && (z2.lowerRight.x > z1.upperLeft.x)))
				
				
				
				|| (z1.upperLeft.x <= z2.lowerLeft.x && z1.upperLeft.y == z2.lowerLeft.y
						&& z1.upperRight.x >= z2.lowerRight.x && z1.upperLeft.y == z2.lowerLeft.y)
				|| (z2.upperLeft.x <= z1.lowerLeft.x && z2.upperLeft.y <= z1.lowerLeft.y
						&& z2.upperRight.x >= z1.lowerRight.x && z2.upperLeft.y == z1.lowerLeft.y);
		
		boolean commonHeight = (z1.lowerRight.x == z2.lowerLeft.x && z1.lowerRight.y == z2.lowerLeft.y && z1.upperRight.x <= z2.upperLeft.x)
				|| ((z1.lowerRight.x == z2.lowerLeft.x) && (z1.lowerRight.y == z2.lowerLeft.y)
						&& (z1.upperRight.y > z1.lowerRight.y) && (z2.upperLeft.y > z1.lowerRight.y))
				|| ((z2.lowerRight.x == z1.lowerLeft.x) && (z2.lowerRight.y == z1.lowerLeft.y)
						&& (z2.upperRight.y > z2.lowerRight.y) && (z1.upperLeft.y > z2.lowerRight.y))
				|| ((z1.upperRight.x == z2.upperLeft.x) && (z2.upperRight.y == z1.upperLeft.y)
						&& (z1.lowerRight.y < z1.upperRight.y) && (z2.lowerLeft.y < z1.upperRight.y))
				|| ((z2.upperRight.x == z1.upperLeft.x) && (z2.upperRight.y == z1.upperLeft.y)
						&& (z2.lowerRight.y < z2.upperRight.y) && (z1.lowerLeft.y < z2.upperRight.y))
				|| (z1.lowerLeft.x == z2.lowerRight.x && z1.lowerLeft.y == z2.lowerRight.y && z1.upperLeft.x >= z2.upperRight.x);

		return (commonWidth || commonHeight);
	}
	
	
	public static boolean isSquareOrRect(Zone z1, Zone z2) {
		
		boolean squareOrRect = (z1.lowerLeft.equals(z2.upperLeft) && z1.lowerRight.equals(z2.upperRight))
				|| (z2.lowerLeft.equals(z1.upperLeft) && z2.lowerRight.equals(z1.upperRight))
				|| (z1.lowerLeft.equals(z2.lowerRight) && z1.upperLeft.equals(z2.upperRight))
				|| (z2.lowerLeft.equals(z1.lowerRight) && z2.upperLeft.equals(z1.upperRight));
		
		return squareOrRect;

	}

}
