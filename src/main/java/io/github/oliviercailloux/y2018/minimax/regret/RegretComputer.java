package io.github.oliviercailloux.y2018.minimax.regret;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.graph.ImmutableGraph;

import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsBuilder;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;
import io.github.oliviercailloux.y2018.minimax.PrefKnowledge;

public class RegretComputer {
	private final PrefKnowledge knowledge;

	public RegretComputer(PrefKnowledge knowledge) {
		this.knowledge = requireNonNull(knowledge);
	}

	public PairwiseMaxRegret getPMR(Alternative x) {
		final ImmutableSet<Alternative> alternatives = knowledge.getAlternatives();
		final ImmutableSet<Voter> voters = knowledge.getVoters();
		final int m = alternatives.size();

		final ImmutableMap<Voter, Integer> ranksOfX;
		final ImmutableSortedMultiset<Integer> multiSetOfRanksOfX;
		{
			final ImmutableMap.Builder<Voter, Integer> ranksOfXBuilder = ImmutableMap.builder();
			for (Voter voter : voters) {
				final int nbWeaklyLessGoodThanX = knowledge.getPartialPreference(voter).asTransitiveGraph()
						.successors(x).size();
				assert 1 <= nbWeaklyLessGoodThanX && nbWeaklyLessGoodThanX <= m;
				final int nbNotWeaklyLessGoodThanX = m - nbWeaklyLessGoodThanX;
				final int rankX = 1 + nbNotWeaklyLessGoodThanX;
				assert 1 <= rankX && rankX <= m;
				ranksOfXBuilder.put(voter, rankX);
			}
			ranksOfX = ranksOfXBuilder.build();
			multiSetOfRanksOfX = ImmutableSortedMultiset.copyOf(ranksOfX.values());
		}

		for (Alternative y : alternatives) {
			if (y.equals(x)) {
				continue;
			}
			final ImmutableMap<Voter, Integer> ranksOfY;
			{
				final ImmutableMap.Builder<Voter, Integer> ranksOfYBuilder = ImmutableMap.builder();
				for (Voter voter : voters) {
					final ImmutableGraph<Alternative> preference = knowledge.getPartialPreference(voter)
							.asTransitiveGraph();
					final int nbStrictlyBetterThanY = preference.predecessors(y).size() - 1;
					assert 0 <= nbStrictlyBetterThanY && nbStrictlyBetterThanY <= m - 1;
					final int beta;
					if (preference.hasEdgeConnecting(x, y)) {
						final int nbIndifferentToX = m - preference.adjacentNodes(x).size();
						/** TODO check */
						assert preference.adjacentNodes(x).contains(x);
						assert 0 <= nbIndifferentToX && nbIndifferentToX <= m - 1;
						beta = nbIndifferentToX;
					} else {
						beta = 0;
					}
					final int rankY = 1 + nbStrictlyBetterThanY + beta;
					assert 1 <= rankY && rankY <= m;
					ranksOfYBuilder.put(voter, rankY);
				}
				ranksOfY = ranksOfYBuilder.build();
			}
			final ImmutableSortedMultiset<Integer> multiSetOfRanksOfY = ImmutableSortedMultiset
					.copyOf(ranksOfY.values());

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
			final PairwiseMaxRegret pmrY = PairwiseMaxRegret.given(x, y, ranksOfX, ranksOfY,
					knowledge.getConstraintsOnWeights().getLastSolution());
		}
	}
}
