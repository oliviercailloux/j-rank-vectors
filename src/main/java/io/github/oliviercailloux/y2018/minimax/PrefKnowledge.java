package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apfloat.Apint;
import org.apfloat.Aprational;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class PrefKnowledge {
	public static PrefKnowledge given(Set<Alternative> alternatives, Set<Voter> voters) {
		return new PrefKnowledge(alternatives, voters);
	}

	private final ImmutableMap<Voter, VoterPartialPreference> partialProfile;
	private final ConstraintsOnWeights cow;
	private final Map<Integer, Range<Aprational>> lambdaRanges;

	private PrefKnowledge(Set<Alternative> alternatives, Set<Voter> voters) {
		final int m = alternatives.size();
		final int n = voters.size();

		checkArgument(m >= 1);
		checkArgument(n >= 1);

		cow = ConstraintsOnWeights.withRankNumber(m);
		cow.setConvexityConstraint();

		final ImmutableMap.Builder<Voter, VoterPartialPreference> builder = ImmutableMap.builder();
		for (Voter voter : voters) {
			builder.put(voter, VoterPartialPreference.about(voter, alternatives));
		}
		partialProfile = builder.build();

		if (m == 1) {
			lambdaRanges = null;
		} else {
			lambdaRanges = new LinkedHashMap<>(m - 2);
			for (int rank = 1; rank <= m - 2; ++rank) {
				assert m - 2 >= 1;
				/** TODO check default upper bound. */
				lambdaRanges.put(rank, Range.closed(new Apint(1), new Apint(n * (m - 2))));
			}
		}
	}

	public void addConstraint(int rank, ComparisonOperator op, Aprational lambda) {
		checkArgument(rank >= 1);
		checkArgument(rank <= getM() - 2);
		cow.addConstraint(rank, op, lambda.doubleValue());

		/** The constraint is that D_i/D_{i+1} OP lambda. */
		final Range<Aprational> providedRange;
		switch (op) {
		case EQ:
			providedRange = Range.closed(lambda, lambda);
			break;
		case GE:
			providedRange = Range.atLeast(lambda);
			break;
		case LE:
			providedRange = Range.atMost(lambda);
			break;
		default:
			throw new AssertionError();
		}
		final Range<Aprational> existingRange = lambdaRanges.get(rank);
		checkArgument(existingRange.isConnected(providedRange),
				"The provided constraint makes the program infeasible.");
		final Range<Aprational> restr = existingRange.intersection(providedRange);
		checkArgument(!restr.isEmpty(), "The provided constraint makes the program (just) infeasible.");
		lambdaRanges.put(rank, restr);
	}

	public ImmutableMap<Voter, VoterPartialPreference> getProfile() {
		return partialProfile;
	}

	public ConstraintsOnWeights getConstraintsOnWeights() {
		return cow;
	}

	public Range<Aprational> getLambdaRange(int rank) {
		checkArgument(rank >= 1);
		checkArgument(rank <= getM() - 2);
		return lambdaRanges.get(rank);
	}

	/**
	 * @return at least one.
	 */
	public int getM() {
		return cow.getM();
	}

	/**
	 * @return at least one.
	 */
	public int getN() {
		return partialProfile.size();
	}

}
