package grundgeruest;

public abstract class Monteur extends Mitarbeiter{
	private boolean verfuegbar;
	private Auftrag auftraege;
	
	public Monteur(int pPersonalnr, String pName, String pVorname, double pUrlaubstage, int pKrankheitstage)
	{
		super(pPersonalnr, pName, pVorname, pUrlaubstage, pKrankheitstage);
	}
}
