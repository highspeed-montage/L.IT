package models;

public class FA_Rechner extends Rechner {

	private static final double arbeitsaufwand = 4;

	public FA_Rechner(int pSeriennr, Auftrag pAuftrag, String pStatus, Teile pTeile) {
		super(pSeriennr, pAuftrag, pStatus, pTeile);
	}

	public double getArbeitsaufwand() {
		return arbeitsaufwand;
	}
}
