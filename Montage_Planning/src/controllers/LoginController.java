package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import application.Datenbank;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Mitarbeiter;

public class LoginController implements Initializable {
	@FXML
	private Label lblMontage;

	@FXML
	private Label lblStatus;
	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtPassword;
	@FXML
	private Button btnAnmelden;

	private String username;
	private String password;

	public static Mitarbeiter user;

	public static Datenbank db = new Datenbank();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		db.openConnection();
	}
	
	public void Eingabe(KeyEvent evt) {
	    String character = evt.getCharacter();
	    if(!character.matches("[0-9]")) {
	        evt.consume();                
	    }                                         
	}

	public void Login(Event event) throws IOException, SQLException {
		int rolle;
		username = txtUsername.getText();
		password = txtPassword.getText();
		
		validate(username, password);

		Mitarbeiter userVergleich = null;

		try {
			userVergleich = db.authenticateUserNEU(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("User nicht gefunden");
			lblStatus.setText("User nicht gefunden");
		}

		if (userVergleich == null) {
			lblStatus.setText("Eingabe ist inkorrekt, bitte wiederholen Sie!");
		} else {
			user = new Mitarbeiter(Integer.parseInt(username), password);
			lblStatus.setText("Anmelden erfolgreich");
			btnAnmelden.setText("Wird durchgefuehrt");

			rolle = db.getMitarbeiterRolle(user);

			if (rolle == 301||rolle == 302)//Monteur
				{new FolgeFenster("/views/Rechneransicht.fxml");}
			
			if (rolle == 303)//Abteilungsleiter
				{new FolgeFenster("/views/Auftragsansicht.fxml");}
			else {
				String folgeTitle = "Fehler in der Datenstruktur";
				String folgeInfo="Es liegt ein Fehler vor bitte melden sie sich beim Support";
				AlertController.error(folgeTitle,folgeInfo);
				}
		}
		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}



	public void validate(String username, String password) {

//		try { // Username == idPersonalnummer sind nur Zahlen
//			Integer.parseInt(username);
//		} catch (NumberFormatException ex) {
//			ex.printStackTrace();
//			String zahlenTitle = "Zahlen im Username";
//			String zahlenInfo = "Der Username besteht nur aus Zahlen!";
//			AlertController.information(zahlenTitle, zahlenInfo);
//			txtUsername.clear();
//			txtPassword.clear();
//		}

		if (username.isEmpty() || username == null) { // null und Empty werden unterschiedlich erkannt --> null!=empty
			String userTitle = "Username";
			String userInfo = "Das Username-Feld darf nicht leer sein!";
			AlertController.information(userTitle, userInfo);
			txtUsername.clear();
			txtPassword.clear();

		} else if (password.isEmpty() || password == null) {
			String passTitle = "Passwort";
			String passInfo = "Das Passwort-Feld darf nicht leer sein!";
			AlertController.information(passTitle, passInfo);
			txtUsername.clear();
			txtPassword.clear();

		}

	}


}