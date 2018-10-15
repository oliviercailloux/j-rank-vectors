package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class WeakOrderRule implements RankBasedVotingRule {

	private Ordering<List<Integer>> m_weakOrder;

	public WeakOrderRule(Comparator<List<Integer>> weakOrder) {
		m_weakOrder = Ordering.from(weakOrder);
	}

	@Override
	public Set<List<Integer>> getWinners(Set<List<Integer>> profile) {
		checkArgument(!profile.isEmpty());
		final Iterator<List<Integer>> i = profile.iterator();
		List<Integer> current = i.next();
		final Set<List<Integer>> winners = Sets.newLinkedHashSet();
		winners.add(current);
		while (i.hasNext()) {
			final List<Integer> challenger = i.next();
			final int comp = m_weakOrder.compare(current, challenger);
			if (comp < 0) {
				current = challenger;
				winners.clear();
				winners.add(challenger);
			} else if (comp == 0) {
				winners.add(challenger);
			} else if (comp > 0) {
				continue;
			}
		}
		assert (!winners.isEmpty());
		assert (allEqual(winners));
		assert (better(winners, Sets.difference(profile, winners)));
		return winners;
	}

	private boolean allEqual(Set<List<Integer>> equivalents) {
		for (List<Integer> rv1 : equivalents) {
			for (List<Integer> rv2 : equivalents) {
				if (m_weakOrder.compare(rv1, rv2) != 0) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean better(Set<List<Integer>> above, SetView<List<Integer>> below) {
		for (List<Integer> top : above) {
			for (List<Integer> bottom : below) {
				final int comp = m_weakOrder.compare(top, bottom);
				if (comp <= 0) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int getNbWinners(Set<List<Integer>> profile) {
		return getWinners(profile).size();
	}

}
