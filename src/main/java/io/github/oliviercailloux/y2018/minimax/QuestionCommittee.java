package io.github.oliviercailloux.y2018.minimax;

import org.apfloat.Aprational;

public class QuestionCommittee {
	
	private QuestionType qt;
	private Aprational lambda;
	private int rank;
	
	public QuestionCommittee(Aprational lambda, int rank) {
		this.qt = QuestionType.COMMITTEE_QUESTION;
		this.lambda = lambda;
		this.rank = rank;
	}
	
	public QuestionType type() {
		return qt;
	}
	
	/**
	 * Given a query of the type: (w_i − w_{i+1}) >= λ (w_{i+1} − w_{i+2}).
	 *
	 * @return λ
	 */
	public Aprational getLambda(){
		return lambda;
	}

	/**
	 * Given a query of the type: (w_i − w_{i+1}) >= λ (w_{i+1} − w_{i+2}).
	 *
	 * @return i
	 */
	public int getRank() {
		return rank;
	}
	
	

}
