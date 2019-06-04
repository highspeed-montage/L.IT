package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import application.Datenbank;
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
import javafx.scene.input.MouseEvent;
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

	Datenbank db = new Datenbank();

	// Woche1 03.06.2019-07.06.2019
	LocalDate woche1_tag1 = LocalDate.of(2019, Month.JUNE, 03);
	LocalDate woche1_tag2 = LocalDate.of(2019, Month.JUNE, 04);
	LocalDate woche1_tag3 = LocalDate.of(2019, Month.JUNE, 05);
	LocalDate woche1_tag4 = LocalDate.of(2019, Month.JUNE, 06);
	LocalDate woche1_tag5 = LocalDate.of(2019, Month.JUNE, 07);

	// Woche2 10.06.2019-14.06.2019
	LocalDate woche2_tag1 = LocalDate.of(2019, Month.JUNE, 10);
	LocalDate woche2_tag2 = LocalDate.of(2019, Month.JUNE, 11);
	LocalDate woche2_tag3 = LocalDate.of(2019, Month.JUNE, 12);
	LocalDate woche2_tag4 = LocalDate.of(2019, Month.JUNE, 13);
	LocalDate woche2_tag5 = LocalDate.of(2019, Month.JUNE, 14);

	// Woche3 17.06.2019-21.06.2019
	LocalDate woche3_tag1 = LocalDate.of(2019, Month.JUNE, 17);
	LocalDate woche3_tag2 = LocalDate.of(2019, Month.JUNE, 18);
	LocalDate woche3_tag3 = LocalDate.of(2019, Month.JUNE, 19);
	LocalDate woche3_tag4 = LocalDate.of(2019, Month.JUNE, 20);
	LocalDate woche3_tag5 = LocalDate.of(2019, Month.JUNE, 21);

	// Funktionen: Wochenansicht:
	// Table: Auftragsverteilung
	// dropDown Wochenauswahl
	// fuer jeden MA werden alle seine Auftraege fuer die jwlg. Woche angezeigt
	// Inhalt zu Tabelle hinzufuegen (MA und Rechner)
	// REchner muessen verschoben werden, wenn sie das taeglich maximale
	// Arbeitspensum
	// des MAs (4h/8h)

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		db.openConnection();

		// ComboBox Wochenangaben als String speichern
		DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		String woche1 = woche1_tag1.format(f) + "-" + woche1_tag5.format(f);
		String woche2 = woche2_tag1.format(f) + "-" + woche2_tag5.format(f);
		String woche3 = woche3_tag1.format(f) + "-" + woche3_tag5.format(f);

		// ComboBox Wochenangaben Liste
		ObservableList<String> options = FXCollections.observableArrayList(woche1, woche2, woche3);

		// ComboBox befüllen
		comboBox_RW_Wochenansicht.setItems(options);

		// Wochen- und Listenansicht Inhalte
		wochenansichtFuellen();
		listenansichtFuellen();

		// db.closeConnection();
	}

	// Wochenansicht Tabelle befüllen
	public void wochenansichtFuellen() {

		ObservableList<Auftragsverteilung> rechnerWochenansichtTabelle = FXCollections.observableArrayList();

		try {
			rechnerWochenansichtTabelle.addAll(db.listRechnerAusAuftragsverteilungWoche(/* LoginController.user */));
			// System.out.println(rechnerWochenansichtTabelle.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		col_RW_Montag.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
		// col_RW_Dienstag.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
		// col_RW_Mittwoch.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
		// col_RW_Donnerstag.setCellValueFactory(new
		// PropertyValueFactory<>("seriennr"));
		// col_RW_Freitag.setCellValueFactory(new PropertyValueFactory<>("seriennr"));

		tableRechnerWoche.setItems(rechnerWochenansichtTabelle);

		// tableRechnerWoche.getColumns().add(col_RW_Montag);

	}

	// Listenansicht Tabelle befüllen
	public void listenansichtFuellen() {

		ObservableList<Auftragsverteilung> rechnerListenansichtTabelle = FXCollections.observableArrayList();

		try {
			rechnerListenansichtTabelle.addAll(db.listRechnerAusAuftragsverteilungListe(/* LoginController.user */));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		col_RL_serienNr.setCellValueFactory(new PropertyValueFactory<>("seriennr"));
		col_RL_status.setCellValueFactory(new PropertyValueFactory<>("status"));
		col_RL_bearbeitungsdatum.setCellValueFactory(new PropertyValueFactory<>("bearbeitungsdatum"));
		col_RL_lieferdatum.setCellValueFactory(new PropertyValueFactory<>("lieferdatum"));

		tableRechnerListe.setItems(rechnerListenansichtTabelle);
	}

	// Wochennansicht - Klick auf Rechner öffnet Rechnerinfo --> Abfrage FA/SA fehlt
	// noch
	public void clickRechnerWoche(MouseEvent e) {

		if (e.getClickCount() == 2) {
			System.out.println(tableRechnerWoche.getSelectionModel().getSelectedItem().getSeriennr());
			try {
				new FolgeFenster("/views/FA_Rechnerinfo.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	// Listenansicht - Klick auf Rechner öffnet Rechnerinfo --> Abfrage FA/SA fehlt
	// noch
	public void clickRechnerListe(MouseEvent e) {

		if (e.getClickCount() == 2) {
			System.out.println(tableRechnerListe.getSelectionModel().getSelectedItem().getSeriennr());
			try {
				new FolgeFenster("/views/FA_Rechnerinfo.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
