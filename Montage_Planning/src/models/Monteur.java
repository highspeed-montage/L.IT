package models;

import java.util.LinkedList;
import java.util.Deque;

public class Monteur extends Mitarbeiter{
	private boolean verfuegbar;
	private Deque<Rechner> pipeline = new LinkedList<Rechner>();
	private int arbeitszeit;
	

	public Monteur(int pPersonalnr, String pName, String pVorname, int pKrankheitstage, boolean pAnwesend)
	{
		super(pPersonalnr, pName, pVorname, pKrankheitstage, pAnwesend);
	}

	public Monteur(int personalnr, String name) {
		super(personalnr, name);
		// TODO Auto-generated constructor stub
	}
	public void rechnerHinzufuegen(Rechner pRechner)
	{
		pipeline.add(pRechner);
	}
	
}




