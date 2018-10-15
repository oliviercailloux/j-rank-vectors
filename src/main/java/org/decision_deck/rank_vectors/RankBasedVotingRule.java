package org.decision_deck.rank_vectors;

import java.util.List;
import java.util.Set;

public interface RankBasedVotingRule {
    /**
     * @param profile
     *            not <code>null</code>, not empty, each list non empty and of the same size.
     * @return a non empty subset of the given set, or the entire set.
     */
    public Set<List<Integer>> getWinners(Set<List<Integer>> profile);

    /**
     * @param profile
     *            not <code>null</code>.
     * @return at least 1.
     */
    public int getNbWinners(Set<List<Integer>> profile);
}
