package org.decision_deck.rank_vectors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apfloat.Apint;
import org.apfloat.Aprational;

public class PSRWeights {
	
	private final List<Aprational> weights;
	private final Aprational upperBound;
	private final Aprational lowerBound;
	
	public PSRWeights(List<Aprational> weights, Aprational ub, Aprational lb) {
		this.upperBound=new Apint(1);
		this.upperBound.add(ub);
		this.lowerBound=new Apint(0);
		this.lowerBound.add(lb);
		if(!checkConvexity(weights)) {
			throw new IllegalArgumentException("Sequence not valid");
		}
		this.weights = new LinkedList<Aprational>();
		this.weights.addAll(weights);
	}
	
	public PSRWeights(List<Aprational> weights) {
		this(weights, new Apint(1), new Apint(0));
	}
	
	private boolean checkMonotonicity(List<Aprational> weights) {
		Iterator<Aprational> ls= weights.iterator();
		Aprational curr=this.upperBound;
		Aprational prev= ls.next();
		if(prev.compareTo(this.upperBound)!=0){
			return false;
		}
		while(ls.hasNext()) {
			curr=ls.next();
			if(prev.compareTo(curr)<0) {
				return false;
			}
			prev=curr;
		}
		if(curr.compareTo(this.lowerBound)!=0){
			return false;
		}
		return true;
	}
	
	private boolean checkConvexity(List<Aprational> weights){
		if(weights.size()<3) {
			//TODO
		}
		Aprational wi1,wi2,wi3;
		if(weights.get(0).compareTo(this.upperBound)!=0){
			return false;
		}
		for(int i=0;i<weights.size()-2;i++) {
			wi1=weights.get(i);
			wi2=weights.get(i+1);
			wi3=weights.get(i+2);
			if(wi1.subtract(wi2).compareTo(wi2.subtract(wi3))<0) {
				return false;
			}
		}
		if(weights.get(weights.size()-1).compareTo(this.lowerBound)!=0){
			return false;
		}
		return true;
	}
	
	public String toString(){
		return this.weights.toString();
	}
	
	public boolean equals(Object o) {
		if(o == this ) return true;
		if(!(o instanceof PSRWeights)) return false;
		PSRWeights w = (PSRWeights)o;
		return w.weights.equals(this.weights);
	}
	
	public int hashCode() {
		return Objects.hash(this.weights);
	}
	
}