package io.github.oliviercailloux.y2018.minimax.regret;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;
import io.github.oliviercailloux.y2018.minimax.PSRWeights;
import io.github.oliviercailloux.y2018.minimax.PrefKnowledge;

class RegretComputerTest {

	@Test
	void testEmptyKSizeOne() {
		final Alternative a = new Alternative(1);
		final Voter v1 = new Voter(1);
		final PrefKnowledge k = PrefKnowledge.given(ImmutableSet.of(a), ImmutableSet.of(v1));
		final RegretComputer regretComputer = new RegretComputer(k);
		final ImmutableMap<Voter, Integer> allFirst = ImmutableMap.of(v1, 1);
		final PairwiseMaxRegret pmrAVsA = PairwiseMaxRegret.given(a, a, allFirst, allFirst,
				PSRWeights.given(ImmutableList.of(1d)));

		assertEquals(ImmutableSet.of(pmrAVsA), regretComputer.getPaiwiseMaxRegrets(a));
	}

	@Test
	void testEmptyKSizeTwo() {
		final Alternative a = new Alternative(1);
		final Alternative b = new Alternative(2);
		final Voter v1 = new Voter(1);
		final Voter v2 = new Voter(2);
		final PrefKnowledge k = PrefKnowledge.given(ImmutableSet.of(a, b), ImmutableSet.of(v1, v2));
		final RegretComputer regretComputer = new RegretComputer(k);
		final ImmutableMap<Voter, Integer> allSecond = ImmutableMap.of(v1, 2, v2, 2);
		final ImmutableMap<Voter, Integer> allFirst = ImmutableMap.of(v1, 1, v2, 1);
		final PairwiseMaxRegret pmrAVsB = PairwiseMaxRegret.given(a, b, allSecond, allFirst,
				PSRWeights.given(ImmutableList.of(1d, 0d)));
		final PairwiseMaxRegret pmrBVsA = PairwiseMaxRegret.given(b, a, allSecond, allFirst,
				PSRWeights.given(ImmutableList.of(1d, 0d)));

		assertEquals(ImmutableSet.of(pmrAVsB), regretComputer.getPaiwiseMaxRegrets(a));
		assertEquals(ImmutableSet.of(pmrBVsA), regretComputer.getPaiwiseMaxRegrets(b));
	}

}
