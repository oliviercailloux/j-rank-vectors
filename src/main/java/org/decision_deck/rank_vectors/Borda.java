package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Ordering;

public class Borda extends Ordering<List<Integer>> implements Comparator<List<Integer>> {
    private final int m_m;

    /**
     * Useful to get scores that are easier to interpret.
     * 
     * @param m
     *            number of candidates.
     */
    public Borda(int m) {
	m_m = m;
    }

    public Borda() {
	m_m = 0;
    }

    @Override
    public int compare(List<Integer> left, List<Integer> right) {
	final int compar = Integer.valueOf(getScore(left)).compareTo(Integer.valueOf(getScore(right)));
	s_logger.debug("Comparing {} to {}: " + compar + ".", left, right);
	return compar;
    }

    private static final Logger s_logger = LoggerFactory.getLogger(Borda.class);

    public int getScore(List<Integer> rv) {
	int score = 0;
	for (int rank : rv) {
	    checkArgument(m_m == 0 || rank <= m_m);
	    final int partial = m_m - rank;
	    score += partial;
	}
	return score;
    }
}
