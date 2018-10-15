package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

public class RuleComparerNbWinners implements RuleComparer {
    public static class RandomProfilesGenerator extends AbstractCollection<Set<List<Integer>>> {
	private final int m_n;
	private final int m_nbTests;
	private final int m_m;

	public RandomProfilesGenerator(int m, int n, int nb) {
	    m_n = n;
	    m_m = m;
	    m_nbTests = nb;
	}

	@Override
	public Iterator<Set<List<Integer>>> iterator() {
	    final AbstractIterator<Set<List<Integer>>> randomPIterator = new AbstractIterator<Set<List<Integer>>>() {
		@Override
		protected Set<List<Integer>> computeNext() {
		    return Sets.newLinkedHashSet(IncreasingRankVectorsUtils.getIncreasingView(RankVectorsUtils
			    .getRandomProfile(m_m, m_n)));
		}
	    };
	    return Iterators.limit(randomPIterator, m_nbTests);
	}

	@Override
	public int size() {
	    return m_nbTests;
	}
    }

    public RuleComparerNbWinners(int m, int n, RankBasedVotingRule target, RankBasedVotingRule approx) {
	checkArgument(m >= 1);
	checkArgument(n >= 1);
	checkNotNull(target);
	checkNotNull(approx);
	m_m = m;
	m_n = n;
	m_target = target;
	m_approx = approx;
    }

    private final RankBasedVotingRule m_target;
    private final RankBasedVotingRule m_approx;
    private final int m_m;
    private final int m_n;

    public double sampleOld(int nbTests) {
	checkArgument(nbTests >= 1);
	int sumError = 0;
	for (int i = 0; i < nbTests; ++i) {
	    final Set<List<Integer>> p = Sets.newLinkedHashSet(IncreasingRankVectorsUtils
		    .getIncreasingView(RankVectorsUtils.getRandomProfile(m_m, m_n)));
	    final int targetNb = m_target.getNbWinners(p);
	    final int approxNb = m_approx.getNbWinners(p);
	    if (approxNb < targetNb) {
		throw new IllegalStateException();
	    }
	    final int error = approxNb - targetNb;
	    s_logger.info("Error on profile {} is {}.", p, error);
	    sumError += error;
	}
	return ((double) sumError) / nbTests;
    }

    @Override
    public String toString() {
	return RuleComparerType.SUPPL_WINNERS.toString();
    }

    public double exhaustWRONG() {
	final Generator<List<Integer>> gen = Factory.createSimpleCombinationGenerator(
		Factory.createVector(new AllRankVectors(m_m, m_n).getRankVectors()), m_m);
	s_logger.info("Gonna test {} profiles.", gen.getNumberOfGeneratedObjects());
	final Iterable<Set<List<Integer>>> sets = Iterables.transform(gen,
		new Function<Iterable<List<Integer>>, Set<List<Integer>>>() {
		    @Override
		    public Set<List<Integer>> apply(Iterable<List<Integer>> input) {
			return ImmutableSet.copyOf(input);
		    }
		});
	final AbstractCollection<Set<List<Integer>>> coll = new AbstractCollection<Set<List<Integer>>>() {
	    @Override
	    public Iterator<Set<List<Integer>>> iterator() {
		return sets.iterator();
	    }

	    @Override
	    public int size() {
		return Ints.checkedCast(gen.getNumberOfGeneratedObjects());
	    }
	};
	assert (!hasDuplicates(coll));
	return getSumError(coll);
    }

    private <E> boolean hasDuplicates(Collection<E> coll) {
	final Set<E> seen = Sets.newLinkedHashSet();
	for (E e : coll) {
	    final boolean eNew = seen.add(e);
	    if (!eNew) {
		return true;
	    }
	}
	return false;
    }

    private double getSumError(Collection<Set<List<Integer>>> testProfiles) {
	int sumError = 0;
	for (Set<List<Integer>> profile : testProfiles) {
	    final int targetNb = m_target.getNbWinners(profile);
	    final int approxNb = m_approx.getNbWinners(profile);
	    if (approxNb < targetNb) {
		throw new IllegalStateException();
	    }
	    final double error = (double) approxNb / (double) targetNb;
	    s_logger.debug("Error on profile {} is {}.", profile, error);
	    sumError += error;
	}
	return sumError;
    }

    @Override
    public double sample(int nbTests) {
	checkArgument(nbTests >= 1);
	final AbstractCollection<Set<List<Integer>>> testProfiles = new RandomProfilesGenerator(m_m, m_n, nbTests);
	return (getSumError(testProfiles)) / nbTests;
    }

    private static final Logger s_logger = LoggerFactory.getLogger(RuleComparerNbWinners.class);

}
