package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;

import java.util.List;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.decision_deck.utils.relation.BinaryRelation;
import org.decision_deck.utils.relation.Preorder;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class RuleComparerNbWinnersTest {

    @Test
    public void test11KnownTarget() {
	final int m = 1;
	final int n = 1;
	final WeakOrderRule bordaRule = new WeakOrderRule(new Borda(m));
	final RuleComparerNbWinners comp = new RuleComparerNbWinners(m, n, bordaRule, bordaRule);
	final double diff = comp.sample(1);
	assertEquals(1, diff, 1e-5);
    }

    @SuppressWarnings({ "static-access", "unused" })
    @Test
    public void test42Approx(final @Mocked RankVectorsUtils mock) {
	final int m = 4;
	final int n = 2;

	final AllRankVectors all = new AllRankVectors(m, n);
	final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();

	final Borda targetWo = new Borda(m);
	final WeakOrderRule targetRule = new WeakOrderRule(targetWo);

	final Preorder<List<Integer>> approxPr = Preorder.copyOf(paretoNT);

	final PreorderRule paretoRule = new PreorderRule(approxPr);

	final RuleComparerNbWinners comp = new RuleComparerNbWinners(m, n, targetRule, paretoRule);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(4, 4));
	    }
	};
	assertEquals(1, comp.sample(3), 1e-5);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 2), ImmutableList.of(3, 3));
	    }
	};
	assertEquals(2, comp.sample(1), 1e-5);
    }

    @SuppressWarnings({ "static-access", "unused" })
    @Test
    public void test22Approx(final @Mocked RankVectorsUtils mock) {
        final int m = 2;
        final int n = 2;
    
        final AllRankVectors all = new AllRankVectors(m, n);
        final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();
    
	final Preorder<List<Integer>> targetPr = Preorder.copyOf(paretoNT);

	final Preorder<List<Integer>> approxPr = Preorder.create();
        approxPr.addTransitive(ImmutableList.of(1, 1), ImmutableList.of(1, 2));
    
        /** approx has two winners for profile (11, 22) instead of one. */
        final PreorderRule approxRule = new PreorderRule(approxPr);
        final PreorderRule targetRule = new PreorderRule(targetPr);
    
        final RuleComparerNbWinners comp = new RuleComparerNbWinners(m, n, targetRule, approxRule);
    
        new NonStrictExpectations() {
            {
        	mock.getRandomProfile(m, n);
        	result = ImmutableSet.of(ImmutableList.of(1, 1), ImmutableList.of(2, 2));
            }
        };
	assertEquals(2, comp.sample(3), 1e-5);
    
        new NonStrictExpectations() {
            {
        	mock.getRandomProfile(m, n);
        	result = ImmutableSet.of(ImmutableList.of(1, 1), ImmutableList.of(1, 2));
            }
        };
	assertEquals(1, comp.sample(1), 1e-5);
    }

}
