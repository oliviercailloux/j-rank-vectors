package org.decision_deck.rank_vectors;

import java.util.List;

import org.decision_deck.utils.Pair;

public interface Fitness {

    /**
     * Retrieves a maximally fitted pair. Semantically, the returned pair is unordered, but it is ordered so that the
     * first element is lower than or equal to the second, lexicographically.
     * 
     * @return not <code>null</code>.
     */
    public Pair<List<Integer>, List<Integer>> getFittest();

}
