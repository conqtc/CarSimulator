package aj;

import javafx.scene.image.*;
import javafx.scene.layout.*;

/**
 * Meteor object
 * @author conqtc 101265224
 *
 */
public class Meteor extends Pane {
	public static final int SCALE = 64;

	private Image frame;
	private ImageView frameView;

	public Meteor(String frameImage){
		frame = new Image(getClass().getResourceAsStream(frameImage));

		frameView = new ImageView(frame);
		frameView.setFitHeight(SCALE);
		frameView.setFitWidth(SCALE);
		getChildren().addAll(this.frameView);
	}
}
