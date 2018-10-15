package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.decision_deck.rank_vectors.RuleComparerNbWinners.RandomProfilesGenerator;

public class RuleUtils {

    public static double getAverageNbWinners(int m, int n, RankBasedVotingRule rule, int nbTests) {
	checkArgument(nbTests >= 1);
	checkArgument(m >= 1);
	checkArgument(n >= 1);
	checkNotNull(rule);
	final AbstractCollection<Set<List<Integer>>> testProfiles = new RandomProfilesGenerator(m, n, nbTests);
	int sumWinners = 0;
	for (Set<List<Integer>> profile : testProfiles) {
	    final int nbWinners = rule.getNbWinners(profile);
	    sumWinners += nbWinners;
	}
	return ((double) sumWinners) / nbTests;
    }

    static public double getAverage(Collection<Double> numbers) {
	double sum = 0;
	for (Double number : numbers) {
	    sum += number;
	}
	final double avg = sum / numbers.size();
	return avg;
    }

    static public double getAverageInt(Collection<Integer> numbers) {
	int sum = 0;
	for (Integer number : numbers) {
            sum += number;
        }
	final double avg = (double) sum / numbers.size();
        return avg;
    }

}
