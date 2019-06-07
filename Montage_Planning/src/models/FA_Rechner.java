package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class FA_Rechner extends Rechner {


	private static final double ARBEITSAUFWAND = 4;

	//DB Abfrage:
		private Date lieferdatum;
//		private Geschaeftskunde gk;
//		private Privatkunde pk;
		private String Firmenname;
		private String PrivatName;
		
		public FA_Rechner(int pSeriennr, Auftrag pAuftrag, String pStatus, Teile[] pTeile) {
			super(pSeriennr, pAuftrag, pStatus, pTeile);
			// TODO Auto-generated constructor stub
		}
		// Auftragsverteilung.Rechner_seriennummer, Rechner.Status_idStatus, Auftragsverteilung.Datum, Kunde.EMail, Kunde.Name, Kunde.idKundennummer
		
		
//		public Rechner(int pSeriennr, Fertigungsauftrag fa, String pStatus, Teile... pTeile)
//		{
//			setSeriennr(pSeriennr);
//			this.fa = fa;
//			status = pStatus;
//			this.teile = new ArrayList<Teile>(Arrays.asList(pTeile));
//		}
		public FA_Rechner(Date lieferdatum, String Firmenname, String PrivatName, int pSeriennr, int auftragsNr, String pStatus, Date pBearbeitungsdatum, Teile... pTeile) {
			super(pSeriennr, auftragsNr, pStatus,pBearbeitungsdatum, pTeile);
			this.lieferdatum = lieferdatum;
			this.Firmenname = Firmenname;
			this.PrivatName = PrivatName;
		}
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
		return ARBEITSAUFWAND;
	}
}
