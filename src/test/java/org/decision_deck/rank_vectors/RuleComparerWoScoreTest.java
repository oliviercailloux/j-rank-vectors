package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;

import java.util.List;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.decision_deck.rank_vectors.RuleComparerWoScore.ScoreType;
import org.decision_deck.utils.relation.BinaryRelation;
import org.decision_deck.utils.relation.Preorder;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

public class RuleComparerWoScoreTest {

    @Test
    public void test11KnownTarget() {
	final int m = 1;
	final int n = 1;
	final Borda borda = new Borda(m);
	final WeakOrderRule bordaRule = new WeakOrderRule(borda);
	final RuleComparerWoScore comp = new RuleComparerWoScore(new AllRankVectors(m, n), bordaRule, borda, bordaRule,
		ScoreType.BY_PROFILE);
	final double diff = comp.sample(1);
	assertEquals(0, diff, 1e-5);
    }

    @SuppressWarnings({ "static-access", "unused" })
    @Test
    public void test42ApproxWorst(final @Mocked RankVectorsUtils mock) {
	final int m = 4;
	final int n = 2;

	final AllRankVectors all = new AllRankVectors(m, n);
	final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();

	final Borda targetWo = new Borda(m);
	final WeakOrderRule targetRule = new WeakOrderRule(targetWo);

	final Preorder<List<Integer>> approxPr = Preorder.create();
	for (List<Integer> rv : all.getRankVectors()) {
	    approxPr.addEqTransitive(rv, rv);
	}
	approxPr.addTransitive(ImmutableList.of(2, 2), ImmutableList.of(1, 4));
	final PreorderRule prRule = new PreorderRule(approxPr);

	final RuleComparerWoScore comp = new RuleComparerWoScore(all, targetRule, targetWo, prRule,
		ScoreType.BY_PROFILE);
	final RuleComparerWoScore compOnApprox = new RuleComparerWoScore(all, targetRule, targetWo, prRule,
		ScoreType.SUM_APPROX_WINNERS);
	final RuleComparerWoScore compOnSuppl = new RuleComparerWoScore(all, targetRule, targetWo, prRule,
		ScoreType.SUM_SUPPL_WINNERS);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(4, 4));
	    }
	};
	assertEquals(4d / 3d, comp.sample(2), 1e-5);
	assertEquals(4d / 3d, compOnApprox.sample(1), 1e-5);
	assertEquals(4d / 1d, compOnSuppl.sample(1), 1e-5);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 2), ImmutableList.of(3, 3),
			ImmutableList.of(3, 4));
	    }
	};
	assertEquals((3d + 2d) / 3d, comp.sample(1), 1e-5);
	assertEquals((3d + 2d) / 3d, compOnApprox.sample(1), 1e-5);
	assertEquals((3d + 2d) / 2d, compOnSuppl.sample(1), 1e-5);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(4, 4));
		result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 2), ImmutableList.of(3, 3),
			ImmutableList.of(3, 4));
	    }
	};
	assertEquals((4d / 3d + 5d / 3d) / 2d, comp.sample(2), 1e-5);
	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(4, 4));
		result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 2), ImmutableList.of(3, 3),
			ImmutableList.of(3, 4));
	    }
	};
	assertEquals((4d + 5d) / 6d, compOnApprox.sample(2), 1e-5);
	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(4, 4));
		result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 2), ImmutableList.of(3, 3),
			ImmutableList.of(3, 4));
	    }
	};
	assertEquals((4d + 2d + 3d) / (1d + 2d), compOnSuppl.sample(2), 1e-5);
    }

    @SuppressWarnings({ "static-access", "unused" })
    @Test
    public void test22Approx(final @Mocked RankVectorsUtils mock) {
	final int m = 2;
	final int n = 2;

	final AllRankVectors all = new AllRankVectors(m, n);
	final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();

	@SuppressWarnings("unchecked")
	final Ordering<List<Integer>> targetWo = Ordering.explicit((List<Integer>) ImmutableList.of(2, 2),
		(List<Integer>) ImmutableList.of(1, 2), (List<Integer>) ImmutableList.of(1, 1));

	final Preorder<List<Integer>> approxPr = Preorder.create();
	for (List<Integer> rv : all.getRankVectors()) {
	    approxPr.addEqTransitive(rv, rv);
	}
	approxPr.addTransitive(ImmutableList.of(1, 1), ImmutableList.of(1, 2));

	/** approx has two winners for profile (11, 22) instead of one. */
	final PreorderRule approxRule = new PreorderRule(approxPr);

	final RuleComparerWoScore comp = new RuleComparerWoScore(all, new WeakOrderRule(targetWo), targetWo,
		approxRule, ScoreType.BY_PROFILE);
	final RuleComparerWoScore compOnApprox = new RuleComparerWoScore(all, new WeakOrderRule(targetWo), targetWo,
		approxRule, ScoreType.SUM_APPROX_WINNERS);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 1), ImmutableList.of(2, 2));
	    }
	};
	assertEquals(1, comp.sample(3), 1e-5);
	assertEquals(1, compOnApprox.sample(3), 1e-5);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 1), ImmutableList.of(1, 2));
	    }
	};
	assertEquals(0, comp.sample(1), 1e-5);
	assertEquals(0, compOnApprox.sample(1), 1e-5);
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

	final RuleComparerWoScore comp = new RuleComparerWoScore(all, targetRule, targetWo, paretoRule,
		ScoreType.BY_PROFILE);
	final RuleComparerWoScore compOnApprox = new RuleComparerWoScore(all, targetRule, targetWo, paretoRule,
		ScoreType.SUM_APPROX_WINNERS);
	final RuleComparerWoScore compOnSuppl = new RuleComparerWoScore(all, targetRule, targetWo, paretoRule,
		ScoreType.SUM_SUPPL_WINNERS);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 3), ImmutableList.of(2, 2), ImmutableList.of(4, 4));
	    }
	};
	assertEquals(0, comp.sample(3), 1e-5);
	assertEquals(0, compOnApprox.sample(3), 1e-5);
	assertEquals(0, compOnSuppl.sample(3), 1e-5);

	new NonStrictExpectations() {
	    {
		mock.getRandomProfile(m, n);
		result = ImmutableSet.of(ImmutableList.of(1, 4), ImmutableList.of(2, 2), ImmutableList.of(3, 3));
	    }
	};
	assertEquals(0.5, comp.sample(1), 1e-5);
	assertEquals(1.0 / 2, compOnApprox.sample(1), 1e-5);
	assertEquals(1.0, compOnSuppl.sample(2), 1e-5);
    }

}
