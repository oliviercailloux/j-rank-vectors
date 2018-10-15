package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Collections2;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.math.LongMath;

public class RankVectorUtilsTest {

	@Test
	public void testGetRankProfile() {
		final List<Integer> ord123 = ImmutableList.of(1, 2, 3);
		final List<Integer> ord321 = ImmutableList.of(3, 2, 1);
		final List<Integer> ord213 = ImmutableList.of(2, 1, 3);

		final Set<List<Integer>> rankProfile213 = RankVectorsUtils.getRankProfile(ImmutableList.of(ord213));
		assertEquals(ImmutableSet.of(ImmutableList.of(2), ImmutableList.of(1), ImmutableList.of(3)), rankProfile213);

		final Set<List<Integer>> rankProfileTwice213 = RankVectorsUtils
				.getRankProfile(ImmutableList.of(ord213, ord213));
		/** Let’s also check the order. */
		assertEquals(ImmutableList.of(ImmutableList.of(2, 2), ImmutableList.of(1, 1), ImmutableList.of(3, 3)),
				ImmutableList.copyOf(rankProfileTwice213));

		final Set<List<Integer>> rankProfileBig = RankVectorsUtils
				.getRankProfile(ImmutableList.of(ord123, ord123, ord321, ord123, ord213));
		assertEquals(ImmutableSet.of(ImmutableList.of(1, 1, 3, 1, 2), ImmutableList.of(2, 2, 2, 2, 1),
				ImmutableList.of(3, 3, 1, 3, 3)), rankProfileBig);
	}

	@Test
	public void testPermutations() throws Exception {
		final Builder<List<Integer>> builder = ImmutableList.builder();
		builder.add(ImmutableList.of(1));
		builder.add(ImmutableList.of(1, 2, 3, 5));
		builder.add(ImmutableList.of(1, 1, 2, 2));
		builder.add(ImmutableList.of(1, 2, 2, 2, 3, 5, 5));
		final ImmutableList<List<Integer>> rvs = builder.build();
		for (List<Integer> rv : rvs) {
			assertEquals(Collections2.orderedPermutations(rv).size(), RankVectorsUtils.getNbPermutations(rv));
		}
		final ImmutableList<Integer> longRv = ImmutableList
				.copyOf(ContiguousSet.create(Range.closed(1, 20), DiscreteDomain.integers()));
		assertEquals(LongMath.factorial(20), RankVectorsUtils.getNbPermutations(longRv));
	}
}
