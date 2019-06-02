package application;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class Datenbank {

	private static final String DB_CONNECTION = "jdbc:mysql://193.196.143.168:3306/aj9s-montage?serverTimezone=UTC";
	private static final String DB_USER = "aj9s-montage";
	private static final String DB_PASSWORD = "TPrKrlU9QsMv6Oh7";

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
		ResultSet rs = stmt.executeQuery("SELECT idPersonalnummer FROM Mitarbeiter"); // Abfrage wie Personalnummern
																						// lauten heiï¿½en

		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData(); // Grï¿½ï¿½e der Tabelle
		int columnCount = rsmd.getColumnCount(); //
		ArrayList<String> list = new ArrayList(columnCount); // Erstellt ArrayList

		while (rs.next()) {
			int i = 1;
			while (i <= columnCount) {
				list.add(rs.getString(i++));
			}
		}
		return list;
	}<<<<<<<

	Updated upstream=======

	// Abfrage der Passwoerter
	public ArrayList Passwortabfrage() throws SQLException {
>>>>>>> Stashed changes

//	// Abfrage der Passwï¿½rter
//	public ArrayList Passwortabfrage() throws SQLException {
//
//		Statement stmt = connection.createStatement();
//		ResultSet rs = stmt.executeQuery("SELECT name FROM Mitarbeiter"); // Abfrage wie Mitarbeiter heiï¿½en
//
//		ResultSetMetaData rsmd = rs.getMetaData(); // Grï¿½ï¿½e der Tabelle
//		int columnCount = rsmd.getColumnCount(); //
//		ArrayList<String> list = new ArrayList(columnCount); // Erstellt ArrayList
//
//		while (rs.next()) {
//			int i = 1;
//			while (i <= columnCount) {
//				list.add(rs.getString(i++));
//			}
//		}
//		return list;
//	}

<<<<<<< Updated upstream
	//
=======
		ResultSetMetaData rsmd = rs.getMetaData(); // Grï¿½ï¿½e der Tabelle
		int columnCount = rsmd.getColumnCount(); //
		ArrayList<String> list = new ArrayList(columnCount); // Erstellt ArrayList

		while (rs.next()) {
			int i = 1;
			while (i <= columnCount) {
				list.add(rs.getString(i++));
			}
		}
		return list;
	}

	/** Auflistung aller Rechner-Seriennummern */
	>>>>>>>

	Stashed changes

	public List<String> listRechnerBySeriennr() throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "SELECT Rechner_seriennummer FROM Auftragsverteilung";
		ResultSet rs = stmt.executeQuery(query);
		List<String> rechnerSeriennr = new ArrayList<>();
		while (rs.next()) {
			System.out.println(rs.getString("Rechner_seriennummer"));
			rechnerSeriennr.add(rs.getString("Rechner_seriennummer"));
		}
		return rechnerSeriennr;
	}

	/** Auflisten aller Rechner-STatus */
	public List<String> listRechnerByStatus() {
		// Aufrufen der status id des jwlg Rechners
		Statement stmt = connection.createStatement();
		String query = "SELECT Status_idStatus FROM Rechner";
		ResultSet rs = stmt.executeQuery(query);
		List<String> rechnerStatus = new ArrayList<>();
		while (rs.next()) {
			// laden des STatus mit der jwlg id
			// hinzufügen zu liste: "rechnerStatus"
			int idStatus = rs.getInt("Status_idStatus");
			String innerQuery = "SELECT Bezeichnung FROM Status WHERE idStatus = '" + idStatus + "' ";
			ResultSet irs = stmt.executeQuery(innerQuery);
			while (irs.next()) {
				rechnerStatus.add(irs.getString("Bezeichnung"));
			}
		}
		return rechnerStatus;
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
	
	
	

	// Hier die Abfragen für die Rechnerinformationscontroller
	/** Seriennummer */
	//FRAGE: wenn man den Hyperlink Seriennummer  in der Rechner Listenansicht anklickt -> dann muss die Seriennummer irgendwo zwischengespeichert werden!
	//Dieser Schritt entfällt deshalb
	//Die Seriennummer ist für viele der folgenden Methoden notwendig
	
	
	
	
	
	/** Kunde */
	public int getKunde(int serienNummer) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "SELECT Kunde FROM Auftrag WHERE idAuftragsnummer = (SELECT Auftrag_idAuftragsnummer FROM Rechner_Teile WHERE Bezeichnung = '"+serienNummer+"')";
		ResultSet rs = stmt.executeQuery(query);
		int lagerbestand;

		while (rs.next()) {

			lagerbestand = rs.getInt("Lagerbestand");
		}
		return lagerbestand;
	}
	
	
	/** KundenEMail */
	public int getKundenMail(int serienNummer) throws SQLException {
		//Auftrag holen
		//Kunde des jwlg Auftrags holen
		//Kundenid + name auf davon holen
		Statement stmt = connection.createStatement();
		String query = "SELECT EMail FROM Kunde WHERE idKundennummer = (SELECT Kunde_idKunde FROM Auftrag WHERE idAuftragsnummer = (SELECT Auftrag_idAuftragsnummer FROM Rechner_Teile WHERE Bezeichnung = '"+serienNummer+"'))";
		ResultSet rs = stmt.executeQuery(query);
		int lagerbestand;

		while (rs.next()) {

			lagerbestand = rs.getInt("Lagerbestand");
		}
		return lagerbestand;
	}
	
	
	/** Bearbeitungsdatum */
	
	
	
	/** Lieferdatum */
	
	
	
	/** Einzelteile */
	public List<String> getRechnerEinzelteile(int serienNummer) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "SELECT Bezeichnung FROM Teile WHERE idTeilenummer = (SELECT Teile_idTeilenummer FROM Rechner_Teile WHERE Rechner_idSeriennummer = '"+ serienNummer + "')";
		ResultSet rs = stmt.executeQuery(query);
		
		List<String> rechnerEinzelteile = new ArrayList<>();

		while (rs.next()) {
			
			rechnerEinzelteile.add(rs.getString("Bezeichnung"));
		}
		return rechnerEinzelteile;
	}

	/** Einzelteil SUche im Servicauftrag */
	public int getEinzelteilLagerbestand(String eingabe) throws SQLException {
		// Hier muss noch gecheckt werden, dass die eingabe überhaupt sinnvoll ist.
		Statement stmt = connection.createStatement();
		String query = "SELECT Lagerbestand FROM Rechner_Teile WHERE Bezeichnung = '" + eingabe + "'";
		ResultSet rs = stmt.executeQuery(query);
		int lagerbestand;

		while (rs.next()) {

			lagerbestand = rs.getInt("Lagerbestand");
		}
		return lagerbestand;
	}
}