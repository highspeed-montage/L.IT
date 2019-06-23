package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import application.Datenbank;
import javafx.beans.property.SimpleObjectProperty;
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

	private ArrayList<Monteur> anwesendMonteure = new ArrayList<>();
	private List<Rechner> rechner = new ArrayList<>();

	private List<Date> bearbeitungsdatum = new ArrayList<>();

	private SimpleDateFormat sdf = new SimpleDateFormat("y-MM-dd");

	private List<String> wochen = new ArrayList<>();

	// Listen fuer Wochen und Listenansicht
	ObservableList<Auftrag> auftragListenansichtTabelle = FXCollections.observableArrayList();
	ObservableList<Auftragsverteilung[]> auftragWochenansichtTabelle = FXCollections.observableArrayList();

	// ComboBox
	private ObservableList<String> options = FXCollections.observableArrayList();

	public static int auftragsnummerAktuell;

	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", Locale.GERMAN);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			db.openConnection();
			auftraegeVerteilen();
		} catch (SQLException e) {
			e.printStackTrace();
			AlertController.error("Fehler", "Keine Verbindung zur Datenbank möglich");
		}

		lblName.setText(LoginController.user.getName());

		// Holt alle Bearbeitungsdaten aus der Datenbank zur dynamischen Befuellung der
		// ComboBox
		try {
			bearbeitungsdatum.addAll(db.getRechnerBearbeitungsdatum());
		} catch (SQLException e) {
			e.printStackTrace();
			AlertController.error("Fehler", "Keine Verbindung zur Datenbank möglich");
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

		// ComboBox befuellen
		options.addAll(wochen);
		comboBox_AW_Wochenansicht.setItems(options.sorted());

		// Spalten für die Wochenansicht
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
			Auftragsverteilung auftrag = cd.getValue()[5];
			return new SimpleObjectProperty<>(auftrag == null ? null : auftrag.getMonteur().getName());
		});

		tableAuftragWoche.getColumns().add(columnMA);

		tableAuftragWoche.setItems(auftragWochenansichtTabelle);

		listenansichtFuellen();
		wochenansichtFuellen();
	}
	
	/*
	 * Befüllung der Listenansicht
	 */

	public void listenansichtFuellen() {

		try {
			auftragListenansichtTabelle.addAll(db.getAuftragFuerListe());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		col_AL_Auftragsnummer.setCellValueFactory(new PropertyValueFactory<>("auftragsnr"));
		col_AL_Status.setCellValueFactory(new PropertyValueFactory<>("status"));
		col_AL_Anzahl.setCellValueFactory(new PropertyValueFactory<>("anzahlRechner"));
		col_AL_Lieferdatum.setCellValueFactory(new PropertyValueFactory<>("lieferdatum"));

		tableAuftragListe.setItems(auftragListenansichtTabelle);

	}

	/*
	 *Befüllung der Wochenansicht  
	 */
	public void wochenansichtFuellen() {

		// ComboBox Listener
		comboBox_AW_Wochenansicht.valueProperty().addListener((o, oldValue, newValue) -> {
			auftragWochenansichtTabelle.clear();

			// String split, Erster Tag der Woche = Montag, Letzter Tag der Woche = Freitag
			String startdatum = newValue.substring(0, 10);
			String enddatum = newValue.substring(11, 21);

			// Befuellen der Wochenansicht Tabelle mit den verteilten Auftraegen aus der
			// Datenbank
			try {
				Auftragsverteilung[][] row = new Auftragsverteilung[50][6];

				int i = 0;
				for (Auftragsverteilung auftrag : db.getRechnerAuftragswoche(startdatum, enddatum)) {

					LocalDate date = auftrag.getBearbeitungsdatum();

					row[i][date.getDayOfWeek().getValue() - 1] = auftrag;
					// row[i][5] = auftrag;
					i++;
				}

				auftragWochenansichtTabelle.addAll(row);
			} catch (SQLException e) {
				e.printStackTrace();
				AlertController.error("Fehler", "Keine Verbindung zur Datenbank möglich");
			}
		});

	}

	/**
	 * Die Auftraege werden den einzelnen Monteuren zugewiesen
	 * 
	 * @throws SQLException
	 */
	public void auftraegeVerteilen() throws SQLException {

		try {
			db.setzeRechnerInAuftragsverteilung();
			anwesendMonteure.addAll(db.monteureBefuellen());
			rechner.clear();
			rechner.addAll(db.holeRechnerAusAuftragsverteilungOhneBearbeitung());

			int gesamtsumme = 0;

			LocalDate bearbeitungsdatum = LocalDate.now();

			if (formatter.format(bearbeitungsdatum).equals("Sonntag")) {
				bearbeitungsdatum = bearbeitungsdatum.plusDays(1);
			} else if (formatter.format(bearbeitungsdatum).equals("Samstag")) {
				bearbeitungsdatum = bearbeitungsdatum.plusDays(2);
			} else {

			}

			for (int x = 0; x <= 4; x++) {

				LocalDate datuu = bearbeitungsdatum.plusDays(x);

				for (int i = 0; i < rechner.size(); i++) { // Jeder Rechner in AV

					if (rechner.get(i).getBearbeitungsdatum() == null) { // Nur wenn Auftrag aus AV nicht in Bearbeitung
																			// ist
						for (int j = 0; j < anwesendMonteure.size(); j++) { // Iteration alle MA
							gesamtsumme = db.holeGesamtArbeitsaufwandAusAuftragsverteilung(
									anwesendMonteure.get(j).getPersonalnr(), datuu);

							if (rechner.get(i).getBearbeitungsdatum() == null) { // Nur wenn Auftrag aus AV nicht in
																					// Bearbeitung ist
								if (gesamtsumme <= (anwesendMonteure.get(j).getWochenstunden() / 5)) {

									db.updateRechnerAuftragsverteilung(anwesendMonteure.get(j).getPersonalnr(), datuu,
											rechner.get(i).getSeriennr());

								} else {

								}
								rechner.clear();
								rechner.addAll(db.holeRechnerAusAuftragsverteilungOhneBearbeitung());
							} else {
							}

						}
					} else {
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Listenansicht: Klick auf Auftrag oeffnet Auftragsinfo
	public void clickRechnerListe(MouseEvent e) {

		if (e.getClickCount() == 2) {
			auftragsnummerAktuell = tableAuftragListe.getSelectionModel().getSelectedItem().getAuftragsnr();

			try {
				new FolgeFenster("/views/Auftragsinfo.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
				AlertController.error("Fehler", "Fenster konnte nicht geladen werden");
			}
		}
	}

	/*
	 * Logout Button 
	 * 
	 */
	public void Logout(Event event) {
		AlertController.confirmation();
		try {
			new FolgeFenster("/views/Login.fxml");
			db.closeConnection();
			final Node source = (Node) event.getSource();
			final Stage stage = (Stage) source.getScene().getWindow();
			stage.close();
		} catch (IOException e) {
			String logoutTitle = "Fehler";
			String logoutInfo = "Sie konnten nicht ausgeloggt werden.";
			AlertController.error(logoutTitle, logoutInfo);
			e.printStackTrace();
		}

	}

}
