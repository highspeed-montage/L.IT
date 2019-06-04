package models;

import java.util.Date;

public class FA_Rechner extends Rechner {


	private static final double arbeitsaufwand = 4;

	//DB Abfrage:
		private Date lieferdatum;
		private Geschaeftskunde gk;
		private Privatkunde pk;
		
		public FA_Rechner(int pSeriennr, Auftrag pAuftrag, String pStatus, Teile[] pTeile) {
			super(pSeriennr, pAuftrag, pStatus, pTeile);
			// TODO Auto-generated constructor stub
		}
		// Auftragsverteilung.Rechner_seriennummer, Rechner.Status_idStatus, Auftragsverteilung.Datum, Kunde.EMail, Kunde.Name, Kunde.idKundennummer
		
		
//	//hier KOnstruktor für DB Abfrage //OHNE TEILE
//		public FA_Rechner(int pSeriennr, String pStatus,  Date lieferdatum, Date bearbeitungsdatum, String emailKunde, String nameKunde, Integer idKunde) {
//			super(pSeriennr, pStatus);
//			//this.teile = teile;
//			this.lieferdatum = lieferdatum;
//			this.bearbeitungsdatum = bearbeitungsdatum;
//			this.emailKunde = emailKunde;
//			this.nameKunde = nameKunde;
//			this.idKunde = idKunde;
//		}
//	
	public double getArbeitsaufwand() {
		return arbeitsaufwand;
	}
}