package aj;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

/**
 * Main entry
 * @author conqtc 101265224
 *
 */
public class Main extends Application {
	public static final int CANVAS_WIDTH = 1200;
	public static final int XMINPOS = 0;
	public static final int XMAXPOS = CANVAS_WIDTH - 2 * Car.CAR_SCALE;

	@Override
	public void start(Stage primaryStage) {
		try {
			// fxml loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("Main.fxml"));
			BorderPane root = (BorderPane) loader.load();

			// link to controller
			MainController controller = loader.getController();
			controller.setRootPane(root);
			controller.setPrimaryStage(primaryStage);

			// initialize
			controller.initializeUI();

			// scene
			Scene scene = new Scene(root, Main.CANVAS_WIDTH, 700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// stage
			primaryStage.setScene(scene);
			primaryStage.setTitle("Car Simulator");
			primaryStage.resizableProperty().setValue(Boolean.FALSE);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
