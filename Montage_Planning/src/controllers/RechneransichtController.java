package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import application.Datenbank;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import models.Mitarbeiter;

public class RechneransichtController {

//Grundfunktionen
@FXML private Button btnLogout;
@FXML private Tab tabWoche;
@FXML private Tab tabListe;
@FXML private Label lblName;

	
//Listenansicht
@FXML private ComboBox comboBox_RL_filter;
@FXML private TextField txt_RL_sucheingabe;
@FXML private TableColumn col_RL_serienNr;
@FXML private TableColumn col_RL_status;
@FXML private TableColumn col_RL_bearbeitungsdatum;
@FXML private TableColumn col_RL_lieferdatum;

//WOchenansicht
@FXML private ComboBox comboBox_RW_Wochenansicht;
@FXML private TableColumn col_RW_Mitarbeit;
@FXML private TableColumn col_RW_Montag;
@FXML private TableColumn col_RW_Dienstag;
@FXML private TableColumn col_RW_Mittwoch;
@FXML private TableColumn col_RW_Donnerstag;
@FXML private TableColumn col_RW_Freitag;


//Funktionen: Wochenansicht:
//Table: Auftragsverteilung
//dropDown Wochenauswahl
//für jeden MA werden alle seine Aufträge für die jwlg. Woche angezeigt
//Klick auf Überschrift einer Tabellenspalte (col_RW_Montag...)
//Inhalt zu Tabelle hinzufügen (MA und Rechner) 
//REchner müssen verschoben werden, wenn sie das täglich maximale Arbeitspensum des MAs (4h/8h)


//FUnktionen: Listenansicht
private ObservableList<List<String>> inhaltColSeriennr = FXCollections.observableArrayList();

public void initialize () throws SQLException {
	Datenbank db = new Datenbank();
	inhaltColSeriennr.add(db.listRechnerBySeriennr());
	col_RL_serienNr.setText(inhaltColSeriennr.toString());;
}




}









