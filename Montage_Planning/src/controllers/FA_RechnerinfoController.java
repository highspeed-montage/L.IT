package controllers;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

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
	private ComboBox<String> comboBox_FAI_Status;
	/** Einzelteiltabelle */
	@FXML
	private TableColumn<?, ?> TableColumn_FAI_einzelteile;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lbl_FAI_status = new Label();
		lbl_FAI_lieferdatum = new Label();
		lbl_FAI_bearbeitungsdatum = new Label();
		lbl_FAI_kunde = new Label();
		lbl_FAI_kundenNr = new Label();
		lbl_FAI_kundenEMail = new Label();
		lbl_FAI_Seriennummer = new Label();
//		TableColumn_FAI_einzelteile = new TableColumn<S, T>();
		String stsBearb = "in Bearbeitung";
		String stsFertig = "erledigt";
		String stsImLager = "im Lager";


		ObservableList<String> status = FXCollections.observableArrayList(stsBearb, stsFertig, stsImLager);

		comboBox_FAI_Status = new ComboBox<String>(status);
		
		
//		TableColumn_FAI_einzelteile.setCellValueFactory(new PropertyValueFactory<>("einzelteile"));
	}

	@Override
	public void handle(Event arg0) {
		// TODO Auto-generated method stub

	}
}
