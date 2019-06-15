package application;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import models.SA_Rechner;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {

		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
			Scene scene = new Scene(root, 800, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		launch(args);
		
		Datenbank db = new Datenbank();
		db.openConnection();
		try {
			SA_Rechner sr = db.getSARechnerInfo(10001);
			db.updateSA_Recher(sr);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
