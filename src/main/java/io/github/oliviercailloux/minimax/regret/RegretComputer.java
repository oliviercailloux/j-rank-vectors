package io.github.oliviercailloux.minimax.regret;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedMultiset;
import com.google.common.graph.ImmutableGraph;

import io.github.oliviercailloux.j_voting.VoterPartialPreference;
import io.github.oliviercailloux.j_voting.elicitation.PrefKnowledge;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsBuilder;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class RegretComputer {
	private final PrefKnowledge knowledge;

	public RegretComputer(PrefKnowledge knowledge) {
		this.knowledge = requireNonNull(knowledge);
	}

	private ImmutableSet<PairwiseMaxRegret> getHighestPairwiseMaxRegrets(Alternative x) {
		assert knowledge.getAlternatives().contains(x);

		final ImmutableMap<Voter, Integer> ranksOfX = getWorstRanksOfX(x);
		final ImmutableSortedMultiset<Integer> multiSetOfRanksOfX = ImmutableSortedMultiset.copyOf(ranksOfX.values());

		final SetMultimap<Double, PairwiseMaxRegret> pmrs = MultimapBuilder.treeKeys().linkedHashSetValues().build();
		for (Alternative y : knowledge.getAlternatives()) {
			final PairwiseMaxRegret pmrY = getPmr(x, y, ranksOfX, multiSetOfRanksOfX);
			pmrs.put(pmrY.getPmrValue(), pmrY);
		}

		final SortedMap<Double, Collection<PairwiseMaxRegret>> sortedPmrs = (SortedMap<Double, Collection<PairwiseMaxRegret>>) pmrs
				.asMap();
		assert !sortedPmrs.isEmpty();
		final double highestRegret = sortedPmrs.lastKey();
		assert highestRegret >= 0;
		return ImmutableSet.copyOf(pmrs.get(highestRegret));
	}

	public SetMultimap<Alternative, PairwiseMaxRegret> getMinimalMaxRegrets() {
		final ImmutableSet<Alternative> alternatives = knowledge.getAlternatives();
		final SetMultimap<Double, Alternative> byPmrValues = MultimapBuilder.treeKeys().linkedHashSetValues().build();
		final SetMultimap<Alternative, PairwiseMaxRegret> pmrValues = MultimapBuilder.hashKeys().linkedHashSetValues()
				.build();
		for (Alternative x : alternatives) {
			final ImmutableSet<PairwiseMaxRegret> highestPairwiseMaxRegrets = getHighestPairwiseMaxRegrets(x);
			byPmrValues.put(highestPairwiseMaxRegrets.iterator().next().getPmrValue(), x);
			pmrValues.putAll(x, highestPairwiseMaxRegrets);
		}
		assert pmrValues.keySet().size() == alternatives.size();

		final SortedMap<Double, Collection<Alternative>> sortedByPmrValues = (SortedMap<Double, Collection<Alternative>>) byPmrValues
				.asMap();
		final double minMaxPmrValue = sortedByPmrValues.firstKey();
		final Collection<Alternative> alternativesWithMinPmrValue = sortedByPmrValues.get(minMaxPmrValue);
		pmrValues.asMap().keySet().retainAll(alternativesWithMinPmrValue);
		/** We check that each of these collections of PMRs have the same value. */
		assert pmrValues.asMap().entrySet().stream()
				.allMatch((e) -> (e.getValue().stream().map(PairwiseMaxRegret::getPmrValue).distinct().count() == 1));
		return pmrValues;
	}

	private PairwiseMaxRegret getPmr(Alternative x, Alternative y, Map<Voter, Integer> ranksOfX,
			SortedMultiset<Integer> multiSetOfRanksOfX) {
		final PairwiseMaxRegret pmrY;
		final int m = knowledge.getAlternatives().size();
		final ImmutableMap<Voter, Integer> ranksOfY = getBestRanksOfY(x, y);
		final ImmutableSortedMultiset<Integer> multiSetOfRanksOfY = ImmutableSortedMultiset.copyOf(ranksOfY.values());

		final SumTermsBuilder builder = SumTerms.builder();
		for (int r = 1; r <= m; ++r) {
			final int coefY = multiSetOfRanksOfY.count(r);
			final int coefX = multiSetOfRanksOfX.count(r);
			final int coef = coefY - coefX;
			if (coef != 0) {
				final Term term = knowledge.getConstraintsOnWeights().getTerm(coef, r);
				builder.add(term);
			}
		}
		final double pmr = knowledge.getConstraintsOnWeights().maximize(builder.build());
		pmrY = PairwiseMaxRegret.given(x, y, ranksOfX, ranksOfY, knowledge.getConstraintsOnWeights().getLastSolution());
		assert Math.abs(pmrY.getPmrValue() - pmr) <= 1e-3;
		return pmrY;
	}

	private ImmutableMap<Voter, Integer> getWorstRanksOfX(Alternative x) {
		final ImmutableMap<Voter, Integer> ranksOfX;
		final ImmutableMap.Builder<Voter, Integer> ranksOfXBuilder = ImmutableMap.builder();
		for (Voter voter : knowledge.getVoters()) {
			final int rankX = getWorstRankOfX(x, knowledge.getPartialPreference(voter));
			ranksOfXBuilder.put(voter, rankX);
		}
		ranksOfX = ranksOfXBuilder.build();
		return ranksOfX;
	}

	private int getWorstRankOfX(Alternative x, VoterPartialPreference partialPreference) {
		final ImmutableGraph<Alternative> transitivePreference = partialPreference.asTransitiveGraph();
		final int m = transitivePreference.nodes().size();
		/** +1 because x itself is to be counted. */
		final int nbWeaklyLessGoodThanX = transitivePreference.successors(x).size() + 1;
		assert 1 <= nbWeaklyLessGoodThanX && nbWeaklyLessGoodThanX <= m;
		final int nbNotWeaklyLessGoodThanX = m - nbWeaklyLessGoodThanX;
		final int rankX = 1 + nbNotWeaklyLessGoodThanX;
		assert 1 <= rankX && rankX <= m;
		return rankX;
	}

	private ImmutableMap<Voter, Integer> getBestRanksOfY(Alternative x, Alternative y) {
		final ImmutableMap<Voter, Integer> ranksOfY;
		final ImmutableMap.Builder<Voter, Integer> ranksOfYBuilder = ImmutableMap.builder();
		for (Voter voter : knowledge.getVoters()) {
			final int rankY = getBestRankOfY(x, y, knowledge.getPartialPreference(voter));
			ranksOfYBuilder.put(voter, rankY);
		}
		ranksOfY = ranksOfYBuilder.build();
		return ranksOfY;
	}

	private int getBestRankOfY(Alternative x, Alternative y, VoterPartialPreference partialPreference) {
		final ImmutableGraph<Alternative> transitivePreference = partialPreference.asTransitiveGraph();
		final int m = transitivePreference.nodes().size();
		final int nbStrictlyBetterThanY = transitivePreference.predecessors(y).size();
		assert 0 <= nbStrictlyBetterThanY && nbStrictlyBetterThanY <= m - 1;
		final int beta;
		if (transitivePreference.hasEdgeConnecting(x, y) || x.equals(y)) {
			/** −1 because x is indifferent to x. */
			final int nbIndifferentToX = m - transitivePreference.adjacentNodes(x).size() - 1;
			assert 0 <= nbIndifferentToX && nbIndifferentToX <= m - 1;
			beta = nbIndifferentToX;
		} else {
			beta = 0;
		}
		final int rankY = 1 + nbStrictlyBetterThanY + beta;
		assert 1 <= rankY && rankY <= m;
		return rankY;
	}
}
