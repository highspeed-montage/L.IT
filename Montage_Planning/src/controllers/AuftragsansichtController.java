package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

import application.Datenbank;
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
	private ArrayList<Monteur> anwesenheitVollzeit = new ArrayList<>();
	private ArrayList<Monteur> anwesenheitTeilzeit = new ArrayList<>();
	
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
	
	/**
	 * Die Methode fuegt alle anwesenden Monteure einer ArrayList fuer Vollzeit- oder Teilzeitmitarbeiter hinzu. 
	 * Auf die Monteure in der ArrayList werden die Auftraege verteilt.
	 */
	public void monteurHinzufuegen()
	{
		for(int i=0; i<Datenbank.monteure.size(); i++)
		{
			if(Datenbank.monteure.get(i).getAnwesend() == true)
			{
				if(Datenbank.monteure.get(i).getWochenstunden()==40)
				{
					anwesenheitVollzeit.add(Datenbank.monteure.get(i));
				}
				else
				{
					anwesenheitTeilzeit.add(Datenbank.monteure.get(i));
				}
			}
		}
	}
	/**
	 * Die Auftraege werden den einzelnen Monteuren zugewiesen
	 */
	public void auftraegeVerteilen()
	{
		Date bearbeitungsdatum = new Date(); 
		Calendar calendar = new GregorianCalendar();
		int rechnerTeilzeit = (Datenbank.rechner.size() / 3);
		int rechnerVollzeit = rechnerTeilzeit*2;
		int rest = (Datenbank.rechner.size() % 3);
		
//		Aufträge werden auf Teilzeitmitarbeiter verteilt
		for(int i=0; i<rechnerTeilzeit; i++)
		{
			calendar.setTime(bearbeitungsdatum);
			calendar.add(Calendar.DAY_OF_MONTH, 1); 
			bearbeitungsdatum = calendar.getTime();
			for(int j=0; j<anwesenheitTeilzeit.size(); j++) 
			{
				anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
				anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
			}
			for(int k=0; k<anwesenheitTeilzeit.size(); k++)
			{
				Datenbank.rechner.remove(0);
			}
		}
		bearbeitungsdatum = new Date();
//		Aufträge werden an Vollzeitmitarbeiter verteilt
		for(int i=0; i<rechnerVollzeit; i++)
		{
			calendar.setTime(bearbeitungsdatum);
			calendar.add(Calendar.DAY_OF_MONTH, 1); 
			bearbeitungsdatum = calendar.getTime();
			for(int j=0; j<anwesenheitVollzeit.size(); j++) 
			{
				anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
				anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
			}
			for(int k=0; k<anwesenheitVollzeit.size(); k++)
			{
				Datenbank.rechner.remove(0);
			}
		}
		bearbeitungsdatum = new Date();
//		rest wird auf Vollzeitmitarbeiter verteilt
		for(int i=0; i<rest; i++)
		{
			calendar.setTime(bearbeitungsdatum);
			calendar.add(Calendar.DAY_OF_MONTH, 1); 
			bearbeitungsdatum = calendar.getTime();
			anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
			anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
		}
		for(int k=0; k<rest; k++)
		{
			Datenbank.rechner.remove(0);
		}
	}
}
