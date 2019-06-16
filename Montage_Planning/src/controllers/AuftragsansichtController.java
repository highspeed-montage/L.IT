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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
	
	private Datenbank db = new Datenbank();
//	private Datenbank_Gabby db = new Datenbank_Gabby();
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
		try 
		{
			db.openConnection();
			
			listenansichtFuellen();
			
			auftraegeVerteilen();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void listenansichtFuellen() {
		
		try {
			auftragListenansichtTabelle.addAll(db.getAuftrag());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		col_AL_Auftragsnummer.setCellValueFactory(new PropertyValueFactory<>("auftragsnr"));
		col_AL_Status.setCellValueFactory(new PropertyValueFactory<>("status"));
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
	 * @throws SQLException 
	 */
	public void auftraegeVerteilen() throws SQLException
	{
		Date bearbeitungsdatum = new Date(); 
		Calendar calendar = new GregorianCalendar();
		int rechnerTeilzeit = (Datenbank.rechner.size() / 3);
		int rechnerVollzeit = rechnerTeilzeit*2;
		int rest = (Datenbank.rechner.size() % 3);
		
//		Auftraege werden auf Teilzeitmitarbeiter verteilt
		for(int i=0; i<rechnerTeilzeit; i++)
		{
			for(int j=0; j<anwesenheitTeilzeit.size(); j++) 
			{
				int idAuftragsverteilung = anwesenheitTeilzeit.get(j).getPersonalnr() + anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr();
				if(anwesenheitTeilzeit.get(j).getArbeitsaufwand()<(anwesenheitTeilzeit.get(j).getWochenstunden()/5))
				{
					switch(calendar.get(Calendar.DAY_OF_WEEK)) {
					case 1: 
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 1); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					case 7:
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 2); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					}
				}
				else
				{
					calendar.setTime(bearbeitungsdatum);
					calendar.add(Calendar.DAY_OF_MONTH, 1); 
					bearbeitungsdatum = calendar.getTime();
					switch(calendar.get(Calendar.DAY_OF_WEEK)) {
					case 1: 
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 1); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					case 7:
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 2); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					}
				}
			}
			for(int k=0; k<anwesenheitTeilzeit.size(); k++)
			{
				Datenbank.rechner.remove(0);
			}
		}
		bearbeitungsdatum = new Date();
//		Auftraege werden an Vollzeitmitarbeiter verteilt
		for(int i=0; i<rechnerVollzeit; i++)
		{
			calendar.setTime(bearbeitungsdatum);
			calendar.add(Calendar.DAY_OF_MONTH, 1); 
			bearbeitungsdatum = calendar.getTime();
			for(int j=0; j<anwesenheitVollzeit.size(); j++) 
			{
				int idAuftragsverteilung = anwesenheitVollzeit.get(j).getPersonalnr() + anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr();
				if(anwesenheitVollzeit.get(j).getArbeitsaufwand()<(anwesenheitVollzeit.get(j).getWochenstunden()/5))
				{
					switch(calendar.get(Calendar.DAY_OF_WEEK)) {
					//1 = Sonntag
					case 1: 
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 1); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					//7 = Samstag
					case 7:
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 2); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					}
				}
				else
				{
					calendar.setTime(bearbeitungsdatum);
					calendar.add(Calendar.DAY_OF_MONTH, 1); 
					bearbeitungsdatum = calendar.getTime();
					switch(calendar.get(Calendar.DAY_OF_WEEK)) {
					case 1: 
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 1); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					case 7:
						calendar.setTime(bearbeitungsdatum);
						calendar.add(Calendar.DAY_OF_MONTH, 2); 
						bearbeitungsdatum = calendar.getTime();
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					}
				}
			}
			for(int k=0; k<anwesenheitVollzeit.size(); k++)
			{
				Datenbank.rechner.remove(0);
			}
		}
		bearbeitungsdatum = new Date();
		
//		Rest wird auf Vollzeitmitarbeiter verteilt
		for(int i=0; i<rest; i++)
		{
			int idAuftragsverteilung = anwesenheitVollzeit.get(i).getPersonalnr() + anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr();
			if(anwesenheitVollzeit.get(i).getArbeitsaufwand()<(anwesenheitVollzeit.get(i).getWochenstunden()/5))
			{
				switch(calendar.get(Calendar.DAY_OF_WEEK)) {
				case 1: 
					calendar.setTime(bearbeitungsdatum);
					calendar.add(Calendar.DAY_OF_MONTH, 1); 
					bearbeitungsdatum = calendar.getTime();
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				case 7:
					calendar.setTime(bearbeitungsdatum);
					calendar.add(Calendar.DAY_OF_MONTH, 2); 
					bearbeitungsdatum = calendar.getTime();
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				default:
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				}
			}
			else
			{
				calendar.setTime(bearbeitungsdatum);
				calendar.add(Calendar.DAY_OF_MONTH, 1); 
				bearbeitungsdatum = calendar.getTime();
				switch(calendar.get(Calendar.DAY_OF_WEEK)) {
				case 1: 
					calendar.setTime(bearbeitungsdatum);
					calendar.add(Calendar.DAY_OF_MONTH, 1); 
					bearbeitungsdatum = calendar.getTime();
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				case 7:
					calendar.setTime(bearbeitungsdatum);
					calendar.add(Calendar.DAY_OF_MONTH, 2); 
					bearbeitungsdatum = calendar.getTime();
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				default:
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung, anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(), anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(), anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				}
			}
		}
		for(int k=0; k<rest; k++)
		{
			Datenbank.rechner.remove(0);
		}
	}
	
	public void Logout(Event event) {
		LoginController Logout =new LoginController();
		Logout.confirmation();
	    final Node source = (Node) event.getSource();
	    final Stage stage = (Stage) source.getScene().getWindow();
	    stage.close();
	}
}
