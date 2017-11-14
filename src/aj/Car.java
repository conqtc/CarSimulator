package aj;

import javafx.geometry.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.*;

/**
 * Car object
 * @author conqtc 101265224
 *
 */
public class Car extends Pane {
	public static final int FRAME_WIDTH = 256;
	public static int CAR_SCALE = 72;

	public static final double MAX_XSPEED = 1;
	public static final double MIN_XSPEED = -1;
	public static final double MAX_COHERENT_SPEED = 40;

	private Car frontCar = null;
	private Car backCar = null;

	private boolean isEmergencyBrake = false;

	private double xPos = 0;
	private double yPos = 0;
	private double xSpeed = 0;
	private double xDirection = 1;

	private Image frames;
	private ImageView framesView;

	public SpriteAnimation animation;

	private Glow effect = new Glow(0.8);
	
	private boolean overSpeedMode = false;

	/**
	 * Constructor from 3x images
	 * @param framesImage
	 */
	public Car(String framesImage){
		frames = new Image(getClass().getResourceAsStream(framesImage));

		framesView = new ImageView(frames);
		framesView.setFitHeight(CAR_SCALE);
		framesView.setFitWidth(CAR_SCALE);
		framesView.setViewport(new Rectangle2D(0, 0, FRAME_WIDTH, FRAME_WIDTH));

		animation = new SpriteAnimation(this.framesView, Duration.millis(200), FRAME_WIDTH);
		getChildren().addAll(this.framesView);
	}

	/**
	 * set layout x position
	 * @param xPos
	 */
	synchronized public void setXPos(double xPos) {
		if (xPos < Main.XMINPOS) {
			xPos = Main.XMINPOS;
		}

		if (xPos > Main.XMAXPOS) {
			xPos = Main.XMAXPOS;
		}

		this.xPos = xPos;
		this.setLayoutX(xPos);
	}

	/**
	 * get x layout position
	 * @return
	 */
	public double getXPos() {
		return this.xPos;
	}

	/**
	 * set y layout position
	 * @param yPos
	 */
	public void setYPos(int yPos) {
		this.yPos = yPos;
		this.setLayoutY(yPos);
	}

	/**
	 * Set xspeed factor
	 * @param xSpeed
	 */
	synchronized public void setXSpeed(double xSpeed) {
		if (xSpeed > MAX_XSPEED) {
			xSpeed = MAX_XSPEED;
		}

		if (xSpeed < MIN_XSPEED) {
			xSpeed = MIN_XSPEED;
		}

		this.xSpeed = xSpeed;
	}

	/**
	 * get x speed factor
	 * @return
	 */
	public double getXSpeed() {
		return this.xSpeed;
	}
	
	// prevent this car to be overspeed
	synchronized public void stopSpeeding() {
		xDirection = -xDirection;
		if (xDirection > 0) {
			setXSpeed(xSpeed + 0.001);
		} else {
			setXSpeed(xSpeed - 0.001);
		}
	}
	
	synchronized public void setOverspeedMode(boolean overSpeed) {
		// finish overspeed mode
		if (this.overSpeedMode && !overSpeed) {
			stopSpeeding();
		}
		this.overSpeedMode = overSpeed;
	}
	
	synchronized public boolean isInOverSpeedMode() {
		return this.overSpeedMode;
	}

	/**
	 * auto speed to accelerate and slow down
	 */
	synchronized public void autoSpeed() {
		// currently in overspeed mode hazard
		if (this.overSpeedMode) {
			return;
		}
		
		double newXPos = xPos + xSpeed * xDirection;

		if (newXPos > Main.XMAXPOS) {
			xDirection = -xDirection;
		}

		if (this.frontCar != null) {
			if (newXPos + CAR_SCALE + 10 > this.frontCar.getXPos()) {
				xDirection = -xDirection;
			}
		}

		if (newXPos < Main.XMINPOS) {
			xDirection = -xDirection;
		}

		if (this.backCar != null) {
			if (newXPos < this.backCar.getXPos() + CAR_SCALE + 10) {
				xDirection = -xDirection;
			}
		}

		setXPos(newXPos);

		if (xDirection > 0) {
			setXSpeed(xSpeed + 0.001);
		} else {
			setXSpeed(xSpeed - 0.001);
		}
	}

	/**
	 * get coherent speed (0 - 40)
	 * @return
	 */
	synchronized public double getCoherentSpeed() {
		return 25.0 + 15 * xSpeed * xDirection;
	}

	/**
	 * set emergency effect (glow)
	 */
	synchronized public void setEmergency() {
		framesView.setEffect(effect);
	}

	synchronized public void removeEmergency() {
		framesView.setEffect(null);
	}

	/**
	 * Set front car
	 * @param frontCar
	 */
	public void setFrontCar(Car frontCar) {
		this.frontCar = frontCar;
		frontCar.setBackCar(this);
	}


	/**
	 * set back car
	 * @param backCar
	 */
	public void setBackCar(Car backCar) {
		this.backCar = backCar;
	}

	/**
	 * urgent brake
	 */
	synchronized public void emergencyBrake() {
		isEmergencyBrake = true;
		this.animation.stop();
		if (this.backCar != null) {
			this.backCar.emergencyBrake();
		}
	}

	/**
	 * reset from the brake
	 */
	synchronized public void resetFromBrake() {
		isEmergencyBrake = false;
		this.animation.play();
		if (this.backCar != null) {
			this.backCar.resetFromBrake();
		}
	}

	/**
	 * is in emergency brake
	 * @return
	 */
	synchronized public boolean isEmergencyBrake() {
		return this.isEmergencyBrake;
	}
}
