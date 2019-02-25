package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.y2018.j_voting.Voter;

/**
 * Immutable
 *
 * @author xoxor
 * @author Olivier Cailloux
 *
 */
public class Oracle {

	public static Oracle build(ImmutableMap<Voter, VoterStrictPreference> profile, PSRWeights weights) {
		return new Oracle(profile, weights);
	}

	private ImmutableMap<Voter, VoterStrictPreference> profile;
	private PSRWeights weights;

	private Oracle(Map<Voter, VoterStrictPreference> profile, PSRWeights weights) {
		this.profile = ImmutableMap.copyOf(profile);
		this.weights = weights;
		checkArgument(profile.entrySet().stream().allMatch((e) -> e.getValue().getVoter().equals(e.getKey())));
		checkArgument(
				profile.values().stream().map((vp) -> vp.asStrictPreference().getAlternatives()).distinct().limit(2).count() <= 1);
		if (profile.size() >= 1) {
			final int nbAlts = profile.values().stream().findAny().get().asStrictPreference().getAlternatives().size();
			checkArgument(weights.getWeights().size() == nbAlts);
		}
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

	public ImmutableMap<Voter, VoterStrictPreference> getProfile() {
		return profile;
	}

	public PSRWeights getWeights() {
		return weights;
	}

}
