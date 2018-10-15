package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.List;
import java.util.Set;

import org.decision_deck.rank_vectors.RuleComparerNbWinners.RandomProfilesGenerator;
import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.Preorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;

/**
 * The relation is guaranteed to be never empty, reflexive and transitive.
 * 
 * @author Olivier Cailloux
 * 
 */
public class FitnessLikelihood implements Fitness {

	private final AllRankVectors m_rvs;

	private RankBasedVotingRule m_rule;
	private Preorder<List<Integer>> m_preorder;

	private boolean m_plus;

	public FitnessLikelihood(AllRankVectors rvs, Preorder<List<Integer>> preorder, RankBasedVotingRule rule,
			boolean plus) {
		m_plus = plus;
		checkArgument(rvs != null);
		checkNotNull(preorder);
		checkNotNull(rule);
		m_rvs = rvs;
		m_preorder = preorder;
		m_rule = rule;
		m_sampleSize = m_plus ? FitnessType.LIKELIHOOD_PLUS_SAMPLE : 1000;
	}

	@Override
	public String toString() {
		return getType().toString();
	}

	public FitnessType getType() {
		return m_plus ? FitnessType.LIKELIHOOD_PLUS : FitnessType.LIKELIHOOD;
	}

	@Override
	public Pair<List<Integer>, List<Integer>> getFittest() {
		final AbstractCollection<Set<List<Integer>>> testProfiles = new RandomProfilesGenerator(m_rvs.getM(),
				m_rvs.getN(), m_sampleSize);
		final Multiset<Pair<List<Integer>, List<Integer>>> togetherWinners = LinkedHashMultiset.create();
		for (Set<List<Integer>> profile : testProfiles) {
			final Set<List<Integer>> winners = m_rule.getWinners(profile);
			final Ordering<Iterable<Integer>> lex = Ordering.natural().lexicographical();
			final ImmutableSortedSet<List<Integer>> winnersOrdered = ImmutableSortedSet.copyOf(lex, winners);
			assert (winnersOrdered.size() >= 1);
			for (List<Integer> winner : winnersOrdered) {
				final ImmutableSortedSet<List<Integer>> greaterWinners = winnersOrdered.tailSet(winner, false);
				for (List<Integer> greaterWinner : greaterWinners) {
					final Pair<List<Integer>, List<Integer>> togetherWin = Pair.create(winner, greaterWinner);
					if (m_preorder.contains(winner, greaterWinner)) {
						assert (m_preorder.contains(greaterWinner, winner));
					} else {
						assert (!m_preorder.contains(greaterWinner, winner));
						togetherWinners.add(togetherWin);
					}
				}
			}
			// assert (togetherWinners.size() == winnersOrdered.size() *
			// (winnersOrdered.size() - 1) / 2);
		}
		final Ordering<Pair<List<Integer>, List<Integer>>> orderByNbOccurences = Ordering.natural()
				.onResultOf(new Function<Pair<List<Integer>, List<Integer>>, Integer>() {
					@Override
					public Integer apply(Pair<List<Integer>, List<Integer>> input) {
						return togetherWinners.count(input);
					}
				});
		if (togetherWinners.isEmpty()) {
			return Pair.create(m_rvs.getRankVectors().first(), m_rvs.getRankVectors().first());
		}
		return orderByNbOccurences.max(togetherWinners);
	}

	@SuppressWarnings({ "unused", "all" })
	private static final Logger s_logger = LoggerFactory.getLogger(FitnessLikelihood.class);

	private int m_sampleSize;

	public int getSampleSize() {
		return m_sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		checkArgument(sampleSize >= 1);
		m_sampleSize = sampleSize;
	}
}
