package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import application.Datenbank_Gabby;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import models.Auftragsverteilung;

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
	private ComboBox<String> comboBox_RL_filter;
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

	// Wochenansicht
	@FXML
	private ComboBox<String> comboBox_RW_Wochenansicht;
	@FXML
	private TableView<Auftragsverteilung> tableRechnerWoche;
	@FXML
	private TableColumn<Auftragsverteilung, Integer> col_RW_Montag;
	@FXML
	private TableColumn<Auftragsverteilung, Integer> col_RW_Dienstag;
	@FXML
	private TableColumn<Auftragsverteilung, Integer> col_RW_Mittwoch;
	@FXML
	private TableColumn<Auftragsverteilung, Integer> col_RW_Donnerstag;
	@FXML
	private TableColumn<Auftragsverteilung, Integer> col_RW_Freitag;

	private Datenbank_Gabby db = new Datenbank_Gabby();

	// ComboBox
	private ObservableList<String> options = FXCollections.observableArrayList();
	private List<Date> bearbeitungsdatum = new ArrayList<>();
	private List<String> wochen = new ArrayList<>();
	
	private SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // gibt Wochentag aus (Montag etc)
	private SimpleDateFormat sdf = new SimpleDateFormat("y-MM-dd"); // 2019-03-06

	public static int seriennrAktuell;
	
	ObservableList<Auftragsverteilung> rechnerWochenansichtTabelle = FXCollections.observableArrayList();
	ObservableList<Auftragsverteilung> rechnerListenansichtTabelle = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		db.openConnection();
		
		// Liste mit allen Bearbeitungsdaten aus der Datenbank (Auftragsverteilung) 
		try {
			bearbeitungsdatum.addAll(db.getRechnerBearbeitungsdatum());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < bearbeitungsdatum.size(); i ++) {
			Date date = bearbeitungsdatum.get(i);
			int kw = getWeekNumberFromDate(date);
			Date montag = getMondayFromWeekNumber(2019, kw);
			Date freitag = getFridayFromWeekNumber(2019, kw);
			String woche = sdf.format(montag) + "-" + sdf.format(freitag);
			
			if (wochen.contains(woche) == false) {
				wochen.add(woche);
			}
		}

		// ComboBox befüllen
		options.addAll(wochen);
		comboBox_RW_Wochenansicht.setItems(options.sorted());

		// Wochen- und Listenansicht Inhalte
		wochenansichtFuellen();
		listenansichtFuellen();

	}

	// Wochenansicht: Tabelle befüllen
	public void wochenansichtFuellen() {
		
		// ComboBox Listener
		comboBox_RW_Wochenansicht.getSelectionModel().selectedItemProperty().addListener((options) -> {
			tableRechnerWoche.getItems().clear();
			String wochenAuswahl = comboBox_RW_Wochenansicht.getSelectionModel().selectedItemProperty().getValue(); // DD.MM.YYYY-DD.MM.YYYY

			// String split
			String startdatum = wochenAuswahl.substring(0, 10);
			String enddatum = wochenAuswahl.substring(11, 21);
			
			System.out.println(startdatum);
			System.out.println(enddatum);
			
			try {
				rechnerWochenansichtTabelle.addAll(db.getRechnerAusAuftragsverteilungWoche(startdatum, enddatum));
			} catch (SQLException e) {
				e.printStackTrace();
//				JOptionPane.showMessageDialog(null, "Fehler", "Datenbankabfrage nicht möglich", 0);
			}


			for (int i = 0; i < rechnerWochenansichtTabelle.size(); i++) {
				
				Date d = rechnerWochenansichtTabelle.get(i).getBearbeitungsdatum();
				Integer seriennr = rechnerWochenansichtTabelle.get(i).getSeriennr(); // SERIENNUMER DIE REIN MUSS
				
				//System.out.println(simpleDateformat.format(d));
			
				
				switch(simpleDateformat.format(d)) {
				
				case "Montag":
					System.out.println("Montag: " + seriennr);
					col_RW_Montag.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
					break;
				case "Dienstag":
					System.out.println("Dienstag: " + seriennr);
					col_RW_Dienstag.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
					break;
				case "Mittwoch":
					System.out.println("Mittwoch: " + seriennr);
					col_RW_Mittwoch.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
					break;
				case "Donnerstag":
					System.out.println("Donnerstag: " + seriennr);
					col_RW_Donnerstag.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
					break;
				case "Freitag":
					System.out.println("Freitag: " + seriennr);
					col_RW_Freitag.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
					break;
				default:
					System.out.println("Kein Tag angegeben");
					break;			
				}
				
			}
			tableRechnerWoche.setItems(rechnerWochenansichtTabelle);
		});
		
	}


	// Listenansicht: Tabelle befüllen
	public void listenansichtFuellen() {
		try {
			rechnerListenansichtTabelle.addAll(db.getRechnerAusAuftragsverteilungListe(/* LoginController.user */));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		col_RL_serienNr.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
		col_RL_status.setCellValueFactory(new PropertyValueFactory<>("status"));
		col_RL_bearbeitungsdatum.setCellValueFactory(new PropertyValueFactory<>("bearbeitungsdatum"));
		col_RL_lieferdatum.setCellValueFactory(new PropertyValueFactory<>("lieferdatum"));

		tableRechnerListe.setItems(rechnerListenansichtTabelle);
	}

	// Wochenansicht: Klick auf Rechner öffnet Rechnerinfo
	public void clickRechnerWoche(MouseEvent e) {

		if (e.getClickCount() == 2) {
			seriennrAktuell = tableRechnerWoche.getSelectionModel().getSelectedItem().getSeriennr();
			
			try {
				int idAuftragsart = db.getRechnerAuftragsart(seriennrAktuell);
				if (idAuftragsart == 502) {
					new FolgeFenster("/views/FA_Rechnerinfo.fxml");
				} else if (idAuftragsart == 501) {
					new FolgeFenster("/views/SA_Rechnerinfo.fxml");
				} else {
					System.out.println("Keine Info vorhanden");
				}
			} catch (SQLException | IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	// Listenansicht: Klick auf Rechner öffnet Rechnerinfo
	public void clickRechnerListe(MouseEvent e) {

		if (e.getClickCount() == 2) {
			seriennrAktuell = tableRechnerListe.getSelectionModel().getSelectedItem().getSeriennr();

			try {
				int idAuftragsart = db.getRechnerAuftragsart(seriennrAktuell);
				System.out.println(idAuftragsart);
				if (idAuftragsart == 502) {
					new FolgeFenster("/views/FA_Rechnerinfo.fxml");
				} else if (idAuftragsart == 501) {
					new FolgeFenster("/views/SA_Rechnerinfo.fxml");
				} else {
					System.out.println("Keine Info vorhanden");
				}
			} catch (SQLException | IOException e2) {
				e2.printStackTrace();
			}

		}
	}
	
	// ComboBox: Kalenderwoche holen
	public static int getWeekNumberFromDate(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    return cal.get(Calendar.WEEK_OF_YEAR);
	}
	
	// ComboBox: Montag der Kalenderwoche holen
	public static Date getMondayFromWeekNumber(int year, int weekNumber) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	    return cal.getTime();
	}
	
	// ComboBox: Freitag der Kalenderwoche holen	
	public static Date getFridayFromWeekNumber(int year, int weekNumber) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
	    return cal.getTime();
	}
}
