package io.github.oliviercailloux.y2018.minimax;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.y2018.j_voting.*;

public class PrefKnowledge {

	private ImmutableMap<Voter,VoterPartialPreference> profile;
	private ConstraintsOnWeights weights;
	
	public PrefKnowledge(ImmutableMap<Voter, VoterPartialPreference> profile, ConstraintsOnWeights weights) {
		this.weights = weights;
		this.profile = ImmutableMap.copyOf(profile);
	}

	public ImmutableMap<Voter, VoterPartialPreference> getProfile() {
		return profile;
	}


	public ConstraintsOnWeights getWeights() {
		return weights;
	}

}
