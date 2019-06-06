package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import models.Auftragsverteilung;
import models.Mitarbeiter;
import models.Privatkunde;
import models.FA_Rechner;
import models.Geschaeftskunde;
import models.Rechner;
import models.Teile;

public class Datenbank {

	// private static final String DB_CONNECTION =
	// "jdbc:mysql://193.196.143.168:3306/aj9s-montage?serverTimezone=UTC";
	// private static final String DB_USER = "aj9s-montage";
	// private static final String DB_PASSWORD = "TPrKrlU9QsMv6Oh7";

	// NICHT LoeSCHEN: Datenbankverbindung GABBY LOKAL fuers testen, weil VPN nicht
	// geht (ich habe mir die Datenbank geklont)
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
			System.out.println("Fehler beim Schlie√üen der Datenbankverbindung.");
		}
	}

	public void listKunde() throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT Name FROM Kunde");
		while (rs.next()) {
			System.out.println(rs.getString("name"));
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
	// ResultSetMetaData rsmd = rs.getMetaData(); // Grroesse der Tabelle
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

	/** Rechner - Listenansicht Daten fuer Tabelleninhalt */
	public List<Auftragsverteilung> listRechnerAusAuftragsverteilungListe(/* Mitarbeiter user */) throws SQLException {

		List<Auftragsverteilung> tabelleninhalt = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT Auftragsverteilung.Datum, Auftragsverteilung.Rechner_seriennummer, Status.Bezeichnung "
				+ "FROM Auftragsverteilung, Status, Rechner "
				+ "WHERE Auftragsverteilung.Rechner_seriennummer = Rechner.idSeriennummer "
				+ "AND Rechner.Status_idStatus = Status.idStatus";
		// + "AND Auftragsverteilung.Mitarbeiter_idPersonalnummer =
		// '"+user.benutzername+"'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			tabelleninhalt.add(new Auftragsverteilung(rs.getDate("Auftragsverteilung.Datum"),
					rs.getDate("Auftragsverteilung.Datum"), rs.getInt("Auftragsverteilung.Rechner_seriennummer"),
					rs.getString("Status.Bezeichnung")));
		}
		return tabelleninhalt;
	}

	/** Rechner - Wochenansicht Daten fuer Tabelleninhalt */
	public List<Auftragsverteilung> listRechnerAusAuftragsverteilungWoche(/* Mitarbeiter user */) throws SQLException {

		List<Auftragsverteilung> tabelleninhalt = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT Rechner_seriennummer, Datum FROM Auftragsverteilung";
		// + "WHERE Auftragsverteilung.Mitarbeiter_idPersonalnummer =
		// '"+user.benutzername+"'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			tabelleninhalt.add(new Auftragsverteilung(rs.getInt("Rechner_seriennummer"), rs.getDate("Datum")));
		}
		return tabelleninhalt;
	}

	// Hier die Abfragen fuer die Rechnerinformationscontroller
	/** Seriennummer */
	// FRAGE: wenn man den Hyperlink Seriennummer in der Rechner Listenansicht
	// anklickt -> dann muss die Seriennummer irgendwo zwischengespeichert werden!
	// Dieser Schritt entfaellt deshalb
	// Die Seriennummer ist fuer viele der folgenden Methoden notwendig

	/** Kunde */
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

	// /** KundenEMail */
	// public String getKundenMail(int serienNummer) throws SQLException {
	// // Auftrag holen
	// // Kunde des jwlg Auftrags holen
	// // Kundenid + name auf davon holen
	// Statement stmt = connection.createStatement();
	// String query = "SELECT EMail FROM Kunde WHERE idKundennummer = (SELECT
	// Kunde_idKunde FROM Auftrag WHERE idAuftragsnummer = (SELECT
	// Auftrag_idAuftragsnummer FROM Rechner_Teile WHERE Bezeichnung = '"
	// + serienNummer + "'))";
	// ResultSet rs = stmt.executeQuery(query);
	// String kundenEMail = null;
	//
	// while (rs.next()) {
	//
	// kundenEMail = rs.getString("EMail");
	// }
	// return kundenEMail;
	// }
	/** Rechnerinfo */
	// Konstutor Rechner: (Integer seriennr, String status, Date lieferdatum, Date
	// bearbeitungsdatum, String emailKunde, String nameKunde, Integer idKunde)
	// Teile erstmal vorweg..: Teile (...SELECT id_rechnerteile WHERE REchner.id_snr
	// = Rechner_Teile.REchner_idsnr)

	// INFO F‹R FA_RECHNER ANSICHT
	public FA_Rechner getFARechnerInfo(int pSeriennr) throws SQLException {

//		List<FA_Rechner> rechnerinfo = new ArrayList<>();
		FA_Rechner fr;

		Statement stmt = connection.createStatement();
		String query = "SELECT Auftragsverteilung.Rechner_seriennummer, Rechner.Status_idStatus, Auftragsverteilung.Datum, Kunde.EMail, Kunde.Name, Kunde.idKundennummer "
				+ "FROM Auftragsverteilung, Rechner, Kunde "
				+ "WHERE (Auftragsverteilung.Rechner_seriennummer = Rechner.idSeriennummer) OR ((SELECT Auftrag.Kunde_idKunde FROM Auftrag WHERE Rechner.Auftrag_idAuftragsnummer = Auftrag.idAuftragsnummer) = Kunde.idKundennummer )";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int auftragsNr; //REcner.Auftrag_idAuftragsnummer
			String pStatus; //Rechner_Status_idStatus -> Status_Bezeichnung
			Teile pTeile;	//In auftragsVert und Rechner gibts die Seriennummer, die in der Teile Klasse benˆtigt wurde-> Such dir die praktischere aus
			Date lieferdatum;	//GIBT ES NOCH NICHT
			Date pBearbeitungsdatum; //Auftragsverteilung.Datum
			String Firmenname = null;
			String PrivatName = null;
			if( rs.getString("Kunde.Firmenname") != null) {
				Firmenname = rs.getString("Kunde.Firmenname");
				}else {
					PrivatName = rs.getString("Kunde.Name");
				}
			
//			Geschaeftskunde gk;
//			Privatkunde pk;
			fr = new FA_Rechner(lieferdatum, Firmenname, PrivatName, pSeriennr, auftragsNr, pStatus, pBearbeitungsdatum, pTeile);

		}
		return fr;
	}

	/** Bearbeitungsdatum */

	/** Lieferdatum */

	/** Einzelteile */
	public List<String> getRechnerEinzelteile(int serienNummer) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "SELECT Bezeichnung FROM Teile WHERE idTeilenummer = (SELECT Teile_idTeilenummer FROM Rechner_Teile WHERE Rechner_idSeriennummer = '"
				+ serienNummer + "')";
		ResultSet rs = stmt.executeQuery(query);

		List<String> rechnerEinzelteile = new ArrayList<>();

		while (rs.next()) {

			rechnerEinzelteile.add(rs.getString("Bezeichnung"));
		}
		return rechnerEinzelteile;
	}

	/** Einzelteil Suche im Servicauftrag */
	public int getEinzelteilLagerbestand(String eingabe) throws SQLException {
		// Hier muss noch gecheckt werden, dass die eingabe ueberhaupt sinnvoll ist.
		Statement stmt = connection.createStatement();
		String query = "SELECT Lagerbestand FROM Rechner_Teile WHERE Bezeichnung = '" + eingabe + "'";
		ResultSet rs = stmt.executeQuery(query);
		int lagerbestand = 0;

		while (rs.next()) {
			lagerbestand = rs.getInt("Lagerbestand");
		}
		return lagerbestand;
	}

	/** Rechner Auftragsart holen */
	public int getRechnerAuftragsart(int seriennr) throws SQLException {
		int idAuftragsart = 0;
		Statement stmt = connection.createStatement();
		String query1 = "SELECT Rechner.Auftrag_idAuftragsnummer, Auftrag.Auftragsart_idAuftragsart FROM Rechner, Auftrag "
				+ "WHERE Rechner.idSeriennummer = '" + seriennr + "' "
				+ "AND Rechner.Auftrag_idAuftragsnummer = Auftrag.idAuftragsnummer";
		ResultSet rs1 = stmt.executeQuery(query1);
		while (rs1.next()) {
			idAuftragsart = rs1.getInt("Auftrag.Auftragsart_idAuftragsart");
		}
		return idAuftragsart;
	}

}
