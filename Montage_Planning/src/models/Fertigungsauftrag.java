package models;

import java.util.Date;

public class Fertigungsauftrag extends Auftrag{
	
	private Geschaeftskunde gk;
	private Privatkunde pk;

	
	//Konstruktor f�r FA_REchner, passend f�r DB-Abfrage GESCHAEFSTKUNDE
	public Fertigungsauftrag(Geschaeftskunde gk, Date lieferdatum) {
		super( lieferdatum);
		this.gk = gk;
	}
	//Konstruktor f�r FA_REchner, passend f�r DB-Abfrage PRIVATKUNDE
	public Fertigungsauftrag(Privatkunde pk, Date lieferdatum) {
		super( lieferdatum);
		this.pk = pk;
	}

	
	
}
