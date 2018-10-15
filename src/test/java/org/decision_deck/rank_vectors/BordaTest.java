package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class BordaTest {

	@SuppressWarnings("boxing")
	@Test
	public void testScore() {
		final Borda borda = new Borda(5);
		final ImmutableList<Integer> rv12 = ImmutableList.of(1, 2);
		final ImmutableList<Integer> rv13 = ImmutableList.of(1, 3);
		final ImmutableList<Integer> rv22 = ImmutableList.of(2, 2);
		final ImmutableList<Integer> rv4 = ImmutableList.of(4);
		final ImmutableList<Integer> rv5 = ImmutableList.of(5);
		final ImmutableList<Integer> rv5554 = ImmutableList.of(5, 5, 5, 4);
		final ImmutableList<Integer> rv5455 = ImmutableList.of(5, 4, 5, 5);
		final ImmutableList<Integer> rv3333 = ImmutableList.of(3, 3, 3, 3);
		final ImmutableList<Integer> rv3134 = ImmutableList.of(3, 1, 3, 4);
		assertTrue(borda.compare(rv12, rv13) > 0);
		assertTrue(borda.compare(rv12, rv22) > 0);
		assertTrue(borda.compare(rv22, rv13) == 0);
		assertTrue(borda.compare(rv4, rv5) > 0);
		assertTrue(borda.compare(rv5, rv5) == 0);
		assertTrue(borda.compare(rv5, rv5) == 0);
		assertTrue(borda.compare(rv5554, rv5455) == 0);
		assertTrue(borda.compare(rv3333, rv5455) > 0);
		assertTrue(borda.compare(rv3134, rv3333) > 0);
	}

}
