package birdprogram;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bird.DynamicBird;
import drawing.Canvas;
import tools.Utils;

public class BirdProgram {
    // GUI 및 데이터 저장을 위한 필드 선언
    private JFrame frame;
    private Canvas canvas;
    private JPanel lowerPanel;
    private JButton addBirdButton;
    private List<DynamicBird> flock;
    private boolean continueRunning;
    private JSlider speedSlider; 
    private JButton removeBirdButton; // 💡 추가: 거북이 삭제 버튼 필드


	public BirdProgram() {
		super();
        // Exercise 8.1: 역할을 분리한 두 서브 메서드 호출 [1]
		
		
        setupGui();
        setupFlock();	
        }
    private void setupGui() {
        // 1. JFrame 인스턴스화 및 설정 [3]
        frame = new JFrame();
        frame.setTitle("Bird Program");
        frame.setSize(800, 600); // 이전 랩 참고
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // 2. Canvas 인스턴스화 (아직 프레임에 추가하지 않음) [3]
        canvas = new Canvas();

        // 3. 하단 컨트롤을 담을 JPanel 인스턴스화 (아직 프레임에 추가하지 않음) [3]
        lowerPanel = new JPanel();
        
        // --- Exercise 8.2: 레이아웃 설정 및 하단 배치 ---
        // lowerPanel에 FlowLayout 매니저를 설정합니다.
        lowerPanel.setLayout(new FlowLayout());
        
        // lowerPanel을 frame의 남쪽(SOUTH, 하단)에 추가합니다.
        frame.add(lowerPanel, BorderLayout.SOUTH);


        // 4. 거북이 추가 버튼 인스턴스화 [2, 3]
        addBirdButton = new JButton("Add Bird"); 
        removeBirdButton = new JButton("Remove Bird");

        addBirdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exercise 8.5: 버튼 텍스트 변경
                addBirdButton.setText("Clicked!"); 

                // 💡 추가 (Section 4): 거북이 추가 작업을 synchronized 블록으로 보호합니다.
                synchronized (flock) {
                    flock.add(new bird.RandomBirdC(canvas, 400, 300));
                    int lastIndex = flock.size() - 1;
                    DynamicBird newlyAddedBird = flock.get(lastIndex);
                    newlyAddedBird.setSpeed(speedSlider.getValue());

                }
            }
        });
        // --- Exercise 8.11: Remove 버튼에 ActionListener 부착 ---
        removeBirdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 💡 추가: Thread-safe를 위한 동기화 블록 [2]
                synchronized (flock) {
                    // 💡 Exercise 8.13: 리스트에 거북이가 1마리 이상 있을 때만 실행 (에러 방지)
                    if (!flock.isEmpty()) { 
                        // 마지막 거북이의 인덱스 구하기
                        int lastIndex = flock.size() - 1;
                        
                        // 💡 Exercise 8.12: 리스트에서 삭제하기 전에 화면에서 먼저 지우기 (잔상 제거)
                        DynamicBird birdToRemove = flock.get(lastIndex);
                        birdToRemove.undraw();
                        
                        // 리스트에서 거북이 데이터 최종 삭제
                        flock.remove(lastIndex);
                    } else {
                        // (선택 사항) 거북이가 없을 때 버튼을 누르면 콘솔에 메시지 출력
                        System.out.println("No bird to remove!");
                    }
                }
            }
        });

        // 💡 3.1: 버튼에 액션 리스너(ButtonListener) 부착
     
        // --- Exercise 8.3: GUI 컴포넌트 부착(add) ---
        // 캔버스를 프레임의 중앙(CENTER)에 배치합니다.
        frame.add(canvas, BorderLayout.CENTER);

        // 거북이 추가 버튼을 하단 패널(lowerPanel) 안에 배치합니다.
        lowerPanel.add(addBirdButton);
        lowerPanel.add(removeBirdButton); // 💡 추가: Add 버튼과 슬라이더 사이에 배치 [1]
        speedSlider = new JSlider(0, 1000, 100);
        
        // (선택 사항) 눈금과 레이블 표시 설정
        speedSlider.setMajorTickSpacing(500); // 큰 눈금 간격
        speedSlider.setMinorTickSpacing(100); // 작은 눈금 간격
        speedSlider.setPaintTicks(true);      // 눈금 보이기
        speedSlider.setPaintLabels(true);     // 숫자 보이기

        // 슬라이더를 버튼 옆(lowerPanel)에 부착
        lowerPanel.add(speedSlider);
        
     // --- Exercise 8.8: 슬라이더 값 변경 이벤트 리스너(익명 클래스) ---
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // 슬라이더의 현재 값을 가져옴
                int newSpeed = speedSlider.getValue();
                
                // 💡 동기화(Thread-safe) 블록 내에서 거북이 리스트 업데이트
                synchronized (flock) {
                    for (DynamicBird bird : flock) {
                        bird.setSpeed(newSpeed); // (Lab 4에서 만든 속도 Setter 사용)
                    }
                }
            }
        });
        // 모든 GUI 구성 요소가 추가된 후 화면이 정상적으로 표시되도록 갱신합니다.
        frame.validate(); 

    }

    private void setupFlock() {
        // 5. 다수의 거북이를 저장할 List를 ArrayList로 인스턴스화 [3]
        flock = Collections.synchronizedList(new ArrayList<>());
    }
    
    // 💡 1. 캔버스에 사각형을 그리기 위한 헬퍼 메서드 추가
    private void drawRectangle(double x, double y, double w, double h) {
        geometry.CartesianCoordinate p1 = new geometry.CartesianCoordinate(x, y);
        geometry.CartesianCoordinate p2 = new geometry.CartesianCoordinate(x + w, y);
        geometry.CartesianCoordinate p3 = new geometry.CartesianCoordinate(x + w, y + h);
        geometry.CartesianCoordinate p4 = new geometry.CartesianCoordinate(x, y + h);
        
        // 빨간색 선으로 4면을 그려 사각형을 만듭니다. (Canvas의 Color 오버로딩 메서드 활용)
        canvas.drawLineBetweenPoints(p1, p2, java.awt.Color.RED);
        canvas.drawLineBetweenPoints(p2, p3, java.awt.Color.RED);
        canvas.drawLineBetweenPoints(p3, p4, java.awt.Color.RED);
        canvas.drawLineBetweenPoints(p4, p1, java.awt.Color.RED);
    }

    // 💡 2. 명세서에 지정된 4개의 필수 장애물을 그리는 메서드 추가
    private void drawObstacles() {
        // size (dx=60, dy=60) with top-left corner at (100, 100)
        drawRectangle(100, 100, 60, 60);
        // size (dx=60, dy=60) with top-left corner at (100, 300)
        drawRectangle(100, 300, 60, 60);
        // size (dx=80, dy=150) with top-left corner at (350, 200)
        drawRectangle(350, 200, 80, 150);
        // size (dx=150, dy=120) with top-left corner at (550, 100)
        drawRectangle(550, 100, 150, 120);
    }

	private void start() {
		// To start the game running once the GUI and turtle collections have been set
		// up.
        gameLoop(); 

	}

	private void gameLoop() {

        int deltaTime = 20;
        continueRunning = true;
        
        while (continueRunning) {
        	canvas.clear();
        	
    		drawObstacles();

        	synchronized (flock) {
             
                
                // 2. 업데이트 및 Wrap
                for (DynamicBird bird : flock) {
                    bird.update(deltaTime);
                    bird.wrapPosition(canvas.getWidth(), canvas.getHeight());
                }
                
                // 3. 그리기
                for (DynamicBird bird : flock) {
                    bird.draw();
                }
            } // synchronized 블록 끝
            
            Utils.pause(deltaTime);
        }
		
	}
	public static void main(String[] args) {
		System.out.println("Running BirdProgram...");
		new BirdProgram().start();
	}
}
