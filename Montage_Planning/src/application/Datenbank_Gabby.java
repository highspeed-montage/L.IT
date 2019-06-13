package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Auftrag;
import models.Auftragsverteilung;
import models.Rechner;

public class Datenbank_Gabby {

	private static final String DB_CONNECTION = "jdbc:mysql://localhost:8889/aj9s-montage?serverTimezone=UTC";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	private Connection connection;

	/**
	 * Verbindung zur Datenbank
	 * 
	 * @return
	 */
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

	/**
	 * Öffnen der Datenbankverbindung
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
			System.out.println("Fehler beim Schließen der Datenbankverbindung.");
		}
	}

	/**
	 * Holt alle Rechner aus der Auftragsverteilung
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Auftragsverteilung> getRechnerAusAuftragsverteilungListe(/* Mitarbeiter user */) throws SQLException {

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
			tabelleninhalt.add(new Auftragsverteilung(rs.getDate("Auftragsverteilung.Datum").toLocalDate(),
					rs.getDate("Auftragsverteilung.Datum"), rs.getInt("Auftragsverteilung.Rechner_seriennummer"),
					rs.getString("Status.Bezeichnung")));
		}
		return tabelleninhalt;
	}

	/**
	 * Holt alle Rechner aus der Auftragsverteilung für die Wochenansicht
	 * 
	 * @param startdatum
	 * @param enddatum
	 * @return
	 * @throws SQLException
	 */
	public List<Auftragsverteilung> getRechnerAusAuftragsverteilungWoche(String startdatum,
			String enddatum/* Mitarbeiter user, */ ) throws SQLException {

		List<Auftragsverteilung> tabelleninhalt = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT * FROM Auftragsverteilung WHERE Datum BETWEEN '" + startdatum + "' AND '" + enddatum
				+ "'";
		// + "WHERE Mitarbeiter_idPersonalnummer = '"+user.benutzername+"'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			tabelleninhalt
					.add(new Auftragsverteilung(rs.getInt("Rechner_seriennummer"), rs.getDate("Datum").toLocalDate()));
		}
		return tabelleninhalt;
	}

	/**
	 * Holt die Auftragsart des Rechners
	 * 
	 * @param seriennr
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * Holt alle Bearbeitungsdaten aus der Auftragsverteilung
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Date> getRechnerBearbeitungsdatum() throws SQLException {
		List<Date> bearbeitungsdatum = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT DISTINCT Datum FROM Auftragsverteilung";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			bearbeitungsdatum.add(rs.getDate("Datum"));
		}
		return bearbeitungsdatum;
	}

	public List<Auftrag> getAuftrag () throws SQLException {
		List<Auftrag> auftraege = new ArrayList<>();
		Statement stmt = connection.createStatement();
		
//		Status fehlt in der Datenbank
		String query = "SELECT idAuftragsnummer, Lieferdatum, FROM Auftrag";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
		auftraege.add(new Auftrag(rs.getInt("Auftrag.idAuftragsnummer"), rs.getDate("Auftrag.Lieferdatum")));
		}
		
		
		return auftraege;		
	}
	
	public List<Rechner> getRechnerZuAuftrag (int auftragsnummer) throws SQLException {
		List<Rechner> rechner = new ArrayList<>();
		Statement stmt = connection.createStatement();
		String query = "SELECT idSeriennummer FROM Rechner "
				+ "WHERE Rechner.Auftrag_idAuftragsnummer = '"+auftragsnummer+"'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			rechner.add(new Rechner(rs.getInt("idSeriennummer")));
		}
		return rechner;
	}
}
