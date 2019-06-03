package controllers;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.Datenbank;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements EventHandler, Initializable  {
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
	 
//	if(txtUsername.getText().equals(db.Usernameabfrage()) && txtPassword.getText().equals(db.Passwortabfrage())) {
	 
	 if(txtUsername.getText().equals("a") && txtPassword.getText().equals("a")) {
		lblStatus.setText("Great Success");
		btnAnmelden.setText("Wird durchgefuehrt");
		new FolgeFenster("/views/Auftragsansicht.fxml");

	} else {
		lblStatus.setText("RIP");
		btnAnmelden.setText("hier geht gar nix");
	}
 }

@Override
public void handle(Event event) {
}

@Override
public void initialize(URL location, ResourceBundle resources) {
	
 lblMontage = new Label();
 lblStatus = new Label();
 txtUsername = new TextField();
 txtPassword = new TextField();
 btnAnmelden = new Button();
	
}
}
