package aj;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.fxml.*;
import javafx.geometry.Insets;

import java.awt.Paint;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;

/**
 * Main controller
 * @author conqtc 101265224
 *
 */
public class MainController {

	public static final int BACKGROUND_SCALE = 300;
	public static int TOTAL_SECONDS = 300;
	public static final int RESPONSE_TIME = 3;
	public static int FPS = 20;
	
	public static final int MIN_NUM_CAR = 4;
	public static final int MAX_NUM_CAR = 9;

	//
	private BorderPane root;

	//
	private Stage primaryStage;

	@FXML
	private Text textDescription;

	@FXML
	private Button buttonStart;

	@FXML
	private Text textTime;

	@FXML
	private Button buttonReport;
	
	@FXML
	private ComboBox<String> comboCars;

	@FXML
	private ComboBox<String> comboMinutes;
	
	@FXML
	private ComboBox<String> comboFps;
	//
	@FXML
	private Pane paneCanvas;
	
	@FXML
	private Text textHazard;

	@FXML
	private Text textResponseTime;

	@FXML
	private Text textHit;
	@FXML
	private Text textMissed;
	
	private int numberOfCar = 0;
	private List<Label> textSpeed;

	private AnimationTimer timer;
	private double offsetBackground = 0;
	private ImageView backgroundImageView;

	private long remainTime;

	private Hazard hazard;
	private Car hazardCar;
	private Car policeCar;
	private Meteor meteor;
	private ArrayList<Car> cars;
	private ArrayList<Hazard> hazards;

	private boolean isInHazard = false;
	private int hit = 0;
	private int missed = 0;

	private double speedFactor = 0;
	
	private Background overSpeedBackground = new Background(new BackgroundFill(Color.PINK, new CornerRadii(5), new Insets(-5)));
	private String chatPng = MainController.class.getResource("chat.png").toExternalForm();
	private String chatPinkPng = MainController.class.getResource("chatPink.png").toExternalForm();
	
	/**
	 * Initialize
	 */
	public void initializeUI() {
		buttonStart.setText(null);
		buttonStart.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("play.png"))));
		buttonStart.setStyle(
	                "-fx-background-radius: 32px; " +
	                "-fx-min-width: 48px; " +
	                "-fx-min-height: 48px; " +
	                "-fx-max-width: 56px; " +
	                "-fx-max-height: 56px;"
	        );
		 
		buttonReport.setVisible(false);

		Image backgroundImage = new Image(getClass().getResourceAsStream("road12x.png"));
		backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.setFitHeight(BACKGROUND_SCALE);
		backgroundImageView.setFitWidth(12 * BACKGROUND_SCALE);
		paneCanvas.getChildren().add(backgroundImageView);

		textHazard.setVisible(false);
		textResponseTime.setVisible(false);
		
		for (int index = MIN_NUM_CAR; index <= MAX_NUM_CAR; index++) {
			comboCars.getItems().add(String.format("%d", index));
		}
		comboCars.getSelectionModel().select(0);

		for (int index = 1; index <= 10; index++) {
			comboMinutes.getItems().add(String.format("%d", index));
		}
		comboMinutes.getSelectionModel().select(0);
		
		comboFps.getItems().addAll("5", "10", "15", "20");
		comboFps.getSelectionModel().select(1);
		comboFps.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				FPS = (comboFps.getSelectionModel().getSelectedIndex() + 1) * 5;
			}
		});

		setupTimer();
	}

	private void setupTimer() {	    
		timer = new AnimationTimer() {
			private long startTime ;
			private long previous = 0;

			@Override
			public void start() {
				startTime = System.currentTimeMillis();
				super.start();
			}

			@Override
			public void handle(long stamp) {
				long current = System.currentTimeMillis();
				if (current > previous + FPS) {	
					long elapsed = (current - startTime) / 1000;
					remainTime = TOTAL_SECONDS - elapsed;
					textTime.setText(Utility.toTimeString(remainTime));
	
					if (elapsed >= TOTAL_SECONDS) {
						stopTimer();
					}
	
					animationFrame();
					
					previous = current;
				}
			}
		};
	}

	private void stopTimer() {
		this.timer.stop();
		for (Car car: cars) {
			car.animation.stop();
		}

		buttonStart.setDisable(false);
		buttonReport.setDisable(false);
		buttonReport.setVisible(true);
	}

	/**
	 * Set root pane ref
	 * @param borderPane
	 */
	public void setRootPane(BorderPane borderPane) {
		this.root = borderPane;
	}

	/**
	 * Set stage ref
	 * @param primaryStage
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	private void setupCars() {
		// click event handler
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				carClicked(event);
			}
		};
		
		String[] carImages = new String[] {"redcar3x.png", "greencar3x.png", "yellowcar3x.png", "blue3x.png"};

		int xPos = Main.XMINPOS;
		Car.CAR_SCALE = (int) (56 * ((double)(numberOfCar + 1) / numberOfCar));
		cars = new ArrayList<>();
		Random random = new Random();
		for (int index = 0; index < numberOfCar; index++) {
			String image = carImages[random.nextInt(carImages.length)];
			if (index < carImages.length) {
				image = carImages[index];
			}
			Car car = new Car(image);
			
			car.setXPos(xPos);
			car.setYPos(BACKGROUND_SCALE - Car.CAR_SCALE - 10);
			car.setXSpeed((double)(random.nextInt(3) + 1) / 10.0);
			
			car.setOnMouseClicked(eventHandler);
			
			cars.add(index, car);

			xPos += 2 * Car.CAR_SCALE;
		}
		
		// set front car
		for (int index = 0; index < numberOfCar - 1; index++) {
			Car car = cars.get(index);
			Car frontCar = cars.get(index + 1);
			
			car.setFrontCar(frontCar);
		}
		
		// setup police car for police hazard
		policeCar = new Car("policecar3x.png");
		policeCar.setLayoutX(- 3 * Car.CAR_SCALE);

		// setup meteor for meteor hazard
		meteor = new Meteor("meteor.png");
		meteor.setLayoutX(-3 * Car.CAR_SCALE);

		paneCanvas.getChildren().clear();
		paneCanvas.getChildren().add(backgroundImageView);
		
		// add all cars here
		for (Car car: cars) {
			paneCanvas.getChildren().add(car);
		}
		
		setupSpeedGrid();

		paneCanvas.getChildren().addAll(policeCar, meteor);
	}
	
	private String getLabelChatStyle(String chatPng) {
		return "-fx-background-image: url('" + chatPng + "'); " +
			   "-fx-background-position: center center; " +
	           "-fx-background-size: stretch; " +
	           "-fx-padding: 0px 5px 10px 5px;";
	}
	
	private void setupSpeedGrid() {
		// initialize text speed grid table
		textSpeed = new ArrayList<>();
		for (int index = 0; index < numberOfCar; index++) {
			Label text = new Label(String.format("Car %d", index));

			text.setStyle(getLabelChatStyle(chatPng));
			
			text.setTextFill(Color.WHITE);
			textSpeed.add(text);
			paneCanvas.getChildren().add(text);
		}
	}

	@FXML
	private void buttonStartClicked(ActionEvent event) {
		hit = 0;
		missed = 0;
		hazards = new ArrayList<>();
		updateHitMissedUI();
		
		numberOfCar = comboCars.getSelectionModel().getSelectedIndex() + MIN_NUM_CAR;
		TOTAL_SECONDS = (comboMinutes.getSelectionModel().getSelectedIndex() + 1) * 60;
		FPS = (comboFps.getSelectionModel().getSelectedIndex() + 1) * 5;
		
		setupCars();
				
		timer.start();

		for (Car car: cars) {
			car.animation.play();
		}

		buttonReport.setDisable(true);
		buttonStart.setDisable(true);
	}

	@FXML
	private void buttonReportClicked(ActionEvent event) {
		displayReport();
	}

	synchronized private void carClicked(MouseEvent event) {
		if (isInHazard) {
			if (event.getSource() == hazardCar && !hazard.isHit()) {
				hit++;
				updateHitMissedUI();
				hazard.setIsHit(true);
				hazard.setClickTimeAt(Instant.now());
				hazardCar.removeEmergency();
				hazardCar.emergencyBrake();
			}
		}
	}	

	/**
	 * Call back function from Hazard
	 */
	synchronized public void hazardStarted() {
		textHazard.setVisible(true);
		textResponseTime.setVisible(true);
		hazardCar.setEmergency();
		if (hazard.getHazardObject() == null) {
			hazardCar.setOverspeedMode(true);
		}
		isInHazard = true;
	}

	/**
	 * Call back function from Hazard
	 * @param elapsed
	 */
	synchronized public void hazardFrames(long elapsed) {
		long remain = RESPONSE_TIME - elapsed;
		textResponseTime.setText(String.format("%d", remain));
	}

	/**
	 * Call back function from Hazard
	 */
	synchronized public void hazardFinished() {
		textHazard.setVisible(false);
		textResponseTime.setVisible(false);
		hazardCar.removeEmergency();
		if (hazard.getHazardObject() == null) {
			hazardCar.setOverspeedMode(false);
		}
		isInHazard = false;

		if (!hazard.isHit()) {
			missed++;
			updateHitMissedUI();
		}

		if (hazardCar.isEmergencyBrake()) {
			hazardCar.resetFromBrake();
		}
	}

	private void updateHitMissedUI() {
		textHit.setText(String.format("Hit: %d", hit));
		textMissed.setText(String.format("Missed: %d", missed));
	}

	synchronized private void generateHazard() {
		if (isInHazard) return;

		if (remainTime < 5) return;

		Random random = new Random();

		// get a random car
		int carIndex = random.nextInt(numberOfCar);
		hazardCar = cars.get(carIndex);

		double y = BACKGROUND_SCALE - Car.CAR_SCALE + 5;
		double x = hazardCar.getXPos() + 1.5 * Car.CAR_SCALE;

		// random hazard
		if (random.nextInt(5) > 1) {
			// police hazard
			hazard = new Hazard(this, this.policeCar, 0, y, x, y);
		} else {
			// meteor hazard
			hazard = new Hazard(this, this.meteor, Main.CANVAS_WIDTH, 0, x, y);
		}

		hazard.setCarIndex(carIndex);
		hazards.add(hazard);
		hazard.startHazard();
	}

	private void animationFrame() {
		updateSpeedUI();

		offsetBackground -= speedFactor;

		if (offsetBackground <= -1200) {
			offsetBackground = 0;
		}

		backgroundImageView.setLayoutX(offsetBackground);

		if (offsetBackground % 2 == 0) {
			generateHazard();
		}

		for (int index = 0; index < numberOfCar; index++) {
			Car car = cars.get(index);
			car.autoSpeed();

			if ((!car.isInOverSpeedMode()) && (car.getCoherentSpeed() >= Car.MAX_COHERENT_SPEED)) {
				tryStartOverspeedHazard(index);
				return;
			}
		}
	}
	
	synchronized private boolean tryStartOverspeedHazard(int carIndex) {
		Car car = cars.get(carIndex);

		if (isInHazard || remainTime < 5) {
			// prevent this car over speed
			car.stopSpeeding();
			return false;
		}
		hazardCar = car;
		hazard = new Hazard(this, null, 0, 0, 0, 0);
		hazard.setCarIndex(carIndex);
		hazards.add(hazard);
		hazard.startHazard();
		
		return true;
	}

	private void updateSpeedUI() {
		
		double totalSpeed = 0;
		
		for (int index = 0; index < numberOfCar; index++) {
			Car car = cars.get(index);
			Label text = textSpeed.get(index);
			
			double speed = car.getCoherentSpeed();
			if (car.isEmergencyBrake()) {
				text.setText("braking");
			} else {
				text.setText(String.format("%.1f kph", speed));
			}
			
			text.setLayoutX(car.getLayoutX());
			text.setLayoutY(car.getLayoutY() - 15);
			
			if (car.isInOverSpeedMode()) {
				text.setText("OVER 40 KPH");
				//text.setBackground(overSpeedBackground);
				text.setStyle(getLabelChatStyle(chatPinkPng));
			} else {	
				//text.setBackground(Background.EMPTY);
				text.setStyle(getLabelChatStyle(chatPng));
			}
			
			totalSpeed += speed;
		}
		speedFactor = totalSpeed / (30 * numberOfCar);
	}

	@FXML
	private void menuHandler(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		switch (menuItem.getId()) {
		case "menuItemClose":
			this.primaryStage.close();
			break;

		case "menuItemAbout":
			displayAbout();
			break;
		}
	}

	/**
	 * 
	 */
	private void displayAbout() {
		try {
			// load about scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("About.fxml"));
			VBox root = (VBox) loader.load();

			Stage dialog = new Stage();
			dialog.setTitle("About");
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.resizableProperty().setValue(Boolean.FALSE);
			dialog.initOwner(this.primaryStage);

			Scene aboutScene = new Scene(root, 300, 300);
			dialog.setScene(aboutScene);

			AboutController controller = (AboutController) loader.getController();
			controller.setDialog(dialog);

			dialog.show();
		} catch (Exception ex) {
			//
		}
	}

	private void displayReport() {
		try {
			// load about scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("Report.fxml"));
			BorderPane root = (BorderPane) loader.load();

			Stage dialog = new Stage();
			dialog.setTitle("Report");
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.resizableProperty().setValue(Boolean.FALSE);
			dialog.initOwner(this.primaryStage);

			Scene Scene = new Scene(root, 600, 400);
			dialog.setScene(Scene);

			ReportController controller = (ReportController) loader.getController();
			controller.setDialog(dialog);
			controller.setData(hazards);

			dialog.show();
		} catch (Exception ex) {
			//
		}
	}

}
