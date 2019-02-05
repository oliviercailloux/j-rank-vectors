package org.decision_deck.rank_vectors;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.y2018.j_voting.*;

public class PrefKnowledge {

	private ConstraintsOnWeights weights;
	private ImmutableMap<Voter,PartialPreference> profile;
	
	
	public PrefKnowledge(ConstraintsOnWeights weights, ImmutableMap<Voter, PartialPreference> profile) {
		this.weights = weights;
		this.profile = profile;
	}
	
	
}
