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
			Parent root = FXMLLoader.load(getClass().getResource("/views/SA_Rechnerinfo.fxml"));
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
			new AuftragsansichtController().monteurHinzufuegen();
			new AuftragsansichtController().auftraegeVerteilen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//			  System.out.println("start");
//			  db.rechnerBefuellen();
	}

////		int lagerbestand = 0;
//		try {
////			lagerbestand = db.getEinzelteilLagerbestand("pasjhhlit", 10001);
//			int lowestId = db.getLowestRechnerIDAuftrag(100001);
//			db.setAuftragStatus(100001, lowestId);
//			System.out.println(db.getAuftragsinfo(100001));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	
}
