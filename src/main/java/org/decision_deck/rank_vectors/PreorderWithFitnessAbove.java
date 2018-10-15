package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.BinaryRelation;
import org.decision_deck.utils.relation.Preorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Considers also the rvs above, suppl to the rvs below the ones whose fitness is computed. Does not seem to be such a
 * brilliant idea.
 * 
 * @author Olivier Cailloux
 * 
 */
public class PreorderWithFitnessAbove extends Preorder<List<Integer>> implements
	BinaryRelation<List<Integer>, List<Integer>> {

    private boolean m_dirty;

    private final AllRankVectors m_rvs;

    /**
     * Invalid iff dirty. When valid, for a given rv: all the rvs it is in relation with (greater than or smaller than
     * or equivalent). As the relation is reflexive, that is never empty. The complement of this set are the rvs
     * incomparable to it.
     */
    private final Map<List<Integer>, Set<List<Integer>>> m_connexions;

    public PreorderWithFitnessAbove(AllRankVectors rvs) {
	checkArgument(rvs != null);
	m_dirty = true;
	m_rvs = rvs;
	m_connexions = Maps.newLinkedHashMap();
	putDiagonal();
    }

    public void updateConnexions() {
	if (!m_dirty) {
	    return;
	}
	final ImmutableSortedSet<List<Integer>> all = m_rvs.getRankVectors();
	for (List<Integer> rv1 : all) {
	    final Builder<List<Integer>> conn = ImmutableSet.builder();
	    for (List<Integer> rv2 : all) {
		if (delegate().contains(rv1, rv2) || delegate().contains(rv2, rv1)) {
		    conn.add(rv2);
		}
	    }
	    m_connexions.put(rv1, conn.build());
	}

	m_dirty = false;
    }

    /**
     * Retrieves the fitness of the given pair: card of Q(x) + card of Q(y) − 2 × card(Q(x) inter Q(y)), where Q(x) is
     * the set of rvs connected to (in relation with) x. Symetric. x = y ⇒ returns 0.
     * 
     * @param x
     *            a member of this relation.
     * @param y
     *            a member of this relation.
     * @return ≥ 0.
     */
    public int getFitness(List<Integer> x, List<Integer> y) {
	updateConnexions();
	final int fit = m_connexions.get(x).size() + m_connexions.get(y).size() - 2
		* Sets.intersection(m_connexions.get(x), m_connexions.get(y)).size();
	return fit;
    }

    /**
     * Retrieves a maximally fitted pair. Semantically, the returned pair is unordered, but it is ordered so that the
     * first element is lower than or equal to the second, lexicographically.
     * 
     * @return not <code>null</code>.
     */
    public Pair<List<Integer>, List<Integer>> getFittest() {
	int fit = -1;
	if (m_rvs.getRankVectors().size() == 1) {
	    final List<Integer> uniqueRv = m_rvs.getRankVectors().iterator().next();
	    return Pair.create(uniqueRv, uniqueRv);
	}

	Pair<List<Integer>, List<Integer>> fittest = null;
	for (List<Integer> rv1 : m_rvs.getRankVectors()) {
	    for (List<Integer> rv2 : m_rvs.getGr(rv1)) {
		final int newFit = getFitness(rv1, rv2);
		if (newFit > fit) {
		    fit = newFit;
		    fittest = Pair.create(rv1, rv2);
		    s_logger.info("Current fittest: {}, fit: {}.", fittest, fit);
		}
	    }
	}
	assert (fittest != null);
	return fittest;
    }

    @SuppressWarnings({ "unused", "all" })
    private static final Logger s_logger = LoggerFactory.getLogger(PreorderWithFitnessAbove.class);

    private void putDiagonal() {
	for (List<Integer> rv : m_rvs.getRankVectors()) {
	    addEqTransitive(rv, rv);
	}
    }

    @Override
    public void addTransitive(List<Integer> x, List<Integer> y) {
	if (!contains(x, y)) {
	    m_dirty = true;
	}
	super.addTransitive(x, y);
    }

    @Override
    public void addEqTransitive(List<Integer> x, List<Integer> y) {
	if (!contains(x, y)) {
	    m_dirty = true;
	}
	super.addEqTransitive(x, y);
    }
}
