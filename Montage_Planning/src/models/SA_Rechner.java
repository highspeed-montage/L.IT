package models;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class SA_Rechner extends Rechner {

//	private double arbeitsaufwand;	
//	private boolean getestet;
//	private boolean neues_Betriebssystem;

	private static final double ARBEITSAUFWAND_SA = 2;
	// Boolean zu Problemfï¿½llen (fï¿½r rbtns der Doku)
	private boolean kundenverschuldet;
	private boolean hardwareverschuldet;
	private boolean softwareverschuldet;
	// Boolean zu HW-Schï¿½den (rbtns unter hw-rbtn)
	private boolean prozessor_kaputt;
	private boolean grafikkarte_kaputt;
	private boolean festplatte_kaputt;
	private boolean dvd_Laufwerk_kaputt;


	/** Konstruktor fï¿½r DB-Interaktionen */
	public SA_Rechner(int pSeriennr, int auftragsNr, String pStatus, LocalDate pBearbeitungsdatum, Date pLieferdatum,
			String pFirmenname, String pPrivatname, int pKundenId, String pEMail, boolean pKundenverschuldet,
			boolean pHardwareverschuldet, boolean pSoftwareverschuldet, boolean pProzessor_kaputt,
			boolean pGrafikkarte_kaputt, boolean pFestplatte_kaputt, boolean pDvd_Laufwerk_kaputt) {
		super(pSeriennr, auftragsNr, pStatus, pBearbeitungsdatum, pLieferdatum, pFirmenname, pPrivatname, pKundenId,
				pEMail);
		this.kundenverschuldet = pKundenverschuldet;
		this.hardwareverschuldet = pHardwareverschuldet;
		this.softwareverschuldet = pSoftwareverschuldet;
		this.prozessor_kaputt = pProzessor_kaputt;
		this.grafikkarte_kaputt = pGrafikkarte_kaputt;
		this.festplatte_kaputt = pFestplatte_kaputt;
		this.dvd_Laufwerk_kaputt = pDvd_Laufwerk_kaputt;
	}

	public void setKundenverschuldet(boolean pKundenverschuldet) {
		this.kundenverschuldet = pKundenverschuldet;
	}

	public void setProzessor_kaputt(boolean pProzessor) {
		this.prozessor_kaputt = pProzessor;
	}

	public void setGrafikkarte_kaputt(boolean pGrafikkarte) {
		this.grafikkarte_kaputt = pGrafikkarte;
	}

	public void setFestplatte_kaputt(boolean pFestplatte) {
		this.festplatte_kaputt = pFestplatte;
	}

	public void setDvd_Laufwerk_kaputt(boolean pLaufwerk) {
		this.dvd_Laufwerk_kaputt = pLaufwerk;
	}

	public void setKundenverschuldet1(boolean pKunde) {
		this.kundenverschuldet = pKunde;
	}

	public void setSoftwareverschuldet(boolean pSoftware) {
		this.softwareverschuldet = pSoftware;
	}

	public void setHardwareverschuldet(boolean pHardware) {
		this.hardwareverschuldet = pHardware;
	}
//GETTER
	public static double getArbeitsaufwandSa() {
		return ARBEITSAUFWAND_SA;
	}

	public boolean isKundenverschuldet() {
		return kundenverschuldet;
	}

	public boolean isHardwareverschuldet() {
		return hardwareverschuldet;
	}

	public boolean isSoftwareverschuldet() {
		return softwareverschuldet;
	}

	public boolean isProzessor_kaputt() {
		return prozessor_kaputt;
	}

	public boolean isGrafikkarte_kaputt() {
		return grafikkarte_kaputt;
	}

	public boolean isFestplatte_kaputt() {
		return festplatte_kaputt;
	}

	public boolean isDvd_Laufwerk_kaputt() {
		return dvd_Laufwerk_kaputt;
	}

	@Override
	public String toString() {
		return "SA_Rechner [kundenverschuldet=" + kundenverschuldet + ", hardwareverschuldet=" + hardwareverschuldet
				+ ", softwareverschuldet=" + softwareverschuldet + ", prozessor_kaputt=" + prozessor_kaputt
				+ ", grafikkarte_kaputt=" + grafikkarte_kaputt + ", festplatte_kaputt=" + festplatte_kaputt
				+ ", dvd_Laufwerk_kaputt=" + dvd_Laufwerk_kaputt + "]";
	}
	
	

}
