package controllers;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.Label;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {
 @FXML
 private Label lblMontage;
<<<<<<< HEAD
 
=======
 @FXML
 private Label lblStatus;
>>>>>>> e60795f99c6e5ffff464e0f37304d962b10ff7be
 @FXML
 private TextField txtUsername;
 
 @FXML
 private TextField txtPassword;
 
 public void Login (ActionEvent event) throws IOException {
	 
<<<<<<< HEAD
	//if(txtUsername.getText().equals("u") && txtPassword.getText().equals("p")) {
		
	//	Stage primaryStage = new Stage();
	//	Parent root = FXMLLoader.load(getClass().getResource("/application/Wochenansicht.fxml"));
	//	Scene scene = new Scene(root,400,400);
	//	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	//	primaryStage.setScene(scene);
	//	primaryStage.show();
	//} 
=======
	if(txtUsername.getText().equals("u") && txtPassword.getText().equals("p")) {
		lblStatus.setText("Success");
		Stage primaryStage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("/application/Wochenansicht.fxml"));
		Scene scene = new Scene(root,400,400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	} else {
		lblStatus.setText("RIP");
	}
>>>>>>> e60795f99c6e5ffff464e0f37304d962b10ff7be
 }
}
