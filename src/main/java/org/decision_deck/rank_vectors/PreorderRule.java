package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.BinaryRelation;

import com.google.common.collect.Sets;

/**
 * A rule based on a preorder, a reflexive and transitive relation, not
 * necessarily complete. If the relation is complete, a {@link WeakOrderRule} is
 * probably more appropriate.
 * 
 * @author Olivier Cailloux
 * 
 */
public class PreorderRule implements RankBasedVotingRule {

	private BinaryRelation<List<Integer>, List<Integer>> m_preorder;

	/**
	 * @param preorder must be reflexive and transitive.
	 */
	public PreorderRule(BinaryRelation<List<Integer>, List<Integer>> preorder) {
		checkArgument(preorder != null);
		m_preorder = preorder;
	}

	@Override
	public Set<List<Integer>> getWinners(Set<List<Integer>> profile) {
		final Set<List<Integer>> candidates = Sets.newLinkedHashSet(profile);
		final Set<List<Integer>> winners = Sets.newLinkedHashSet();
		while (!candidates.isEmpty()) {
			final Iterator<List<Integer>> iterator = candidates.iterator();
			final List<Integer> candidate = iterator.next();
			final Set<List<Integer>> equivalentCandidates = Sets.newLinkedHashSet();
			equivalentCandidates.add(candidate);
			iterator.remove();
			while (iterator.hasNext()) {
				final List<Integer> challenger = iterator.next();
				final ComparisonStatus status = compare(candidate, challenger);
				if (status == ComparisonStatus.EQUIVALENT) {
					equivalentCandidates.add(challenger);
					iterator.remove();
				} else if (status == ComparisonStatus.BETTER) {
					iterator.remove();
				} else if (status == ComparisonStatus.WORST) {
					equivalentCandidates.clear();
					break;
				} else if (status == ComparisonStatus.INCOMPARABLE) {
					// nothing
				} else {
					throw new IllegalStateException();
				}
			}
			winners.addAll(equivalentCandidates);
		}

		return winners;
	}

	public ComparisonStatus compare(List<Integer> rv1, List<Integer> rv2) {
		final boolean geq = m_preorder.asPairs().contains(Pair.create(rv1, rv2));
		final boolean leq = m_preorder.asPairs().contains(Pair.create(rv2, rv1));
		if (geq && leq) {
			return ComparisonStatus.EQUIVALENT;
		}
		if (geq) {
			return ComparisonStatus.BETTER;
		}
		if (leq) {
			return ComparisonStatus.WORST;
		}
		return ComparisonStatus.INCOMPARABLE;
	}

	static public enum ComparisonStatus {
		INCOMPARABLE, BETTER, EQUIVALENT, WORST
	}

	@Override
	public int getNbWinners(Set<List<Integer>> profile) {
		return getWinners(profile).size();
	}

}
