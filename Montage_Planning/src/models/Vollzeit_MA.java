package models;
// Kann geloescht werden, da die Art der Beschaeftigung bei der Auftragsverteilung nicht beruecksichtigt wird. 
public class Vollzeit_MA extends Monteur {
	private static final int ARBEITSZEIT = 8;
	
	public Vollzeit_MA(int pPersonalnr, String pName, String pVorname, double pUrlaubstage, int pKrankheitstage)
	{
		super(pPersonalnr, pName, pVorname, pUrlaubstage, pKrankheitstage);
	}
	public int getArbeitszeit()
	{
		return ARBEITSZEIT;
	}
}
