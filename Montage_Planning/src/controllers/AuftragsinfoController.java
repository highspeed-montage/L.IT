package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import application.Datenbank;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Auftrag;
import models.Rechner;

public class AuftragsinfoController implements Initializable {

	@FXML
	private TitledPane titledPane_AI_rechner1;

	@FXML
	private Label lbl_AI_AuftragsNr;
	@FXML
	private Label lbl_AI_Status;
	@FXML
	private Label lbl_AI_lieferdatum;
	@FXML
	private Label lbl_AI_bestelldatum;
	@FXML
	private Label lbl_AI_kunde;
	@FXML
	private Label lbl_AI_kundentyp;
	@FXML
	private Label lbl_AI_kundenNr;
	@FXML
	private Label lbl_AI_kundenEMail;
	@FXML
	private TableColumn<Rechner, Integer> tableColumn_AI_SerienNr;
	@FXML
	private TableColumn<Rechner, String> tableColumn_AI_Status;
	@FXML
	private TableView<Rechner> tableView_AI_Rechner;
	
	private Datenbank db = new Datenbank();

	Auftrag a1 = null;
	
	ObservableList<Rechner> rechner = FXCollections.observableArrayList();


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		db.openConnection();
		auftragsInfoFuellen();

	}
/**
 * AuftragsInfo-Tabelle wird gefuellt
 */
	private void auftragsInfoFuellen() {
		try {
			a1 = db.getAuftragsinfo(AuftragsansichtController.auftragsnummerAktuell);
		} catch (SQLException e) {
			e.printStackTrace();
			AlertController.information("Fehler", "Datenbankverbindung nicht möglich");
		}
		lbl_AI_AuftragsNr.setText(String.valueOf(a1.getAuftragsnr()));
		lbl_AI_bestelldatum.setText(String.valueOf(a1.getBestelldatum()));
		lbl_AI_lieferdatum.setText(String.valueOf(a1.getLieferdatum()));
		if (a1.getFirmenname() != null) {
			lbl_AI_kunde.setText(a1.getFirmenname());
		} else {
			lbl_AI_kunde.setText(a1.getPrivatname());
		}
		lbl_AI_kundenEMail.setText(a1.getKundenEMail());
		lbl_AI_kundenNr.setText(String.valueOf(a1.getKundenNr()));
		lbl_AI_kundentyp.setText(a1.getKundentyp());
		
		lbl_AI_Status.setText(a1.getStatus());
		
		rechner.addAll(a1.getRechnerVonAuftrag());

		tableColumn_AI_SerienNr.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
		tableColumn_AI_Status.setCellValueFactory(new PropertyValueFactory<>("status"));
		tableView_AI_Rechner.setItems(rechner);
	}

}
