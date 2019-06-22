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
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import controllers.AlertController;

//import com.mysql.cj.jdbc.result.ResultSetMetaData;
//Ruth: Der obere Import funktioniert so bei mir nicht 
import java.sql.ResultSetMetaData;

import models.Auftrag;
import models.Auftragsverteilung;
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

	private Connection connection;

	/**
	 * Verbindung zur Datenbank
	 * 
	 * @return
	 */
	private Connection getConnection() {
		Connection dbConnection = null;
		try {
			// Treiber werden geladen
			Class.forName("com.mysql.cj.jdbc.Driver");
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbConnection;
	}

	/**
	 * Oeffnen der Datenbankverbindung
	 */
	public void openConnection() {
		this.connection = getConnection();
	}
	
	
	

	/**
	 * Trennen der Datenbankverbindung
	 */
	public void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Fehler beim SchlieÃƒÂŸen der Datenbankverbindung.");
		}
	}
	
	/**
	 * Holt alle Rechner aus der Auftragsverteilung
	 * 
	 * @return List<Auftragsverteilung> tabelleninhalt
	 * @throws SQLException
	 */
	public List<Auftragsverteilung> getRechnerAusAuftragsverteilungListe(/* Mitarbeiter user */) throws SQLException {

		List<Auftragsverteilung> tabelleninhalt = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT Auftragsverteilung.Bearbeitungsdatum, Auftragsverteilung.Rechner_seriennummer, Status.Bezeichnung "
				+ "FROM Auftragsverteilung, Status, Rechner "
				+ "WHERE Auftragsverteilung.Rechner_seriennummer = Rechner.idSeriennummer "
				+ "AND Rechner.Status_idStatus = Status.idStatus";
		// + "AND Auftragsverteilung.Mitarbeiter_idPersonalnummer =
		// '"+user.benutzername+"'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			tabelleninhalt.add(new Auftragsverteilung(rs.getDate("Auftragsverteilung.Bearbeitungsdatum").toLocalDate(),
					rs.getDate("Auftragsverteilung.Bearbeitungsdatum"),
					rs.getInt("Auftragsverteilung.Rechner_seriennummer"), rs.getString("Status.Bezeichnung")));
		}
		return tabelleninhalt;
	}

	/**
	 * Holt alle Rechner aus der Auftragsverteilung fuer die Wochenansicht
	 * 
	 * @param startdatum
	 * @param enddatum
	 * @return
	 * @throws SQLException
	 */
	public List<Auftragsverteilung> getRechnerAusAuftragsverteilungWoche(String startdatum,
			String enddatum ) throws SQLException {

		List<Auftragsverteilung> tabelleninhalt = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT * FROM Auftragsverteilung WHERE Bearbeitungsdatum BETWEEN '" + startdatum + "' AND '"
				+ enddatum + "'";
		// + "WHERE Mitarbeiter_idPersonalnummer = '"+user.benutzername+"'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			tabelleninhalt.add(new Auftragsverteilung(rs.getInt("Rechner_seriennummer"),
					rs.getDate("Bearbeitungsdatum").toLocalDate()));
		}
		return tabelleninhalt;
	}

	/**
	 * Holt die Auftragsart des Rechners
	 * 
	 * @param seriennr
	 * @return idAuftragsart
	 * @throws SQLException
	 */
	public int getRechnerAuftragsart(int seriennr) throws SQLException {
		int idAuftragsart = 0;
		Statement stmt = connection.createStatement();
		String query1 = "SELECT Auftragsart_idAuftragsart FROM Rechner " + "WHERE idSeriennummer = '" + seriennr + "' ";
		ResultSet rs1 = stmt.executeQuery(query1);
		while (rs1.next()) {
			idAuftragsart = rs1.getInt("Auftragsart_idAuftragsart");
		}
		return idAuftragsart;
	}

	/**
	 * Holt alle Bearbeitungsdaten aus der Auftragsverteilung
	 * 
	 * @return bearbeitunsdatum
	 * @throws SQLException
	 */
	public List<Date> getRechnerBearbeitungsdatum() throws SQLException {
		List<Date> bearbeitungsdatum = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT DISTINCT Bearbeitungsdatum FROM Auftragsverteilung";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			bearbeitungsdatum.add(rs.getDate("Bearbeitungsdatum"));
		}
		return bearbeitungsdatum;
	}
/**
 * Erstellt Liste fuer alle Auftraege
 * 
 * @return List<Auftrag> auftraege
 * @throws SQLException
 */
	public List<Auftrag> getAuftragFuerListe() throws SQLException {
		List<Auftrag> auftraege = new ArrayList<>();
		Statement stmt = connection.createStatement();
		Statement stmtAR = connection.createStatement();
		int idAuftragsnummer = 0;
		int anzahlRechner = 0;

		String query = "SELECT Auftrag.idAuftragsnummer, Auftrag.Lieferdatum, Status.Bezeichnung "
				+ "FROM Auftrag, Status WHERE Auftrag.fk_Status_idStatus = Status.idStatus";
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			idAuftragsnummer = rs.getInt("Auftrag.idAuftragsnummer");
			String queryAnzahlRechner = "SELECT COUNT(Auftrag_idAuftragsnummer) AS count FROM Rechner WHERE Auftrag_idAuftragsnummer = '"
					+ idAuftragsnummer + "'";
			ResultSet rsAR = stmtAR.executeQuery(queryAnzahlRechner);
			while (rsAR.next()) {
				anzahlRechner = rsAR.getInt("count");
			}
			auftraege.add(new Auftrag(idAuftragsnummer, rs.getDate("Auftrag.Lieferdatum"), anzahlRechner,
					rs.getString("Status.Bezeichnung")));
		}

		return auftraege;
	}
	/**
	 * Holt alle Rechenr eines Auftrags
	 * 
	 * @param auftragsnummer
	 * @return List<Rechner> rechner
	 * @throws SQLException
	 */

	public List<Rechner> getRechnerZuAuftrag(int auftragsnummer) throws SQLException {
		List<Rechner> rechner = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT idSeriennummer FROM Rechner " + "WHERE Rechner.Auftrag_idAuftragsnummer = '"
				+ auftragsnummer + "'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			rechner.add(new Rechner(rs.getInt("idSeriennummer")));
		}
		return rechner;
	}
/**
 * Holt Daten für die Wochenansicht
 *
 * @param startdatum
 * @param enddatum
 * @return List<Monteur> auftraegeMonteur
 * @throws SQLException
 */
	public List<Monteur> getRechnerMitarbeiterWoche(String startdatum, String enddatum) throws SQLException {
		List<Monteur> auftraegeMonteur = new ArrayList<>();
		Statement stmtM = connection.createStatement();
		int idMonteur = 0;
		String queryMonteure = "SELECT Mitarbeiter_idPersonalnummer FROM Auftragsverteilung";
		ResultSet rsM = stmtM.executeQuery(queryMonteure);

		while (rsM.next()) {
			idMonteur = rsM.getInt("Mitarbeiter_idPersonalnummer");
			String query = "SELECT Auftragsverteilung.Bearbeitungsdatum, Auftragsverteilung.Rechner_seriennummer, Mitarbeiter.Name, "
					+ "Mitarbeiter.Vorname FROM Auftragsverteilung, Mitarbeiter "
					+ "WHERE Auftragsverteilung.Mitarbeiter_idPersonalnummer = '" + idMonteur + "' "
					+ "AND Auftragsverteilung.Mitarbeiter_idPersonalnummer = Mitarbeiter.idPersonalnummer "
					+ "AND Bearbeitungsdatum BETWEEN '" + startdatum + "' AND '" + enddatum + "'";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Deque<Rechner> pipeline = new LinkedList<>();
				pipeline.add(new Rechner(rs.getInt("Auftragsverteilung.Rechner_seriennummer"),
						rs.getDate("Auftragsverteilung.Bearbeitungsdatum").toLocalDate()));
				Monteur monteur = new Monteur(rs.getString("Mitarbeiter.Name"), rs.getString("Mitarbeiter.Vorname"),
						pipeline);
				auftraegeMonteur.add(monteur);
			}
		}
		return auftraegeMonteur;
	}
	
	/**
	 * erstellt Liste mit Rechneraufträge
	 * @param startdatum
	 * @param enddatum
	 * @return List<Auftragsverteilung> auftraege
	 * @throws SQLException
	 */

	public List<Auftragsverteilung> getRechnerAuftragswoche(String startdatum, String enddatum) throws SQLException {
		List<Auftragsverteilung> auftraege = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT Auftragsverteilung.Bearbeitungsdatum, Auftragsverteilung.Rechner_seriennummer, Mitarbeiter.Name, "
				+ "Mitarbeiter.Vorname FROM Auftragsverteilung, Mitarbeiter "
				+ "WHERE Auftragsverteilung.Mitarbeiter_idPersonalnummer = Mitarbeiter.idPersonalnummer "
				+ "AND Bearbeitungsdatum BETWEEN '" + startdatum + "' AND '" + enddatum + "'";

		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			Monteur monteur = new Monteur(rs.getString("Mitarbeiter.Name"), rs.getString("Mitarbeiter.Vorname"));
			auftraege.add(new Auftragsverteilung(rs.getDate("Auftragsverteilung.Bearbeitungsdatum").toLocalDate(),
					rs.getInt("Auftragsverteilung.Rechner_seriennummer"), monteur));
		}
		return auftraege;
	}
	
	/**
	 *Holt Namen von Kunden 
	 *
	 * @throws SQLException
	 */

	public void listKunde() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT Name FROM Kunde");
		while (rs.next()) {
			System.out.println(rs.getString("Name"));
		}
	}

	/**
	 * Abfrage der usernamen
	 * 
	 * @return ArrayList<String> list
	 * @throws SQLException
	 */
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
/** speichert alle fuer einen Auftrag noetigen Infos ab
 * 
 * @param pAuftragsnr
 * @return Auftrag a1
 * @throws SQLException
 */
	public Auftrag getAuftragsinfo(int pAuftragsnr) throws SQLException {

		Auftrag a1 = null;
		Statement stmt = connection.createStatement();
		List<Rechner> rechner = new ArrayList<>();
		String queryRechner = "SELECT Rechner.idSeriennummer, Status.Bezeichnung " + "FROM Rechner, Status "
				+ "WHERE Rechner.Auftrag_idAuftragsnummer = '" + pAuftragsnr + "' "
				+ "AND Rechner.Status_idStatus = Status.idStatus";
		ResultSet rsRechner = stmt.executeQuery(queryRechner); 
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
	 * holt info fuer FA_Rechner nach Seriennummer
	 * @return FA_Rechner fr
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
	 * holt info fuer SA_Rechner nach Seriennummer
	 * @return SA_Rechner sr
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
	 * holt den Rechner mit der kleinsten ID
	 * @param pAuftragsnummer
	 * @return lowestID
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
	 * Update des Auftragsstatus auf niedrigste Statusid aller zugehörigen Rechner
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
	 * @return boolean
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
	 * @return 
	 * @throws SQLException
	 */
	public void rechnerVerteilung(int idAuftragsverteilung, LocalDate bearbeitungsdatum, int seriennummer,
			int personalnummer) throws SQLException {
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		String query = "INSERT INTO Auftragsverteilung VALUES(" + idAuftragsverteilung + ", '"
				+ bearbeitungsdatum + "', '" + seriennummer + "', '" + personalnummer + "')";
		stmt.executeUpdate(query);
	}
	
	/**
	 * Stellt fest ob User, mit eingebenen Daten existiert
	 * username = idPersonalnummer, passwort = name
	 * @param username
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public Mitarbeiter authenticateUserNEU(String username, String password) throws SQLException {
		Statement stmt = connection.createStatement();
		int usernameZahl = Integer.parseInt(username);
		Mitarbeiter userVergleich = null;
		String query = "SELECT Name, idPersonalnummer FROM Mitarbeiter WHERE Name = '" + password
				+ "' AND idPersonalnummer = '" + usernameZahl + "'";
		ResultSet rs = stmt.executeQuery(query);

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
 * Holt die Rolle(AL oder Monteur) des eben eingeloggten Users
 * @param user
 * @return
 * @throws SQLException
 */
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
