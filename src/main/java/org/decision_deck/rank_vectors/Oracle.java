package org.decision_deck.rank_vectors;

import java.util.Set;

import io.github.oliviercailloux.y2018.j_voting.*;

public class Oracle {
	
	private StrictPreference pref;
	private PSRWeights w;
	
	public Oracle(StrictPreference pref, PSRWeights w) {
		this.pref = new StrictPreference(pref.getAlternatives());
		this.w = new PSRWeights(w.getWeights());
	}
	
	public boolean isYes(Question q) {
		switch (q.type()) {
		case VOTER_QUESTION:
			return askVoter((QuestionVoter)q);
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
