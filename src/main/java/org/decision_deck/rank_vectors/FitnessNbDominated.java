package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.Preorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * The relation is guaranteed to be never empty, reflexive and transitive.
 * 
 * @author Olivier Cailloux
 * 
 */
public class FitnessNbDominated implements Fitness {

	private final AllRankVectors m_rvs;

	/**
	 * Invalid iff dirty. When valid, for a given rv x: R(x). As the relation is
	 * reflexive, that is never empty. The complement of this set are the rvs
	 * incomparable to it or strictly better than it.
	 */
	private final Map<List<Integer>, Set<List<Integer>>> m_dominated;

	private Preorder<List<Integer>> m_preorder;

	private final boolean m_optimistic;

	public FitnessNbDominated(AllRankVectors rvs, Preorder<List<Integer>> preorder, boolean optimistic,
			boolean weighted) {
		if (weighted) {
			checkArgument(rvs.getN() <= 20, "Problem too large: some permutations will not fit a Long.");
		}
		m_optimistic = optimistic;
		checkNotNull(preorder);
		checkArgument(rvs != null);
		m_preorder = preorder;
		m_rvs = rvs;
		m_dominated = Maps.newLinkedHashMap();
		m_weighted = weighted;
		putDiagonal();
		if (weighted) {
			countPermutations();
		}
	}

	private void countPermutations() {
		for (List<Integer> rv : m_rvs.getRankVectors()) {
			assert (rv.size() == m_rvs.getN());
			m_nbPermutations.put(rv, RankVectorsUtils.getNbPermutations(rv));
		}
	}

	void updateConnexions() {
		final ImmutableSortedSet<List<Integer>> all = m_rvs.getRankVectors();
		for (List<Integer> rv1 : all) {
			final Builder<List<Integer>> conn = ImmutableSet.builder();
			for (List<Integer> rv2 : all) {
				if (m_preorder.contains(rv1, rv2) && !m_preorder.contains(rv2, rv1)) {
					conn.add(rv2);
				}
			}
			m_dominated.put(rv1, conn.build());
		}
	}

	/**
	 * <p>
	 * Connexions must be up to date.
	 * </p>
	 * <p>
	 * Retrieves the fitness of the given pair.
	 * </p>
	 * 
	 * @param x a member of this relation.
	 * @param y a member of this relation.
	 * @return ≥ 0.
	 */
	long getFitness(List<Integer> x, List<Integer> y) {
		checkArgument(m_preorder.contains(x, x));
		checkArgument(m_preorder.contains(y, y));
		if (m_preorder.contains(x, y) || m_preorder.contains(y, x)) {
			return 0;
		}
		final SetView<List<Integer>> domX = Sets.difference(m_dominated.get(x), m_dominated.get(y));
		final SetView<List<Integer>> domY = Sets.difference(m_dominated.get(y), m_dominated.get(x));
		final long fitSideX;
		final long fitSideY;

		if (m_weighted) {
			long fitSideXCount = 0;
			for (List<Integer> rv : domX) {
				fitSideXCount += m_nbPermutations.get(rv);
			}
			fitSideX = fitSideXCount;
			long fitSideYCount = 0;
			for (List<Integer> rv : domY) {
				fitSideYCount += m_nbPermutations.get(rv);
			}
			fitSideY = fitSideYCount;
		} else {
			fitSideX = domX.size();
			fitSideY = domY.size();
		}

		final long fit;
		if (m_optimistic) {
			fit = fitSideX + fitSideY;
		} else {
			fit = Math.min(fitSideX, fitSideY);
		}
		return fit;
	}

	/**
	 * Retrieves a maximally fitted pair. Semantically, the returned pair is
	 * unordered, but it is ordered so that the first element is lower than or equal
	 * to the second, lexicographically.
	 * 
	 * @return not <code>null</code>.
	 */
	@Override
	public Pair<List<Integer>, List<Integer>> getFittest() {
		updateConnexions();
		long fit = -1;
		if (m_rvs.getRankVectors().size() == 1) {
			final List<Integer> uniqueRv = m_rvs.getRankVectors().iterator().next();
			return Pair.create(uniqueRv, uniqueRv);
		}

		assert (m_rvs.getM() > 1 || m_rvs.getN() > 1);

		Pair<List<Integer>, List<Integer>> fittest = null;
		for (List<Integer> rv1 : m_rvs.getRankVectors()) {
			for (List<Integer> rv2 : m_rvs.getGr(rv1)) {
				final long newFit = getFitness(rv1, rv2);
				if (newFit > fit) {
					fit = newFit;
					fittest = Pair.create(rv1, rv2);
					s_logger.debug("Current fittest: {}, fit: {}.", fittest, fit);
				}
			}
		}
		assert (fittest != null);
		return fittest;
	}

	@SuppressWarnings({ "unused", "all" })
	private static final Logger s_logger = LoggerFactory.getLogger(FitnessNbDominated.class);

	private final boolean m_weighted;

	private final Map<List<Integer>, Long> m_nbPermutations = Maps.newHashMap();

	private void putDiagonal() {
		for (List<Integer> rv : m_rvs.getRankVectors()) {
			m_preorder.addEqTransitive(rv, rv);
		}
	}

	@Override
	public String toString() {
		return m_optimistic
				? (m_weighted ? FitnessType.OPTIMISTIC_WEIGHTED.toString() : FitnessType.OPTIMISTIC.toString())
				: (m_weighted ? FitnessType.PESSIMISTIC_WEIGHTED.toString() : FitnessType.PESSIMISTIC.toString());
	}
}
