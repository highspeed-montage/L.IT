package models;

import java.util.Date;

public class Serviceauftrag extends Auftrag{

	public Serviceauftrag(Integer auftragsnr, Date lieferdatum, Integer anzahlRechner, String status) {
		super(auftragsnr, lieferdatum, anzahlRechner, status);
	}
	
}
