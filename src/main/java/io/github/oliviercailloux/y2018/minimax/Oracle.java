package io.github.oliviercailloux.y2018.minimax;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.y2018.j_voting.*;

public class Oracle {

	public static Oracle build(ImmutableMap<Voter, VoterStrictPreference> pref, PSRWeights w) {
		return new Oracle(pref,w);
	}
	
	private ImmutableMap<Voter, VoterStrictPreference> profile;
	private PSRWeights weights;

	private Oracle(ImmutableMap<Voter, VoterStrictPreference> pref, PSRWeights w) {
		this.profile = ImmutableMap.copyOf(pref);
		this.weights = PSRWeights.given(w.getWeights());
	}

	public Answer getAnswer(Question q) {
		switch (q.getType()) {
		case VOTER_QUESTION: {
			QuestionVoter qv = q.getQuestionVoter();
			Voter v = qv.getVoter();
			VoterStrictPreference vsp = profile.get(v);
			return vsp.askQuestion(qv);
		}
		case COMMITTEE_QUESTION: {
			QuestionCommittee qc = q.getQuestionCommittee();
			return weights.askQuestion(qc);
		}
		default:
			throw new IllegalStateException();
		}
	}
	
	public ImmutableMap<Voter, VoterStrictPreference> getProfile (){
		return profile;
	}

	public PSRWeights getWeights() {
		return weights;
	}
	
}
