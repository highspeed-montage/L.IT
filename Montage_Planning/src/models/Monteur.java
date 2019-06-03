package models;

//<<<<<<< Updated upstream
import java.util.LinkedList;
import java.util.Deque;

public abstract class Monteur extends Mitarbeiter{
	private boolean verfuegbar;
	private Deque<Rechner> queue = new LinkedList<Rechner>();
//>>>>>>> Stashed changes
	
	public Monteur(int pPersonalnr, String pName, String pVorname, double pUrlaubstage, int pKrankheitstage)
	{
		super(pPersonalnr, pName, pVorname, pUrlaubstage, pKrankheitstage);
	}
}
