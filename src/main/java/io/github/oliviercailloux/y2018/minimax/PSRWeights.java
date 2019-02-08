package io.github.oliviercailloux.y2018.minimax;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apfloat.Aprational;

import com.google.common.base.Preconditions;

public class PSRWeights {
	
	private final List<Double> weights;
	private final double upperBound=1;
	private final double lowerBound=0;
	
	public PSRWeights(List<Double> weights) {
		Preconditions.checkNotNull(weights);
		if(weights.size()>1) {
			if(weights.get(0)!=upperBound || weights.get(weights.size()-1)!=lowerBound || !checkConvexity(weights)) {
				throw new IllegalArgumentException("Sequence not valid");
			}
		}
		this.weights = new LinkedList<Double>();
		this.weights.addAll(weights);
	}
	
	@SuppressWarnings("unused")
	private boolean checkMonotonicity(List<Double> weights) {
		Iterator<Double> ls= weights.iterator();
		double curr;
		double prev= ls.next();
		while(ls.hasNext()) {
			curr=ls.next();
			if(prev < curr) {
				return false;
			}
			prev=curr;
		}
		return true;
	}
	
	private boolean checkConvexity(List<Double> weights){
		double wi1,wi2,wi3;
		for(int i=0;i<weights.size()-2;i++) {
			wi1=weights.get(i);
			wi2=weights.get(i+1);
			wi3=weights.get(i+2);
			if((wi1-wi2)<(wi2-wi3)) {
				return false;
			}
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
	
	/**
	 * @return the result of the query (w_i − w_{i+1}) >= λ (w_{i+1} − w_{i+2})
	 */
	public boolean askQuestion(QuestionCommittee qc) {
		int i= qc.getRank();
		Aprational lambda = qc.getLambda();
		return (weights.get(i-1)-weights.get(i))>=(lambda.doubleValue()*(weights.get(i)-weights.get(i+1)));
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