package models;

public class Mitarbeiter {
	private int personalnr;
	private String name;
	private String vorname;
	private double urlaubstage;
	private int krankheitstage;
	// Vollzeit, Teilzeit, Abteilungsleiter
	private String mitarbeitervertragsart;
	private boolean anwesend;

	public Mitarbeiter(int pPersonalnr, String pName, String pVorname, int pKrankheitstage, boolean pAnwesend) {
		personalnr = pPersonalnr;
		name = pName;
		vorname = pVorname;
		krankheitstage = pKrankheitstage;
		anwesend = pAnwesend;
	}

	public Mitarbeiter(int personalnr, String name) {
		super();
		this.personalnr = personalnr;
		this.name = name;
	}

	public Mitarbeiter(int personalnr, String name, String mitarbeitervertragsart) {
		super();
		this.personalnr = personalnr;
		this.name = name;
		this.mitarbeitervertragsart = mitarbeitervertragsart;
	}

	public int getPersonalnr() {
		return personalnr;
	}

	public void setPersonalnr(int personalnr) {
		this.personalnr = personalnr;
	}

	public void setName(String pName) {
		name = pName;
	}

	public void setVorname(String pVorname) {
		vorname = pVorname;
	}

	public void setUrlaubstage(double pUrlaubstage) {
		urlaubstage = pUrlaubstage;
	}

	public void setKrankheitstage(int pKrankheitstage) {
		krankheitstage = pKrankheitstage;
	}

	public int getPerosnalnr() {
		return personalnr;
	}

	public String getName() {
		return name;
	}

	public String getVorname() {
		return vorname;
	}

	public double getUrlaubstage() {
		return urlaubstage;
	}

	public int getKrankheitstage() {
		return krankheitstage;
	}

	public void setAnwesend(boolean pAnwesend) {
		anwesend = pAnwesend;
	}

	public boolean getAnwesend() {
		return anwesend;
	}

	public String getMitarbeitervertragsart() {
		return mitarbeitervertragsart;
	}

	public void setMitarbeitervertragsart(String mitarbeitervertragsart) {
		this.mitarbeitervertragsart = mitarbeitervertragsart;
	}

	@Override
	public String toString() {
		return "Mitarbeiter [personalnr=" + personalnr + ", name=" + name + ", vorname=" + vorname + ", urlaubstage="
				+ urlaubstage + ", krankheitstage=" + krankheitstage + ", mitarbeitervertragsart="
				+ mitarbeitervertragsart + ", anwesend=" + anwesend + "]";
	}

}
