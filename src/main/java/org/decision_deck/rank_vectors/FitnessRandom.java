package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.Preorder;
import org.decision_deck.utils.relation.RelationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

/**
 * The relation is guaranteed to be never empty, reflexive and transitive.
 * 
 * @author Olivier Cailloux
 * 
 */
public class FitnessRandom implements Fitness {

    private final AllRankVectors m_rvs;

    private Preorder<List<Integer>> m_preorder;

    public FitnessRandom(AllRankVectors rvs, Preorder<List<Integer>> preorder) {
	checkNotNull(preorder);
	checkArgument(rvs != null);
	m_preorder = preorder;
	m_rvs = rvs;
	putDiagonal();
	m_random = null;
    }

    @Override
    public String toString() {
	return FitnessType.RANDOM.toString();
    }

    /**
     * Retrieves a maximally fitted pair. Semantically, the returned pair is unordered, but it is ordered so that the
     * first element is lower than or equal to the second, lexicographically.
     * 
     * @return not <code>null</code>.
     */
    @Override
    public Pair<List<Integer>, List<Integer>> getFittest() {
	if (m_random == null) {
	    m_random = new Random();
	}
	final Pair<List<Integer>, List<Integer>> inc = getRandomIncomparablePair(m_preorder, m_random);
	if (inc == null) {
	    return Pair.create(m_rvs.getRankVectors().first(), m_rvs.getRankVectors().first());
	}
	return inc;
    }

    @SuppressWarnings({ "unused", "all" })
    private static final Logger s_logger = LoggerFactory.getLogger(FitnessRandom.class);

    private Random m_random;

    private void putDiagonal() {
	for (List<Integer> rv : m_rvs.getRankVectors()) {
	    m_preorder.addEqTransitive(rv, rv);
	}
    }

    Pair<List<Integer>, List<Integer>> getRandomIncomparablePair(Preorder<List<Integer>> p, Random r) {
	final Set<Pair<List<Integer>, List<Integer>>> i = RelationUtils.getIncomp(p);
	if (i.isEmpty()) {
	    return null;
	}
	return Iterables.get(i, r.nextInt(i.size()));
    }

    public void setRandom(Random random) {
	m_random = random;
    }
}
