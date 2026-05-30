package geometry;

public class LineSegment {
	private final CartesianCoordinate startPoint;
	private final CartesianCoordinate endPoint;
	
	public LineSegment(CartesianCoordinate startPoint, CartesianCoordinate endPoint) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public CartesianCoordinate getStartPoint() {
		return startPoint;
	}

	public CartesianCoordinate getEndPoint() {
		return endPoint;
	}
	
	public String toString() {
	    return "(" + startPoint + ", " + endPoint + ")";
	}

	public double length() {
			double x1 = startPoint.getX();
			double y1 = startPoint.getY();
			double x2 = endPoint.getX();
			double y2 = endPoint.getY();
			
			double deltaX = x2 - x1;
			double deltaY = y2 - y1;
			
			double length = Math.hypot(deltaX, deltaY);
			return length;
	}		
	
}