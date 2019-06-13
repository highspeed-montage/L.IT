package models;

import java.util.List;

public class Serviceauftrag extends Auftrag{

	public Serviceauftrag(int pAuftragsnr, String pStatus, Kunde pKunde, boolean pZugewiesen, List<Rechner> pRechner) {
		super(pAuftragsnr, pStatus, pKunde, pZugewiesen, pRechner);
	}

	
}
