package de.hft.LIT.models;

public class Teile {
	private int teilenr;
	private String kategorie;
	private int lagerbestand;
	
	public Teile(int pTeilenr, String pKategorie, int pLagerbestand)
	{
		teilenr = pTeilenr;
		kategorie = pKategorie; 
		lagerbestand = pLagerbestand;
	}
	public void setKategorie(String pKategorie)
	{
		kategorie = pKategorie;
	}
	//Lagerbestand berechnen?
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
}
