package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class FA_Rechner extends Rechner implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final double ARBEITSAUFWAND = 4;

	public FA_Rechner(int pSeriennr, int auftragsNr, String pStatus, LocalDate pBearbeitungsdatum, Date pLieferdatum,
			String pFirmenname, String pPrivatname, int pKundenId, String pEMail, List<Teile> pTeile) {
		super(pSeriennr, auftragsNr, pStatus, pBearbeitungsdatum, pLieferdatum, pFirmenname, pPrivatname, pKundenId,
				pEMail, pTeile);
	}


	public double getArbeitsaufwand() {
		return ARBEITSAUFWAND;
	}

	@Override
	public String toString() {
		return "FA_Rechner [toString()=" + super.toString() + "]";
	}

}
