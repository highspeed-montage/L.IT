package models;

import java.io.Serializable;

public class Kunde implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String eMail;

	public Kunde(String pEMail) {
		eMail = pEMail;
	}

	public void setEMail(String pEMail) {
		eMail = pEMail;
	}

	public String getEMail() {
		return eMail;
	}

}