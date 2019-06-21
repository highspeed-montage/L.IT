package application;

import java.sql.SQLException;

import controllers.AuftragsansichtController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {

		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/Auftragsinfo.fxml"));
			Scene scene = new Scene(root, 800, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
		Datenbank db = new Datenbank();
//		AuftragsansichtController auftragsansichtController = new AuftragsansichtController();
		
		db.openConnection();

//		  try {
//			  auftragsansichtController.initialize(null, null);
//			  auftragsansichtController.auftraegeVerteilen();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//			  System.out.println("start");
//			  db.rechnerBefuellen();
	
	}

}
