package it.polito.tdp.PremierLeague.model;

public class SquadraDiff implements Comparable<SquadraDiff>{

	private Team squadra;
	private int differenza;
	
	
	public SquadraDiff(Team squadra, int differenza) {
		super();
		this.squadra = squadra;
		this.differenza = differenza;
	}
	
	public Team getSquadra() {
		return squadra;
	}
	public int getDifferenza() {
		return differenza;
	}

	@Override
	public int compareTo(SquadraDiff a) {
		
		if(this.differenza<a.differenza) {
			return -1;
		}else if(this.differenza>a.differenza) {
			return 1;
		}else {
			return 0;
		}
		
	}

	@Override
	public String toString() {
		return squadra + " ("+ differenza + ")";
	}
	
	
	
}
