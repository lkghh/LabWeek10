package birdprogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bird.DynamicBird;
import bird.RandomBirdC;
import drawing.Canvas;
import geometry.CartesianCoordinate;
import tools.Utils;

public class BirdProgram {

    // Declare constants.
    // Magic numbers removed
	private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 900;
    private static final int DELTA_TIME = 20;
    private static final int DEFAULT_SPEED = 100;
    private static final int DEFAULT_WEIGHT = 100;
    private static final int MAX_WEIGHT = 200;

    // Declare GUI fields
    private JFrame frame;
    private Canvas canvas;
    private JPanel lowerPanel;
    
    private JButton addBirdButton;
    private JButton removeBirdButton;
    private JButton add5BirdsButton;
    private JButton remove5BirdsButton;
    
    private JSlider speedSlider; 
    private JSlider cohesionSlider;
    private JSlider alignmentSlider;
    private JSlider separationSlider;
    
    private JLabel speedLabel;
    private JLabel cohesionLabel;
    private JLabel alignmentLabel;
    private JLabel separationLabel;
    
    // ==========================================
    // Data and state fields
    // ==========================================
    private List<DynamicBird> flock;
    private boolean continueRunning;
    
    public BirdProgram() {
        super();
        setupFlock(); // Initialise list that will hold the data first
        setupGui();   // Then initialise interface
    }

    private void setupFlock() {
    		// Use synchronised list to manage multiple objects safely
        flock = Collections.synchronizedList(new ArrayList<>());
    }

    // Constructing GUI
    private void setupGui() {
        setupWindow();
        setupButtons();
        setupSliders();
        layoutComponents();
        
        // Update the screen after all GUI components have been added
        frame.validate(); 
    }

    // Simulation window setting
    private void setupWindow() {
        frame = new JFrame();
        frame.setTitle("Flocking Simulation");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new Canvas();
        
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new FlowLayout());
        
        frame.setVisible(true);
    }

    	// Adding buttons for adjusting number of birds
    private void setupButtons() {
        addBirdButton = new JButton("Add Bird");
        removeBirdButton = new JButton("Remove Bird");
        add5BirdsButton = new JButton("Add 5");
        remove5BirdsButton = new JButton("Remove 5");

        // ActionListeners for each button press
        addBirdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBirds(1);
            }
        });

        removeBirdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBirds(1);
            }
        });
        
        add5BirdsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBirds(5);
            }
        });
        
        remove5BirdsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBirds(5);
            }
        });
    }
    
	// Adding sliders for each flocking parameter & speed
    private void setupSliders() {
        // Speed Slider
        speedLabel = new JLabel("Speed:" + DEFAULT_SPEED);
        speedSlider = new JSlider(0, 500, DEFAULT_SPEED);
        speedSlider.setMajorTickSpacing(200);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int newSpeed = speedSlider.getValue();
                speedLabel.setText("Speed: " + newSpeed);
                synchronized (flock) {
                    for (DynamicBird bird : flock) {
                        bird.setSpeed(newSpeed); 
                    }
                }
            }
        });

        // Cohesion controller
        cohesionLabel = new JLabel("Cohesion:");
        cohesionSlider = createWeightSlider();
        
        // Alignment controller
        alignmentLabel = new JLabel("Alignment:");
        alignmentSlider = createWeightSlider();
        
        // Separation controller
        separationLabel = new JLabel("Separation:");
        separationSlider = createWeightSlider();
    }

    //Creating duplicate sliders into refactoring
    private JSlider createWeightSlider() {
        JSlider slider = new JSlider(0, MAX_WEIGHT, DEFAULT_WEIGHT);
        slider.setMajorTickSpacing(50);
        slider.setPaintTicks(true);
        return slider;
    }

    private void layoutComponents() {
        // place canvas in the centre 
        frame.add(canvas, BorderLayout.CENTER);
        
        // Place controllers in the bottom panel
        lowerPanel.add(addBirdButton);
        lowerPanel.add(removeBirdButton);
        lowerPanel.add(add5BirdsButton);
        lowerPanel.add(remove5BirdsButton);
        
        lowerPanel.add(speedLabel);
        lowerPanel.add(speedSlider);
        lowerPanel.add(cohesionLabel);
        lowerPanel.add(cohesionSlider);
        lowerPanel.add(alignmentLabel);
        lowerPanel.add(alignmentSlider);
        lowerPanel.add(separationLabel);
        lowerPanel.add(separationSlider);
        
        frame.add(lowerPanel, BorderLayout.SOUTH);
    }

    // Add/Remove bird method
    private void addBirds(int count) {
        synchronized (flock) {
            for (int i = 0; i < count; i++) {
                DynamicBird newBird = new RandomBirdC(canvas, 400, 300);
                newBird.setSpeed(speedSlider.getValue());
                flock.add(newBird);
            }
        }
    }

    private void removeBirds(int count) {
        synchronized (flock) {
            for (int i = 0; i < count; i++) {
                if (!flock.isEmpty()) {
                    int lastIndex = flock.size() - 1;
                    DynamicBird birdToRemove = flock.get(lastIndex);
                    
                    birdToRemove.undraw();   // Prevent ghosting
                    flock.remove(lastIndex); // Remove from the flock list
                } else {
                    System.out.println("No more birds to remove!");
                    break; // break the loop when no birds
                }
            }
        }
    }

    // Obstacle placement
    private void drawRectangle(double x, double y, double w, double h) {
        CartesianCoordinate p1 = new CartesianCoordinate(x, y);
        CartesianCoordinate p2 = new CartesianCoordinate(x + w, y);
        CartesianCoordinate p3 = new CartesianCoordinate(x + w, y + h);
        CartesianCoordinate p4 = new CartesianCoordinate(x, y + h);
        
        canvas.drawLineBetweenPoints(p1, p2, Color.RED);
        canvas.drawLineBetweenPoints(p2, p3, Color.RED);
        canvas.drawLineBetweenPoints(p3, p4, Color.RED);
        canvas.drawLineBetweenPoints(p4, p1, Color.RED);
    }
    
    // 4 rectangular obstacles
    private void drawObstacles() {
        drawRectangle(100, 100, 60, 60);
        drawRectangle(100, 300, 60, 60);
        drawRectangle(350, 200, 80, 150);
        drawRectangle(550, 100, 150, 120);
    }
    
    // Starting simulation
    public void start() {
        gameLoop(); 
    }
    
    // main simulation loop
    private void gameLoop() {
        continueRunning = true;
        
        while (continueRunning) {
            canvas.clear();
            drawObstacles();

            synchronized (flock) {
                //  Convert slider values from 0–200 to ratio of 0.0–2.0
                double wCohesion = cohesionSlider.getValue() / 100.0;
                double wAlignment = alignmentSlider.getValue() / 100.0;
                double wSeparation = separationSlider.getValue() / 100.0;
                
                // Update position and state 
                for (DynamicBird bird : flock) {
                    bird.update(DELTA_TIME, flock, wCohesion, wAlignment, wSeparation);
                    bird.wrapPosition(canvas.getWidth(), canvas.getHeight());
                }
                
                // Draw at updated position
                for (DynamicBird bird : flock) {
                    bird.draw();
                }
            } 
            
            Utils.pause(DELTA_TIME);
        }
    }

    public static void main(String[] args) {
        System.out.println("Running BirdProgram...");
        new BirdProgram().start();
    }
}