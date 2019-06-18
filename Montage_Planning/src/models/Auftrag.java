package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Auftrag {
	private Integer auftragsnr;
	private String status;
	private Kunde kunde;
	private boolean zugewiesen;
	private List<Rechner> rechner = new ArrayList<Rechner>();
	private Integer anzahlRechner;
	private double arbeitsaufwand;
	private static final int lieferzeit = 14;
	private Date lieferdatum;

	// Fuer DB Abfrage Auftragsinfo:
	private Date bestelldatum;
	private String kundentyp;
	private String kundenNr;
	private String kundenEMail;
	private List<Rechner> rechnerVonAuftrag = new ArrayList<Rechner>();

	public Auftrag(Integer pAuftragsnr) {
		auftragsnr = pAuftragsnr;
	}

	public Date getBestelldatum() {
		return bestelldatum;
	}

	public String getKundentyp() {
		return kundentyp;
	}

	public String getKundenNr() {
		return kundenNr;
	}

	public String getKundenEMail() {
		return kundenEMail;
	}

	public List<Rechner> getRechnerVonAuftrag() {
		return rechnerVonAuftrag;
	}

	public Auftrag(Integer auftragsnr, Date lieferdatum, Integer anzahlRechner, String status) {
		this.auftragsnr = auftragsnr;
		this.lieferdatum = lieferdatum;
		this.anzahlRechner = anzahlRechner;
		this.status = status;
	}

	/** Für DB Abfragen um AUftragsinfo zu füllen */
	public Auftrag(Integer pAuftragsnr, String pStatus, Date pLieferdatum, Date pBestelldatum, String pKundentyp,
			Integer pKundenNr, String pKundenEmail, String pKundenName, List<Rechner> pRechner) {
		this.auftragsnr = pAuftragsnr;
		this.status = pStatus;
		this.lieferdatum = pLieferdatum;
		this.bestelldatum = pBestelldatum;
		this.kundentyp = pKundentyp;
		this.rechnerVonAuftrag = pRechner;
	}

	public Auftrag(Integer auftragsnr, Date lieferdatum) {
		super();
		this.auftragsnr = auftragsnr;
		this.lieferdatum = lieferdatum;
	}

	public Auftrag(Date lieferdatum) {
		this.lieferdatum = lieferdatum;
	}

	public void setAngelegt() {
		status = "angelegt";
	}

	public void disponieren() {
		status = "disponiert";
	}

	public void inBearbeitung() {
		status = "in Bearbeitung";
	}

	public void erledigt() {
		status = "erledigt";
	}

	public void imLager() {
		status = "im Lager";
	}

	public void setZugewiesen(boolean pZugewiesen) {
		zugewiesen = pZugewiesen;
	}

	public Integer getAuftragsnr() {
		return auftragsnr;
	}

	public String getStatus() {
		return status;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public boolean getZugewiesen() {
		return zugewiesen;
	}

	public double getArbeitsaufwand() {
		return arbeitsaufwand;
	}

	public int getLieferzeit() {
		return lieferzeit;
	}

	public List<Rechner> getRechner() {
		return rechner;
	}

	public void setRechner(List<Rechner> rechner) {
		this.rechner = rechner;
	}

	public Date getLieferdatum() {
		return lieferdatum;
	}

	public void setLieferdatum(Date lieferdatum) {
		this.lieferdatum = lieferdatum;
	}

	public Integer getAnzahlRechner() {
		return anzahlRechner;
	}

	public void setAnzahlRechner(Integer anzahlRechner) {
		this.anzahlRechner = anzahlRechner;
	}

	public void setAuftragsnr(Integer auftragsnr) {
		this.auftragsnr = auftragsnr;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public void setArbeitsaufwand(double arbeitsaufwand) {
		this.arbeitsaufwand = arbeitsaufwand;
	}

}
