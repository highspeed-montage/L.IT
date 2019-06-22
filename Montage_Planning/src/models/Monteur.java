package models;

import java.util.LinkedList;
import java.util.Deque;

public class Monteur extends Mitarbeiter {
	private boolean verfuegbar;
	private Deque<Rechner> pipeline = new LinkedList<Rechner>();
	private int wochenstunden;
	private int arbeitsaufwand;

	public Monteur(int pPersonalnr, String pName, String pVorname, int pKrankheitstage, boolean pAnwesend,
			int pWochenstunden) {
		super(pPersonalnr, pName, pVorname, pKrankheitstage, pAnwesend);
		wochenstunden = pWochenstunden;
	}

	public Monteur(int personalnr, String name) {
		super(personalnr, name);
	}
	

	public Monteur(String name, String vorname, Deque<Rechner> pipeline) {
		super(name, vorname);
		this.pipeline = pipeline;
	}
	
	public Monteur(String name, String vorname) {
		super(name, vorname);
	}

	public void rechnerHinzufuegen(Rechner pRechner) {
		pipeline.add(pRechner);
	}

	public Rechner rechnerAuslesen() {
		return pipeline.getFirst();
	}

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

	public Deque<Rechner> getPipeline() {
		return pipeline;
	}

	public void setPipeline(Deque<Rechner> pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public String toString() {
		return super.toString() + pipeline;		
	}


	


}

