package io.github.oliviercailloux.y2018.minimax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class XPRunnerTest {

	@Test
	public void testGenWeights() {
		int m = 4;

		//final PSRWeights weights = XPRunner.genWeights(m);
		//System.out.println(weights);
	}
	
	
	@Test
	public void testGenProfile() {
		int m = 3;
		int n = 2;

		final Map<Voter, VoterStrictPreference> rv = XPRunner.genProfile(n, m);
		for (int i = 1; i <= n; i++) {
			Voter v = new Voter(i);
			System.out.println(rv.get(v));
		}

//		final AllRankVectors all = new AllRankVectors(m, n);
//		final Set<List<Integer>> rvs = all.getRankVectors();
//		assertEquals(210, rvs.size());
//		final List<Integer> ones = Collections.nCopies(n, 1);
//		assertEquals(ones, rvs.iterator().next());
//		final ImmutableList<Integer> start = ImmutableList.of(1, 2, 3, 3, 4, 5);
//		final ImmutableSortedSet<List<Integer>> gr = all.getGr(start);
//		assertFalse(gr.contains(start));
//
//		final ImmutableSet<ImmutableList<Integer>> following = ImmutableSet.of(ImmutableList.of(1, 2, 3, 3, 5, 5),
//				ImmutableList.of(1, 2, 3, 4, 4, 4), ImmutableList.of(1, 2, 3, 4, 4, 5));
//		assertEquals(following, ImmutableSet.copyOf(Iterables.limit(gr, 3)));
	}

}
