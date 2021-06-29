package it.polito.tdp.PremierLeague.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	PremierLeagueDAO dao;
	
	Graph<Team, DefaultWeightedEdge> grafo;
	Map<Integer, Team> idMap;
	Map<Team, Integer> classifica;
	
	public Model() {
		dao  = new PremierLeagueDAO();
		idMap = new HashMap<>();
		dao.listAllTeams(idMap);
		
		classifica = new HashMap<>();
	}
	
	public String creaGrafo() {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

		//creo vertici
		Graphs.addAllVertices(this.grafo, dao.getVertici(idMap));
		
		dao.getArchi(classifica, idMap);
		//creo archi
		
		
		for(Team squadra1 : classifica.keySet()) {
			for(Team squadra2 : classifica.keySet()) {
				
				if(!this.grafo.containsEdge(squadra1, squadra2) && !this.grafo.containsEdge(squadra2, squadra1)) {
					
					if(classifica.get(squadra1)>classifica.get(squadra2)) {
						
						Graphs.addEdge(this.grafo, squadra1, squadra2, classifica.get(squadra1)-classifica.get(squadra2));
					}else if(classifica.get(squadra1)<classifica.get(squadra2)) {
						
						Graphs.addEdge(this.grafo, squadra2, squadra1, classifica.get(squadra2)-classifica.get(squadra1));
					}
				}				
			}
		}
		
		
		
		return String.format("Grafo creato!\n# di vertici: %d\n# di archi: %d", this.grafo.vertexSet().size(), this.grafo.edgeSet().size() );
	}

	public List<SquadraDiff> getPeggiori(Team squadra){
		
		List<SquadraDiff> result = new LinkedList<>();
		for(DefaultWeightedEdge arco : this.grafo.outgoingEdgesOf(squadra)) {
			SquadraDiff s = new SquadraDiff(Graphs.getOppositeVertex(this.grafo, arco, squadra),(int) this.grafo.getEdgeWeight(arco));
			result.add(s);
		}
		Collections.sort(result);
		return result;
	}
	
	public List<SquadraDiff> getMigliori(Team squadra){
		
		List<SquadraDiff> result = new LinkedList<>();
		for(DefaultWeightedEdge arco : this.grafo.incomingEdgesOf(squadra)) {
			SquadraDiff s = new SquadraDiff(Graphs.getOppositeVertex(this.grafo, arco, squadra),(int) this.grafo.getEdgeWeight(arco));
			result.add(s);
		}
		Collections.sort(result);
		return result;
	}

	public List<Team> getTeam(){
		return dao.getVertici(idMap);
	}


}
