package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.Datenbank;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Mitarbeiter;
import models.Vollzeit_MA;

public class LoginController implements EventHandler, Initializable {
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

	public void Login(Event event) throws IOException {

		// in der fxml-Datei hat die fx:id gefehlt, deswegen war das Textfeld in der
		// fxml nicht mit dem Textfeld im Controller verbunden
		username = txtUsername.getText();
		password = txtPassword.getText();

		validate(username, password);

		Mitarbeiter userVergleich = null;

		try {
			userVergleich = db.authenticateUserNEU(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("User nicht gefunden");
		}
		
		if(userVergleich == null) {
			lblStatus.setText("Eingabe ist inkorrekt, bitte wiederholen Sie!");
		} else {
			user = new Mitarbeiter(Integer.parseInt(username), password);
			lblStatus.setText("Anmelden erfolgreich");
			btnAnmelden.setText("Wird durchgefuehrt");
			new FolgeFenster("/views/Rechneransicht.fxml"); // --> prüfen welcher Mitarbeiter (ob Monteuer oder Abteilungsleiter)	
		}
	}


	// @Hendrik: Strings kann man nicht mit dem Operator == vergleichen, sondern nur
	// mit .equals()  --> hier muss noch abgefragt werden, ob die eingabe username zahlen sind
	public void validate(String username, String password) {

		if (username.equals("")) {
			lblStatus.setText("Username cannot be blank");

		} else if (password.equals("")) {
			lblStatus.setText("Password cannot be blank");

		}
	}

	@Override
	public void handle(Event event) {

	}
}