package io.github.oliviercailloux.y2018.minimax;

import io.github.oliviercailloux.y2018.j_voting.*;

public class QuestionVoter {

	private QuestionType qt;
	private Voter voter;
	private Alternative a,b;
	
	
	public QuestionVoter(Voter voter, Alternative a, Alternative b) {
		this.qt = QuestionType.VOTER_QUESTION;
		this.voter=voter;
		this.a=a;
		this.b=b;
	}

	public QuestionType type() {
		return qt;
	}

	public Voter getVoter() {
		return this.voter;
	}
	
	
	/**
	 * A Question to the voter has the form a > b
	 *
	 * @return a
	 */
	public Alternative getFirstAlternative(){
		return a;
	}
	
	/**
	 * A Question to the voter has the form a > b
	 *
	 * @return a
	 */
	public Alternative getSecondAlternative(){
		return b;
	}
	
}
