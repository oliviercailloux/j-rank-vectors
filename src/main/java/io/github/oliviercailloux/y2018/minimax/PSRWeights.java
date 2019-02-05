package io.github.oliviercailloux.y2018.minimax;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PSRWeights {
	
	private final List<Double> weights;
	private final double upperBound;
	private final double lowerBound;
	
	public PSRWeights(List<Double> weights) {
		this.upperBound=1;
		this.lowerBound=0;
		//TODO check size and 1 m position
		if(!checkConvexity(weights)) {
			throw new IllegalArgumentException("Sequence not valid");
		}
		this.weights = new LinkedList<Double>();
		this.weights.addAll(weights);
	}
	
	private boolean checkMonotonicity(List<Double> weights) {
		Iterator<Double> ls= weights.iterator();
		double curr=this.upperBound;
		double prev= ls.next();
		if(prev != this.upperBound){
			return false;
		}
		while(ls.hasNext()) {
			curr=ls.next();
			if(prev < curr) {
				return false;
			}
			prev=curr;
		}
		if(curr != this.lowerBound){
			return false;
		}
		return true;
	}
	
	private boolean checkConvexity(List<Double> weights){
		if(weights.size()<3) {
			//TODO
		}
		double wi1,wi2,wi3;
		if(weights.get(0) != this.upperBound){
			return false;
		}
		for(int i=0;i<weights.size()-2;i++) {
			wi1=weights.get(i);
			wi2=weights.get(i+1);
			wi3=weights.get(i+2);
			if((wi1-wi2)<(wi2-wi3)) {
				return false;
			}
		}
		if(weights.get(weights.size()-1) != this.lowerBound){
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieves the weight associated to the given rank. The first position of the ranking is 1.
	 * 
	 * @return the weight of the given rank
	 */
	public double getWeightAtRank(int rank) {
		return this.weights.get(rank-1);
	}
	
	public List<Double> getWeights(){
		return this.weights;
	}
	
	public String toString(){
		return this.weights.toString();
	}
	
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null) return false;
		if(!(o instanceof PSRWeights)) return false;
		PSRWeights w = (PSRWeights)o;
		return w.weights.equals(this.weights);
	}
	
	public int hashCode() {
		return Objects.hash(this.weights);
	}
		
}