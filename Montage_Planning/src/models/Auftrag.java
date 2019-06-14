package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Auftrag {
	private int auftragsnr;
	private String status;
	private Kunde kunde;
	private boolean zugewiesen;
	private List<Rechner> rechner = new ArrayList<Rechner>();
	private int anzahlRechner;
	private double arbeitsaufwand;
	private static final int lieferzeit = 14;
	private Date lieferdatum;

	public Auftrag(int pAuftragsnr, String pStatus, Kunde pKunde, boolean pZugewiesen) {
		auftragsnr = pAuftragsnr;
		status = pStatus;
		kunde = pKunde;
		zugewiesen = pZugewiesen;
	}

	public Auftrag(int auftragsnr, Date lieferdatum, int anzahlRechner) {
		super();
		this.auftragsnr = auftragsnr;
		this.lieferdatum = lieferdatum;
		this.anzahlRechner = anzahlRechner;
	}
	
	public Auftrag(int auftragsnr, Date lieferdatum) {
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

	public int getAuftragsnr() {
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

}
