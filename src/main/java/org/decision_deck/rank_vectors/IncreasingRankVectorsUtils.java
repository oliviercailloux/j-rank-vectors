package org.decision_deck.rank_vectors;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

public class IncreasingRankVectorsUtils {

    /**
     * Retrieves a view of given rank-vectors where all rank-vectors are increasing. The returned collection has no
     * connexion between voter and preferences.
     * 
     * @param rankVectors
     *            not <code>null</code>, may be empty.
     * @return not <code>null</code>, a collection of the same size than given.
     */
    static public Collection<List<Integer>> getIncreasingView(Collection<List<Integer>> rankVectors) {
	final Collection<List<Integer>> transformed = Collections2.transform(rankVectors,
		new Function<List<Integer>, List<Integer>>() {
		    @Override
		    public List<Integer> apply(List<Integer> input) {
			return ImmutableList.copyOf(Ordering.natural().sortedCopy(input));
		    }
		});
	return transformed;
    }

}
