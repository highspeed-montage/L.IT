package de.hft.LIT.controllers;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

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


}
