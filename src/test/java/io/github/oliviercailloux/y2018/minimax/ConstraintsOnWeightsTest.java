package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsBuilder;

public class ConstraintsOnWeightsTest {
	@Test
	void testOneC() throws Exception {
		final ConstraintsOnWeights cow = ConstraintsOnWeights.withRankNumber(3);
		/** (w1 − w2) ≥ 3(w2 − w3) thus w1 + 3 w3 ≥ 4 w2 thus w2 ≤ 1/4. **/
		cow.addConstraint(1, ComparisonOperator.GE, 3d);
		assertThrows(IllegalArgumentException.class, () -> cow.getRange(0));
		assertEquals(Range.closed(1d, 1d), cow.getRange(1));
		assertEquals(Range.closed(0d, 0.25d), cow.getRange(2));
		assertEquals(Range.closed(0d, 0d), cow.getRange(3));
	}
	
	
	@Test
	void testMax() throws Exception {
		final ConstraintsOnWeights cow = ConstraintsOnWeights.withRankNumber(5);
		SumTermsBuilder sb=SumTerms.builder();
		sb.add(cow.getTerm(1d, 1));
		sb.add(cow.getTerm(-2d, 3));
		sb.add(cow.getTerm(1d, 5));
		assertEquals(1d,cow.maximize(sb.build()),0d);
	}
	
	
	
}
