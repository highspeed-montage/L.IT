package models;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class Monteur extends Mitarbeiter {
	private List<Rechner> pipeline = new ArrayList<Rechner>();
	private int wochenstunden;
	private int arbeitsaufwand;
	

	public Monteur(int pPersonalnr, String pName, String pVorname, int pKrankheitstage, boolean pAnwesend, int wochenstunden) {
		super(pPersonalnr, pName, pVorname, pKrankheitstage, pAnwesend);
		this.wochenstunden = wochenstunden;
	}


	public Monteur(int personalnr, String name, String mitarbeitervertragsart) {
		super(personalnr, name, mitarbeitervertragsart);
		// TODO Auto-generated constructor stub
	}


	public Monteur(int personalnr, String name) {
		super(personalnr, name);
		// TODO Auto-generated constructor stub
	}
	

	public Monteur(String name, String vorname, List<Rechner> pipeline) {
		super(name, vorname);
		this.pipeline = pipeline;
	}
	
	public Monteur(String name, String vorname) {
		super(name, vorname);
	}

	public void rechnerHinzufuegen(Rechner pRechner) {
		pipeline.add(pRechner);
	}

//	public Rechner rechnerAuslesen() {
//		return pipeline.;
//	}

	public int getWochenstunden() {
		return wochenstunden;
	}

	public void setWochenstunden(int wochenstunden) {
		this.wochenstunden = wochenstunden;
	}

	public int getArbeitsaufwand() {
		return arbeitsaufwand;
	}

	public void setArbeitsaufwand(int pArbeitsaufwand) {
		arbeitsaufwand = arbeitsaufwand + pArbeitsaufwand;
	}

	public List<Rechner> getPipeline() {
		return pipeline;
	}

	public void setPipeline(List<Rechner> pipeline) {
		this.pipeline = pipeline;
	}


	@Override
	public String toString() {
		return "Monteur [pipeline=" + pipeline + ", wochenstunden=" + wochenstunden + ", arbeitsaufwand="
				+ arbeitsaufwand + ", toString()=" + super.toString() + "]";
	}
	
	


}

