package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import controllers.AlertController;

//import com.mysql.cj.jdbc.result.ResultSetMetaData;
//Ruth: Der obere Import funktioniert so bei mir nicht 
import java.sql.ResultSetMetaData;

import models.Auftrag;
import models.FA_Rechner;
import models.Mitarbeiter;
import models.Monteur;
import models.Rechner;
import models.SA_Rechner;
import models.Teile;

public class Datenbank {

	private static final String DB_CONNECTION = "jdbc:mysql://193.196.143.168:3306/aj9s-montage?serverTimezone=UTC";
	private static final String DB_USER = "aj9s-montage";
	private static final String DB_PASSWORD = "TPrKrlU9QsMv6Oh7";

	// NICHT LOESCHEN: Datenbankverbindung GABBY LOKAL
	// private static final String DB_CONNECTION =
	// "jdbc:mysql://localhost:3306/aj9s-montage?serverTimezone=UTC"; //fuer jan
//	private static final String DB_CONNECTION = "jdbc:mysql://localhost:8889/aj9s-montage_neu?serverTimezone=UTC";
//	private static final String DB_USER = "root";
//	private static final String DB_PASSWORD = "root";

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
		ArrayList<String> list = new ArrayList<>(columnCount);
		while (rs.next()) {
			int i = 1;
			while (i <= columnCount) {
				list.add(rs.getString(i++));
			}
		}
		return list;
	}

	public Auftrag getAuftragsinfo(int pAuftragsnr) throws SQLException {

		Auftrag a1 = null;
		Statement stmt = connection.createStatement();
		List<Rechner> rechner = new ArrayList<>();
		String queryRechner = "SELECT Rechner.idSeriennummer, Status.Bezeichnung " + "FROM Rechner, Status "
				+ "WHERE Rechner.Auftrag_idAuftragsnummer = '" + pAuftragsnr + "' "
				+ "AND Rechner.Status_idStatus = Status.idStatus";
		ResultSet rsRechner = stmt.executeQuery(queryRechner); // FUNKTIONIERT
		System.out.println(queryRechner);
		while (rsRechner.next()) {
			String rechnerStatus = rsRechner.getString("Status.Bezeichnung");
			int rechnerSNr = rsRechner.getInt("Rechner.idSeriennummer");
			rechner.add(new Rechner(rechnerSNr, rechnerStatus));
		}
		String queryInfo = "SELECT Auftrag.idAuftragsnummer, Status.Bezeichnung, Auftrag.Lieferdatum, Auftrag.Bestelldatum, "
				+ "Auftrag.Kunde_idKunde, Kunde.EMail, Kunde.Name, Kunde.Firmenname, Kundengruppe.Bezeichnung "
				+ "FROM Status, Auftrag, Kundengruppe, Kunde " + "WHERE Auftrag.idAuftragsnummer = '" + pAuftragsnr
				+ "' AND Auftrag.Status_idStatus = Status.idStatus "
				+ "AND Auftrag.Kunde_idKunde = Kunde.idKundennummer AND Kunde.Kundengruppe_idKundengruppe = Kundengruppe.idKundengruppe ";
		ResultSet rsInfo = stmt.executeQuery(queryInfo); // FUNKTIONIERT
		System.out.println(queryInfo);
		while (rsInfo.next()) {
			int auftragsnr = rsInfo.getInt("Auftrag.idAuftragsnummer");
			String pStatus = rsInfo.getString("Status.Bezeichnung");
			Date pLieferdatum = rsInfo.getDate("Auftrag.Lieferdatum");
			Date pBestelldatum = rsInfo.getDate("Auftrag.Bestelldatum");
			String pKundentyp = rsInfo.getString("Kundengruppe.Bezeichnung");
			int pKundenNr = rsInfo.getInt("Auftrag.Kunde_idKunde");
			String pKundenEmail = rsInfo.getString("Kunde.EMail");
			String pFirmenname = null;
			String pPrivatname = null;

			if (rsInfo.getString("Kunde.Firmenname") != null) {
				pFirmenname = rsInfo.getString("Kunde.Firmenname");

			} else {
				pPrivatname = rsInfo.getString("Kunde.Name");
			}

			a1 = new Auftrag(auftragsnr, pStatus, pLieferdatum, pBestelldatum, pKundentyp, pKundenNr, pKundenEmail,
					pFirmenname, pPrivatname, rechner);
			System.out.println(a1.toString());
		}

		return a1;
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

		String queryInfo = "SELECT Auftragsverteilung.Rechner_seriennummer, Status.Bezeichnung, Auftrag.Lieferdatum, "
				+ "Auftragsverteilung.Bearbeitungsdatum, Kunde.Firmenname, Kunde.idKundennummer, Kunde.Name, Kunde.EMail, "
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
			Date pLieferdatum = rsInfo.getDate("Auftrag.Lieferdatum");
			LocalDate pBearbeitungsdatum = rsInfo.getDate("Auftragsverteilung.Bearbeitungsdatum").toLocalDate();
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

	/**
	 * holt info für SA_Rechner nach Seriennummer
	 * 
	 * @throws SQLException
	 */
	public SA_Rechner getSARechnerInfo(int pSeriennr) throws SQLException {

		SA_Rechner sr = null;

		Statement stmt = connection.createStatement();

		String queryInfo = "SELECT Auftragsverteilung.Rechner_seriennummer, Status.Bezeichnung, Auftrag.Lieferdatum, "
				+ "Auftragsverteilung.Bearbeitungsdatum, Kunde.Firmenname, Kunde.idKundennummer, Kunde.Name, Kunde.EMail, "
				+ "Rechner.Auftrag_idAuftragsnummer, Auftrag.Kunde_idKunde, Rechner.kundenverschuldet,"
				+ "Rechner.hardwareverschuldet, Rechner.softwareverschuldet, Rechner.prozessorKaputt, "
				+ "Rechner.grafikkarteKaputt, Rechner.festplatteKaputt, Rechner.laufwerkKaputt  "
				+ "FROM Auftragsverteilung, Status, Auftrag, Kunde, Rechner "
				+ "WHERE Auftragsverteilung.Rechner_seriennummer = '" + pSeriennr + "' "
				+ "AND Rechner.idSeriennummer = Auftragsverteilung.Rechner_seriennummer "
				+ "AND Auftragsverteilung.Rechner_seriennummer = Rechner.idSeriennummer "
				+ "AND Rechner.Status_idStatus = Status.idStatus "
				+ "AND Rechner.Auftrag_idAuftragsnummer = Auftrag.idAuftragsnummer "
				+ "AND Auftrag.Kunde_idKunde = Kunde.idKundennummer ";

		System.out.println(queryInfo);

		ResultSet rsInfo = stmt.executeQuery(queryInfo);

		while (rsInfo.next()) {

			int seriennr = rsInfo.getInt("Auftragsverteilung.Rechner_seriennummer");
			int pAuftragsNr = rsInfo.getInt("Rechner.Auftrag_idAuftragsnummer");
			int pKundenId = rsInfo.getInt("Auftrag.Kunde_idKunde");
			String pStatus = rsInfo.getString("Status.Bezeichnung");
			Date pLieferdatum = rsInfo.getDate("Auftrag.Lieferdatum");
			LocalDate pBearbeitungsdatum = rsInfo.getDate("Auftragsverteilung.Bearbeitungsdatum").toLocalDate();
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

			sr = new SA_Rechner(seriennr, pAuftragsNr, pStatus, pBearbeitungsdatum, pLieferdatum, pFirmenname,
					pPrivatname, pKundenId, pEMail, pKundenverschuldet, pHardwareverschuldet, pSoftwareverschuldet,
					pProzessor_kaputt, pGrafikkarte_kaputt, pFestplatte_kaputt, pDvd_Laufwerk_kaputt);

			System.out.println(sr.toString());

		}
		return sr;
	}

	/**
	 * 
	 * @param pAuftragsnummer
	 * @return Status IDs aller REchner eines Auftrags
	 * @throws SQLException
	 */
	public int getLowestRechnerIDAuftrag(int pAuftragsnummer) throws SQLException {
		List<Integer> statusIDs = new ArrayList<Integer>();
		Statement stmt = connection.createStatement();
		String query = "SELECT Rechner.Status_idStatus FROM Rechner WHERE Rechner.Auftrag_idAuftragsnummer = '"
				+ pAuftragsnummer + "'";
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			statusIDs.add(rs.getInt("Rechner.Status_idStatus"));
		}
		Collections.sort(statusIDs);
		int lowestId = statusIDs.get(0);
		System.out.println(lowestId);
		return lowestId;

	}

	/**
	 * Update des Auftragsstatus auf niedrigste STatusid aller zugeh�rigen Rechner
	 * 
	 * @param pAuftragsnummer
	 * @param lowestId
	 * @throws SQLException
	 */
	public boolean setAuftragStatus(int pAuftragsnummer, int lowestId) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "UPDATE Auftrag SET Auftrag.Status_idStatus = '" + lowestId + "' "
				+ "WHERE Auftrag.idAuftragsnummer = '" + pAuftragsnummer + "'";
		int updatedRows = stmt.executeUpdate(query);
		return updatedRows == 1;
	}

	/**
	 * Booleanvariablen der Problemdoku werden aktualisiert
	 * 
	 * @throws SQLException
	 */
	public boolean updateSA_Recher(SA_Rechner sr) throws SQLException {
		int tinyHardware = 0;
		int tinySoftware = 0;
		int tinyKunde = 0;
		int tinyProzessor = 0;
		int tinyGrafikkarte = 0;
		int tinyFestplatte = 0;
		int tinyLaufwerk = 0;
		if (sr.isHardwareverschuldet() == true) {
			tinyHardware = 1;
			if (sr.isDvd_Laufwerk_kaputt())
				tinyLaufwerk = 1;
			if (sr.isFestplatte_kaputt())
				tinyFestplatte = 1;
			if (sr.isGrafikkarte_kaputt())
				tinyGrafikkarte = 1;
			if (sr.isProzessor_kaputt())
				tinyProzessor = 1;
		} else if (sr.isSoftwareverschuldet() == true) {
			tinySoftware = 1;
		} else if (sr.isKundenverschuldet() == true) {
			tinyKunde = 1;
		}
		Statement stmt = connection.createStatement();
		String query = "UPDATE Rechner SET Rechner.kundenverschuldet= '" + tinyKunde + "', "
				+ "Rechner.hardwareverschuldet ='" + tinyHardware + "', Rechner.softwareverschuldet ='"
				+ tinySoftware + "', Rechner.prozessorKaputt = '" + tinyProzessor + "', "
				+ "Rechner.grafikkarteKaputt = '" + tinyGrafikkarte + "', " + "Rechner.festplatteKaputt = '"
				+ tinyFestplatte + "', Rechner.laufwerkKaputt = '" + tinyLaufwerk + "' "
				+ "WHERE Rechner.idSeriennummer = '" + sr.getSeriennr() + "'";

//				+ "WHERE Auftragsverteilung.Rechner_seriennummer = '" + sr.getSeriennr() + "' "
//				+ "AND Rechner.idSeriennummer = Auftragsverteilung.Rechner_seriennummer";

		System.out.println(query);

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
		int statusID = 0;

		String queryStatusId = "SELECT idStatus FROM Status WHERE Bezeichnung = '" + pStatus + "' ";
		ResultSet rsInfo = stmt.executeQuery(queryStatusId);

		while (rsInfo.next()) {
			statusID = rsInfo.getInt("idStatus");
		}
		String query = "UPDATE Rechner SET Status_idStatus = '" + statusID + "' " + "WHERE idSeriennummer = '"
				+ pSerienNr + "'";
		int updatedRows = stmt.executeUpdate(query);
		return updatedRows == 1;
	}

//
//	/**
//	 * Diese Methode listet die Bezeichnung der Teile eines Rechners auf.
//	 * 
//	 * @param pSeriennummer Die Seriennummer des Rechners, dessen Teile aufgelistet
//	 *                      werden sollen.
//	 * @throws SQLException
//	 * 
//	 * @return Die Methode gibt eine Liste von Teilen aus, die für den jeweiligen
//	 *         Auftrag benoetigt werden.
//	 */
//	public List<Teile> listTeileAuftrag(int pSeriennummer) throws SQLException {
//		List<Teile> teileAuflistung = new ArrayList<>();
//		Statement stmt = connection.createStatement();
//		ResultSet rs = stmt.executeQuery(
//				"SELECT idTeilenummer, Teile.Bezeichnung, Teilekategorie.Bezeichnung, Lagerbestand FROM RechnerTeile, Teile, Rechner, Teilekategorie "
//						+ "WHERE Rechner.idSeriennummer=RechnerTeile.Rechner_idSeriennummer AND "
//						+ "RechnerTeile.Teile_idTeilenummer=Teile.idTeilenummer "
//						+ "AND Teilekategorie.idTeilekategorie=Teile.Teilekategorie_idTeilekategorie "
//						+ "AND idSeriennummer=pSeriennummer");
//		while (rs.next()) {
////			teileAuflistung.add(new Teile(rs.getInt("idTeilenummer"), rs.getString("Teile.Bezeichnung"),
////					rs.getString("Teilekategorie.Bezeichung"), rs.getInt("Lagerbestand")));
//		}
//		return teileAuflistung;
//	}
	/**
	 * Die Methode gibt den Lagerbestand eines Einzelteils aus und veraendert den
	 * Status des Rechners, wenn
	 * 
	 * @param eingabe
	 * @param pSeriennummer
	 * @return Lagerbestand des Einzelteils
	 * @throws SQLException
	 */
	public int getEinzelteilLagerbestand(String eingabe, int pSeriennummer) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "SELECT Lagerbestand FROM Teile WHERE Bezeichnung LIKE '%" + eingabe + "%'";
		ResultSet rs = stmt.executeQuery(query);
		int lagerbestand = 0;
System.out.println(query);
		while (rs.next()) {
			if (!rs.wasNull()) {
				lagerbestand = rs.getInt("Lagerbestand");
				System.out.println(lagerbestand);
			} else {
				lagerbestand = 9999;
			}
		}
		return lagerbestand;
	}

	public String authenticateUser(String username, String passwort) throws SQLException {

		// String passwort = String.valueOf(passwortInt);

		int userNameDB = 0; // username = idPersonalnummer
		String passwordDB = null; // passwort = Name

		int usernameEingabe = Integer.parseInt(username);

		System.out.println(username);
		System.out.println(passwort);

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT Name, idPersonalnummer FROM Mitarbeiter");

		while (rs.next()) {
			userNameDB = rs.getInt("idPersonalnummer");
			passwordDB = rs.getString("Name");
		}

		if (userNameDB == usernameEingabe && passwordDB.equals(passwort)) {
			return "SUCCESS";
		} else {
			return "Invalid user credentials";
		}
	}

	// username = idPersonalnummer, passwort = name
	public Mitarbeiter authenticateUserNEU(String username, String password) throws SQLException {
		Statement stmt = connection.createStatement();
		int usernameZahl = Integer.parseInt(username);
		Mitarbeiter userVergleich = null;
		String query = "SELECT Name, idPersonalnummer FROM Mitarbeiter WHERE Name = '" + password
				+ "' AND idPersonalnummer = '" + usernameZahl + "'";
		ResultSet rs = stmt.executeQuery(query);

		// nach elegenateren Loesung gucken, wenn zeit
		int i = 0;
		while (rs.next()) {
			i++;
			userVergleich = new Mitarbeiter(rs.getInt("idPersonalnummer"), rs.getString("Name"));
		}
		if (i == 0) {
			userVergleich = null;
		}
		return userVergleich;
	}

	/**
	 * Die Methode befuellt eine ArrayList mit allen Monteuren
	 * 
	 * @throws SQLException
	 */
	public ArrayList<Monteur> monteureBefuellen() throws SQLException {
		ArrayList<Monteur> monteure = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT idPersonalnummer, Name, Vorname, Krankheitstage, anwesend, Wochenstunden "
				+ "FROM Mitarbeiter, MitarbeiterVertragsart "
				+ "WHERE Mitarbeiter.MitarbeiterVertragsart_idMitarbeiterVertragsart=301 "
				+ "AND Mitarbeiter.MitarbeiterVertragsart_idMitarbeiterVertragsart=MitarbeiterVertragsart.idMitarbeiterVertragsart "
				+ "OR Mitarbeiter.MitarbeiterVertragsart_idMitarbeiterVertragsart=302 "
				+ "AND Mitarbeiter.MitarbeiterVertragsart_idMitarbeiterVertragsart=MitarbeiterVertragsart.idMitarbeiterVertragsart";
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			monteure.add(new Monteur(rs.getInt("idPersonalnummer"), rs.getString("Name"), rs.getString("Vorname"),
					rs.getInt("Krankheitstage"), rs.getBoolean("anwesend"), rs.getInt("Wochenstunden")));
		}
		System.out.println(monteure.toString());
		return monteure;
	}

	/**
	 * Die Methode befuellt eine ArrayList mit allen Auftraegen aus der Datenbank
	 * 
	 * @throws SQLException
	 */
	public ArrayList<Rechner> rechnerBefuellen() throws SQLException {
		ArrayList<Rechner> rechner = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT idSeriennummer, Auftrag.idAuftragsnummer, Status.Bezeichnung, Auftragsart.Arbeitsaufwand "
				+ "FROM Rechner, Status, Auftrag, Auftragsart "
				+ "WHERE Status.idStatus=Rechner.Status_idStatus AND Rechner.Auftrag_idAuftragsnummer=Auftrag.idAuftragsnummer "
				+ "AND Auftragsart.idAuftragsart=Rechner.Auftragsart_idAuftragsart "
				+ "AND Rechner.Status_idStatus='1'";
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			rechner.add(new Rechner(rs.getInt("Rechner.idSeriennummer"), rs.getInt("Auftrag.idAuftragsnummer"),
					rs.getString("Status.Bezeichnung"), rs.getInt("Auftragsart.Arbeitsaufwand")));
		}
		System.out.println(rechner.toString());
		return rechner;
	}

	/**
	 * Die Methode uebertraegt die Rechnerverteilung in die Datenbank
	 * 
	 * @param idAuftragsverteilung
	 * @param bearbeitungsdatum
	 * @param seriennummer
	 * @param personalnummer
	 * @throws SQLException
	 */
	public void rechnerVerteilung(int idAuftragsverteilung, LocalDate bearbeitungsdatum, int seriennummer,
			int personalnummer) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("INSERT INTO Auftragsverteilung" + "VALUES(" + idAuftragsverteilung + ", '"
				+ bearbeitungsdatum + "', '" + seriennummer + "', '" + personalnummer + "'");
		int updatedRows = stmt.executeUpdate(
				"UPDATE Rechner SET Status_idStatus = '3' WHERE idSeriennummer = '" + seriennummer + "'");
	}

	public int getMitarbeiterRolle(Mitarbeiter user) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT MitarbeiterVertragsart_idMitarbeiterVertragsart " + "FROM Mitarbeiter "
				+ "WHERE Mitarbeiter.idPersonalnummer='" + user.getPerosnalnr() + "'");
		int rolle = 0;
		while (rs.next()) {
			rolle = rs.getInt("MitarbeiterVertragsart_idMitarbeiterVertragsart");
		}
		return rolle;
	}
}
