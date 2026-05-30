package bird;

import drawing.Canvas;
import geometry.CartesianCoordinate;

public class Bird {
    private Canvas canvas;
    private CartesianCoordinate currentPosition;
    private int currentAngle; // 각도 (도 단위)
    private boolean isPenDown;

    // 생성자
    public Bird(Canvas canvas) {
        this.canvas = canvas;
        // 힌트에 따라 화면 좌측 상단(0,0)을 피해 임의의 넉넉한 위치에서 시작합니다.
        this.currentPosition = new CartesianCoordinate(200.0, 200.0);
        this.setCurrentAngle(0); // 초기 방향은 0도로 설정 (일반적으로 오른쪽)
        this.isPenDown = false; // 기본적으로 펜은 올라가 있습니다.
    }
    
    // Exercise 7.10: 거북이의 x, y 위치를 반환하는 메서드 추가
    public int getPositionX() {
        return (int) this.currentPosition.getX(); 
    }

    public int getPositionY() {
        return (int) this.currentPosition.getY();
    }
    
	public int getCurrentAngle() {
		return currentAngle;
	}

	public void setCurrentAngle(int currentAngle) {
		this.currentAngle = currentAngle;
	}
	
    // Exercise 7.13: 거북이가 화면 밖으로 나가면 반대편에서 나타나게 하는 메서드
    public void wrapPosition(int canvasWidth, int canvasHeight) {
        // 현재 x, y 좌표 가져오기
        double currentX = this.currentPosition.getX();
        double currentY = this.currentPosition.getY();
        
        // 위치 변경 여부를 추적하는 변수
        boolean isChanged = false;

        // X 좌표 확인: 0보다 작으면 오른쪽 끝으로, 최대 너비보다 크면 0으로 이동 [3]
        if (currentX < 0) {
            currentX = canvasWidth;
            isChanged = true;
        } else if (currentX > canvasWidth) {
            currentX = 0;
            isChanged = true;
        }

        // Y 좌표 확인: 0보다 작으면 아래쪽 끝으로, 최대 높이보다 크면 0으로 이동 [3]
        if (currentY < 0) {
            currentY = canvasHeight;
            isChanged = true;
        } else if (currentY > canvasHeight) {
            currentY = 0;
            isChanged = true;
        }

        // 위치가 실제로 변경되었을 때만 새로운 좌표 객체를 생성하여 할당 [2]
        if (isChanged) {
            this.currentPosition = new CartesianCoordinate(currentX, currentY);
        }
    }


    // 펜을 내리는 메서드
    public void putPenDown() {
        this.isPenDown = true;
    }

    // 펜을 올리는 메서드
    public void putPenUp() {
        this.isPenDown = false;
    }

    // 지정된 각도만큼 방향을 전환하는 메서드 (int 인자 사용)
    public void turn(int angle) {
        this.setCurrentAngle((this.getCurrentAngle() + angle) % 360);
    }

    // 지정된 픽셀 거리만큼 이동하는 메서드 (int 인자 사용)
    public void move(int distance) {
        // 현재 방향을 라디안으로 변환
        double radians = Math.toRadians(this.getCurrentAngle());
        
        // 캔버스의 좌표계 특성(y축이 아래를 향함)을 반영하여 새 좌표 계산
        double newX = currentPosition.getX() + (distance * Math.cos(radians));
        double newY = currentPosition.getY() + (distance * Math.sin(radians));
        

 
        CartesianCoordinate newPosition = new CartesianCoordinate(newX, newY);

        // 펜이 내려가 있다면 현재 위치에서 새 위치까지 캔버스에 선을 그립니다.
        if (isPenDown) {
            canvas.drawLineBetweenPoints(currentPosition, newPosition);
        }
        
        // 이동 후 위치 업데이트
        this.currentPosition = newPosition;
    }

    // 디버깅과 테스트를 돕는 toString() 메서드
    @Override
    public String toString() {
        return "Turtle is at (" + currentPosition.getX() + ", " + currentPosition.getY() + 
               "), facing " + getCurrentAngle() + " degrees. Pen Down: " + isPenDown;
    }

	public void draw() {
		// 거북이의 위치 표시
	    // 1. 상태 복구를 위해 현재 거북이의 위치, 각도, 펜 상태를 임시 변수에 백업
	    CartesianCoordinate originalPosition = this.currentPosition;
	    int originalAngle = this.getCurrentAngle();
	    boolean wasPenDown = this.isPenDown;
	    
	    // 2. 펜을 내리고 거북이 모양(정삼각형) 그리기
	    this.putPenDown();
	    this.move(20);
	    this.turn(120);
	    this.move(20);
	    this.turn(120);
	    this.move(20);
	    
	    // 3. 백업해둔 원래 상태로 완벽히 복구 (다음 이동에 영향을 주지 않기 위함)
	    this.currentPosition = originalPosition;
	    this.setCurrentAngle(originalAngle);
	    
	    // 원래 펜이 올라가 있었다면 다시 올리고, 내려가 있었다면 그대로 유지
	    if (!wasPenDown) {
	        this.putPenUp();
	    }
		
	}

	public void undraw() {
	    // 1. draw()에서 그린 선의 개수(예: 정삼각형이면 3개)만큼 캔버스에서 선 제거
	    this.canvas.removeMostRecentLine();
	    this.canvas.removeMostRecentLine();
	    this.canvas.removeMostRecentLine();		
	    // 2. 화면 강제 새로고침 (지워진 상태를 즉시 화면에 반영)
	    this.canvas.repaint();

	}


}