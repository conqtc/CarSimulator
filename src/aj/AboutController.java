package aj;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.fxml.*;
import javafx.event.*;

/**
 * About dialog controller
 * @author conqtc 101265224
 *
 */
public class AboutController {
	// stage reference
	private Stage dialog;
	
	/**
	 * Set dialog reference
	 * @param dialog
	 */
	public void setDialog(Stage dialog) {
		this.dialog = dialog;
	}

	@FXML
	/**
	 * Click close button
	 * @param event
	 */
	private void buttonClicked(ActionEvent event) {
		this.dialog.close();
	}
}
