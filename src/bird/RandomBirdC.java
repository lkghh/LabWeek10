package bird;

import java.util.List;

import drawing.Canvas;

public class RandomBirdC extends DynamicBird {
	// 매직 넘버를 피하기 위한 상수
	private static final int MAX_ANGULAR_VELOCITY = 180; // 초당 최대 회전 각도 (180도/초)
	private static final int MAX_COUNTDOWN = 50; // 같은 각속도를 유지할 무작위 프레임 범위

	// 2단계: 각속도 및 카운터 필드 선언
	private int angularVelocity = 0;
	private int turnCountdown = 0;
	
	// 1단계: 2개의 생성자
	public RandomBirdC(Canvas canvas) {
		super(canvas);
	}

	public RandomBirdC(Canvas canvas, double xPosition, double yPosition) {
		super(canvas, xPosition, yPosition);
	}
	 // -----------------------------------------------------------
    // 💡 1. 최단 회전 각도 계산 헬퍼 메서드 (중요)
    // 350도에서 10도로 돌 때 340도를 도는 것이 아니라 +20도를 돌도록 보정합니다.
    // -----------------------------------------------------------
    private double getShortestTurn(double currentAngle, double targetAngle) {
        double diff = targetAngle - currentAngle;
        while (diff <= -180) diff += 360;
        while (diff > 180) diff -= 360;
        return diff;
    }
    
	// 3단계: update 메서드 재정의
    @Override
	public void update(int deltaTime, List<DynamicBird> flock, double wCohesion, double wAlignment, double wSeparation) {
    	
        int perceptionRadius = 150; // 이웃을 인식하는 시야 반경 (r) [4]
        int neighborCount = 0;
        
        double avgX = 0, avgY = 0;
        double avgAngleX = 0, avgAngleY = 0;

		turnCountdown--;

		
        for (DynamicBird other : flock) {
            if (other != this) {
                double dx = other.getPositionX() - this.getPositionX();
                double dy = other.getPositionY() - this.getPositionY();
                double flockDistance = Math.sqrt(dx * dx + dy * dy);

                if (flockDistance > 0 && flockDistance < perceptionRadius) {
                    avgX += other.getPositionX();
                    avgY += other.getPositionY();
                    // 방향의 평균을 정확히 구하기 위해 삼각함수 벡터 합산 사용
                    avgAngleX += Math.cos(Math.toRadians(other.getCurrentAngle()));
                    avgAngleY += Math.sin(Math.toRadians(other.getCurrentAngle()));
                    neighborCount++;
                }
            }
        }

        if (neighborCount > 0) {
            avgX /= neighborCount;
            avgY /= neighborCount;

            // ① Cohesion (응집): 이웃들의 평균 위치를 향하는 각도 계산 [7]
            double dxC = avgX - this.getPositionX();
            double dyC = avgY - this.getPositionY();
            double targetAngleC = Math.toDegrees(Math.atan2(dyC, dxC)); 
            if (targetAngleC < 0) targetAngleC += 360; 
            double thetaC = getShortestTurn(this.getCurrentAngle(), targetAngleC);

            // ② Separation (분리): 이웃들의 평균 위치의 반대 방향 각도 계산 [8]
            double targetAngleS = (targetAngleC + 180) % 360; 
            double thetaS = getShortestTurn(this.getCurrentAngle(), targetAngleS);

            // ③ Alignment (정렬): 이웃들의 평균 방향 각도 계산 [9]
            double targetAngleA = Math.toDegrees(Math.atan2(avgAngleY, avgAngleX)); 
            if (targetAngleA < 0) targetAngleA += 360; 
            double thetaA = getShortestTurn(this.getCurrentAngle(), targetAngleA);

            // ④ 가중치(kc, ka, ks)를 적용하여 최종 회전량(totalTurn) 결정 [8, 10, 11]
            double totalTurn = (wCohesion * thetaC) + (wAlignment * thetaA) + (wSeparation * thetaS);

            // 이웃이 있을 때는 무작위 비행을 멈추고 플로킹 힘에 따라 각속도를 부드럽게 조정
            this.angularVelocity = (int) totalTurn;
        } else {
            // 이웃이 없다면 기존의 무작위 곡선 비행 유지 (RandomTurtleC 로직) [12]
            turnCountdown--;
            if (turnCountdown <= 0) {
                this.angularVelocity = (int) (Math.random() * (MAX_ANGULAR_VELOCITY * 2 + 1)) - MAX_ANGULAR_VELOCITY;
                turnCountdown = (int) (Math.random() * MAX_COUNTDOWN) + 10;
            }
        }

		// 카운터가 0에 도달하면 각속도와 카운터 재설정 [1, 2]
		if (turnCountdown <= 0) {
			// -180 ~ +180 범위의 무작위 각속도 설정
			angularVelocity = (int) (Math.random() * (MAX_ANGULAR_VELOCITY * 2 + 1)) - MAX_ANGULAR_VELOCITY;
			turnCountdown = (int) (Math.random() * MAX_COUNTDOWN) + 10;
		}
		

        // 1. 실제로 이동하기 전에 다음 도착 예정 좌표를 예측합니다.
        double currentX = this.getPositionX();
        double currentY = this.getPositionY();
        boolean inDangerZone = false;

        
        // 장애물 1: 시각(100~160, 100~160) -> 논리(80~180, 80~180)
        if (currentX >= 80 && currentX <= 180 && currentY >= 80 && currentY <= 180) inDangerZone = true;
        
        // 장애물 2: 시각(100~160, 300~360) -> 논리(80~180, 280~380)
        if (currentX >= 80 && currentX <= 180 && currentY >= 280 && currentY <= 380) inDangerZone = true;
        
        // 장애물 3: 시각(350~430, 200~350) -> 논리(330~450, 180~370)
        if (currentX >= 330 && currentX <= 450 && currentY >= 180 && currentY <= 370) inDangerZone = true;
        
        // 장애물 4: 시각(550~700, 100~220) -> 논리(530~720, 80~240)
        if (currentX >= 530 && currentX <= 720 && currentY >= 80 && currentY <= 240) inDangerZone = true;
		// 부모 클래스(Turtle)에서 상속받은 move 메서드를 이용해 갱신된 거리만큼 이동
        // 4. 최종 행동 결정 및 이동 수행
        if (inDangerZone) {
            // 군집 본능이 아무리 강해도 장애물에 닿으면 즉시 무시하고 180도 튕겨냄
            this.turn(180);
            this.turnCountdown = 0; 
            
        } else {
            // 안전할 때는 각속도를 기반으로 부드럽게 회전 (반드시 실수형 나눗셈 사용) [15]
            int angleTurned = (int) (this.angularVelocity * (deltaTime / MILLISECONDS_PER_SECOND));
            this.turn(angleTurned);
        }

        // 5. 방향 결정이 모두 끝난 후 마지막에 1회 직진 전진
        int distance = (int) (this.getSpeed() * (deltaTime / MILLISECONDS_PER_SECOND));
        
        this.move(distance);
    }

}
