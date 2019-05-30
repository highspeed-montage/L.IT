package de.hft.LIT.views.testViews;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		mainWindow();
		
	}
	
	public void mainWindow() throws IOException {
		Parent loader = FXMLLoader.load(getClass().getResource("Auftragsinfo.fxml"));
		AnchorPane pane = null;
		
		primaryStage.setMinHeight(300);
		primaryStage.setMinWidth(400);
//		AuftragsinfoController controller = loader.getController();
//		controller.setMain(this);
//		Scene scene = new Scene(pane);
		Scene scene2 = new Scene(loader);
//		try {
//			pane = loader.load();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		primaryStage.setTitle("Auftragsinfo");
		primaryStage.setScene(new Scene(loader));
		primaryStage.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	}

}
