package org.decision_deck.rank_vectors;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class RankVector {
	
	private final int voter;
	
	private final int alternatives;

	private final List<Integer> rank;
	
	private boolean completePref;
	
	public RankVector(int voter, int alternatives, List<Integer> rank) {
		this.voter = voter;
		this.alternatives=alternatives;
		this.rank = new LinkedList<Integer>();
		this.rank.addAll(rank);
		updateFlag();
	}

	public int getVoter() {
		return voter;
	}
	
	/**
	 * Compares the rank of alternative a and b if both are present in the preference profile. 
	 * 
	 * @return true if a is preferred to b, false otherwise. 
	 */
	//what if a=b?
	public boolean isPreferred(int a, int b) throws IncompleteProfileException {
		if (!this.rank.contains(a) || !this.rank.contains(b)){
			throw new IncompleteProfileException();
		}
		return this.rank.indexOf(a) < this.rank.indexOf(b);
	}
	
	/**
	 * Retrieves the rank of alternative a if the voter's preference profile is complete.
	 * 
	 * @return the index starting from 1.
	 */
	public int getRank(int a) throws RuntimeException{
		if (!this.completePref){
			throw new IncompleteProfileException();
		}
		if (!this.rank.contains(a)) {
			throw new IllegalArgumentException("Alternative not valid");
		}
		return this.rank.indexOf(a)+1;
	}
	
	/**
	 * Retrieves the alternative at the rank specified if the voter's preference profile is complete.
	 * 
	 * @return the alternative at the given rank.
	 */
	public int getAlternative(int rank) throws RuntimeException{
		if (!this.completePref){
			throw new IncompleteProfileException();
		}
		if (rank > this.rank.size()) {
			throw new IllegalArgumentException("Rank not valid");
		}
		return this.rank.get(rank-1);
	}
	
	/**
	 * Add the alternative x after the alternative a if this is present in the voter's preference profile.
	 * 
	 * @return void.
	 */
	public void addAfter (int a, int x) throws IllegalArgumentException{
		if (!this.rank.contains(a)) {
			throw new IllegalArgumentException("Alternative "+a+" not ranked");
		}
		if (this.rank.contains(x)) {
			throw new IllegalArgumentException("Alternative "+x+" already ranked");
		}
		int i = this.rank.indexOf(a) + 1;
		this.rank.add(i, x);
		updateFlag();
	}
	
	/**
	 * Add the alternative x before the alternative a if this is present in the voter's preference profile.
	 * 
	 * @return void.
	 */
	public void addBefore (int a, int x) throws IllegalArgumentException{
		if (!this.rank.contains(a)) {
			throw new IllegalArgumentException("Alternative "+a+" not ranked");
		}
		if (this.rank.contains(x)) {
			throw new IllegalArgumentException("Alternative "+x+" already ranked");
		}
		int i = this.rank.indexOf(a);
		this.rank.add(i, x);
		updateFlag();
	}

	private void updateFlag() {
		if(this.rank.size()==this.alternatives)
			this.completePref=true;
		else this.completePref=false;
	}
	
	public String toString(){
		return this.voter + " " + this.rank.toString();
	}
	
	public boolean equals(Object o) {
		if(o == this ) return true;
		if(!(o instanceof RankVector)) return false;
		RankVector rv = (RankVector)o;
		return rv.voter == this.voter && rv.alternatives==this.alternatives &&
				rv.completePref == this.completePref && rv.rank.equals(this.rank);
	}
	
	public int hashCode() {
		return Objects.hash(this.rank);
	}
	
}

