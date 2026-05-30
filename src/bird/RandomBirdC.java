package bird;

import java.util.List;

import drawing.Canvas;

public class RandomBirdC extends DynamicBird {
	// Constants
	private static final int MAX_ANGULAR_VELOCITY = 180; // Maximum angular velocity (180 degrees per second)
	private static final int MAX_COUNTDOWN = 50; // Range of random frames angular velocity remains constant
	private static final int PERCEPTION_RADIUS = 150; // Field of view radius for detecting neighbours

	// angularVelocity and countdown
	private int angularVelocity = 0;
	private int turnCountdown = 0;

	// Constructors
	public RandomBirdC(Canvas canvas) {
		super(canvas);
	}

	public RandomBirdC(Canvas canvas, double xPosition, double yPosition) {
		super(canvas, xPosition, yPosition);
	}

	// When rotating from 350 degrees to 10 degrees,
	// this corrects the rotation so that it turns +20 degrees rather than 340
	// degrees
	private double getShortestTurn(double currentAngle, double targetAngle) {
		double diff = targetAngle - currentAngle;
		while (diff <= -180)
			diff += 360;
		while (diff > 180)
			diff -= 360;
		return diff;
	}

	// Override update method
	@Override
	public void update(int deltaTime, List<DynamicBird> flock, double wCohesion, double wAlignment,
			double wSeparation) {

		int neighborCount = 0;

		double avgX = 0, avgY = 0;
		double avgAngleX = 0, avgAngleY = 0;

		turnCountdown--;

		for (DynamicBird other : flock) {
			if (other != this) {
				double dx = other.getPositionX() - this.getPositionX();
				double dy = other.getPositionY() - this.getPositionY();
				double flockDistance = Math.sqrt(dx * dx + dy * dy);

				if (flockDistance > 0 && flockDistance < PERCEPTION_RADIUS) {
					avgX += other.getPositionX();
					avgY += other.getPositionY();
					// Use trigonometric vector addition to calculate average
					avgAngleX += Math.cos(Math.toRadians(other.getCurrentAngle()));
					avgAngleY += Math.sin(Math.toRadians(other.getCurrentAngle()));
					neighborCount++;
				}
			}
		}

		if (neighborCount > 0) {
			avgX /= neighborCount;
			avgY /= neighborCount;

			// Cohesion: Calculate the angle pointing towards the average position of
			// neighbours
			double dxC = avgX - this.getPositionX();
			double dyC = avgY - this.getPositionY();
			double targetAngleC = Math.toDegrees(Math.atan2(dyC, dxC));
			if (targetAngleC < 0)
				targetAngleC += 360;
			double thetaC = getShortestTurn(this.getCurrentAngle(), targetAngleC);

			// Separation: Calculate the angle in the opposite direction to the average
			// position
			// of neighbours
			double targetAngleS = (targetAngleC + 180) % 360;
			double thetaS = getShortestTurn(this.getCurrentAngle(), targetAngleS);

			// Alignment: Calculate the average direction angle of neighbours
			double targetAngleA = Math.toDegrees(Math.atan2(avgAngleY, avgAngleX));
			if (targetAngleA < 0)
				targetAngleA += 360;
			double thetaA = getShortestTurn(this.getCurrentAngle(), targetAngleA);

			// Determine the final rotation amount by applying weights from sliders
			double totalTurn = (wCohesion * thetaC) + (wAlignment * thetaA) + (wSeparation * thetaS);

			// Stop random movement when neighbours are present
			this.angularVelocity = (int) totalTurn;
		} else {
			turnCountdown--;
			if (turnCountdown <= 0) {
				this.angularVelocity = (int) (Math.random() * (MAX_ANGULAR_VELOCITY * 2 + 1)) - MAX_ANGULAR_VELOCITY;
				turnCountdown = (int) (Math.random() * MAX_COUNTDOWN) + 10;
			}
		}

		// reset the angular velocity and counter when counter hits 0
		if (turnCountdown <= 0) {
			// Set random angular velocity
			angularVelocity = (int) (Math.random() * (MAX_ANGULAR_VELOCITY * 2 + 1)) - MAX_ANGULAR_VELOCITY;
			turnCountdown = (int) (Math.random() * MAX_COUNTDOWN) + 10;
		}

		double currentX = this.getPositionX();
		double currentY = this.getPositionY();
		boolean inDangerZone = false;

		// Margin is 20
		// Collision detection for Obstacle 1 at (100, 100)
		if (currentX >= 80 && currentX <= 180 && currentY >= 80 && currentY <= 180)
			inDangerZone = true;

		// Collision detection for Obstacle 2 at (100, 300)
		if (currentX >= 80 && currentX <= 180 && currentY >= 280 && currentY <= 380)
			inDangerZone = true;

		// Collision detection for Obstacle 3 at (350, 200)
		if (currentX >= 330 && currentX <= 450 && currentY >= 180 && currentY <= 370)
			inDangerZone = true;

		// Collision detection for Obstacle 4 at (550, 100)
		if (currentX >= 530 && currentX <= 720 && currentY >= 80 && currentY <= 240)
			inDangerZone = true;

		if (inDangerZone) {
			// deflect by 180 degrees after meeting obstacles
			this.turn(180);
			this.turnCountdown = 0;

		} else {
			// When safe, rotate smoothly based on the angular velocity
			int angleTurned = (int) (this.angularVelocity * (deltaTime / MILLISECONDS_PER_SECOND));
			this.turn(angleTurned);
		}

		// After directional decisions, move forward
		int distance = (int) (this.getSpeed() * (deltaTime / MILLISECONDS_PER_SECOND));

		this.move(distance);
	}

}
