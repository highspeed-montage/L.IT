package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblMontage = new Label();
		lblStatus = new Label();
		txtUsername = new TextField();
		txtPassword = new TextField();
		btnAnmelden = new Button();
	}

	// public static Datenbank db = new Datenbank();
	public void Login(Event event) throws IOException {

		username = txtUsername.getText();
		password = txtPassword.getText();
		
		String user = "a";
		String pw = "a";
		
		
		if (username.equals(user) && password.equals(pw)) {
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
}