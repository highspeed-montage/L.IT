package models;

public class Teile {
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
