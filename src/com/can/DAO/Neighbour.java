package com.can.DAO;

/**
 * The class Neighbour is used as a custom data structure to maintain a list of
 * neighbour with their respective details.
 * 
 * @author chetan
 *
 */
public class Neighbour {

	private double upperRightX;
	private double upperRightY;
	private double lowerLeftX;
	private double lowerLeftY;

	public Neighbour(double upperRightXVal, double upperRightYVal, double lowerLeftXVal, double lowerLeftYVal) {

		upperRightX = upperRightXVal;
		
		upperRightY = upperRightYVal;
		
		lowerLeftX = lowerLeftXVal;
		
		lowerLeftY = lowerLeftYVal;
	}

	public Double getUpperRightXValue() {
		
		return upperRightX;
	}

	public void setUpperRightXValue(double upperRightXVal) {
		
		upperRightX = upperRightXVal;
	}

	public Double getUpperRightYValue() {
		
		return upperRightY;
	}

	public void setUpperRightYValue(double upperRightYVal) {
		
		upperRightY = upperRightYVal;
	}

	public Double getLowerLeftXValue() {
		
		return lowerLeftX;
	}

	public void setLowerLeftXValue(double lowerLeftXVal) {
		
		lowerLeftX = lowerLeftXVal;
	}

	public Double getLowerLeftYValue() {
		
		return lowerLeftY;
	}
	
	public void setLowerLeftYValue(double lowerLeftYVal) {
		
		lowerLeftY = lowerLeftYVal;
	}

}
