package application;

<<<<<<< Updated upstream
import java.awt.List;
import java.sql.SQLException;
=======
import java.sql.SQLException;
import java.util.List;
>>>>>>> Stashed changes

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application 
{

	@Override
	public void start(Stage primaryStage) {
		try {

<<<<<<< Updated upstream
			Parent root = FXMLLoader.load(getClass().getResource("\\views\\Login.fxml"));
=======
			Parent root = FXMLLoader.load(getClass().getResource("views\\auftragsinfo.fxml"));
>>>>>>> Stashed changes
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		launch(args);
		

		//Datenbank db = new Datenbank();
		//db.openConnection();
		
		
//		Datenbank db = new Datenbank();
//		db.closeConnection();

	
	}
	
	
}
