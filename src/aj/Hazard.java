package aj;

import java.time.*;

import javafx.animation.*;
import javafx.scene.layout.*;

/**
 * Hazard object
 * @author conqtc 101265224
 *
 */
public class Hazard {
	public static final int RESP_TIME = 3;

	private Pane objectHazard;
	private AnimationTimer timer;

	private double xStart = 0;
	private double yStart = 0;
	private double xEnd = 0;
	private double yEnd = 0;
	private double x = 0;
	private double y = 0;
	private double xStep = 0;
	private double yStep = 0;

	private Instant startAt;
	private Instant clickedAt;
	private boolean isHit = false;

	private MainController controller;
	
	private int carIndex = -1;

	/**
	 * Constructor
	 * @param controller
	 * @param objectHazard
	 * @param xStart
	 * @param yStart
	 * @param xEnd
	 * @param yEnd
	 */
	public Hazard(MainController controller, Pane objectHazard, double xStart, double yStart, double xEnd, double yEnd) {
		this.controller = controller;
		this.objectHazard = objectHazard;

		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;

		xStep = (xEnd - xStart) / 20;
		yStep = (yEnd - yStart) / 20;

		x = xStart;
		y = yStart;


		timer = new AnimationTimer() {
			private long startTime ;
			private long previous;

			@Override
			public void start() {
				startAt = Instant.now();
				startTime = System.currentTimeMillis();
				super.start();
			}

			@Override
			public void handle(long stamp) {
				long current = System.currentTimeMillis();
				if (current > previous + MainController.FPS) {	
					long elapsed = (current - startTime) / 1000;
	
					controller.hazardFrames(elapsed);
	
					if (elapsed >= RESP_TIME) {
						finishHazard();
					}
	
					hazardFrames();
					
					previous = current;
				}
			}
		};

	}
	
	public Pane getHazardObject() {
		return this.objectHazard;
	}

	/**
	 * Record is hit
	 * @param isHit
	 */
	public void setIsHit(boolean isHit) {
		this.isHit = isHit;
	}

	public boolean isHit() {
		return this.isHit;
	}
	
	public void setCarIndex(int index) {
		this.carIndex = index;
	}
	
	public int getCarIndex() {
		return this.carIndex;
	}

	/**
	 * record clicked time
	 * @param instant
	 */
	public void setClickTimeAt(Instant instant) {
		this.clickedAt = instant;
	}

	public Instant getStartTime() {
		return this.startAt;
	}

	public Instant getClickedTime() {
		return this.clickedAt;
	}

	/**
	 * Start hazard
	 */
	public void startHazard() {
		timer.start();
		if (objectHazard != null && objectHazard instanceof Car) {
			// police hazard
			((Car) objectHazard).animation.play();
		}
		controller.hazardStarted();
	}

	/**
	 * Hazard animation frame: moving to the destined point
	 */
	private void hazardFrames() {
		if (objectHazard == null) {
			return;
		}
		
		if (Math.abs(x - xEnd) > 0.01) {
			x = x + xStep;
			y = y + yStep;
			objectHazard.setLayoutX(x);
			objectHazard.setLayoutY(y);
		}
	}

	/**
	 * Finish hazard
	 */
	public void finishHazard() {
		timer.stop();
		// put the object outside of canvas
		if (objectHazard != null) {
			objectHazard.setLayoutX(- 3 * Car.CAR_SCALE);
		}
		
		controller.hazardFinished();
	}
}
