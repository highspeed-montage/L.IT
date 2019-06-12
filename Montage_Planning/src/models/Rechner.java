package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class Rechner {
	private Integer seriennr;
	private Auftrag auftrag;
	private String status;
	private Monteur monteur;
	private List<Teile> teile = new ArrayList<Teile>();
	private boolean montiert;
	private boolean installiert;
	private boolean getestet;
	private Integer auftragsNr;
	private Date bearbeitungsdatum;
	private Date lieferdatum;
	private String firmenname;
	private String privatName;
	private Integer kundenId;
	private String eMail;

	public Rechner(int pSeriennr, Auftrag pAuftrag, String pStatus, List<Teile> pTeile) {
		setSeriennr(pSeriennr);
		setAuftrag(pAuftrag);
		status = pStatus;
		// this.teile = new ArrayList<Teile>(Arrays.asList(pTeile));
		teile = pTeile;
	}

	// KOnstruktor fuer FA_REchner
	public Rechner(int pSeriennr, int auftragsNr, String pStatus, Date pBearbeitungsdatum, Date pLieferdatum,
			String pFirmenname, String pPrivatname, int pKundenId, String pEMail, List<Teile> pTeile) {
		setSeriennr(pSeriennr);
		this.auftragsNr = auftragsNr;
		status = pStatus;
		this.bearbeitungsdatum = pBearbeitungsdatum;
		this.lieferdatum = pLieferdatum;
		this.firmenname = pFirmenname;
		this.privatName = pPrivatname;
		this.kundenId = pKundenId;
		this.eMail = pEMail;
		this.teile = pTeile;

	}

	// Konstruktor fuer SA_Rechner
	public Rechner(int pSeriennr, int auftragsNr, String pStatus, Date pBearbeitungsdatum, Date pLieferdatum,
			String pFirmenname, String pPrivatname, int pKundenId, String pEMail) {
		setSeriennr(pSeriennr);
		this.auftragsNr = auftragsNr;
		status = pStatus;
		this.bearbeitungsdatum = pBearbeitungsdatum;
		this.lieferdatum = pLieferdatum;
		this.firmenname = pFirmenname;
		this.privatName = pPrivatname;
		this.kundenId = pKundenId;
		this.eMail = pEMail;

	}

	public void disponieren() {
		status = "disponieren";
	}

	public void setUnvollstaendig() {
		status = "unvollstaendig";
	}

	public void setAngelegt() {
		status = "angelegt";
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

	public Integer getSeriennr() {
		return seriennr;
	}

	public void setSeriennr(int seriennr) {
		this.seriennr = seriennr;
	}

	public Auftrag getAuftrag() {
		return auftrag;
	}

	public void setAuftrag(Auftrag auftrag) {
		this.auftrag = auftrag;
	}

	public String getStatus() {
		return status;
	}

	public Monteur getMonteur() {
		return monteur;
	}

	public void setMonteur(Monteur pMonteur) {
		monteur = pMonteur;
	}

	public boolean isMontiert() {
		return montiert;
	}

	public void setMontiert(boolean pMontiert) {
		montiert = pMontiert;
	}

	public boolean isInstalliert() {
		return installiert;
	}

	public void setInstalliert(boolean pInstalliert) {
		installiert = pInstalliert;
	}

	public boolean isGetestet() {
		return getestet;
	}

	public void setGetestet(boolean pGetestet) {
		getestet = pGetestet;
	}

	// Getter speziell f�r DB-Abfrage FA_Rechner
	public Integer getAuftragsNr() {
		return auftragsNr;
	}

	public Date getBearbeitungsdatum() {
		return bearbeitungsdatum;
	}

	public Date getLieferdatum() {
		return lieferdatum;
	}

	public String getFirmenname() {
		return firmenname;
	}

	public String getPrivatName() {
		return privatName;
	}

	public Integer getKundenId() {
		return kundenId;
	}

	public String geteMail() {
		return eMail;
	}

	public List<Teile> getTeile() {
		return teile;
	}

	public void setTeile(List<Teile> teile) {
		this.teile = teile;
	}

	public void setSeriennr(Integer seriennr) {
		this.seriennr = seriennr;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setAuftragsNr(Integer auftragsNr) {
		this.auftragsNr = auftragsNr;
	}

	public void setBearbeitungsdatum(Date bearbeitungsdatum) {
		this.bearbeitungsdatum = bearbeitungsdatum;
	}

	public void setLieferdatum(Date lieferdatum) {
		this.lieferdatum = lieferdatum;
	}

	public void setFirmenname(String firmenname) {
		this.firmenname = firmenname;
	}

	public void setPrivatName(String privatName) {
		this.privatName = privatName;
	}

	public void setKundenId(Integer kundenId) {
		this.kundenId = kundenId;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	@Override
	public String toString() {
		return "Rechner [seriennr=" + seriennr + ", auftrag=" + auftrag + ", status=" + status + ", monteur=" + monteur
				+ ", teile=" + teile + ", montiert=" + montiert + ", installiert=" + installiert + ", getestet="
				+ getestet + ", auftragsNr=" + auftragsNr + ", bearbeitungsdatum=" + bearbeitungsdatum
				+ ", lieferdatum=" + lieferdatum + ", firmenname=" + firmenname + ", privatName=" + privatName
				+ ", kundenId=" + kundenId + ", eMail=" + eMail + "]";
	}

}
