package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import application.Datenbank_Gabby;
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
import models.Auftrag;
import models.Auftragsverteilung;
import models.Monteur;
import models.Rechner;

public class AuftragsansichtController implements Initializable {
	
	//Grundfunktionen
	@FXML private Button btnLogout;
	@FXML private Label lblName;
	@FXML private Tab tabWoche;
	@FXML private Tab tabListe;
	
	//Wochenansicht:
	@FXML private ComboBox<String> comboBox_AW_Wochenansicht;
	@FXML private TableColumn col_AW_Mitarbeit;
	@FXML private TableColumn col_AW_Montag;
	@FXML private TableColumn col_AW_Dienstag;
	@FXML private TableColumn col_AW_Mittwoch;
	@FXML private TableColumn col_AW_Donnerstag;
	@FXML private TableColumn col_AW_Freitag;
	
	//Listenansicht:
	@FXML private ComboBox<String> comboBox_AL_filter;
	@FXML private TextField txt_AL_suche;
	@FXML private TableView<Auftrag> tableAuftragListe;
	@FXML private TableColumn<Auftrag, Integer> col_AL_Auftragsnummer;
	@FXML private TableColumn<Auftrag, String> col_AL_Status;
	@FXML private TableColumn<Auftrag, Integer> col_AL_Anzahl;
	@FXML private TableColumn<Auftrag, Date> col_AL_Lieferdatum;
	
	private Datenbank_Gabby db = new Datenbank_Gabby();
	private ArrayList<Monteur> abwesenheit = new ArrayList<>();
	
	@FXML
	public void filter() {
		//Liste mit Filtern (erstellen oder laden)
		//Liste mit Filtern als String in ComboBox laden
		//Wird eins angeklickt, dann entsprechends filtern
	}
	
	
	ObservableList<Auftrag> auftragListenansichtTabelle = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		db.openConnection();
		
		listenansichtFuellen();
		
	}

	public void listenansichtFuellen() {
		
		// Auftrag (int auftragsnr, String status, ArrayList<Rechner> rechner.size(), Date lieferdatum)
		
		try {
			auftragListenansichtTabelle.addAll(db.getAuftrag());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		List<Rechner> rechner = new ArrayList<>();
		for (int i = 0; i < auftragListenansichtTabelle.size(); i++) {
			int auftragsnummer = auftragListenansichtTabelle.get(i).getAuftragsnr();
			try {
				rechner.addAll(db.getRechnerZuAuftrag(auftragsnummer));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		
		col_AL_Auftragsnummer.setCellValueFactory(new PropertyValueFactory<>("auftragsnr"));
//		col_AL_Status;
		col_AL_Anzahl.setCellValueFactory(new PropertyValueFactory<>("anzahlRechner"));;
		col_AL_Lieferdatum.setCellValueFactory(new PropertyValueFactory<>("lieferdatum"));
	
		tableAuftragListe.setItems(auftragListenansichtTabelle);
		
	}
	public void Monteurhinzufuegen()
	{
		
	}
}
