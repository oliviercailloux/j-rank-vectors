package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class XPRunner {

	public static Map<Voter, VoterStrictPreference> genProfile(int nbVoters, int nbAlternatives) {
		checkArgument(nbVoters >= 1);
		checkArgument(nbAlternatives >= 1);
		Map<Voter, VoterStrictPreference> profile = new HashMap<>();

		List<Alternative> availableRanks = new LinkedList<>();
		for (int i = 1; i <= nbAlternatives; i++) {
			availableRanks.add(new Alternative(i));
		}

		for (int i = 1; i <= nbVoters; ++i) {
			Voter v = new Voter(i);
			List<Alternative> linearOrder = Lists.newArrayList(availableRanks);
			Collections.shuffle(linearOrder);
			VoterStrictPreference pref = new VoterStrictPreference(v, linearOrder);
			profile.put(v, pref);
		}

		return profile;
	}

}
