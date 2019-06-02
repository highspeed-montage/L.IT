package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Datenbank;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Auftragsverteilung;
import models.Mitarbeiter;
import models.Rechner;

public class RechneransichtController implements Initializable {

	// Grundfunktionen, Menüleiste
	@FXML
	private Button btnLogout;
	@FXML
	private Tab tabWoche;
	@FXML
	private Tab tabListe;
	@FXML
	private Label lblName;

	// Listenansicht
	@FXML
	private ComboBox comboBox_RL_filter;
	@FXML
	private TextField txt_RL_sucheingabe;
	@FXML
	private TableView<Auftragsverteilung> tableRechnerListe;
	
	@FXML
	private TableColumn<Auftragsverteilung, Integer> col_RL_serienNr;
	@FXML
	private TableColumn<Auftragsverteilung, String> col_RL_status;
	@FXML
	private TableColumn<Auftragsverteilung, Date> col_RL_bearbeitungsdatum;
	@FXML
	private TableColumn<Auftragsverteilung, Date> col_RL_lieferdatum;

	// WOchenansicht
	@FXML
	private ComboBox comboBox_RW_Wochenansicht;
	@FXML
	private TableColumn col_RW_Mitarbeit;
	@FXML
	private TableColumn col_RW_Montag;
	@FXML
	private TableColumn col_RW_Dienstag;
	@FXML
	private TableColumn col_RW_Mittwoch;
	@FXML
	private TableColumn col_RW_Donnerstag;
	@FXML
	private TableColumn col_RW_Freitag;

	// Funktionen: Wochenansicht:
	// Table: Auftragsverteilung
	// dropDown Wochenauswahl
	// f�r jeden MA werden alle seine Auftr�ge f�r die jwlg. Woche angezeigt
	// Klick auf �berschrift einer Tabellenspalte (col_RW_Montag...)
	// Inhalt zu Tabelle hinzuf�gen (MA und Rechner)
	// REchner m�ssen verschoben werden, wenn sie das t�glich maximale Arbeitspensum
	// des MAs (4h/8h)
	
	// Wenn Benutzer-Rolle = Monteur, dann Laden aller Rechner als Liste

	

	
	// Rechner - Listenansicht Tabelle befüllen
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ObservableList<Auftragsverteilung> rechnerListenansichtTabelle = FXCollections.observableArrayList();
		Datenbank db = new Datenbank();
		db.openConnection();

		try {
			rechnerListenansichtTabelle.addAll(db.listRechnerAusAuftragsverteilung());
			System.out.println(rechnerListenansichtTabelle.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		col_RL_serienNr.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
		col_RL_status.setCellValueFactory(new PropertyValueFactory<>("status"));
		col_RL_bearbeitungsdatum.setCellValueFactory(new PropertyValueFactory<>("bearbeitungsdatum"));
		col_RL_lieferdatum.setCellValueFactory(new PropertyValueFactory<>("lieferdatum"));
		
		tableRechnerListe.setItems(rechnerListenansichtTabelle);
	}
}
