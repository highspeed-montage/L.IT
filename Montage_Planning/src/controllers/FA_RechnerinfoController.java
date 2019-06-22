package controllers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import application.Datenbank;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.FA_Rechner;
import models.Teile;

public class FA_RechnerinfoController implements Initializable {
	/** Rechnerinfo */
	@FXML
	private Label lbl_FAI_status;
	@FXML
	private Label lbl_FAI_lieferdatum;
	@FXML
	private Label lbl_FAI_bearbeitungsdatum;
	@FXML
	private Label lbl_FAI_kunde;
	@FXML
	private Label lbl_FAI_kundenNr;
	@FXML
	private Label lbl_FAI_kundenEMail;
	@FXML
	private Label lbl_FAI_Seriennummer;
	@FXML
	private ComboBox<String> comboBox_FAI_Status = null;
	@FXML
	private Button btn_FAI_SNrDrucken;

	/** Einzelteiltabelle */
	@FXML
	private TableView<Teile> tableFARechnerInfo;

	@FXML
	private TableColumn<Teile, String> TableColumn_FAI_einzelteile;

	private Datenbank db = new Datenbank();

	// ComboBox Inhalte
	ObservableList<String> status = FXCollections.observableArrayList();

	FA_Rechner fr = null;

	String stsBearb, stsFertig, stsImLager;

	ObservableList<Teile> einzelteile = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		db.openConnection();
		stsBearb = "in Bearbeitung";
		stsFertig = "erledigt";
		stsImLager = "im Lager";

		status.addAll(stsBearb, stsFertig, stsImLager);

		comboBox_FAI_Status.setItems(status);

		FA_RechnerInfo_fuellen();
	}

	/**
	 * Labels der FA_RECHNERINFO Ansicht werden befuellt mit Werten aus der
	 * Datenbank
	 */
	public void FA_RechnerInfo_fuellen() {

		try {
			fr = db.getFARechnerInfo(RechneransichtController.seriennrAktuell);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		lbl_FAI_Seriennummer.setText(String.valueOf(RechneransichtController.seriennrAktuell));

		if (fr.getFirmenname() != null) {
			lbl_FAI_kunde.setText(fr.getFirmenname());
		} else {
			lbl_FAI_kunde.setText(fr.getPrivatName());
		}

		lbl_FAI_bearbeitungsdatum.setText(String.valueOf(fr.getBearbeitungsdatum()));
		lbl_FAI_kundenEMail.setText(fr.geteMail());
		lbl_FAI_kundenNr.setText(String.valueOf(fr.getKundenId()));
		lbl_FAI_lieferdatum.setText(String.valueOf(fr.getLieferdatum()));
		lbl_FAI_status.setText(fr.getStatus());

		einzelteile.addAll(fr.getTeile());

		TableColumn_FAI_einzelteile.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
		tableFARechnerInfo.setItems(einzelteile);
	}

	/**
	 * Bekommt gewaehltes Element der ComboBox Aktualisiert dementsprechend den
	 * Rechnerstatus
	 */
	@FXML
	public void setFAStatus(ActionEvent event) {
		String selectedSatus = comboBox_FAI_Status.getSelectionModel().getSelectedItem();

		try {
			db.setRechnerStatus(fr.getSeriennr(), selectedSatus);
			lbl_FAI_status.setText(selectedSatus);
			int lowestId = db.getLowestRechnerIDAuftrag(fr.getAuftragsNr());
			db.setAuftragStatus(fr.getAuftragsNr(), lowestId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	/**
	 * Seriennummer wird in .txt Datei geschrieben
	 * fuers Drucken der Sticker zur identifikation
	 */
	public void createSeriennummerPDF() {

		PrintWriter pWriter = null;
		try {
			pWriter = new PrintWriter(new BufferedWriter(new FileWriter("src/application/seriennummer.txt")));
			pWriter.println(RechneransichtController.seriennrAktuell);
			String title = "PDF";
			String info = "Seriennummer PDF generiert";
			AlertController.information(title, info);
			System.out.println("PDF gedruckt"); // Message Dialog "PDF ist gespeichert."
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (pWriter != null) {
				pWriter.flush();
				pWriter.close();
			}

		}
	}
}
