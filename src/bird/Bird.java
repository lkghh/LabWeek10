package bird;

import drawing.Canvas;
import geometry.CartesianCoordinate;

public class Bird {
	private Canvas canvas;
	private CartesianCoordinate currentPosition;
	private int currentAngle;
	private boolean isPenDown;

	// Constructor
	public Bird(Canvas canvas) {
		this.canvas = canvas;
		// Start at a position avoiding the top-left corner
		this.currentPosition = new CartesianCoordinate(200.0, 200.0);
		this.setCurrentAngle(0); // Set initial direction to 0 degrees
		this.isPenDown = false; // By default, pen is up.
	}

	// Getter returning bird’s x and y positions
	public int getPositionX() {
		return (int) this.currentPosition.getX();
	}

	public int getPositionY() {
		return (int) this.currentPosition.getY();
	}

	// Getter returning bird’s angle
	public int getCurrentAngle() {
		return currentAngle;
	}

	// Setter for bird’s angle
	public void setCurrentAngle(int currentAngle) {
		this.currentAngle = currentAngle;
	}

	// Method to make bird reappear on the opposite side if it flies off screen
	public void wrapPosition(int canvasWidth, int canvasHeight) {
		// Get current x and y coordinates
		double currentX = this.currentPosition.getX();
		double currentY = this.currentPosition.getY();

		// Variable to track whether the position has changed
		boolean isChanged = false;

		// Check x-coordinate: if less than 0, move to the right edge;
		// if greater than the maximum width, move to 0
		if (currentX < 0) {
			currentX = canvasWidth;
			isChanged = true;
		} else if (currentX > canvasWidth) {
			currentX = 0;
			isChanged = true;
		}

		// Check Y coordinate: if less than 0, move to the bottom edge;
		// if greater than the maximum height, move to 0
		if (currentY < 0) {
			currentY = canvasHeight;
			isChanged = true;
		} else if (currentY > canvasHeight) {
			currentY = 0;
			isChanged = true;
		}

		// Assign new coordinates only when the position has changed
		if (isChanged) {
			this.currentPosition = new CartesianCoordinate(currentX, currentY);
		}
	}

	// Putting pen down
	public void putPenDown() {
		this.isPenDown = true;
	}

	// Putting pen up
	public void putPenUp() {
		this.isPenDown = false;
	}

	// Rotate by the specified integer angle
	public void turn(int angle) {
		this.setCurrentAngle((this.getCurrentAngle() + angle) % 360);
	}

	// move by integer number of pixels
	public void move(int distance) {
		// Convert current direction to radians
		double radians = Math.toRadians(this.getCurrentAngle());

		// Calculate new coordinates reflecting the canvas coordinate system
		// where the y-axis points downwards
		double newX = currentPosition.getX() + (distance * Math.cos(radians));
		double newY = currentPosition.getY() + (distance * Math.sin(radians));

		CartesianCoordinate newPosition = new CartesianCoordinate(newX, newY);

		// If pen is down, draw a line on the canvas
		if (isPenDown) {
			canvas.drawLineBetweenPoints(currentPosition, newPosition);
		}

		// Update position after movement
		this.currentPosition = newPosition;
	}

	public void draw() {
		// Display Bird's position
		// Backup turtle’s current position, angle and pen state to temporary variables
		CartesianCoordinate originalPosition = this.currentPosition;
		int originalAngle = this.getCurrentAngle();
		boolean wasPenDown = this.isPenDown;

		// Lower pen and draw Bird
		this.putPenDown();
		this.move(20);
		this.turn(120);
		this.move(20);
		this.turn(120);
		this.move(20);

		// Go back to original position
		this.currentPosition = originalPosition;
		this.setCurrentAngle(originalAngle);

		// If the pen was originally up, raise it again; if it was down, keep it down
		if (!wasPenDown) {
			this.putPenUp();
		}

	}

	public void undraw() {
		// Erase bird
		this.canvas.removeMostRecentLine();
		this.canvas.removeMostRecentLine();
		this.canvas.removeMostRecentLine();

		this.canvas.repaint();

	}

}