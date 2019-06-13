package models;

import java.time.LocalDate;
import java.util.Date;

public class Auftragsverteilung {

	private LocalDate bearbeitungsdatum;
	private Date lieferdatum;
	private Integer seriennr;
	private String status;

	public Auftragsverteilung(LocalDate bearbeitungsdatum, Date lieferdatum, Integer seriennr, String status) {
		super();
		this.bearbeitungsdatum = bearbeitungsdatum;
		this.lieferdatum = lieferdatum;
		this.seriennr = seriennr;
		this.status = status;
	}

	// Konstruktor für Datenbankabfrage listRechnerAusAuftragsverteilungWoche()
	public Auftragsverteilung(Integer seriennr, LocalDate bearbeitungsdatum) {
		super();
		this.seriennr = seriennr;
		this.bearbeitungsdatum = bearbeitungsdatum;
	}

	@Override
	public String toString() {
		return "Auftragsverteilung [bearbeitungsdatum=" + bearbeitungsdatum + ", lieferdatum=" + lieferdatum
				+ ", seriennr=" + seriennr + ", status=" + status + "]";
	}

	public LocalDate getBearbeitungsdatum() {
		return bearbeitungsdatum;
	}

	public void setBearbeitungsdatum(LocalDate bearbeitungsdatum) {
		this.bearbeitungsdatum = bearbeitungsdatum;
	}

	public Date getLieferdatum() {
		return lieferdatum;
	}

	public void setLieferdatum(Date lieferdatum) {
		this.lieferdatum = lieferdatum;
	}

	public Integer getSeriennr() {
		return seriennr;
	}

	public void setSeriennr(Integer seriennr) {
		this.seriennr = seriennr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
