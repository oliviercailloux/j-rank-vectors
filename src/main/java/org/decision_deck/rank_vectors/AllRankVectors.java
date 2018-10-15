package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.BinaryRelation;
import org.decision_deck.utils.relation.BinaryRelationImpl;
import org.decision_deck.utils.relation.HomogeneousPair;
import org.decision_deck.utils.relation.PairToHomogeneous;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * <p>
 * All weakly increasing (thus: non-decreasing) rank-vectors, given a number of alternatives m, a number of voters n.
 * For m=2, n=2, these are the three rank vectors 11, 12, 22.
 * </p>
 * <p>
 * Immutable.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class AllRankVectors {
    public static class Lex extends Ordering<List<Integer>> {
	@Override
	public int compare(List<Integer> left, List<Integer> right) {
	    checkArgument(left.size() == right.size());
	    final Iterator<Integer> i1 = left.iterator();
	    final Iterator<Integer> i2 = right.iterator();
	    while (i1.hasNext()) {
		final Integer l = i1.next();
		final Integer r = i2.next();
		if (l.compareTo(r) == 0) {
		    continue;
		}
		return l.compareTo(r);
	    }
	    return 0;
	}
    }

    /**
     * Non empty.
     */
    private final ImmutableSortedSet<List<Integer>> m_allRvs;
    /**
     * ≥ 1.
     */
    private final int m_m;
    /**
     * ≥ 1.
     */
    private final int m_n;
    private BinaryRelationImpl<List<Integer>, List<Integer>> m_pareto;

    @SuppressWarnings("boxing")
    public AllRankVectors(int m, int n) {
	checkArgument(m >= 1);
	checkArgument(n >= 1);
	m_m = m;
	m_n = n;
	// final Ordering<List<Integer>> lex = new Lex();
	final Ordering<Iterable<Integer>> lex = Ordering.natural().lexicographical();
	final Builder<List<Integer>> builder = new ImmutableSortedSet.Builder<List<Integer>>(lex);
	final List<Integer> currentSequence = Lists.newLinkedList(Collections.nCopies(n, 1));
	builder.add(ImmutableList.copyOf(currentSequence));
	while (nextIncreasing(currentSequence, m)) {
	    builder.add(ImmutableList.copyOf(currentSequence));
	}
	m_allRvs = builder.build();
	m_pareto = null;
    }

    /**
     * @return a set of size C(n + m − 1, m − 1).
     */
    public ImmutableSortedSet<List<Integer>> getRankVectors() {
	return m_allRvs;
    }

    /**
     * Retrieves the rank vectors which are strictly greater, in lexicographic terms, than the given rank-vector.
     * 
     * @param rv
     *            must be an element of this set.
     * @return not <code>null</code>, may be empty.
     */
    public ImmutableSortedSet<List<Integer>> getGr(List<Integer> rv) {
	checkArgument(m_allRvs.contains(rv));
	return m_allRvs.tailSet(rv, false);
    }

    /**
     * </p>Retrieves the irreflexive relation of strict pareto-dominance without transitive edges. Contains only weakly
     * increasing rank-vectors.</p>
     * <p>
     * A rank-vector is in relation with every rank-vector having all the same ranks except at one position, where it
     * has original rank + 1, except if this would break weak-increasingness, thus if the original rank at the next
     * position was the same, and except if the original rank at that position was m.
     * </p>
     * <p>
     * For example, the rank-vector 122 is in relation with 123 and with 222, and not with 132 as this would break
     * weak-increasingness.
     * </p>
     * <p>
     * The pairs are ordered lexicographically according to the lexicographic order of each component. Thus a pair
     * p=(p1, p2) appear before p'=(p'1, p'2) iff p1 is lexicographically lower than p'1 or if p1 equals p'1 and p2 is
     * lexicographically lower than p'2.
     * </p>
     * 
     * @return not <code>null</code>, empty iff m=1.
     */
    @SuppressWarnings("boxing")
    public BinaryRelation<List<Integer>, List<Integer>> getParetoDominanceNonTransitive() {
	if (m_pareto != null) {
	    return m_pareto;
	}
	m_pareto = new BinaryRelationImpl<List<Integer>, List<Integer>>();
	for (List<Integer> dominant : m_allRvs) {
	    int nextPositionRank = m_m;
	    for (int k = m_n - 1; k >= 0; --k) {
		final int originalRank = dominant.get(k);
		assert (originalRank <= m_m);
		assert (originalRank > 0);
		assert (originalRank <= nextPositionRank);
		final boolean skip = originalRank == nextPositionRank;
		nextPositionRank = originalRank;
		if (originalRank == m_m) {
		    continue;
		}
		if (skip) {
		    continue;
		}
		final List<Integer> dominated = Lists.newArrayList(dominant);
		dominated.set(k, originalRank + 1);
		m_pareto.asPairs().add(Pair.create(dominant, (List<Integer>) ImmutableList.copyOf(dominated)));
	    }
	}
	final Ordering<Integer> i = Ordering.natural();
	final Ordering<Iterable<Integer>> l1 = i.lexicographical();
	final Ordering<Iterable<List<Integer>>> l = l1.<List<Integer>> lexicographical();
	final Collection<HomogeneousPair<List<Integer>>> paretoAsHPairs = Collections2.transform(m_pareto.asPairs(),
		new PairToHomogeneous());
	assert (l.isOrdered(paretoAsHPairs));
	return m_pareto;
    }

    /**
     * Retrieves the number of voters, which is the length of any rank-vector.
     * 
     * @return at least 1.
     */
    public int getN() {
	return m_n;
    }

    /**
     * Retrieves the number of alternatives, which is the maximal number used in any position of any rank-vector.
     * 
     * @return at least 1.
     */
    public int getM() {
	return m_m;
    }

    /**
     * Modifies the list in place, replacing the provided sequence of integers by the next one, considering only weakly
     * increasing (meaning: non decreasing) sequences. Thus, the sequence 1111 with limit > 2 becomes 1112, the sequence
     * 223 with limit 3 becomes 233.
     * 
     * @param l
     *            not <code>null</code>, may be empty.
     * @param limit
     *            must be ≥ all integers in the provided list.
     * @return <code>true</code> iff the list has been increased, <code>false</code> iff all elements equal the limit.
     */
    @SuppressWarnings("boxing")
    static boolean nextIncreasing(List<Integer> l, int limit) {
	final ListIterator<Integer> i = l.listIterator(l.size());
	boolean found = false;
	int n = -1;
	while (i.hasPrevious()) {
	    n = i.previous();
	    checkArgument(n <= limit);
	    if (n < limit) {
		found = true;
		break;
	    }
	}
	if (!found) {
	    return false;
	}
	final int increased = n + 1;
	i.set(increased);
	while (i.hasNext()) {
	    i.next();
	    i.set(increased);
	}
	return true;
    }

    @SuppressWarnings("boxing")
    static boolean nextIncreasingGrThan(List<Integer> l, int limit, List<Integer> min) {
	checkArgument(l.size() == min.size());
	final ListIterator<Integer> i = l.listIterator(l.size());
	final int increased;
	{
	    boolean found = false;
	    int n = -1;
	    while (i.hasPrevious()) {
		n = i.previous();
		checkArgument(n <= limit);
		if (n < limit) {
		    found = true;
		    break;
		}
	    }
	    if (!found) {
		return false;
	    }
	    increased = n + 1;
	}
	i.set(increased);
	while (i.hasNext()) {
	    i.next();
	    final int minN = min.get(i.previousIndex());
	    i.set(Math.min(increased, minN));
	}
	return true;
    }
}
