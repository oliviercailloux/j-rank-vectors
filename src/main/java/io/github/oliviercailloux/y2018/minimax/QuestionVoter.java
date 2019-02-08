package io.github.oliviercailloux.y2018.minimax;

import java.util.LinkedList;
import java.util.List;

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
	 * Return a question of the type a > b
	 *
	 * @return a list of alternative. The first position contains "a", the second "b".
	 */
	public List<Alternative> getQuestion(){
		List<Alternative> s=new LinkedList<Alternative>();
		s.add(this.a);
		s.add(this.b);
		return s;
	}
	
}
