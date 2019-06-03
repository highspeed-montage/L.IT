package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class SA_RechnerinfoController {
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
	
	
}
