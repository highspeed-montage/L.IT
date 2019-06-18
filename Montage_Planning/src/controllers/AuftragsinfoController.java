package controllers;



import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.Datenbank;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import models.Auftrag;
import models.Rechner;

public class AuftragsinfoController implements Initializable{
	
	@FXML private TitledPane titledPane_AI_rechner1;
	
	@FXML private Label lbl_AI_AuftragsNr;
	@FXML private Label lbl_AI_status;
	@FXML private Label lbl_AI_lieferdatum;
	@FXML private Label lbl_AI_bestelldatum;
	@FXML private Label lbl_AI_kunde;
	@FXML private Label lbl_AI_kundentyp;
	@FXML private Label lbl_AI_kundenNr;
	@FXML private Label lbl_AI_kundenEMail;
	@FXML private TableColumn<Rechner, Integer> tableColumn_AI_SerienNr;
	@FXML private TableColumn<Rechner, String> tableColumn_AI_Status;
	@FXML private TableView<Rechner> tableView_AI_Rechner;
	private Datenbank db = new Datenbank();
	Auftrag a1 = null;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		db.openConnection();

		try {
			db.getAuftragsinfo(AuftragsansichtController.auftragsnummerAktuell);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		auftragsInfoFuellen();
		
		
	}
	private void auftragsInfoFuellen() {
				
	}
	
	
}
