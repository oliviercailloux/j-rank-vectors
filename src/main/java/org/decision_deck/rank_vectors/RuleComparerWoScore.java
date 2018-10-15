package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.decision_deck.rank_vectors.RuleComparerNbWinners.RandomProfilesGenerator;
import org.decision_deck.utils.relation.graph.Preorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class RuleComparerWoScore implements RuleComparer {
    static public enum ScoreType {
	BY_PROFILE, SUM_APPROX_WINNERS, SUM_SUPPL_WINNERS;
    }

    private ScoreType m_type;

    public RuleComparerWoScore(AllRankVectors all, RankBasedVotingRule target, Comparator<List<Integer>> targetWo,
	    RankBasedVotingRule approx, ScoreType type) {
	checkNotNull(type);
	checkNotNull(target);
	checkNotNull(approx);
	m_type = type;
	m_m = all.getM();
	m_n = all.getN();
	m_target = target;
	m_approx = approx;
	m_targetByRank = new Preorder<List<Integer>>(all.getRankVectors(), targetWo);
	s_logger.debug("Created target complete preorder {}.", m_targetByRank);
    }

    private final Preorder<List<Integer>> m_targetByRank;
    private final RankBasedVotingRule m_approx;
    private final int m_m;
    private final int m_n;
    private final RankBasedVotingRule m_target;

    private int getTargetAverageWoScore(Set<List<Integer>> targetWinners) {
	final Iterator<List<Integer>> iterator = targetWinners.iterator();
	final List<Integer> first = iterator.next();
	final int targetWoScore = getWoScore(first);
	while (iterator.hasNext()) {
	    assert (getWoScore(iterator.next()) == targetWoScore);
	}
	return targetWoScore;
    }

    private int getSumInts(List<Integer> numbers) {
	int tot = 0;
	for (Integer number : numbers) {
	    tot += number;
	}
	return tot;
    }

    private double getSumWoScore(Set<List<Integer>> winners) {
	double tot = 0;
	for (List<Integer> winner : winners) {
	    final int score = getWoScore(winner);
	    tot += score;
	}
	return tot;
    }

    private int getWoScore(List<Integer> winner) {
	final int ranksCount = m_targetByRank.getRanksCount();
	assert (ranksCount >= 1);
	final Integer rank = m_targetByRank.getRank(winner);
	assert (rank >= 1 && rank <= ranksCount);
	return ranksCount - rank;
    }

    /**
     * <p>
     * For a given sample, with {@link ScoreType#BY_PROFILE}, the score is targetWoScore - (sum wo score approx winners
     * / nb approx winners).
     * </p>
     * 
     * @param nbTests
     *            at least one.
     * @return the average score over the tests.
     */
    @Override
    public double sample(int nbTests) {
	checkArgument(nbTests >= 1);
	final AbstractCollection<Set<List<Integer>>> testProfiles = new RandomProfilesGenerator(m_m, m_n, nbTests);
	final List<Double> scores = Lists.newArrayList();
	final int denom;
	if (m_type == ScoreType.BY_PROFILE) {
	    for (Set<List<Integer>> testProfile : testProfiles) {
		Set<List<Integer>> approxWinners = m_approx.getWinners(testProfile);
		final Set<List<Integer>> targetWinners = m_target.getWinners(testProfile);
		assert (!approxWinners.isEmpty());
		assert (!targetWinners.isEmpty());
		final double approxSumScore = getSumWoScore(approxWinners);
		final int targetWoScore = getTargetAverageWoScore(targetWinners);
		assert (targetWoScore >= 0);
		assert (approxSumScore >= 0);

		final double error = approxWinners.size() * targetWoScore - approxSumScore;
		if (error < -0.01) {
		    throw new IllegalStateException();
		}
		scores.add(error / approxWinners.size());
	    }
	    assert (scores.size() == nbTests);
	    denom = nbTests;
	} else if (m_type == ScoreType.SUM_APPROX_WINNERS) {
	    final List<Integer> testWeight = Lists.newArrayList();
	    for (Set<List<Integer>> testProfile : testProfiles) {
		final Set<List<Integer>> approxWinners = m_approx.getWinners(testProfile);
		final Set<List<Integer>> targetWinners = m_target.getWinners(testProfile);
		assert (!approxWinners.isEmpty());
		assert (!targetWinners.isEmpty());
		final double approxSumScore = getSumWoScore(approxWinners);
		final int targetWoScore = getTargetAverageWoScore(targetWinners);
		assert (targetWoScore >= 0);
		assert (approxSumScore >= 0);

		final double error = approxWinners.size() * targetWoScore - approxSumScore;
		if (error < -0.01) {
		    throw new IllegalStateException();
		}
		scores.add(error);
		testWeight.add(approxWinners.size());
	    }
	    assert (scores.size() == nbTests);
	    assert (testWeight.size() == nbTests);
	    denom = getSumInts(testWeight);
	} else {
	    if (m_type != ScoreType.SUM_SUPPL_WINNERS) {
		throw new IllegalStateException();
	    }
	    final List<Integer> testWeights = Lists.newArrayList();
	    for (Set<List<Integer>> testProfile : testProfiles) {
		final Set<List<Integer>> approxWinners = m_approx.getWinners(testProfile);
		final Set<List<Integer>> targetWinners = m_target.getWinners(testProfile);
		assert (!approxWinners.isEmpty());
		assert (!targetWinners.isEmpty());
		if (!approxWinners.containsAll(targetWinners)) {
		    throw new IllegalStateException();
		}
		final SetView<List<Integer>> supplWinners = Sets.difference(approxWinners, targetWinners);
		final double approxSumScore = getSumWoScore(approxWinners);
		final double supplSumScore = getSumWoScore(supplWinners);
		final int targetWoScore = getTargetAverageWoScore(targetWinners);
		assert (targetWoScore >= 0);
		assert (approxSumScore >= 0);

		final double error = approxWinners.size() * targetWoScore - approxSumScore;
		if (error < -0.01) {
		    throw new IllegalStateException();
		}
		final double error2 = supplWinners.size() * targetWoScore - supplSumScore;
		assert (Math.abs(error - error2) < 0.01);
		scores.add(error);
		testWeights.add(supplWinners.size());
	    }
	    assert (scores.size() == nbTests);
	    assert (testWeights.size() == nbTests);
	    denom = getSumInts(testWeights);
	}
	final double num = getSumDoubles(scores);
	assert (num >= 0);
	assert (denom >= 0);
	if (denom == 0) {
	    assert (num < 0.001);
	    return 0d;
	}
	return num / denom;
    }

    private double getSumDoubles(List<Double> scores) {
	double tot = 0;
	for (Double score : scores) {
	    tot += score;
	}
	return tot;
    }

    @Override
    public String toString() {
	return getType().toString();
    }

    public RuleComparerType getType() {
	switch (m_type) {
	case BY_PROFILE:
	    return RuleComparerType.WO_SCORE;
	case SUM_APPROX_WINNERS:
	    return RuleComparerType.WO_SCORE_APPROX_WINNER;
	case SUM_SUPPL_WINNERS:
	    return RuleComparerType.WO_SCORE_SUPPL;
	default:
	    throw new IllegalStateException();
	}
    }

    @SuppressWarnings({ "unused", "all" })
    private static final Logger s_logger = LoggerFactory.getLogger(RuleComparerWoScore.class);

}
