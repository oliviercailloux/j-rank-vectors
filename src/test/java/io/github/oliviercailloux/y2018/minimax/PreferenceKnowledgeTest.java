package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

class PreferenceKnowledgeTest {

	@Test
	void testLambdaRange() throws Exception {
		final PrefKnowledge k = PrefKnowledge.given(getAlternatives(5), getVoters(1));
		final Range<Aprational> startRange = k.getLambdaRange(1);
		assertEquals(1d, startRange.lowerEndpoint().doubleValue());
		final Aprational startUpper = startRange.upperEndpoint();
		final Apint ap1 = new Apint(1);
		final Apint ap2 = new Apint(2);
		final Apint ap3 = new Apint(3);
		k.addConstraint(1, ComparisonOperator.GE, ap2);
		assertEquals(Range.closed(ap2, startUpper), k.getLambdaRange(1));
		k.addConstraint(1, ComparisonOperator.LE, ap3);
		assertEquals(Range.closed(ap2, ap3), k.getLambdaRange(1));
		k.addConstraint(1, ComparisonOperator.LE, ap2);
		assertEquals(Range.closed(ap2, ap2), k.getLambdaRange(1));
		k.addConstraint(1, ComparisonOperator.LE, ap3);
		assertEquals(Range.closed(ap2, ap2), k.getLambdaRange(1));
		assertThrows(IllegalArgumentException.class, () -> k.addConstraint(1, ComparisonOperator.LE, ap1));
	}

	private ImmutableSet<Alternative> getAlternatives(int m) {
		final ImmutableSet.Builder<Alternative> builder = ImmutableSet.builder();
		for (int i = 1; i <= m; ++i) {
			builder.add(new Alternative(i));
		}
		return builder.build();
	}

	private ImmutableSet<Voter> getVoters(int n) {
		final ImmutableSet.Builder<Voter> builder = ImmutableSet.builder();
		for (int i = 1; i <= n; ++i) {
			builder.add(new Voter(i));
		}
		return builder.build();
	}

}
