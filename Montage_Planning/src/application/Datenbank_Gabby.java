package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import models.Auftrag;
import models.Auftragsverteilung;
import models.Monteur;
import models.Rechner;

public class Datenbank_Gabby {

	private static final String DB_CONNECTION = "jdbc:mysql://localhost:8889/aj9s-montage_neu?serverTimezone=UTC";
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
	 * @return
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
	 * @return
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

	public List<Auftrag> getAuftrag() throws SQLException {
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

}
