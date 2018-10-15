package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.math.LongMath;

public class RankVectorsUtils {

	/**
	 * Given a list of n linear orders viewed each as a column, each of size m,
	 * retrieves a copy of the same information as a set of m rows.
	 * 
	 * @param linearOrders not <code>null</code>, not empty, each embedded list must
	 *                     be of the same size and non empty.
	 * @return a set of size m ≥ 1, each entry being of size n ≥ 1.
	 */
	static public Set<List<Integer>> getRankProfile(List<List<Integer>> linearOrders) {
		checkNotNull(linearOrders);
		checkArgument(!linearOrders.isEmpty());

		final List<Iterator<Integer>> preferenceIteratorsPerVoter = Lists
				.newLinkedList(Lists.transform(linearOrders, new Function<List<Integer>, Iterator<Integer>>() {
					@Override
					public Iterator<Integer> apply(List<Integer> input) {
						return input.iterator();
					}
				}));
		final Set<List<Integer>> rvs = Sets.newLinkedHashSet();
		/** n ≥ 1 */
		assert (!preferenceIteratorsPerVoter.isEmpty());
		boolean theresMore = true;
		do {
			final Builder<Integer> rv = ImmutableList.builder();
			for (Iterator<Integer> preferenceIterator : preferenceIteratorsPerVoter) {
				/** m ≥ 1 */
				checkArgument(preferenceIterator.hasNext());
				final Integer rank = preferenceIterator.next();
				rv.add(rank);
				if (!theresMore) {
					assert (!preferenceIterator.hasNext());
				}
				theresMore = preferenceIterator.hasNext();
			}
			final boolean isNew = rvs.add(rv.build());
			assert (isNew);
		} while (theresMore);

		return rvs;
	}

	public static long getLongValue(BigInteger value) {
		if (value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0
				|| value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
			throw new IllegalArgumentException("cannot cast to long.");
		}
		return value.longValue();
	}

	static public Set<List<Integer>> getRandomProfile(int m, int n) {
		checkArgument(m >= 1);
		checkArgument(n >= 1);
		final ImmutableList<Integer> availableRanks = ContiguousSet
				.create(Range.closed(1, m), DiscreteDomain.integers()).asList();

		final List<List<Integer>> ranksPerVoter = Lists.newLinkedList();
		for (int i = 0; i < n; ++i) {
			final List<Integer> linearOrder = Lists.newArrayList(availableRanks);
			Collections.shuffle(linearOrder);
			ranksPerVoter.add(linearOrder);
		}

		final Set<List<Integer>> rankProfile = RankVectorsUtils.getRankProfile(ranksPerVoter);
		s_logger.debug("Returning random profile {}.", rankProfile);
		return rankProfile;
	}

	private static final Logger s_logger = LoggerFactory.getLogger(RankVectorsUtils.class);

	static public long getNbPermutations(List<Integer> rv) {
		assert (Ordering.<Integer>natural().isOrdered(rv));
		long nbPerms = 1;
		final Iterator<Integer> iterator = rv.iterator();
		int nextRank = -1;
		int nbSpacesLeft = rv.size();
		if (iterator.hasNext()) {
			nextRank = iterator.next();
		}
		while (nbSpacesLeft > 0) {
			assert (nextRank != -1);
			final int rank = nextRank;
			int nbSameRank = 1;
			while (iterator.hasNext() && rank == nextRank) {
				nextRank = iterator.next();
				if (rank == nextRank) {
					++nbSameRank;
				}
			}
			assert (nbSpacesLeft >= nbSameRank);
			final long binomial = LongMath.binomial(nbSpacesLeft, nbSameRank);
			if (binomial == Long.MAX_VALUE) {
				throw new IllegalArgumentException();
			}
			nbPerms = LongMath.checkedMultiply(nbPerms, binomial);
			/** Double check that the loop is not infinite as nbSpacesLeft decreases. */
			assert (nbSameRank >= 1);
			nbSpacesLeft -= nbSameRank;
		}
		assert (!iterator.hasNext());
		return nbPerms;
	}

}
