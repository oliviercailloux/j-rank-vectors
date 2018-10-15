package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;

import java.util.List;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.Preorder;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FitnessLikelihoodTest {

	@SuppressWarnings({ "static-access", "unused" })
	@Test
	public void testComplex(final @Mocked RankVectorsUtils mock) {
		final Preorder<List<Integer>> preorder = new Preorder<List<Integer>>();
		final ImmutableList<Integer> rv11 = ImmutableList.of(1, 1);
		final ImmutableList<Integer> rv12 = ImmutableList.of(1, 2);
		final ImmutableList<Integer> rv13 = ImmutableList.of(1, 3);
		final ImmutableList<Integer> rv22 = ImmutableList.of(2, 2);
		final ImmutableList<Integer> rv44 = ImmutableList.of(4, 4);
		final ImmutableList<Integer> rv14 = ImmutableList.of(1, 4);
		final ImmutableList<Integer> rv23 = ImmutableList.of(2, 3);
		final ImmutableList<Integer> rv24 = ImmutableList.of(2, 4);
		final ImmutableList<Integer> rv33 = ImmutableList.of(3, 3);
		preorder.addEqTransitive(rv14, rv23);
		preorder.addTransitive(rv12, rv13);
		preorder.addTransitive(rv12, rv22);
		preorder.addTransitive(rv13, rv44);
		preorder.addTransitive(rv22, rv44);
		preorder.addEqTransitive(rv24, rv24);
		preorder.addEqTransitive(rv33, rv33);
		/** preorder: {14=23}, {12} > {13, 22} > {44}, {24}, {33} */
		assert (!preorder.contains(rv13, rv22));
		assert (preorder.contains(rv12, rv44));

		final int m = 4;
		final int n = 2;
		final FitnessLikelihood fitness = new FitnessLikelihood(new AllRankVectors(m, n), preorder,
				new PreorderRule(preorder), false);

		new NonStrictExpectations() {
			{
				mock.getRandomProfile(m, n);
				result = ImmutableSet.of(ImmutableList.of(1, 2), ImmutableList.of(1, 3));
			}
		};
		fitness.setSampleSize(1);
		assertEquals(Pair.create(rv11, rv11), fitness.getFittest());

		new NonStrictExpectations() {
			{
				mock.getRandomProfile(m, n);
				result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2));
			}
		};
		fitness.setSampleSize(1);
		assertEquals(Pair.create(rv13, rv22), fitness.getFittest());

		new NonStrictExpectations() {
			{
				mock.getRandomProfile(m, n);
				result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 3));
			}
		};
		fitness.setSampleSize(1);
		assertEquals(Pair.create(rv11, rv11), fitness.getFittest());

		new NonStrictExpectations() {
			{
				mock.getRandomProfile(m, n);
				result = ImmutableSet.of(ImmutableList.of(1, 2), ImmutableList.of(1, 3));
				result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2));
				result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 3));
			}
		};
		fitness.setSampleSize(3);
		assertEquals(Pair.create(rv13, rv22), fitness.getFittest());

		new NonStrictExpectations() {
			{
				mock.getRandomProfile(m, n);
				result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 3), ImmutableList.of(1, 3),
						ImmutableList.of(2, 2));
			}
		};
		fitness.setSampleSize(1);
		assertEquals(Pair.create(rv13, rv14), fitness.getFittest());

		new NonStrictExpectations() {
			{
				mock.getRandomProfile(m, n);
				result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 3), ImmutableList.of(1, 2),
						ImmutableList.of(1, 3));
			}
		};
		fitness.setSampleSize(1);
		assertEquals(Pair.create(rv12, rv14), fitness.getFittest());

		new NonStrictExpectations() {
			{
				mock.getRandomProfile(m, n);
				result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(2, 4),
						ImmutableList.of(3, 3));
				result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(1, 4));
				result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(4, 4));
				result = ImmutableSet.of(ImmutableList.of(2, 4), ImmutableList.of(1, 2), ImmutableList.of(4, 4));
				result = ImmutableSet.of(ImmutableList.of(2, 4), ImmutableList.of(3, 3), ImmutableList.of(1, 4));
			}
		};
		fitness.setSampleSize(5);
		assertEquals(Pair.create(rv13, rv22), fitness.getFittest());
	}

}
