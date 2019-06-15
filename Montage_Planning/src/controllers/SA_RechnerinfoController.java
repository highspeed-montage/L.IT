package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.Datenbank;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import models.SA_Rechner;

public class SA_RechnerinfoController implements Initializable {
	/** Rechnerinfo */
	@FXML
	private Label lbl_SAI_status;
	@FXML
	private Label lbl_SAI_lieferdatum;
	@FXML
	private Label lbl_SAI_bearbeitungsdatum;
	@FXML
	private Label lbl_SAI_kunde;
	@FXML
	private Label lbl_SAI_kundenNr;
	@FXML
	private Label lbl_SAI_kundenEMail;
	@FXML
	private Label lbl_SAI_Seriennummer;

	/** Problemdokumentation */
	@FXML
	private ToggleGroup toggle_SAI_Dokumentation;
	@FXML
	private ComboBox<String> comboBox_SAI_Bearbeitungsstatus;
	@FXML
	private Label lbl_SAI_SuchStatus;

	@FXML
	private TextField txt_SAI_Einzelteilsuche;

	@FXML
	private RadioButton rbtn_SAI_Hardware;
	@FXML
	private RadioButton rbtn_SAI_Software;
	@FXML
	private RadioButton rbtn_SAI_Kunde;
	@FXML
	private Button btn_SAI_pdf;
	@FXML
	private Button btn_SAI_Speichern;

	// Teile

	@FXML
	private CheckBox cBox_SAI_Prozessor;
	@FXML
	private CheckBox cBox_SAI_Grafikkarte;
	@FXML
	private CheckBox cBox_SAI__Festplatte;
	@FXML
	private CheckBox cBox_SAI_Laufwerk;

	// Datenbank
	private Datenbank db = new Datenbank();
	private SA_Rechner sr = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		db.openConnection();

		// String stsGetestet = "getestet"; //Anstelle von stsBearb
		String stsBearb = "in Bearbeitung";
		String stsFertig = "erledigt";
		String stsImLager = "im Lager";

		ObservableList<String> status = FXCollections.observableArrayList(stsBearb, stsFertig, stsImLager);
		comboBox_SAI_Bearbeitungsstatus.setItems(status);

		SA_RechnerInfo_fuellen();

		txt_SAI_Einzelteilsuche.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent ke) {
				// wenn der rbtn_Hwardware ausgewählt ist, DANN per Enter gesucht werden!
				if (rbtn_SAI_Hardware.isSelected()) {
					int lagerbestand = 0;
					if (ke.getCode().equals(KeyCode.ENTER)) {
						String eingabe = txt_SAI_Einzelteilsuche.getText();
						if (eingabe.isEmpty()) {
							lbl_SAI_SuchStatus.setText("leere Eingabe");
						} else if (eingabe.contains("prozessor") | eingabe.equalsIgnoreCase("prozessor")
								| eingabe.equalsIgnoreCase("hauptspeicher") | eingabe.equalsIgnoreCase("festplatte")
								| eingabe.equalsIgnoreCase("laufwerk")) {
							try {
								lagerbestand = db.getEinzelteilLagerbestand(eingabe);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						} else {
							lbl_SAI_SuchStatus.setText("kein ET mit diser Bezeichnung");
						}
						if (lagerbestand > 0) {
							lbl_SAI_SuchStatus.setText("auf Lager: " + lagerbestand);
						} else if (lagerbestand == 0) {
							lbl_SAI_SuchStatus.setText("nicht auf Lager");
						}
						{
							lbl_SAI_SuchStatus.setText("ungültige Eingabe");
						}
					}
				}

			}
		});
		/**
		 * Radio button toggle Group Doku Problem muss in Rechner gepackt werden ->
		 * fehlt noch.. WO KOMMT DAS HIN? => SA Konstruktor ändern
		 */

		toggle_SAI_Dokumentation.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				RadioButton rb = (RadioButton) toggle.getToggleGroup().getSelectedToggle();
				System.out.println("(test)Selected rb: " + rb.getText());
				String problem = rb.getText();

				if (problem.equalsIgnoreCase("Hardware")) {
					sr.setHardwareverschuldet(true);

				} else if (problem.equalsIgnoreCase("Software")) {
					sr.setSoftwareverschuldet(true);

				} else if (problem.equalsIgnoreCase("Kunde")) {
					sr.setSoftwareverschuldet(true);
				}
			}

		});

	}

	private void SA_RechnerInfo_fuellen() {
		try {
			sr = db.getSARechnerInfo(RechneransichtController.seriennrAktuell);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		lbl_SAI_Seriennummer.setText(String.valueOf(sr.getSeriennr()));

		if (sr.getFirmenname() != null) {
			lbl_SAI_kunde.setText(sr.getFirmenname());
		} else {
			lbl_SAI_kunde.setText(sr.getPrivatName());
		}

		lbl_SAI_bearbeitungsdatum.setText(String.valueOf(sr.getBearbeitungsdatum()));
		lbl_SAI_kundenEMail.setText(sr.geteMail());
		lbl_SAI_kundenNr.setText(String.valueOf(sr.getKundenId()));
		lbl_SAI_lieferdatum.setText(String.valueOf(sr.getLieferdatum()));
		lbl_SAI_status.setText(sr.getStatus());

		if (sr.isHardwareverschuldet() == true) {
			rbtn_SAI_Hardware.setSelected(true);
			if (sr.isDvd_Laufwerk_kaputt() == true) {
				cBox_SAI_Laufwerk.setSelected(true);
			}
			if (sr.isFestplatte_kaputt() == true) {
				cBox_SAI__Festplatte.setSelected(true);
			}
			if (sr.isGrafikkarte_kaputt() == true) {
				cBox_SAI_Grafikkarte.setSelected(true);
			}
			if (sr.isProzessor_kaputt() == true) {
				cBox_SAI_Prozessor.setSelected(true);
			}
		} else if (sr.isSoftwareverschuldet() == true) {
			rbtn_SAI_Software.setSelected(true);
		} else if (sr.isKundenverschuldet() == true) {
			rbtn_SAI_Kunde.setSelected(true);

		}

	}

	/**
	 * Bekommt gewähltes Element der ComboBox Aktualisiert dementsprechend den
	 * Rechnerstatus
	 */
	@FXML
	public void setSAStatus(ActionEvent event) {
		String selectedSatus = comboBox_SAI_Bearbeitungsstatus.getSelectionModel().getSelectedItem();

		try {
			db.setRechnerStatus(sr.getSeriennr(), selectedSatus);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	CHeckBox HAndler:
	/** handle Checkbox "Prozessor". Falls geklickt: set true */
	@FXML
	private void handleCBoxProzessor() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setProzessor_kaputt(true); // Wenn der Wert false war
//			if(cBox_SAI_Prozessor.isSelected()) {	//BRAUCH ICH DAS FALLS UNSELECTED WIRD??
//				sr.setProzessor_kaputt(true); 
//			}else {
//				sr.setProzessor_kaputt(false);
//			}
		} else {
			lbl_SAI_SuchStatus.setText("kein Hardwareproblem");// Falls kein HW Problem, kann dies auch niht ankgekreuzt
																// werden
		}
	}

	/** handle Checkbox "Grafikkarte". Falls geklickt: set true */
	@FXML
	private void handleCBoxGrafikkarte() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setGrafikkarte_kaputt(true);
			;
		} else {
			lbl_SAI_SuchStatus.setText("kein Hardwareproblem");
		}
	}

	/** handle Checkbox "Festplatte". Falls geklickt: set true */
	@FXML
	private void handleCBoxFestplatte() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setFestplatte_kaputt(true);
		} else {
			lbl_SAI_SuchStatus.setText("kein Hardwareproblem");
		}
	}

	/** handle Checkbox "Laufwerk". Falls geklickt: set true */
	@FXML
	private void handleCBoxLaufwerk() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setDvd_Laufwerk_kaputt(true);
		} else {
			lbl_SAI_SuchStatus.setText("kein Hardwareproblem");
		}
	}

	/** SPeichernButton, Änderungen an SA_Rechner Objekt werden in DB geladen */
	@FXML
	private void handleSpeichernToDb(ActionEvent event) {
		try {
			db.updateSA_Recher(sr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
