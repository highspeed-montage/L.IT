package controllers;

import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class AlertController {
	/**
	 * Alert-Fenster zur Darstellung von Informationen
	 * 
	 * @param title
	 * @param info
	 */
	public static void information(String title, String info) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(info);
		alert.showAndWait();
	}

/**
 * Alert-Fenster zur Darstellung eines Fehlers
 * 
 * @param title
 * @param info
 */
	public static void error(String title, String info) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(info);
	}
	
	/**
	 * Alert-Fenster zur Abfrage ob man ausgeloggt werden moechte
	 */
	public static void confirmation() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Sind Sie sicher?");
		alert.setHeaderText(null);
		alert.setContentText("Wenn sie auf OK klicken wird das Programm beendet.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Platform.exit();
		} else {
			alert.close();
		}

	}

}
