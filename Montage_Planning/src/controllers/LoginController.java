package controllers;

import java.io.IOException;
import java.net.URL;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblMontage = new Label();
		lblStatus = new Label();
		txtUsername = new TextField();
		txtPassword = new TextField();
		btnAnmelden = new Button();

	}

	 public static Datenbank db = new Datenbank();
	public void Login(Event event) throws IOException {

		/*
		 * @Hendrik: wenn die Daten bei der Anmeldung richtig eingegeben werden, werden
		 * die Attribute vom Objekt Mitarbeiter user gesetzt Konstruktor:
		 * Mitarbeiter(int personalnr, String name) --> das ist unser Login-Objekt
		 * 
		 */

		username = txtUsername.getText();
		password = txtPassword.getText();
		
		validate(username, password);
		
		int intUsername = Integer.parseInt(username);
		String userValidate = db.authenticateUser(username, password);

		if (userValidate.equals("SUCCESS")) {
			user = new Mitarbeiter(intUsername, password);
			lblStatus.setText("Anmelden erfolgreich");
			btnAnmelden.setText("Wird durchgefuehrt");
			new FolgeFenster("/views/Auftragsansicht.fxml");
		} else {
			lblStatus.setText("Eingabe sind inkorrekt bitte wiederholen Sie!");

		}
	}

//			System.out.println("1");
//		if(txtUsername.getText().equals("") && txtPassword.getText().equals("a")) {
//			System.out.println("2");
//			lblStatus.setText("Great Success");
//			btnAnmelden.setText("Wird durchgefuehrt");
//			new FolgeFenster("/views/Auftragsansicht.fxml");
//		} else {
//			lblStatus.setText("RIP");
//			btnAnmelden.setText("hier geht gar nix");
//		}
//	}
	public void validate(String username, String password) {

		if (username == null || username == "") {
			lblStatus.setText("Username cannot be blank");

		} else if (password == null || password == "") {
			lblStatus.setText("Password cannot be blank");

		}
	}

	@Override
	public void handle(Event event) {

	}
}