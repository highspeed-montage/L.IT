package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import com.mysql.cj.jdbc.result.ResultSetMetaData;
//Ruth: Der obere Import funktioniert so bei mir nicht 
import java.sql.ResultSetMetaData;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import models.FA_Rechner;
import models.Monteur;
import models.SA_Rechner;
import models.Teile;

public class Datenbank {

	// private static final String DB_CONNECTION =
	// "jdbc:mysql://193.196.143.168:3306/aj9s-montage?serverTimezone=UTC";
	// private static final String DB_USER = "aj9s-montage";
	// private static final String DB_PASSWORD = "TPrKrlU9QsMv6Oh7";

	// NICHT LOESCHEN: Datenbankverbindung GABBY LOKAL
	// private static final String DB_CONNECTION =
	// "jdbc:mysql://localhost:3306/aj9s-montage?serverTimezone=UTC"; //fuer jan
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:8889/aj9s-montage?serverTimezone=UTC";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	private Connection connection;

	// Datenbankverbindung herstellen
	private Connection getConnection() {
		Connection dbConnection = null;
		try {
			// Treiber laden
			Class.forName("com.mysql.cj.jdbc.Driver");
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbConnection;
	}

	// Datenbankverbindung oeffnen
	public void openConnection() {
		this.connection = getConnection();
	}

	// Datenbankverbindung trennen
	public void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Fehler beim SchlieÃen der Datenbankverbindung.");
		}
	}

	public void listKunde() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT Name FROM Kunde");
		while (rs.next()) {
			System.out.println(rs.getString("Name"));
		}
	}

	// Abfrage der Usernamen
	public ArrayList<String> Usernameabfrage() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idPersonalnummer FROM Mitarbeiter");
		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
		int columnCount = rsmd.getColumnCount(); //
		ArrayList<String> list = new ArrayList(columnCount);
		while (rs.next()) {
			int i = 1;
			while (i <= columnCount) {
				list.add(rs.getString(i++));
			}
		}
		return list;
	}

	// // Abfrage der Passwoerter
	// public ArrayList Passwortabfrage() throws SQLException {
	//
	// Statement stmt = connection.createStatement();
	// ResultSet rs = stmt.executeQuery("SELECT name FROM Mitarbeiter"); // Abfrage
	// wie Mitarbeiter heissen
	//
	// ResultSetMetaData rsmd = rs.getMetaData(); // Groesse der Tabelle
	// int columnCount = rsmd.getColumnCount(); //
	// ArrayList<String> list = new ArrayList(columnCount); // Erstellt ArrayList
	//
	// while (rs.next()) {
	// int i = 1;
	// while (i <= columnCount) {
	// list.add(rs.getString(i++));
	// }
	// }
	// return list;
	// }

	/**
	 * @deprecated nicht mehr benötigt, da Kunde in Datenbank.getFARechnerInfo
	 *             geladen
	 */
	public String getKunde(int serienNummer) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "SELECT Kunde FROM Auftrag WHERE idAuftragsnummer = (SELECT Auftrag_idAuftragsnummer FROM Rechner_Teile WHERE Bezeichnung = '"
				+ serienNummer + "')";
		ResultSet rs = stmt.executeQuery(query);
		String kunde = null;

		while (rs.next()) {

			kunde = rs.getString("kunde");
		}
		return kunde;
	}

	/**
	 * holt info für FA_Rechner nach Seriennummer
	 * 
	 * @throws SQLException
	 */
	public FA_Rechner getFARechnerInfo(int pSeriennr) throws SQLException {

		FA_Rechner fr = null;

		Statement stmt = connection.createStatement();

		List<Teile> rechnerEinzelteile = new ArrayList<>();
		String queryTeile = "SELECT Teile.Bezeichnung, RechnerTeile.Rechner_idSeriennummer FROM Teile, RechnerTeile "
				+ "WHERE RechnerTeile.Rechner_idSeriennummer = '" + pSeriennr + "' "
				+ "AND RechnerTeile.Teile_idTeilenummer = Teile.idTeilenummer";
		ResultSet rsTeile = stmt.executeQuery(queryTeile);

		while (rsTeile.next()) {

			rechnerEinzelteile.add(new Teile(rsTeile.getString("Teile.Bezeichnung")));
		}

		String queryInfo = "SELECT Auftragsverteilung.Rechner_seriennummer, Status.Bezeichnung, Auftrag.Lieferzeit, "
				+ "Auftragsverteilung.Datum, Kunde.Firmenname, Kunde.idKundennummer, Kunde.Name, Kunde.EMail, "
				+ "Rechner.Auftrag_idAuftragsnummer, Auftrag.Kunde_idKunde "
				+ "FROM Auftragsverteilung, Status, Auftrag, Kunde, Rechner "
				+ "WHERE Auftragsverteilung.Rechner_seriennummer = '" + pSeriennr + "' "
				+ "AND Rechner.idSeriennummer = Auftragsverteilung.Rechner_seriennummer "
				+ "AND Auftragsverteilung.Rechner_seriennummer = Rechner.idSeriennummer "
				+ "AND Rechner.Status_idStatus = Status.idStatus "
				+ "AND Rechner.Auftrag_idAuftragsnummer = Auftrag.idAuftragsnummer "
				+ "AND Auftrag.Kunde_idKunde = Kunde.idKundennummer ";

		ResultSet rsInfo = stmt.executeQuery(queryInfo);

		while (rsInfo.next()) {

			int seriennr = rsInfo.getInt("Auftragsverteilung.Rechner_seriennummer");
			int pAuftragsNr = rsInfo.getInt("Rechner.Auftrag_idAuftragsnummer");
			int pKundenId = rsInfo.getInt("Auftrag.Kunde_idKunde");
			String pStatus = rsInfo.getString("Status.Bezeichnung");
			// Lieferzeit zu Lieferdatum in Datenbank Ã¤ndern
			Date pLieferdatum = rsInfo.getDate("Auftragsverteilung.Datum");
			Date pBearbeitungsdatum = rsInfo.getDate("Auftragsverteilung.Datum");
			String pFirmenname = null;
			String pPrivatname = null;
			String pEMail = rsInfo.getString("Kunde.EMail");
			if (rsInfo.getString("Kunde.Firmenname") != null) {
				pFirmenname = rsInfo.getString("Kunde.Firmenname");

			} else {
				pPrivatname = rsInfo.getString("Kunde.Name");
			}
			// Geschaeftskunde gk;
			// Privatkunde pk;

			System.out.println(rechnerEinzelteile);
			fr = new FA_Rechner(seriennr, pAuftragsNr, pStatus, pBearbeitungsdatum, pLieferdatum, pFirmenname,
					pPrivatname, pKundenId, pEMail, rechnerEinzelteile);

			System.out.println(fr.toString());

		}
		return fr;
	}

	/** holt info für SA_Rechner nach Seriennummer 
	 * @throws SQLException*/
	public SA_Rechner getSARechnerInfo(int pSeriennr) throws SQLException {

		SA_Rechner sr = null;

		Statement stmt = connection.createStatement();


		String queryInfo = "SELECT Auftragsverteilung.Rechner_seriennummer, Status.Bezeichnung, Auftrag.Lieferzeit, "
				+ "Auftragsverteilung.Datum, Kunde.Firmenname, Kunde.idKundennummer, Kunde.Name, Kunde.EMail, "
				+ "Rechner.Auftrag_idAuftragsnummer, Auftrag.Kunde_idKunde, Rechner.kundenverschuldet,"
				+ "Rechner.hardwareverschuldet, Rechner.softwareverschuldet, Rechner.prozessorKaputt, "
				+ "Rechner.grafikkarteKaputt, Rechner.festplatteKaputt, Rechner.laufwerkKaputt   "
				+ "FROM Auftragsverteilung, Status, Auftrag, Kunde, Rechner "
				+ "WHERE Auftragsverteilung.Rechner_seriennummer = '" + pSeriennr + "' "
				+ "AND Rechner.idSeriennummer = Auftragsverteilung.Rechner_seriennummer "
				+ "AND Auftragsverteilung.Rechner_seriennummer = Rechner.idSeriennummer "
				+ "AND Rechner.Status_idStatus = Status.idStatus "
				+ "AND Rechner.Auftrag_idAuftragsnummer = Auftrag.idAuftragsnummer "
				+ "AND Auftrag.Kunde_idKunde = Kunde.idKundennummer ";

		ResultSet rsInfo = stmt.executeQuery(queryInfo);

		while (rsInfo.next()) {

			int seriennr = rsInfo.getInt("Auftragsverteilung.Rechner_seriennummer");
			int pAuftragsNr = rsInfo.getInt("Rechner.Auftrag_idAuftragsnummer");
			int pKundenId = rsInfo.getInt("Auftrag.Kunde_idKunde");
			String pStatus = rsInfo.getString("Status.Bezeichnung");
			// Lieferzeit zu Lieferdatum in Datenbank Ã¤ndern
			Date pLieferdatum = rsInfo.getDate("Auftragsverteilung.Datum");
			Date pBearbeitungsdatum = rsInfo.getDate("Auftragsverteilung.Datum");
			String pFirmenname = null;
			String pPrivatname = null;
			String pEMail = rsInfo.getString("Kunde.EMail");
			if (rsInfo.getString("Kunde.Firmenname") != null) {
				pFirmenname = rsInfo.getString("Kunde.Firmenname");

			} else {
				pPrivatname = rsInfo.getString("Kunde.Name");
			}
			boolean pKundenverschuldet = rsInfo.getBoolean("Rechner.kundenverschuldet");
			boolean pHardwareverschuldet = rsInfo.getBoolean("Rechner.hardwareverschuldet");
			boolean pSoftwareverschuldet = rsInfo.getBoolean("Rechner.softwareverschuldet");
			boolean pProzessor_kaputt = rsInfo.getBoolean("Rechner.prozessorKaputt");
			boolean pGrafikkarte_kaputt = rsInfo.getBoolean("Rechner.grafikkarteKaputt");
			boolean pFestplatte_kaputt = rsInfo.getBoolean("Rechner.festplatteKaputt");
			boolean pDvd_Laufwerk_kaputt = rsInfo.getBoolean("Rechner.laufwerkKaputt");
			
			sr = new SA_Rechner(seriennr, pAuftragsNr, pStatus, pBearbeitungsdatum, pLieferdatum, pFirmenname, pPrivatname, pKundenId, pEMail, 
								pKundenverschuldet, pHardwareverschuldet, pSoftwareverschuldet, pProzessor_kaputt,pGrafikkarte_kaputt, pFestplatte_kaputt, pDvd_Laufwerk_kaputt);

			System.out.println(sr.toString());

		}
		return sr;
	}

	/**
	 * Booleanvariablen der Problemdoku werden aktualisiert
	 * 
	 * @throws SQLException
	 */
	public boolean updateSA_Recher(SA_Rechner sr) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "UPDATE Rechner SET Rechner.kundenverschuldet= '" + sr.isKundenverschuldet() + "', "
				+ "Rechner.hardwareverschuldet ='"+sr.isHardwareverschuldet()+"', Rechner.softwareverschuldet ='"+sr.isSoftwareverschuldet()+"', "
				+ "Rechner.prozessorKaputt = '"+sr.isProzessor_kaputt()+"', Rechner.grafikkarteKaputt = '"+sr.isGrafikkarte_kaputt()+"', "
				+ "Rechner.festplatteKaputt = '"+sr.isFestplatte_kaputt()+"' ,Rechner.laufwerkKaputt = '"+sr.isDvd_Laufwerk_kaputt()+"'"
				+ "	WHERE Auftragsverteilung.Rechner_seriennummer = '" + sr.getSeriennr() + "'"
				+ "AND Rechner.idSeriennummer = Auftragsverteilung.Rechner_seriennummer";

		int updatedRows = stmt.executeUpdate(query);
		return updatedRows == 1;
	}

	/**
	 * Bearbeitungsstatus von FA_R/SA_R wird in db aktualisiert
	 * 
	 * @throws SQLException
	 */
	public boolean setRechnerStatus(int pSerienNr, String pStatus) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "UPDATE Status SET Bezeichnung = '" + pStatus + "' "
				+ "		WHERE Auftragsverteilung.Rechner_seriennummer = '" + pSerienNr + "'"
				+ "AND Rechner.idSeriennummer = Auftragsverteilung.Rechner_seriennummer"
				+ "AND Rechner.Status_idStatus = Status.idStatus";
		int updatedRows = stmt.executeUpdate(query);
		return updatedRows == 1;
	}

	/**
	 * @deprecated nicht mehr verwendet, ET werden in DAtenbank.getFAREchnerInfo
	 *             geladen
	 */
	public List<Teile> getRechnerEinzelteile(int serienNummer) throws SQLException {
		List<Teile> rechnerEinzelteile = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT Teile.Bezeichnung, RechnerTeile.Rechner_idSeriennummer FROM Teile, RechnerTeile "
				+ "WHERE RechnerTeile.Rechner_idSeriennummer = '" + serienNummer + "' "
				+ "AND RechnerTeile.Teile_idTeilenummer = Teile.idTeilenummer";
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {

			rechnerEinzelteile.add(new Teile(rs.getString("Teile.Bezeichnung")));
		}
	}

	/**
	 * Diese Methode listet die Bezeichnung der Teile eines Rechners auf.
	 * 
	 * @param pSeriennummer Die Seriennummer des Rechners, dessen Teile aufgelistet
	 *                      werden sollen.
	 * @throws SQLException
	 * 
	 * @return Die Methode gibt eine Liste von Teilen aus, die für den jeweiligen
	 *         Auftrag benoetigt werden.
	 */
	public List<Teile> listTeileAuftrag(int pSeriennummer) throws SQLException {
		List<Teile> teileAuflistung = new ArrayList<>();
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(
				"SELECT idTeilenummer, Teile.Bezeichnung, Teilekategorie.Bezeichnung, Lagerbestand FROM RechnerTeile, Teile, Rechner, Teilekategorie "
						+ "WHERE Rechner.idSeriennummer=RechnerTeile.Rechner_idSeriennummer AND "
						+ "RechnerTeile.Teile_idTeilenummer=Teile.idTeilenummer "
						+ "AND Teilekategorie.idTeilekategorie=Teile.Teilekategorie_idTeilekategorie "
						+ "AND idSeriennummer=pSeriennummer");
		while (rs.next()) {
			teileAuflistung.add(new Teile(rs.getInt("idTeilenummer"), rs.getString("Teile.Bezeichnung"),
					rs.getString("Teilekategorie.Bezeichung"), rs.getInt("Lagerbestand")));
		}
		return teileAuflistung;
	}

	/**
	 * Diese Methode prueft ob alle Teile des Rechners im Lager sind
	 * (Serviceauftrag).
	 * 
	 * @param pSeriennummer Die Seriennummer des Rechners, dessen Lagerbestand
	 *                      aufgerufen werden soll.
	 * @throws SQLException
	 */
	public int lagerbestandPruefen(int pSeriennummer) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT Lagerbestand FROM RechnerTeile, Teile, Rechner "
				+ "WHERE Rechner.idSeriennummer=RechnerTeile.Rechner_idSeriennummer "
				+ "AND RechnerTeile.Teile_idTeilenummer=Teile.idTeilenummer" + "AND idSeriennummer=pSeriennummer");
		int teilenichtvorhanden = 0;
		while (rs.next()) {
			if (rs.getInt("Lagerbestand") == 0) {
				teilenichtvorhanden++;
			}
		}
		if (teilenichtvorhanden > 0) {
			System.out.println("Es sind nicht alle Teile vorhanden. Auftrag wird an den Einkauf geschickt");
			ResultSet rs2 = stmt
					.executeQuery("UPDATE Rechner SET Status_idStatus = '7' WHERE idSeriennummer = pSeriennummer");
			return 1;// hier ein rs für update verwendet?
		} else {
			return 0;
		}
	}

	/**
	 * Frägt ET Lagerbestand ab. Benötigt für ET Suche bei SA_Rechner
	 */
	public int getEinzelteilLagerbestand(String eingabe) throws SQLException {
		// in der ET Suche in SA_REchner wurde die Eingabe schon überprüft
		Statement stmt = connection.createStatement();
		String query = "SELECT Lagerbestand FROM Rechner_Teile WHERE Bezeichnung = '" + eingabe + "'";
		ResultSet rs = stmt.executeQuery(query);
		int lagerbestand = 0;

		while (rs.next()) {
			lagerbestand = rs.getInt("Lagerbestand");
		}
		return lagerbestand;
	}
	public String authenticateUser(String username, String passwort) {

		//String passwort = String.valueOf(passwortInt);
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet = null;

		String userNameDB = "";
		String passwordDB = "";

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Name,idPersonalnummer FROM Mitarbeiter");

			while (rs.next()) {
				userNameDB = resultSet.getString("idPersonalnummer");
				passwordDB = resultSet.getString("Name");

				if (username.equals(userNameDB) && passwort.equals(passwordDB)) {
					return "SUCCESS";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Invalid user credentials";
	}
	/**
	 * 
	 * Die Methode berechnet die Anzahl aller Monteure.
	 * @return Die Anzahl der Monteure
	 * @throws SQLException
	 */
	public int Monteurezaehlen() throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idPersonalnummer FROM Mitarbeiter");
		ArrayList<Integer> monteure = new ArrayList<Integer>();
		
		while(rs.next())
		{
			monteure.add(rs.getInt("idPersonalnummer"));
		}
		return monteure.size();
	}
	/**
	 * Die Methode berechnet die Anzahl der zu verteilenden Rechner
	 * @return Die Anzahl der Rechner
	 * @throws SQLException
	 */
	public int Rechnerzaehlen() throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idSeriennummer FROM Rechner");
		ArrayList<Integer> rechner = new ArrayList<Integer>();
		
		while(rs.next())
		{
			rechner.add(rs.getInt("idSeriennummer"));
		}
		return rechner.size();
	}
	
	//Ist noch nicht dynamisch!
	Monteur monteur1;
	Monteur monteur2;
	Monteur monteur3;
	Monteur monteur4;
	Monteur monteur5;
	
	public void monteureBefuellen() throws SQLException
	{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idPersonalnummer, Name, Vorname, Krankheitstage");
		
	}
}
