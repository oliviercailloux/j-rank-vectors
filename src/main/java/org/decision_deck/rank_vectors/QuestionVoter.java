package org.decision_deck.rank_vectors;

import java.util.HashSet;
import java.util.Set;

import io.github.oliviercailloux.y2018.j_voting.*;

public class QuestionVoter implements Question{

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
	
	public Set<Alternative> getQuestion(){
		Set<Alternative> s=new HashSet<Alternative>();
		s.add(this.a);
		s.add(this.b);
		return s;
	}
	
}
