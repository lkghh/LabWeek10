package bird;

import java.util.List;

import drawing.Canvas;

public class DynamicBird extends Bird {
	// 2단계: 속도 필드 (초당 100픽셀 이동)
	private int speed = 100;

	// 단위 변환을 위한 상수 (int형 연산 오류 방지)
	protected static final double MILLISECONDS_PER_SECOND = 1000.0;

	public DynamicBird(Canvas canvas) {
		super(canvas);
		draw();
	}

	public DynamicBird(Canvas canvas, double xPosition, double yPosition) {
		super(canvas);

		// 2. 초기 위치로 이동하는 동안 선이 그려지지 않도록 펜을 올림
		this.putPenUp();

		// 3. 지정된 좌표로 이동 (기본 위치 (0,0)에서 시작)
		// 랩 4의 Shape 생성자에서 했던 것처럼 y축으로 먼저 이동 후 x축으로 이동합니다.
		this.turn(90); // 아래쪽(y축 양의 방향)을 향하게 회전
		this.move((int) yPosition); // y좌표만큼 이동

		this.turn(-90); // 원래 방향인 오른쪽(x축 양의 방향)을 향하게 회전
		this.move((int) xPosition); // x좌표만큼 이동

		// 4. 원래 바라보던 방향을 유지한 채로 거북이를 화면에 그림
		this.draw();
	}

	// 속도 Getter & Setter
	public int getSpeed() {
		return speed;
	}
	

	
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * 3단계: 경과된 시간에 따라 거북이의 위치를 갱신합니다.
	 * 
	 * @param deltaTime 마지막 갱신 이후 경과된 시간 (밀리초 단위)
	 */
	public void update(int deltaTime) {
		// 거리 = 속도 * 시간
	    int distance = (int) (this.speed * (deltaTime / MILLISECONDS_PER_SECOND));
	    
	    // 계산된 거리만큼 실제로 움직이도록 move() 호출 추가
	    this.move(distance);	
	    }
 
    public void update(int deltaTime, List<DynamicBird> flock, double wCohesion, double wAlignment, double wSeparation) {
        // 부모 클래스인 DynamicBird는 플로킹 지능이 없으므로 단순 직진인 기존 update만 호출합니다.
        // 실제 군집 로직은 이것을 오버라이드(Override)하는 RandomBirdC에서 실행됩니다.
        this.update(deltaTime);
    }

	

}
