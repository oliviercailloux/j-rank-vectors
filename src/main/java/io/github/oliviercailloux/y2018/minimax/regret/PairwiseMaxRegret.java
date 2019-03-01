package io.github.oliviercailloux.y2018.minimax.regret;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;
import io.github.oliviercailloux.y2018.minimax.PSRWeights;
import io.github.oliviercailloux.y2018.minimax.VoterStrictPreference;

/**
 * Immutable.
 *
 * @author Olivier Cailloux
 *
 */
public class PairwiseMaxRegret {
	private Alternative x;

	private PairwiseMaxRegret(Alternative x, Alternative y, Map<Voter, Integer> ranksOfX, Map<Voter, Integer> ranksOfY,
			PSRWeights weights) {
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		this.ranksOfX = ImmutableMap.copyOf(requireNonNull(ranksOfX));
		this.ranksOfY = ImmutableMap.copyOf(requireNonNull(ranksOfY));
		this.weights = requireNonNull(weights);
		pMRValue = getScore(ranksOfY, weights) - getScore(ranksOfX, weights);
	}

	public static double getScore(Map<Voter, Integer> ranks, PSRWeights weights) {
		return ranks.values().stream().mapToDouble(weights::getWeightAtRank).sum();
	}

	public static double getScore(Alternative alternative, Map<Voter, VoterStrictPreference> profile,
			PSRWeights weights) {
		return profile.values().stream().mapToDouble((v) -> getScore(alternative, v, weights)).sum();
	}

	public Alternative getX() {
		return x;
	}

	public Alternative getY() {
		return y;
	}

	public ImmutableMap<Voter, Integer> getRanksOfX() {
		return ranksOfX;
	}

	public ImmutableMap<Voter, Integer> getRanksOfY() {
		return ranksOfY;
	}

	public PSRWeights getWeights() {
		return weights;
	}

	public double getPMRValue() {
		return pMRValue;
	}

	public static double getScore(Alternative alternative, VoterStrictPreference v, PSRWeights weights) {
		final int rank = v.getAlternativeRank(alternative);
		return weights.getWeightAtRank(rank);
	}

	public static PairwiseMaxRegret given(Alternative x, Alternative y, Map<Voter, Integer> ranksOfX,
			Map<Voter, Integer> ranksOfY, PSRWeights weights) {
		return new PairwiseMaxRegret(x, y, ranksOfX, ranksOfY, weights);
	}

	private Alternative y;
	private ImmutableMap<Voter, Integer> ranksOfX;
	private ImmutableMap<Voter, Integer> ranksOfY;
	private PSRWeights weights;
	private double pMRValue;
}
