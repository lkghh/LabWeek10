package bird;

import java.util.List;

import drawing.Canvas;

public class DynamicBird extends Bird {
	// Speed of Bird: 100 pixels per second
	private int speed = 100;

	// Constant for unit conversion
	protected static final double MILLISECONDS_PER_SECOND = 1000.0;

	// Constructors
	public DynamicBird(Canvas canvas) {
		super(canvas);
		draw();
	}

	public DynamicBird(Canvas canvas, double xPosition, double yPosition) {
		super(canvas);

		// Lift the pen while moving to the initial position
		this.putPenUp();

		this.turn(90);
		this.move((int) yPosition);

		this.turn(-90);
		this.move((int) xPosition);

		// Draw bird
		this.draw();
	}

	// Speed Getter & Setter
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	// Update location
	public void update(int deltaTime) {
		int distance = (int) (this.speed * (deltaTime / MILLISECONDS_PER_SECOND));

		// Move after calculating distance
		this.move(distance);
	}

	public void update(int deltaTime, List<DynamicBird> flock, double wCohesion, double wAlignment,
			double wSeparation) {

		// Actual flocking logic is executed in RandomBirdC, which overrides this
		this.update(deltaTime);
	}

}
