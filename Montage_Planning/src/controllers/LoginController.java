package controllers;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

import application.Datenbank;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements EventHandler  {
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
 
// public static Datenbank db = new Datenbank();
 public void Login (Event event) throws IOException{
	 
	//if(txtUsername.getText().equals(db.Usernameabfrage()) && txtPassword.getText().equals(db.Passwortabfrage())) {
	 if(txtUsername.getText().equals("") && txtPassword.getText().equals("")) {
		lblStatus.setText("Great Success");
		btnAnmelden.setText("Wird durchgefuehrt");
		new FolgeFenster("/application/Auftragsansicht.fxml");

	} else {
		lblStatus.setText("RIP");
		btnAnmelden.setText("hier geht gar nix");
	}
 }

@Override
public void handle(Event event) {
}
}
