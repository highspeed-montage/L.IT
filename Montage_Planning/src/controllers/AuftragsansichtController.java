package controllers;

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

	Alert alert = new Alert(AlertType.INFORMATION);

	// ComboBox
	private ObservableList<String> options = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		db.openConnection();
		dbG.openConnection();

		// Holt alle Bearbeitungsdaten aus der Datenbank zur dynamischen Befuellung der
		// ComboBox
		try {
			bearbeitungsdatum.addAll(dbG.getRechnerBearbeitungsdatum());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Wochen fuer die ComboBox ermitteln anhand der Bearbeitungsdaten aus der
		// Datenbank
		for (int i = 0; i < bearbeitungsdatum.size(); i++) {
			Date date = bearbeitungsdatum.get(i);
			int kw = RechneransichtController.getWeekNumberFromDate(date);
			Date montag = RechneransichtController.getMondayFromWeekNumber(2019, kw);
			Date freitag = RechneransichtController.getFridayFromWeekNumber(2019, kw);
			String woche = sdf.format(montag) + "-" + sdf.format(freitag);

			if (wochen.contains(woche) == false) {
				wochen.add(woche);
			}
		}

		// ComboBox befüllen
		options.addAll(wochen);
		comboBox_AW_Wochenansicht.setItems(options.sorted());

		// Spalten für die Wochenansicht
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", Locale.GERMAN);

		for (int i = 1; i <= 5; i++) {
			final int dayIndex = i - 1;
			DayOfWeek day = DayOfWeek.of(i);
			TableColumn<Auftragsverteilung[], Integer> column = new TableColumn<>(formatter.format(day));
			column.setCellValueFactory(cd -> {
				Auftragsverteilung auftrag = cd.getValue()[dayIndex];
				return new SimpleObjectProperty<>(auftrag == null ? null : auftrag.getSeriennr());
			});

			tableAuftragWoche.getColumns().add(column);
		}

		String name = "Mitarbeiter";
		TableColumn<Auftragsverteilung[], String> columnMA = new TableColumn<>(name);
		columnMA.setCellValueFactory(cd -> {
			Auftragsverteilung auftrag = cd.getValue()[6];
			return new SimpleObjectProperty<>(auftrag == null ? null : auftrag.getMonteur().getName());
		});

		tableAuftragWoche.getColumns().add(columnMA);

		tableAuftragWoche.setItems(auftragWochenansichtTabelle);

		// auftraegeVerteilen();
		listenansichtFuellen();
		wochenansichtFuellen();
	}

	public void listenansichtFuellen() {

		try {
			auftragListenansichtTabelle.addAll(dbG.getAuftrag());
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

//					row[i][5] = auftrag;
					i++;
				}

				auftragWochenansichtTabelle.addAll(row);
			} catch (SQLException e) {
				e.printStackTrace();
				alert.setTitle("Fehlermeldung");
				alert.setHeaderText("Keine Datenbankverbindung");
				alert.setContentText("Bitte überprüfen Sie Ihre Datenbankverbindung");
				alert.showAndWait();
			}
		});

	}

	/**
	 * Die Methode fuegt alle anwesenden Monteure einer ArrayList fuer Vollzeit-
	 * oder Teilzeitmitarbeiter hinzu. Auf die Monteure in der ArrayList werden die
	 * Auftraege verteilt.
	 */
	public void monteurHinzufuegen() {
		for (int i = 0; i < Datenbank.monteure.size(); i++) {
			if (Datenbank.monteure.get(i).getAnwesend() == true) {
				if (Datenbank.monteure.get(i).getWochenstunden() == 40) {
					anwesenheitVollzeit.add(Datenbank.monteure.get(i));
				} else {
					anwesenheitTeilzeit.add(Datenbank.monteure.get(i));
				}
			}
		}
	}

	/**
	 * Die Auftraege werden den einzelnen Monteuren zugewiesen
	 * 
	 * @throws SQLException
	 */
	public void auftraegeVerteilen() throws SQLException {
		LocalDate bearbeitungsdatum = LocalDate.now();
		int rechnerTeilzeit = (Datenbank.rechner.size() / 3);
		int rechnerVollzeit = rechnerTeilzeit * 2;
		int rest = (Datenbank.rechner.size() % 3);

		// Auftraege werden auf Teilzeitmitarbeiter verteilt
		for (int i = 0; i < rechnerTeilzeit; i++) {
			for (int j = 0; j < anwesenheitTeilzeit.size(); j++) {
				int idAuftragsverteilung = anwesenheitTeilzeit.get(j).getPersonalnr()
						+ anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr();
				if (anwesenheitTeilzeit.get(j)
						.getArbeitsaufwand() < (anwesenheitTeilzeit.get(j).getWochenstunden() / 5)) {
					switch (bearbeitungsdatum.getDayOfWeek()) {
					case SUNDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					case SATURDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					}
				} else {
					bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
					switch (bearbeitungsdatum.getDayOfWeek()) {
					case SUNDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					case SATURDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitTeilzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitTeilzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitTeilzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitTeilzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitTeilzeit.get(j).getPersonalnr());
						break;
					}
				}
			}
			for (int k = 0; k < anwesenheitTeilzeit.size(); k++) {
				Datenbank.rechner.remove(0);
			}
		}
		bearbeitungsdatum = LocalDate.now();
		// Auftraege werden an Vollzeitmitarbeiter verteilt
		for (int i = 0; i < rechnerVollzeit; i++) {
			for (int j = 0; j < anwesenheitVollzeit.size(); j++) {
				int idAuftragsverteilung = anwesenheitVollzeit.get(j).getPersonalnr()
						+ anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr();
				if (anwesenheitVollzeit.get(j)
						.getArbeitsaufwand() < (anwesenheitVollzeit.get(j).getWochenstunden() / 5)) {
					switch (bearbeitungsdatum.getDayOfWeek()) {
					case SUNDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					case SATURDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					}
				} else {
					bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
					switch (bearbeitungsdatum.getDayOfWeek()) {
					case SUNDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					case SATURDAY:
						bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					default:
						anwesenheitVollzeit.get(j).rechnerHinzufuegen(Datenbank.rechner.get(j));
						anwesenheitVollzeit.get(j).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
						anwesenheitVollzeit.get(j).setArbeitsaufwand(Datenbank.rechner.get(j).getBearbeitungszeit());
						db.rechnerVerteilung(idAuftragsverteilung,
								anwesenheitVollzeit.get(j).rechnerAuslesen().getBearbeitungsdatum(),
								anwesenheitVollzeit.get(j).rechnerAuslesen().getSeriennr(),
								anwesenheitVollzeit.get(j).getPersonalnr());
						break;
					}
				}
			}
			for (int k = 0; k < anwesenheitVollzeit.size(); k++) {
				Datenbank.rechner.remove(0);
			}
		}
		bearbeitungsdatum = LocalDate.now();

		// Rest wird auf Vollzeitmitarbeiter verteilt
		for (int i = 0; i < rest; i++) {
			int idAuftragsverteilung = anwesenheitVollzeit.get(i).getPersonalnr()
					+ anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr();
			if (anwesenheitVollzeit.get(i).getArbeitsaufwand() < (anwesenheitVollzeit.get(i).getWochenstunden() / 5)) {
				switch (bearbeitungsdatum.getDayOfWeek()) {
				case SUNDAY:
					bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung,
							anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(),
							anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(),
							anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				case SATURDAY:
					bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung,
							anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(),
							anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(),
							anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				default:
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung,
							anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(),
							anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(),
							anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				}
			} else {
				bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
				switch (bearbeitungsdatum.getDayOfWeek()) {
				case SUNDAY:
					bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung,
							anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(),
							anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(),
							anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				case SATURDAY:
					bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung,
							anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(),
							anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(),
							anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				default:
					anwesenheitVollzeit.get(i).rechnerHinzufuegen(Datenbank.rechner.get(i));
					anwesenheitVollzeit.get(i).rechnerAuslesen().setBearbeitungsdatum(bearbeitungsdatum);
					anwesenheitVollzeit.get(i).setArbeitsaufwand(Datenbank.rechner.get(i).getBearbeitungszeit());
					db.rechnerVerteilung(idAuftragsverteilung,
							anwesenheitVollzeit.get(i).rechnerAuslesen().getBearbeitungsdatum(),
							anwesenheitVollzeit.get(i).rechnerAuslesen().getSeriennr(),
							anwesenheitVollzeit.get(i).getPersonalnr());
					break;
				}
			}
		}
		for (int k = 0; k < rest; k++) {
			Datenbank.rechner.remove(0);
		}
	}

	public void Logout(Event event) {
		LoginController Logout = new LoginController();
		Logout.confirmation();
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
