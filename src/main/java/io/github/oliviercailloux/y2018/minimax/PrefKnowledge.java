package io.github.oliviercailloux.y2018.minimax;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.y2018.j_voting.*;

public class PrefKnowledge {

	private ImmutableMap<Voter,PartialPreference> profile;
	private ConstraintsOnWeights weights;
	
	public PrefKnowledge(ImmutableMap<Voter, PartialPreference> profile, ConstraintsOnWeights weights) {
		this.weights = weights;
		this.profile = ImmutableMap.copyOf(profile);
	}

	public ImmutableMap<Voter, PartialPreference> getProfile() {
		return profile;
	}


	public ConstraintsOnWeights getWeights() {
		return weights;
	}

}
