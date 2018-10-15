package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.BinaryRelation;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

public class AllRankVectorsTest {

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void test0() {
	new AllRankVectors(0, 1);
    }

    @SuppressWarnings("boxing")
    @Test
    public void testM1Pareto() {
	final int m = 19;
	final AllRankVectors all = new AllRankVectors(m, 1);
	final BinaryRelation<List<Integer>, List<Integer>> pareto = all.getParetoDominanceNonTransitive();
	assertEquals(m - 1, pareto.asPairs().size());

	final Set<Pair<List<Integer>, List<Integer>>> p = pareto.asPairs();
	int i = 1;
	int startSeq = 1;
	for (Pair<List<Integer>, List<Integer>> pair : p) {
	    final List<Integer> dominator = ImmutableList.of(i);
	    final List<Integer> dominated = ImmutableList.of(i + 1);
	    final Pair<List<Integer>, List<Integer>> exp = Pair.create(dominator, dominated);
	    assertEquals(exp, pair);
	    if (i < m - 1) {
		++i;
	    } else {
		++startSeq;
		i = startSeq;
	    }
	}

    }

    @SuppressWarnings("boxing")
    @Test
    public void testMN() {
	int m = 5;
	int n = 6;
	final AllRankVectors all = new AllRankVectors(m, n);
	final Set<List<Integer>> rvs = all.getRankVectors();
	assertEquals(210, rvs.size());
	final List<Integer> ones = Collections.nCopies(n, 1);
	assertEquals(ones, rvs.iterator().next());
	final ImmutableList<Integer> start = ImmutableList.of(1, 2, 3, 3, 4, 5);
	final ImmutableSortedSet<List<Integer>> gr = all.getGr(start);
	assertFalse(gr.contains(start));

	final ImmutableSet<ImmutableList<Integer>> following = ImmutableSet.of(ImmutableList.of(1, 2, 3, 3, 5, 5),
		ImmutableList.of(1, 2, 3, 4, 4, 4), ImmutableList.of(1, 2, 3, 4, 4, 5));
	assertEquals(following, ImmutableSet.copyOf(Iterables.limit(gr, 3)));
    }

    @SuppressWarnings("boxing")
    @Test
    public void test1N() {
	final int n = 19;
	final AllRankVectors all = new AllRankVectors(1, n);
	final List<Integer> ones = Collections.nCopies(n, 1);
	final ImmutableSortedSet<List<Integer>> rvs = all.getRankVectors();
	assertEquals(ImmutableSet.of(ones), rvs);
	assertTrue(all.getGr(rvs.first()).isEmpty());
    }

    @SuppressWarnings("boxing")
    @Test
    public void test12() {
	final int n = 2;
	final AllRankVectors all = new AllRankVectors(1, n);
	final List<Integer> ones = Collections.nCopies(n, 1);
	final ImmutableSortedSet<List<Integer>> rvs = all.getRankVectors();
	assertEquals(ImmutableSet.of(ones), rvs);
	assertTrue(all.getGr(rvs.first()).isEmpty());
    }

    @SuppressWarnings("boxing")
    @Test
    public void testM1() {
	int m = 15;
	final AllRankVectors all = new AllRankVectors(m, 1);
	final Set<List<Integer>> rvs = all.getRankVectors();
	int i = 1;
	for (List<Integer> rv : rvs) {
	    assertEquals(i, Iterables.getOnlyElement(rv).intValue());
	    ++i;
	}
	assertEquals(m - 5, all.getGr(ImmutableList.of(5)).size());
    }

    @SuppressWarnings("boxing")
    @Test
    public void test1() {
	final AllRankVectors all = new AllRankVectors(1, 1);
	assertEquals(1, all.getM());
	assertEquals(1, all.getN());
	final ImmutableList<Integer> one = ImmutableList.of(1);
	final ImmutableSortedSet<List<Integer>> rvs = all.getRankVectors();
	assertEquals(ImmutableSet.of(one), rvs);
	assertTrue(all.getGr(rvs.first()).isEmpty());
    }

    @Test
    public void test1Pareto() {
	final AllRankVectors all = new AllRankVectors(1, 1);
	final BinaryRelation<List<Integer>, List<Integer>> pareto = all.getParetoDominanceNonTransitive();
	assertTrue(pareto.asPairs().isEmpty());
    }

    @Test
    public void test1NPareto() {
	final int n = 12;
	final AllRankVectors all = new AllRankVectors(1, n);
	final BinaryRelation<List<Integer>, List<Integer>> pareto = all.getParetoDominanceNonTransitive();
	assertTrue(pareto.asPairs().isEmpty());

    }

    @SuppressWarnings("boxing")
    @Test
    public void testMNPareto() {
	final int m = 8;
	final int n = 4;
	final AllRankVectors all = new AllRankVectors(m, n);
	final BinaryRelation<List<Integer>, List<Integer>> pareto = all.getParetoDominanceNonTransitive();
	final ImmutableList<Integer> rv2367 = ImmutableList.of(2, 3, 6, 7);
	final ImmutableList<Integer> rv2377 = ImmutableList.of(2, 3, 7, 7);
	final ImmutableList<Integer> rv2467 = ImmutableList.of(2, 4, 6, 7);
	final ImmutableList<Integer> rv2477 = ImmutableList.of(2, 4, 7, 7);
	final ImmutableList<Integer> rv2222 = ImmutableList.of(2, 2, 2, 2);
	final ImmutableList<Integer> rv2223 = ImmutableList.of(2, 2, 2, 3);
	final ImmutableList<Integer> rv2232 = ImmutableList.of(2, 2, 3, 2);
	final ImmutableList<Integer> rv111m = ImmutableList.of(1, 1, 1, m);
	final ImmutableList<Integer> rv111mp1 = ImmutableList.of(1, 1, 1, m + 1);

	assertTrue(pareto.asPairs().contains(Pair.create(rv2367, rv2377)));
	assertTrue(pareto.asPairs().contains(Pair.create(rv2367, rv2467)));
	assertTrue(pareto.asPairs().contains(Pair.create(rv2467, rv2477)));
	assertFalse(pareto.asPairs().contains(Pair.create(rv2367, rv2477)));
	assertTrue(pareto.asPairs().contains(Pair.create(rv2222, rv2223)));
	assertFalse(pareto.asPairs().contains(Pair.create(rv2222, rv2232)));
	assertFalse(pareto.asPairs().contains(Pair.create(rv111m, rv111mp1)));
    }

}
