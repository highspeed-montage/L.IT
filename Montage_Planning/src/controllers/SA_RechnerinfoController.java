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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SA_RechnerinfoController implements EventHandler, Initializable {
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

	private Datenbank db = new Datenbank();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		db.openConnection();

		//String stsGetestet = "getestet";	//Anstelle von stsBearb
		String stsBearb = "in Bearbeitung";
		String stsFertig = "erledigt";
		String stsImLager = "im Lager";

		ObservableList<String> status = FXCollections.observableArrayList(stsBearb, stsFertig, stsImLager);
		comboBox_SAI_Bearbeitungsstatus.setItems(status);
		
		SA_RechnerInfo_fuellen();

		txt_SAI_Einzelteilsuche.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent ke) {
				//wenn der rbtn_Hwardware ausgewählt ist, DANN per Enter gesucht werden!
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
				// ab in die DB damit
				//
				//
			}

		});

	}

	/**
	 * Bekommt gewähltes Element der ComboBox Aktualisiert dementsprechend den
	 * Rechnerstatus
	 */
	@FXML
	public void setSAStatus(ActionEvent event) { 
		String selectedSatus = comboBox_SAI_Bearbeitungsstatus.getSelectionModel().getSelectedItem();

		db.setRechnerStatus(pSerienNr, selectedSatus);// pSeriennummer aus SA_Rechner sr (sr.getSNr..);
	}

	@Override
	public void handle(Event arg0) {
		// TODO Auto-generated method stub

	}

}
