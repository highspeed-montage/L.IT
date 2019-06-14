package models;

import java.util.LinkedList;
import java.util.Deque;

public class Monteur extends Mitarbeiter{
	private boolean verfuegbar;
	private Deque<Rechner> rechner = new LinkedList<Rechner>();
	private boolean vollzeit;
	private boolean anwesend;

	public Monteur(int pPersonalnr, String pName, String pVorname, double pUrlaubstage, int pKrankheitstage, boolean pVollzeit)
	{
		super(pPersonalnr, pName, pVorname, pUrlaubstage, pKrankheitstage, pVollzeit);
	}

	public Monteur(int personalnr, String name) {
		super(personalnr, name);
		// TODO Auto-generated constructor stub
	}
	
	public void setVollzeit(boolean pVollzeit)
	{
		vollzeit = pVollzeit;
	}
	
	public boolean getVollzeit()
	{
		return vollzeit;
	}
	
	public void setAnwesend(boolean pAnwesend)
	{
		anwesend = pAnwesend;
	}
	public boolean getAnwesend()
	{
		return anwesend;
	}
	
	
}




