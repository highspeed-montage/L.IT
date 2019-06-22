package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import application.Datenbank;
import application.Datenbank_Gabby;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Auftrag;
import models.Auftragsverteilung;
import models.Monteur;
import models.Rechner;

public class AuftragsansichtController implements Initializable {

	// Grundfunktionen
	@FXML
	private Button btnLogout;
	@FXML
	private Label lblName;
	@FXML
	private Tab tabWoche;
	@FXML
	private Tab tabListe;

	// Wochenansicht:
	@FXML
	private ComboBox<String> comboBox_AW_Wochenansicht;
	@FXML
	private TableView<Auftragsverteilung[]> tableAuftragWoche;

	// Listenansicht:
	@FXML
	private ComboBox<String> comboBox_AL_filter;
	@FXML
	private TextField txt_AL_suche;
	@FXML
	private TableView<Auftrag> tableAuftragListe;
	@FXML
	private TableColumn<Auftrag, Integer> col_AL_Auftragsnummer;
	@FXML
	private TableColumn<Auftrag, String> col_AL_Status;
	@FXML
	private TableColumn<Auftrag, Integer> col_AL_Anzahl;
	@FXML
	private TableColumn<Auftrag, Date> col_AL_Lieferdatum;

	private Datenbank db = new Datenbank();
	private Datenbank_Gabby dbG = new Datenbank_Gabby();
	private ArrayList<Monteur> anwesenheitVollzeit = new ArrayList<>();
	private ArrayList<Monteur> anwesenheitTeilzeit = new ArrayList<>();

	private List<Date> bearbeitungsdatum = new ArrayList<>();

	private SimpleDateFormat sdf = new SimpleDateFormat("y-MM-dd"); // 2019-03-06

	private List<String> wochen = new ArrayList<>();

	// Listen fuer Wochen und Listenansicht
	ObservableList<Auftrag> auftragListenansichtTabelle = FXCollections.observableArrayList();
	ObservableList<Auftragsverteilung[]> auftragWochenansichtTabelle = FXCollections.observableArrayList();

	// Alert alert = new Alert(AlertType.INFORMATION);

	// ComboBox
	private ObservableList<String> options = FXCollections.observableArrayList();

	public static int auftragsnummerAktuell;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		db.openConnection();
		// dbG.openConnection();
		try {
			auftraegeVerteilen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Holt alle Bearbeitungsdaten aus der Datenbank zur dynamischen Befuellung der
		// ComboBox
		// try {
		// bearbeitungsdatum.addAll(dbG.getRechnerBearbeitungsdatum());
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		//
		// // Wochen fuer die ComboBox ermitteln anhand der Bearbeitungsdaten aus der
		// // Datenbank
		// for (int i = 0; i < bearbeitungsdatum.size(); i++) {
		// Date date = bearbeitungsdatum.get(i);
		// int kw = RechneransichtController.getWeekNumberFromDate(date);
		// Date montag = RechneransichtController.getMondayFromWeekNumber(2019, kw);
		// Date freitag = RechneransichtController.getFridayFromWeekNumber(2019, kw);
		// String woche = sdf.format(montag) + "-" + sdf.format(freitag);
		//
		// if (wochen.contains(woche) == false) {
		// wochen.add(woche);
		// }
		// }
		//
		// // ComboBox befuellen
		// options.addAll(wochen);
		// comboBox_AW_Wochenansicht.setItems(options.sorted());
		//
		// // Spalten für die Wochenansicht
		// final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE",
		// Locale.GERMAN);
		//
		// for (int i = 1; i <= 5; i++) {
		// final int dayIndex = i - 1;
		// DayOfWeek day = DayOfWeek.of(i);
		// TableColumn<Auftragsverteilung[], Integer> column = new
		// TableColumn<>(formatter.format(day));
		// column.setCellValueFactory(cd -> {
		// Auftragsverteilung auftrag = cd.getValue()[dayIndex];
		// return new SimpleObjectProperty<>(auftrag == null ? null :
		// auftrag.getSeriennr());
		// });
		//
		// tableAuftragWoche.getColumns().add(column);
		// }
		//
		// String name = "Mitarbeiter";
		// TableColumn<Auftragsverteilung[], String> columnMA = new TableColumn<>(name);
		// columnMA.setCellValueFactory(cd -> {
		// Auftragsverteilung auftrag = cd.getValue()[6];
		// return new SimpleObjectProperty<>(auftrag == null ? null :
		// auftrag.getMonteur().getName());
		// });
		//
		// tableAuftragWoche.getColumns().add(columnMA);
		//
		// tableAuftragWoche.setItems(auftragWochenansichtTabelle);
		//
		// listenansichtFuellen();
		// wochenansichtFuellen();
	}

	public void listenansichtFuellen() {

		try {
			auftragListenansichtTabelle.addAll(dbG.getAuftragFuerListe());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		col_AL_Auftragsnummer.setCellValueFactory(new PropertyValueFactory<>("auftragsnr"));
		col_AL_Status.setCellValueFactory(new PropertyValueFactory<>("status"));
		col_AL_Anzahl.setCellValueFactory(new PropertyValueFactory<>("anzahlRechner"));
		;
		col_AL_Lieferdatum.setCellValueFactory(new PropertyValueFactory<>("lieferdatum"));

		tableAuftragListe.setItems(auftragListenansichtTabelle);

	}

	public void wochenansichtFuellen() {

		// ComboBox Listener
		comboBox_AW_Wochenansicht.valueProperty().addListener((o, oldValue, newValue) -> {
			auftragWochenansichtTabelle.clear();

			// String split, first date = monday, last date = friday
			String startdatum = newValue.substring(0, 10);
			String enddatum = newValue.substring(11, 21);

			// Befuellen der Wochenansicht Tabelle mit den verteilten Auftraegen aus der
			// Datenbank
			try {
				Auftragsverteilung[][] row = new Auftragsverteilung[50][100];
				int i = 0;
				for (Auftragsverteilung auftrag : dbG.getRechnerAuftragswoche(startdatum, enddatum)) {

					// Auftrag: Datum, Seriennr, Mitarbeitername

					System.out.println(auftrag.getMonteur().getName());

					LocalDate date = auftrag.getBearbeitungsdatum();
					System.out.println(date);

					row[i][date.getDayOfWeek().getValue() - 1] = auftrag;

					System.out.println(i);
					System.out.println(date.getDayOfWeek().getValue() - 1);

					// row[i][5] = auftrag;
					i++;
				}

				auftragWochenansichtTabelle.addAll(row);
			} catch (SQLException e) {
				e.printStackTrace();
				String title = "keine Datenbankverbindung";
				String info = "Bitte �berpr�fen sie die Datanbankverbindung";
				AlertController.error(title, info);
				// alert.setTitle("Fehlermeldung");
				// alert.setHeaderText("Keine Datenbankverbindung");
				// alert.setContentText("Bitte überprüfen Sie Ihre Datenbankverbindung");
				// alert.showAndWait();
			}
		});

	}

	/**
	 * Die Methode fuegt alle anwesenden Monteure einer ArrayList fuer Vollzeit-
	 * oder Teilzeitmitarbeiter hinzu. Auf die Monteure in der ArrayList werden die
	 * Auftraege verteilt.
	 * 
	 * @throws SQLException
	 */
	// public void monteurHinzufuegen() {
	// for (int i = 0; i < Datenbank.monteure.size(); i++) {
	// if (Datenbank.monteure.get(i).getAnwesend() == true) {
	// if (Datenbank.monteure.get(i).getWochenstunden() == 40) {
	// anwesenheitVollzeit.add(Datenbank.monteure.get(i));
	// } else {
	// anwesenheitTeilzeit.add(Datenbank.monteure.get(i));
	// }
	// }
	// }
	// }

	public void monteurHinzufuegen() {
		try {
			ArrayList<Monteur> monteure = db.monteureBefuellen();
			for (int i = 0; i < monteure.size(); i++) {
				if (monteure.get(i).getAnwesend() == true) {
					if (monteure.get(i).getWochenstunden() == 40) {
						anwesenheitVollzeit.add(monteure.get(i));
					} else {
						anwesenheitTeilzeit.add(monteure.get(i));
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Auftraege werden den einzelnen Monteuren zugewiesen
	 * 
	 * @throws SQLException
	 */
	public void auftraegeVerteilen() throws SQLException {

		try {
			ArrayList<Monteur> monteure = db.monteureBefuellen();
			for (int i = 0; i < monteure.size(); i++) {
				if (monteure.get(i).getAnwesend() == true) {
					if (monteure.get(i).getWochenstunden() == 40) {
						anwesenheitVollzeit.add(monteure.get(i));
					} else {
						anwesenheitTeilzeit.add(monteure.get(i));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<Rechner> rechner = db.rechnerBefuellen();
		System.out.println(rechner.toString());
		LocalDate bearbeitungsdatum = LocalDate.now();

		int k = 0;
		for (int j = 0; j < anwesenheitTeilzeit.size(); j++) {

			while (anwesenheitTeilzeit.get(j)
					.getArbeitsaufwand() < (anwesenheitTeilzeit.get(j).getWochenstunden() / 5)) {

				switch (bearbeitungsdatum.getDayOfWeek()) {
				case SUNDAY:
					System.out.println("SUNDAY");
					bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
					break;
				case SATURDAY:
					System.out.println("SATURDAY");
					bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
					break;
				default:
					break;
				}

				System.out.println("Teilzeit Mitarbeiter");
			

				anwesenheitTeilzeit.get(j).rechnerHinzufuegen(rechner.get(k));
				anwesenheitTeilzeit.get(j).getPipeline().get(0).setBearbeitungsdatum(bearbeitungsdatum);
				anwesenheitTeilzeit.get(j).setArbeitsaufwand(rechner.get(k).getBearbeitungszeit());

				try {
					db.rechnerVerteilung(anwesenheitTeilzeit.get(j).getPipeline().get(0).getBearbeitungsdatum(),
							anwesenheitTeilzeit.get(j).getPipeline().get(0).getSeriennr(),
							anwesenheitTeilzeit.get(j).getPersonalnr());
				} catch (SQLException e) {
					e.printStackTrace();
			
				}
				System.out.println("---------------" + k);
				
				k++;
			}			
		}
		
		System.out.println("---------------BREAK______");
		System.out.println("---------------" + k);

		for (int i = 0; i < anwesenheitVollzeit.size(); i++) {
		

			while (anwesenheitVollzeit.get(i)
					.getArbeitsaufwand() < (anwesenheitVollzeit.get(i).getWochenstunden() / 5)) {
				switch (bearbeitungsdatum.getDayOfWeek()) {
				case SUNDAY:
					System.out.println("SUNDAY");
					bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
					break;
				case SATURDAY:
					System.out.println("SATURDAY");
					bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
					break;
				default:
					break;
				}

				System.out.println("Vollzeit Mitarbeiter");
				System.out.println("---------------" + k);

				anwesenheitVollzeit.get(i).rechnerHinzufuegen(rechner.get(k));
				anwesenheitVollzeit.get(i).getPipeline().get(0).setBearbeitungsdatum(bearbeitungsdatum);
				anwesenheitVollzeit.get(i).setArbeitsaufwand(rechner.get(k).getBearbeitungszeit());

				try {
					db.rechnerVerteilung(anwesenheitVollzeit.get(i).getPipeline().get(0).getBearbeitungsdatum(),
							anwesenheitVollzeit.get(i).getPipeline().get(0).getSeriennr(),
							anwesenheitVollzeit.get(i).getPersonalnr());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("---------------" + k);
				k++;
			}
		}
		}

		// System.out.println("ELSE");
		// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
		// switch (bearbeitungsdatum.getDayOfWeek()) {
		// case SUNDAY:
		// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
		// break;
		// case SATURDAY:
		// bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
		// break;
		// default:
		// break;
		// }
//	}

	//
	// bearbeitungsdatum = LocalDate.now();
	// // Auftraege werden an Vollzeitmitarbeiter verteilt
	// // for (int z = 0; z < rechnerVollzeit; z++) {
	// for (int j = 0; j < anwesenheitVollzeit.size(); j++) {
	// if (anwesenheitVollzeit.get(j).getArbeitsaufwand() <
	// (anwesenheitVollzeit.get(j).getWochenstunden() / 5)) {
	// switch (bearbeitungsdatum.getDayOfWeek()) {
	// case SUNDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
	// break;
	// case SATURDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
	// break;
	// default:
	// break;
	// }
	// } else {
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
	// switch (bearbeitungsdatum.getDayOfWeek()) {
	// case SUNDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
	// break;
	// case SATURDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
	// break;
	// default:
	// break;
	// }
	// }
	// System.out.println("Vollzeit Mitarbeiter");
	// System.out.println("_________" + rechner.size() + "_________");
	//
	// anwesenheitVollzeit.get(j).rechnerHinzufuegen(rechner.get(k));
	//
	// anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
	// anwesenheitVollzeit.get(j).setArbeitsaufwand(rechner.get(k).getBearbeitungszeit());
	// int idAuftragsverteilung = ((anwesenheitVollzeit.get(j).getPersonalnr()
	// + anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr()));
	// try {
	// db.rechnerVerteilung(anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
	// anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(),
	// anwesenheitVollzeit.get(j).getPersonalnr());
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// for (int k = 0; k < anwesenheitVollzeit.size(); k++) {
	// rechner.remove(0);
	// }
	// }
	// bearbeitungsdatum = LocalDate.now();

	// Rest wird auf Vollzeitmitarbeiter verteilt
	// for (int i1 = 0; i1 < rest; i1++) {
	// if (anwesenheitVollzeit.get(i1)
	// .getArbeitsaufwand() < (anwesenheitVollzeit.get(i1).getWochenstunden() / 5))
	// {
	// switch (bearbeitungsdatum.getDayOfWeek()) {
	// case SUNDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
	// break;
	// case SATURDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
	// break;
	// default:
	// break;
	// }
	// } else {
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
	// switch (bearbeitungsdatum.getDayOfWeek()) {
	// case SUNDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
	// break;
	// case SATURDAY:
	// bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
	// anwesenheitVollzeit.get(i1).rechnerHinzufuegen(rechner.get(i1));
	// break;
	// default:
	// break;
	// }
	// }
	// System.out.println("Rest");
	// anwesenheitVollzeit.get(i1).rechnerHinzufuegen(rechner.get(i1));
	// anwesenheitVollzeit.get(i1).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
	// anwesenheitVollzeit.get(i1).setArbeitsaufwand(rechner.get(i1).getBearbeitungszeit());
	// int idAuftragsverteilung = anwesenheitVollzeit.get(i1).getPersonalnr()
	// + anwesenheitVollzeit.get(i1).rechnerAuslesen().getSeriennr();
	// try {
	// db.rechnerVerteilung(/* idAuftragsverteilung, */
	// anwesenheitVollzeit.get(i1).rechnerAuslesen().getBearbeitungsdatum(),
	// anwesenheitVollzeit.get(i1).rechnerAuslesen().getSeriennr(),
	// anwesenheitVollzeit.get(i1).getPersonalnr());
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// for (int k = 0; k < rest; k++) {
	// rechner.remove(0);
	// }
	// }

	// // Wochenansicht: Klick auf Aufrtrag oeffnet Auftragsinfo
	// public void clickRechnerWoche(MouseEvent e) {
	//
	// if (e.getClickCount() == 2) {
	//
	// seriennrAktuell = tableAuftragWoche.getSelectionModel().getSelectedItem().ge;
	//
	// try {
	// int idAuftragsart = db.getRechnerAuftragsart(seriennrAktuell);
	// if (idAuftragsart == 502) {
	// new FolgeFenster("/views/FA_Rechnerinfo.fxml");
	// } else if (idAuftragsart == 501) {
	// new FolgeFenster("/views/SA_Rechnerinfo.fxml");
	// } else {
	// System.out.println("Keine Info vorhanden");
	// }
	// } catch (SQLException | IOException e2) {
	// e2.printStackTrace();
	// }
	// }
	// }

	// Listenansicht: Klick auf Auftrag oeffnet Auftragsinfo
	public void clickRechnerListe(MouseEvent e) {

		if (e.getClickCount() == 2) {
			auftragsnummerAktuell = tableAuftragListe.getSelectionModel().getSelectedItem().getAuftragsnr();

			try {
				new FolgeFenster("/views/Auftragsinfo.fxml");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void Logout(Event event) {
		AlertController.confirmation();
		try {
			new FolgeFenster("/views/Login.fxml");
		} catch (IOException e) {
			String logoutTitle = "Fehler";
			String logoutInfo = "Sie konnten nicht ausgeloggt werden.";
			AlertController.error(logoutTitle, logoutInfo);
			e.printStackTrace();
		}
		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void filter() {
		// Liste mit Filtern (erstellen oder laden)
		// Liste mit Filtern als String in ComboBox laden
		// Wird eins angeklickt, dann entsprechends filtern
	}
}
