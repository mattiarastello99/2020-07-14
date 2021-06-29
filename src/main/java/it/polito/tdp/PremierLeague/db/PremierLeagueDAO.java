package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void listAllTeams(Map<Integer, Team> mappa){
		String sql = "SELECT * FROM Teams";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				mappa.put(res.getInt("TeamID"), team);
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> getVertici(Map<Integer, Team> mappa){
		
		String sql = "SELECT DISTINCT TeamID "
				+ "FROM teams";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(mappa.get(res.getInt("TeamID"))!=null) {
					Team team = mappa.get(res.getInt("TeamID"));
					result.add(team);
				}
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void getArchi(Map<Team, Integer> classifica, Map<Integer, Team> idMap) {
		
		this.calcolaCasa(classifica, idMap);
		this.calcolaOspite(classifica, idMap);
		this.calcolaPareggio(classifica, idMap);
		
	}
	
	private void calcolaCasa(Map<Team, Integer> classifica, Map<Integer, Team> idMap) {
		
		String sql = "SELECT m.TeamHomeID "
				+ "FROM matches m "
				+ "WHERE m.ResultOfTeamHome=1 ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				//aggiungere 3 punti agli id
				
				Team squadra = idMap.get(res.getInt("TeamHomeID"));
				if(squadra!=null) {
					if(!classifica.containsKey(squadra)) {
						//prima giornata di campionato
						classifica.put(squadra, 3);
					}else {
					//squadra ha gia punti
						classifica.put(squadra, classifica.get(squadra)+3);
					}
					
				}
		
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void calcolaOspite(Map<Team, Integer> classifica, Map<Integer, Team> idMap) {
		
		String sql = "SELECT m.TeamAwayID "
				+ "FROM matches m "
				+ "WHERE m.ResultOfTeamHome=-1";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				//aggiungere 3 punti agli id
				
				Team squadra = idMap.get(res.getInt("TeamAwayID"));
				if(squadra!=null) {
					if(!classifica.containsKey(squadra)) {
						//prima giornata di campionato
						classifica.put(squadra, 3);
					}else {
					//squadra ha gia punti
						classifica.put(squadra, classifica.get(squadra)+3);
					}
					
				}
		
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void calcolaPareggio(Map<Team, Integer> classifica, Map<Integer, Team> idMap) {
		
		String sql = "SELECT m.TeamHomeID, m.TeamAwayID "
				+ "FROM matches m "
				+ "WHERE m.ResultOfTeamHome=0";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				//aggiungere 3 punti agli id
				
				Team squadra1 = idMap.get(res.getInt("TeamHomeID"));
				Team squadra2 = idMap.get(res.getInt("TeamAwayID"));
				
				if(squadra1!=null && squadra2!=null) {
					if(!classifica.containsKey(squadra1)) {
						//prima giornata di campionato
						classifica.put(squadra1, 1);
					}else {
					//squadra ha gia punti
						classifica.put(squadra1, classifica.get(squadra1)+1);
					}
					
					if(!classifica.containsKey(squadra1)) {
						//prima giornata di campionato
						classifica.put(squadra2, 1);
					}else {
					//squadra ha gia punti
						classifica.put(squadra2, classifica.get(squadra2)+1);
					}
					
				}
		
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}




}
