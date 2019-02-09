package io.github.oliviercailloux.y2018.minimax;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.y2018.j_voting.*;

public class Oracle {
	
	private ImmutableMap<Voter,VoterStrictPreference> profile;
	private PSRWeights weights;
	
	public Oracle(ImmutableMap<Voter,VoterStrictPreference> pref, PSRWeights w) {
		this.profile= ImmutableMap.copyOf(pref);
		this.weights = new PSRWeights(w.getWeights());
	}
	
	public boolean isYes(Question q) {
		switch (q.getType()) {
		case VOTER_QUESTION:{
				QuestionVoter qv= q.getQuestionVoter();
				Voter v=qv.getVoter();
				VoterStrictPreference vsp= profile.get(v);
				return vsp.askQuestion(qv);
			}
		case COMMITTEE_QUESTION:{
			QuestionCommittee qc = q.getQuestionCommittee();
			return weights.askQuestion(qc);
		}
		default:
			throw new IllegalStateException();
		}
	}
	
}
