package io.github.oliviercailloux.y2018.minimax;

import java.util.List;

import io.github.oliviercailloux.y2018.j_voting.*;

public class VoterStrictPreference {

	private Voter voter;
	private StrictPreference pref;
	
	public VoterStrictPreference(Voter voter, List<Alternative> preferences) {
		this.voter = voter;
		this.pref = new StrictPreference(preferences);
	}

	public Voter getVoter() {
		return voter;
	}

	public StrictPreference getPref() {
		return pref;
	}

	/**
	 * @return the result of the query a > b
	 */
	public boolean askQuestion(QuestionVoter qv) {
		return pref.getAlternativeRank(qv.getFirstAlternative()) > pref.getAlternativeRank(qv.getSecondAlternative());
	}
	
}
