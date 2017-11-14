package aj;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.fxml.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.event.*;

/**
 * Report controller object
 * @author conqtc 101265224
 *
 */
public class ReportController {
	private ArrayList<Hazard> hazards;
	
	//
	private Stage dialog;
	
	@FXML
	private GridPane gridReport;
	
	/**
	 * 
	 * @param dialog
	 */
	public void setDialog(Stage dialog) {
		this.dialog = dialog;
	}
	
	@FXML
	private void buttonClicked(ActionEvent event) {
		this.dialog.close();
	}

	@FXML
	private void saveReportClicked(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(this.dialog);
		
		if (file != null) {
		    try {	            
	            PrintWriter writer = new PrintWriter(file);
	            
	            int count = 0;
	    		for (Hazard hazard: hazards) {
	    			count++;
	    			
	    			String textNo = String.format("%d", count);
	    			String textStart = Utility.instantToString(hazard.getStartTime(), "hh:mm:ss");
	    			String textClicked = "";
	    			String textHit = "";
	    			if (hazard.isHit()) {
	    				textHit = "Y";
	    				textClicked = Utility.instantToString(hazard.getClickedTime(), "hh:mm:ss");
	    			} else {
	    				textHit = "N";
	    			}
	    			
	    			writer.println(textNo + " " + textStart + " " + textHit + " " + textClicked);
	    		}

	            writer.close();
	            
	            Alert alert = new Alert(AlertType.INFORMATION, "Report saved!");
	            alert.setTitle("Save Report");
	            alert.setHeaderText("Successful");
	            alert.showAndWait();
	        } catch (Exception ex) {
	        	new Alert(Alert.AlertType.ERROR, "Unable to save report to file.").showAndWait();
	        }
		}
	}
	
	/**
	 * To be called when report dialog is displayed
	 * List of hazard will be passed in
	 * @param hazards
	 */
	public void setData(ArrayList<Hazard> hazards) {
		this.hazards = hazards;
		
		int count = 0;
		for (Hazard hazard: hazards) {
			count++;
			
			Text textNo = new Text(String.format("%d", count));
			Text textStart = new Text(Utility.instantToString(hazard.getStartTime(), "hh:mm:ss"));
			Text textClicked = new Text("");
			Text textHit = new Text("");
			if (hazard.isHit()) {
				textHit.setText("Y");
				textClicked.setText(Utility.instantToString(hazard.getClickedTime(), "hh:mm:ss"));
			} else {
				textHit.setText("N");
			}
			
			gridReport.addRow(count, textNo, textStart, textHit, textClicked);
		}
	}
}
