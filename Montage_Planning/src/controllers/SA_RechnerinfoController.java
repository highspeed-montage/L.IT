package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import application.Datenbank;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
	private SA_Rechner sr;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		db.openConnection();

		String stsBearb = "in Bearbeitung";
		String stsFertig = "erledigt";
		String stsImLager = "im Lager";

		ObservableList<String> status = FXCollections.observableArrayList(stsBearb, stsFertig, stsImLager);
		comboBox_SAI_Bearbeitungsstatus.setItems(status);

		SA_RechnerInfo_fuellen();

		txt_SAI_Einzelteilsuche.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent ke) {

				int lagerbestand = 0;
				if (ke.getCode().equals(KeyCode.ENTER)) {
					if (rbtn_SAI_Hardware.isSelected()) {
						String eingabe = txt_SAI_Einzelteilsuche.getText().toLowerCase();
						if (eingabe.isEmpty()) {
							lbl_SAI_SuchStatus.setText("leere Eingabe");
						} else {
							try {
								lagerbestand = db.getEinzelteilLagerbestand(eingabe, sr.getSeriennr());
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							if (lagerbestand == 0) {
								try { // Aktualisieung Rechner Status zu id = 7
									db.setRechnerStatus(sr.getSeriennr(), "unvollstaendig");
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if (lagerbestand > 0) {
								lbl_SAI_SuchStatus.setText("auf Lager: " + lagerbestand);
							} else if (lagerbestand == 9999) {
								String title = "Unbekannte Eingabe";
								String info = "Kein Einzelteil mit dieser Bezeichnung";
								AlertController.error(title, info);							}
						}
					} else {
						String title = "Einzelteilsuche";
						String info = "RadioButton \"Hardware\" muss ausgew�hlt sein";
						AlertController.information(title, info);
					}
				}

			}
		});


		rbtn_SAI_Hardware.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (rbtn_SAI_Hardware.isSelected() == true) {
						sr.setHardwareverschuldet(true);
						sr.setSoftwareverschuldet(false);
						sr.setKundenverschuldet(false);
						System.out.println(sr.toString());
					}
				});

		rbtn_SAI_Software.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (rbtn_SAI_Software.isSelected() == true) {
						sr.setSoftwareverschuldet(true);
						sr.setHardwareverschuldet(false);
						sr.setKundenverschuldet(false);
						System.out.println(sr.toString());
						sr.setProzessor_kaputt(false);
						sr.setDvd_Laufwerk_kaputt(false);
						sr.setGrafikkarte_kaputt(false);
						sr.setFestplatte_kaputt(false);
						cBox_SAI__Festplatte.setSelected(false);
						cBox_SAI_Grafikkarte.setSelected(false);
						cBox_SAI_Laufwerk.setSelected(false);
						cBox_SAI_Prozessor.setSelected(false);
					}
				});

		rbtn_SAI_Kunde.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (rbtn_SAI_Kunde.isSelected() == true) {
						sr.setKundenverschuldet(true);
						sr.setHardwareverschuldet(false);
						sr.setSoftwareverschuldet(false);
						System.out.println(sr.toString());
						sr.setProzessor_kaputt(false);
						sr.setDvd_Laufwerk_kaputt(false);
						sr.setGrafikkarte_kaputt(false);
						sr.setFestplatte_kaputt(false);
						cBox_SAI__Festplatte.setSelected(false);
						cBox_SAI_Grafikkarte.setSelected(false);
						cBox_SAI_Laufwerk.setSelected(false);
						cBox_SAI_Prozessor.setSelected(false);
					}
				});

	}
/**
 * Rechnerinfo wird gefuellt mit Informationen
 */
	private void SA_RechnerInfo_fuellen() {
		try {
			sr = db.getSARechnerInfo(RechneransichtController.seriennrAktuell);// RechneransichtController.seriennrAktuell
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		System.out.println(sr.toString());

		lbl_SAI_Seriennummer.setText(String.valueOf(RechneransichtController.seriennrAktuell));// RechneransichtController.seriennrAktuell

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
	 *  ComboBox Aktualisiert dementsprechend den
	 * Rechnerstatus
	 * @param event
	 */
	@FXML
	public void setSAStatus(ActionEvent event) {
		String selectedSatus = comboBox_SAI_Bearbeitungsstatus.getSelectionModel().getSelectedItem();

		try {
			db.setRechnerStatus(sr.getSeriennr(), selectedSatus);
			lbl_SAI_status.setText(selectedSatus);
			//Update Auftragsstatus:
			int lowestId = db.getLowestRechnerIDAuftrag(sr.getAuftragsNr());
			db.setAuftragStatus(sr.getAuftragsNr(), lowestId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checkbox Handler Prozessor
	 */
	@FXML
	private void handleCBoxProzessor() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setProzessor_kaputt(true);
			// }
		} else {
			String title = "Checkbox";
			String info = "RadioButton \"Hardware\" muss ausgew�hlt sein";
			AlertController.information(title, info);
		}
	}

	/**
	 * Checkbox Handler Grafikkarte
	 */
	@FXML
	private void handleCBoxGrafikkarte() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setGrafikkarte_kaputt(true);
			;
		} else {
			String title = "Checkbox";
			String info = "RadioButton \"Hardware\" muss ausgew�hlt sein";
			AlertController.information(title, info);
		}
	}

	/**
	 * Checkbox Handler Festplatte
	 */
	@FXML
	private void handleCBoxFestplatte() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setFestplatte_kaputt(true);
		} else {
			String title = "Checkbox";
			String info = "RadioButton \"Hardware\" muss ausgew�hlt sein";
			AlertController.information(title, info);
		}
	}

	/**
	 * Checkbox Handler Laufwerk
	 */
	@FXML
	private void handleCBoxLaufwerk() {
		if (rbtn_SAI_Hardware.isSelected()) {
			sr.setDvd_Laufwerk_kaputt(true);
		} else {
			String title = "Checkbox";
			String info = "RadioButton \"Hardware\" muss ausgew�hlt sein";
			AlertController.information(title, info);
		}
	}

	/**
	 * Checkbox Handler Aenderungen werden in DB geladen
	 */
	@FXML
	private void handleSpeichernToDb(ActionEvent event) {
		try {
			db.updateSA_Recher(sr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
