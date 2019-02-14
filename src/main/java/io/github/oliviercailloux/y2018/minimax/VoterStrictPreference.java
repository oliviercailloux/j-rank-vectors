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
	 * Given a query a > b
	 * 
	 * @return if a is greater or lower than b
	 */
	public Answer askQuestion(QuestionVoter qv) {
		if (pref.getAlternativeRank(qv.getFirstAlternative()) > pref.getAlternativeRank(qv.getSecondAlternative()))
			return Answer.GREATER;
		return Answer.LOWER;
	}

	@Override
	public String toString() {
		return "Voter: " + voter + " " + pref.toString();
	}
}
