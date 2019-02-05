package io.github.oliviercailloux.y2018.minimax;

import java.util.Set;

import io.github.oliviercailloux.y2018.j_voting.*;

public class Oracle {
	
	//mustbeamap
	private VoterStrictPreference pref;
	private PSRWeights w;
	
	public Oracle(StrictPreference pref, PSRWeights w) {
	//	this.pref = new VoterStrictPreference(pref.getAlternatives());
		this.w = new PSRWeights(w.getWeights());
	}
	
	public boolean isYes(Question q) {
		switch (q.getType()) {
		case VOTER_QUESTION:
			return pref.askQuestion(q.getQuestionVoter());
		case COMMITTEE_QUESTION:
			return askCommittee(q);
		default:
			throw new IllegalStateException();
		}
	}

	private boolean askVoter(QuestionVoter q) {
		Voter voter=q.getVoter();
		Set<Alternative> s= q.getQuestion();

		return false;
	}
	
	private boolean askCommittee(Question q) {
		return false;
	}
	
}
