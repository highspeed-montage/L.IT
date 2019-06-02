package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
			System.out.println("Fehler beim Schließen der Datenbankverbindung.");
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
																						// lauten hei�en

		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData(); // Gr��e der Tabelle
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

//	// Abfrage der Passw�rter
//	public ArrayList Passwortabfrage() throws SQLException {
//
//		Statement stmt = connection.createStatement();
//		ResultSet rs = stmt.executeQuery("SELECT name FROM Mitarbeiter"); // Abfrage wie Mitarbeiter hei�en
//
//		ResultSetMetaData rsmd = rs.getMetaData(); // Gr��e der Tabelle
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

	//
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
}
	
	
	
	
