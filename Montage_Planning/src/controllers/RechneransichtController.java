package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import application.Datenbank_Gabby;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	private TableView<Auftragsverteilung[]> tableRechnerWoche;

	private Datenbank_Gabby db = new Datenbank_Gabby();

	// ComboBox
	private ObservableList<String> options = FXCollections.observableArrayList();
	private List<Date> bearbeitungsdatum = new ArrayList<>();
	private List<String> wochen = new ArrayList<>();

	private SimpleDateFormat sdf = new SimpleDateFormat("y-MM-dd"); // 2019-03-06

	public static int seriennrAktuell;

	ObservableList<Auftragsverteilung[]> rechnerWochenansichtTabelle = FXCollections.observableArrayList();
	ObservableList<Auftragsverteilung> rechnerListenansichtTabelle = FXCollections.observableArrayList();

	Alert alert = new Alert(AlertType.INFORMATION);

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		db.openConnection();

		// Holt alle Bearbeitungsdaten aus der Datenbank zur dynamischen Befuellung der
		// ComboBox
		try {
			bearbeitungsdatum.addAll(db.getRechnerBearbeitungsdatum());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Wochen fuer die ComboBox ermitteln anhand der Bearbeitungsdaten aus der
		// Datenbank
		for (int i = 0; i < bearbeitungsdatum.size(); i++) {
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

		// Spalten für die Wochenansicht
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", Locale.GERMAN);

		for (int i = 1; i <= 5; i++) {
			final int dayIndex = i - 1;
			DayOfWeek day = DayOfWeek.of(i);
			TableColumn<Auftragsverteilung[], Integer> column = new TableColumn<>(formatter.format(day));
			column.setCellValueFactory(cd -> {
				Auftragsverteilung auftrag = cd.getValue()[dayIndex];
				System.out.println(cd.getValue()[dayIndex]);
				return new SimpleObjectProperty<>(auftrag == null ? null : auftrag.getSeriennr());
			});

			tableRechnerWoche.getColumns().add(column);
		}

		tableRechnerWoche.setItems(rechnerWochenansichtTabelle);

		// Wochen- und Listenansicht Inhalte
		wochenansichtFuellen();
		listenansichtFuellen();

	}

	// Wochenansicht: Tabelle befüllen
	public void wochenansichtFuellen() {

		// ComboBox Listener
		comboBox_RW_Wochenansicht.valueProperty().addListener((o, oldValue, newValue) -> {
			rechnerWochenansichtTabelle.clear();

			// String split, first date = monday, last date = friday
			String startdatum = newValue.substring(0, 10);
			String enddatum = newValue.substring(11, 21);

			// Befuellen der Wochenansicht Tabelle mit den verteilten Auftraegen aus der
			// Datenbank
			try {
				Auftragsverteilung[][] row = new Auftragsverteilung[10][DayOfWeek.FRIDAY.getValue()];
				for (Auftragsverteilung auftrag : db.getRechnerAusAuftragsverteilungWoche(startdatum, enddatum)) {
					LocalDate date = auftrag.getBearbeitungsdatum();
					int i = 0;
					while (row[i][date.getDayOfWeek().getValue() - 1] != null) {
						i++;
					}
					row[i][date.getDayOfWeek().getValue() - 1] = auftrag;
				}
				rechnerWochenansichtTabelle.addAll(row);
			} catch (SQLException e) {
				e.printStackTrace();
				alert.setTitle("Fehlermeldung");
				alert.setHeaderText("Keine Datenbankverbindung");
				alert.setContentText("Bitte überprüfen Sie Ihre Datenbankverbindung");
				alert.showAndWait();
			}
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

			seriennrAktuell = tableRechnerListe.getSelectionModel().getSelectedItem().getSeriennr();

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
}
