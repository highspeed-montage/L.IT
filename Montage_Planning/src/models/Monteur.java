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
	public void abwesenheit(Monteur e) 
	{
		  if (anwesend == false) 
		  {
		//   monteurHinzufuegen(e);
		//   Methode muss noch implementiert werden, wenn Button vorhanden ist
		//   abwesenheit.add(e);
		   System.out.println(e.getName()+ " ist abwesend und wurde in die Liste der Abwesenden hinzugef�gt.");
		  } 
		  else 
		  {
		   System.out.println(e.getName()+ " ist anwesend.");
		  }
	}
	
}




