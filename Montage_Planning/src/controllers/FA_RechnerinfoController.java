package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class FA_RechnerinfoController {
	/**Rechnerinfo*/
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
	/**Einzelteiltabelle*/
	@FXML
	private TableColumn<?, ?> TableColumn_FAI_einzelteile;
}
