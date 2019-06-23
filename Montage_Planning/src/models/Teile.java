package models;

import java.io.Serializable;

public class Teile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int teilenr;
	private String kategorie;
	private int lagerbestand;
	private String bezeichnung;
	
	public Teile(int pTeilenr, String pBezeichnung, String pKategorie)
	{
		teilenr = pTeilenr;
		kategorie = pKategorie;
		bezeichnung = pBezeichnung;
	}
	public Teile(int pTeilenr, String pBezeichnung, String pKategorie, int pLagerbestand)
	{
		teilenr = pTeilenr;
		kategorie = pKategorie;
		bezeichnung = pBezeichnung;
		lagerbestand = pLagerbestand;
	}
	//Konstruktor fuer Db Abfrage getFARechnerInfo()
	public Teile(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public void setKategorie(String pKategorie)
	{
		kategorie = pKategorie;
	}
	public void setBezeichnung(String pBezeichnung)
	{
		bezeichnung = pBezeichnung;
	}
	public int getTeilenr()
	{
		return teilenr;
	}
	public String getKategorie()
	{
		return kategorie;
	}
	public int getLagerbestand()
	{
		return lagerbestand;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}
	@Override
	public String toString() {
		return "Teile [teilenr=" + teilenr + ", kategorie=" + kategorie + ", lagerbestand=" + lagerbestand
				+ ", bezeichnung=" + bezeichnung + "]";
	}

	

	

	
}
