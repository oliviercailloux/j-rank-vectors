package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class WeakOrderRuleTest {

    @Test
    public void testBorda() {
	final WeakOrderRule bordaRule = new WeakOrderRule(new Borda());
	final List<Integer> rv12 = ImmutableList.of(1, 2);
	final List<Integer> rv13 = ImmutableList.of(1, 3);
	final List<Integer> rv22 = ImmutableList.of(2, 2);
	final List<Integer> rv4 = ImmutableList.of(4);
	final List<Integer> rv5 = ImmutableList.of(5);
	final List<Integer> rv5554 = ImmutableList.of(5, 5, 5, 4);
	final List<Integer> rv5455 = ImmutableList.of(5, 4, 5, 5);
	final List<Integer> rv3333 = ImmutableList.of(3, 3, 3, 3);
	final List<Integer> rv3134 = ImmutableList.of(3, 1, 3, 4);
	final List<Integer> rv3234 = ImmutableList.of(3, 2, 3, 4);

	assertEquals(ImmutableSet.of(rv12), bordaRule.getWinners(ImmutableSet.of(rv12, rv13, rv22)));
	assertEquals(ImmutableSet.of(rv13, rv22), bordaRule.getWinners(ImmutableSet.of(rv13, rv22)));
	assertEquals(ImmutableSet.of(rv13), bordaRule.getWinners(ImmutableSet.of(rv13)));
	assertEquals(ImmutableSet.of(rv4), bordaRule.getWinners(ImmutableSet.of(rv4, rv5)));
	assertEquals(ImmutableSet.of(rv3333, rv3234),
		bordaRule.getWinners(ImmutableSet.of(rv5554, rv5455, rv3333, rv3234)));
	assertEquals(ImmutableSet.of(rv3134),
		bordaRule.getWinners(ImmutableSet.of(rv5554, rv5455, rv3333, rv3134)));
    }



}
