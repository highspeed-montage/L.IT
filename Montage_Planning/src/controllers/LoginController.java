package controllers;

import java.awt.event.ActionEvent;
import java.io.IOException;
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
<<<<<<< HEAD
 
=======
 @FXML
 private Label lblStatus;
>>>>>>> e60795f99c6e5ffff464e0f37304d962b10ff7be
 @FXML
 private TextField txtUsername;
 
 @FXML
 private TextField txtPassword;
 @FXML
 private Button btnAnmelden;
 
 public void Login (Event event) throws IOException {
	 
	//if(txtUsername.getText().equals("u") && txtPassword.getText().equals("p")) {
		lblStatus.setText("Great Success");
		btnAnmelden.setText("Wird durchgef�hrt");
		new FolgeFenster();

	//} else {
	//	lblStatus.setText("RIP");
	//	btnAnmelden.setText("hier geht gar nix");
	//}
 }

@Override
public void handle(Event event) {
	
//	if(txtUsername.getText().equals("u") && txtPassword.getText().equals("p")) {
//		lblStatus.setText("Great Success");
//		btnAnmelden.setText("Wird durchgef�hrt");
//		Stage primaryStage = new Stage();
//		Parent root = null;
//		try {
//			root = FXMLLoader.load(getClass().getResource("/application/Wochenansicht.fxml"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Scene scene = new Scene(root,400,400);
//		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	} else {
//		lblStatus.setText("RIP");
//		btnAnmelden.setText("hier geht gar nix");
//	}
	
}
}
