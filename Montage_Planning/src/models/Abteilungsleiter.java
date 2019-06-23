package models;

import java.io.Serializable;

public class Abteilungsleiter extends Mitarbeiter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Abteilungsleiter(int personalnr, String name) {
		super(personalnr, name);
	}

}
