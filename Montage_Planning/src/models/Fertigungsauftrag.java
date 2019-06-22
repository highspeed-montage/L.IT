package models;

import java.util.Date;

public class Fertigungsauftrag extends Auftrag{
	
	private Geschaeftskunde gk;
	private Privatkunde pk;

	public Fertigungsauftrag(Geschaeftskunde gk, Date lieferdatum) {
		super( lieferdatum);
		this.gk = gk;
	}
	
	public Fertigungsauftrag(Privatkunde pk, Date lieferdatum) {
		super( lieferdatum);
		this.pk = pk;
	}

	
	
}
