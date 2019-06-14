package models;

import java.util.LinkedList;
import java.util.Deque;

public class Monteur extends Mitarbeiter{
	private boolean verfuegbar;
	private Deque<Rechner> rechner = new LinkedList<Rechner>();
	private boolean anwesend;

	public Monteur(int pPersonalnr, String pName, String pVorname, int pKrankheitstage)
	{
		super(pPersonalnr, pName, pVorname, pKrankheitstage);
	}

	public Monteur(int personalnr, String name) {
		super(personalnr, name);
		// TODO Auto-generated constructor stub
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
		   System.out.println(e.getName()+ " ist abwesend und wurde in die Liste der Abwesenden hinzugefügt.");
		  } 
		  else 
		  {
		   System.out.println(e.getName()+ " ist anwesend.");
		  }
	}
	
}




