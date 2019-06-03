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

public class Datenbank {

	// private static final String DB_CONNECTION =
	// "jdbc:mysql://193.196.143.168:3306/aj9s-montage?serverTimezone=UTC";
	// private static final String DB_USER = "aj9s-montage";
	// private static final String DB_PASSWORD = "TPrKrlU9QsMv6Oh7";

	// NICHT LoeSCHEN: Datenbankverbindung GABBY LOKAL fuers testen, weil VPN nicht geht (ich habe mir die Datenbank geklont)
//	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/aj9s-montage?serverTimezone=UTC";	//für jan
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
			System.out.println("Fehler beim SchlieÃŸen der Datenbankverbindung.");
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
	public ArrayList Usernameabfrage() throws SQLException {
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

	// Rechner - Listenansicht Daten fuer Tabelleninhalt
	public List<Auftragsverteilung> listRechnerAusAuftragsverteilung(/* String user */) throws SQLException {

		List<Auftragsverteilung> tabelleninhalt = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT Auftragsverteilung.Datum, Auftragsverteilung.Rechner_seriennummer, "
				+ "Rechner.Status_idStatus FROM Auftragsverteilung, Rechner "
				+ "WHERE Auftragsverteilung.Rechner_seriennummer = Rechner.idSeriennummer ";
		// + "AND Auftragsverteilung.Mitarbeiter_idPersonalnummer = '"+user+"'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			tabelleninhalt.add(new Auftragsverteilung(rs.getDate("Auftragsverteilung.Datum"),
					rs.getDate("Auftragsverteilung.Datum"), rs.getInt("Auftragsverteilung.Rechner_seriennummer"),
					rs.getString("Rechner.Status_idStatus")));
		}
		return tabelleninhalt;
	}

	/** Auflisten aller Rechner-Bearbeitungsdaten */
	public List<Date> listRechnerByBearbeitungsdatum() throws SQLException { // java.util.date
		Statement stmt = connection.createStatement();
		String query = "SELECT Datum FROM Auftragsverteilung";
		ResultSet rs = stmt.executeQuery(query);
		List<Date> rechnerBearbeitungsdaten = new ArrayList<>(); // Welcher Datentyp?

		while (rs.next()) {
			java.sql.Date sqlDate = rs.getDate("Datum"); // wie ist der ColumnName wirklich??
			java.util.Date javaDate = new java.util.Date(sqlDate.getTime()); // Umwandlung von sql.Date zu util.Date
			rechnerBearbeitungsdaten.add(javaDate);
		}
		return rechnerBearbeitungsdaten;
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

	/** KundenEMail */
	public String getKundenMail(int serienNummer) throws SQLException {
		// Auftrag holen
		// Kunde des jwlg Auftrags holen
		// Kundenid + name auf davon holen
		Statement stmt = connection.createStatement();
		String query = "SELECT EMail FROM Kunde WHERE idKundennummer = (SELECT Kunde_idKunde FROM Auftrag WHERE idAuftragsnummer = (SELECT Auftrag_idAuftragsnummer FROM Rechner_Teile WHERE Bezeichnung = '"
				+ serienNummer + "'))";
		ResultSet rs = stmt.executeQuery(query);
		String kundenEMail = null;

		while (rs.next()) {

			kundenEMail = rs.getString("EMail");
		}
		return kundenEMail;
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
}
