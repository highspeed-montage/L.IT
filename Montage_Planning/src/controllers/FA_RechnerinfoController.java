package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import application.Datenbank;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Auftragsverteilung;
import models.FA_Rechner;
import models.Teile;

public class FA_RechnerinfoController implements EventHandler, Initializable {
	/** Rechnerinfo */
	@FXML
	private Label lbl_FAI_status;
	@FXML
	private Label lbl_FAI_lieferdatum;
	@FXML
	private Label lbl_FAI_bearbeitungsdatum;
	@FXML
	private Label lbl_FAI_kunde;
	@FXML
	private Label lbl_FAI_kundenNr;
	@FXML
	private Label lbl_FAI_kundenEMail;
	@FXML
	private Label lbl_FAI_Seriennummer;
	@FXML
	private ComboBox<String> comboBox_FAI_Status = null;
	
	/** Einzelteiltabelle */
	@FXML
	private TableView<Teile> tableFARechnerInfo;
	
	@FXML
	private TableColumn<Teile, String> TableColumn_FAI_einzelteile;

	private Datenbank db = new Datenbank();
	
	// ComboBox Inhalte
	ObservableList<String> status;
	
	FA_Rechner fr = null;
	
	String stsBearb, stsFertig, stsImLager;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		db.openConnection();
		stsBearb = "in Bearbeitung";
		stsFertig = "erledigt";
		stsImLager = "im Lager";

		status = FXCollections.observableArrayList(stsBearb, stsFertig, stsImLager);

		comboBox_FAI_Status.setItems(status);

		FA_RechnerInfo_fuellen();

	}

	/**
	 * Labels der FA_RECHNERINFO Ansicht werden befuellt mit Werten aus der Datenbank
	 */
	public void FA_RechnerInfo_fuellen() {
		
		try {
			fr = db.getFARechnerInfo(RechneransichtController.seriennrAktuell);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		lbl_FAI_Seriennummer.setText(String.valueOf(fr.getSeriennr()));
		
		if (fr.getFirmenname() != null) {
			lbl_FAI_kunde.setText(fr.getFirmenname());
		} else {
			lbl_FAI_kunde.setText(fr.getPrivatName());
		}
	
		lbl_FAI_bearbeitungsdatum.setText(String.valueOf(fr.getBearbeitungsdatum()));		
		lbl_FAI_kundenEMail.setText(fr.geteMail());
		lbl_FAI_kundenNr.setText(String.valueOf(fr.getKundenId()));
		lbl_FAI_lieferdatum.setText(String.valueOf(fr.getLieferdatum()));
		lbl_FAI_status.setText(fr.getStatus());
		
		ObservableList<Teile> einzelteile = FXCollections.observableArrayList();
		einzelteile.addAll(fr.getTeile());
		
		TableColumn_FAI_einzelteile.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
		tableFARechnerInfo.setItems(einzelteile);
	}

	@Override
	public void handle(Event arg0) {
		// TODO Auto-generated method stub

	}
}
