package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.BinaryRelation;
import org.decision_deck.utils.relation.BinaryRelationImpl;
import org.decision_deck.utils.relation.graph.mess.GraphUtils;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class PreorderRuleTest {

	@Test
	public void testEquiv() {
		final BinaryRelationImpl<List<Integer>, List<Integer>> preorder = new BinaryRelationImpl<List<Integer>, List<Integer>>();
		final List<Integer> rv1 = ImmutableList.of(1);
		final List<Integer> rv2 = ImmutableList.of(2);
		final List<Integer> rv3 = ImmutableList.of(3);
		final List<Integer> rv4 = ImmutableList.of(4);
		preorder.asPairs().add(Pair.create(rv1, rv2));
		preorder.asPairs().add(Pair.create(rv2, rv1));
		preorder.asPairs().add(Pair.create(rv4, rv1));
		preorder.asPairs().add(Pair.create(rv4, rv2));
		final PreorderRule rule = new PreorderRule(preorder);
		assertEquals(ImmutableSet.of(rv1, rv2, rv3), rule.getWinners(ImmutableSet.of(rv1, rv2, rv3)));
		assertEquals(ImmutableSet.of(rv4, rv3), rule.getWinners(ImmutableSet.of(rv1, rv2, rv3, rv4)));
		assertEquals(ImmutableSet.of(rv4), rule.getWinners(ImmutableSet.of(rv1, rv4, rv2)));
	}

	@Test
	public void testPareto2() {
		final AllRankVectors all = new AllRankVectors(2, 1);
		final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();
		final BinaryRelation<List<Integer>, List<Integer>> pareto = GraphUtils.getTransitiveClosure(paretoNT);
		assertTrue(pareto.asPairs().size() == 1);
		assertTrue(pareto.equals(paretoNT));
		final PreorderRule rule = new PreorderRule(pareto);
		final List<Integer> rv1 = ImmutableList.of(1);
		final List<Integer> rv2 = ImmutableList.of(2);
		assertEquals(ImmutableSet.of(rv1), rule.getWinners(ImmutableSet.of(rv1, rv2)));
	}

	@Test
	public void testParetoM1() {
		final int m = 8;
		final AllRankVectors all = new AllRankVectors(m, 1);
		final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();
		final BinaryRelation<List<Integer>, List<Integer>> pareto = GraphUtils.getTransitiveClosure(paretoNT);
		assertTrue(paretoNT.asPairs().size() == m - 1);
		assertTrue(pareto.asPairs().size() == m * (m - 1) / 2);
		final PreorderRule rule = new PreorderRule(pareto);
		final List<Integer> rv1 = ImmutableList.of(1);
		final List<Integer> rv2 = ImmutableList.of(2);
		final List<Integer> rv5 = ImmutableList.of(5);
		assertEquals(ImmutableSet.of(rv1), rule.getWinners(ImmutableSet.of(rv1, rv2)));
		assertEquals(ImmutableSet.of(rv5), rule.getWinners(ImmutableSet.of(rv5)));
		assertEquals(ImmutableSet.of(rv2), rule.getWinners(ImmutableSet.of(rv5, rv2)));
	}

	@Test
	public void testPareto1() {
		final AllRankVectors all = new AllRankVectors(1, 1);
		final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();
		final BinaryRelation<List<Integer>, List<Integer>> pareto = GraphUtils.getTransitiveClosure(paretoNT);
		assertTrue(pareto.isEmpty());
		final PreorderRule rule = new PreorderRule(pareto);
		final List<Integer> rv = ImmutableList.of(1);
		assertEquals(all.getRankVectors(), rule.getWinners(ImmutableSet.of(rv)));
	}

	@Test
	public void testParetoMN() {
		final int m = 5;
		final int n = 3;
		final AllRankVectors all = new AllRankVectors(m, n);
		final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();
		final BinaryRelation<List<Integer>, List<Integer>> pareto = GraphUtils.getTransitiveClosure(paretoNT);
		final PreorderRule rule = new PreorderRule(pareto);
		final List<Integer> rv122 = ImmutableList.of(1, 2, 2);
		final List<Integer> rv113 = ImmutableList.of(1, 1, 3);
		final List<Integer> rv222 = ImmutableList.of(2, 2, 2);
		assertEquals(ImmutableSet.of(rv122, rv113), rule.getWinners(ImmutableSet.of(rv222, rv122, rv113)));
		assertEquals(ImmutableSet.of(rv122, rv113), rule.getWinners(ImmutableSet.of(rv122, rv113, rv222)));
		assertEquals(ImmutableSet.of(rv122, rv113), rule.getWinners(ImmutableSet.of(rv122, rv113)));
	}

}
