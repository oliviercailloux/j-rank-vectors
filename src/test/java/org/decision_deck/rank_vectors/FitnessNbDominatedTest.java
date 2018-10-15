package org.decision_deck.rank_vectors;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.Preorder;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class FitnessNbDominatedTest {

    @Test
    public void test43() {
	final AllRankVectors all = new AllRankVectors(4, 4);
	final Preorder<List<Integer>> pr = new Preorder<List<Integer>>();
	final Set<Pair<List<Integer>, List<Integer>>> paretoPairs = all.getParetoDominanceNonTransitive().asPairs();
	for (Pair<List<Integer>, List<Integer>> paretoPair : paretoPairs) {
	    pr.addTransitive(paretoPair.getElt1(), paretoPair.getElt2());
	}
	final List<Integer> rv1113 = ImmutableList.of(1, 1, 1, 3);
	final List<Integer> rv1122 = ImmutableList.of(1, 1, 2, 2);
	final List<Integer> rv1114 = ImmutableList.of(1, 1, 1, 4);
	final List<Integer> rv1222 = ImmutableList.of(1, 2, 2, 2);
	final List<Integer> rv2222 = ImmutableList.of(2, 2, 2, 2);
	final List<Integer> rv2233 = ImmutableList.of(2, 2, 3, 3);

	final FitnessNbDominated fitOpt = new FitnessNbDominated(all, pr, true, false);
	final FitnessNbDominated fitPess = new FitnessNbDominated(all, pr, false, false);
	fitOpt.updateConnexions();
	fitPess.updateConnexions();

	assertEquals(1 + 2, fitOpt.getFitness(rv1113, rv1122));
	assertEquals(10 + 2, fitOpt.getFitness(rv1114, rv2233));
	assertEquals(1, fitPess.getFitness(rv1113, rv1122));
	assertEquals(2, fitPess.getFitness(rv1114, rv2233));

	assertEquals(Pair.create(rv1113, rv2222), fitOpt.getFittest());

	pr.addTransitive(rv1114, rv2233);
	fitOpt.updateConnexions();
	fitPess.updateConnexions();
	assertEquals(0, fitOpt.getFitness(rv1114, rv2233));
	assertEquals(8, fitOpt.getFitness(rv1114, rv1222));
	assertEquals(0, fitPess.getFitness(rv1114, rv2233));
    }

    @Test
    public void testWeighted() {
	final AllRankVectors all = new AllRankVectors(4, 3);
	final Preorder<List<Integer>> pr = new Preorder<List<Integer>>();
	final Set<Pair<List<Integer>, List<Integer>>> paretoPairs = all.getParetoDominanceNonTransitive().asPairs();
	for (Pair<List<Integer>, List<Integer>> paretoPair : paretoPairs) {
	    pr.addTransitive(paretoPair.getElt1(), paretoPair.getElt2());
	}
	final List<Integer> rv113 = ImmutableList.of(1, 1, 3);
	final List<Integer> rv114 = ImmutableList.of(1, 1, 4);
	final List<Integer> rv222 = ImmutableList.of(2, 2, 2);

	final FitnessNbDominated fitOpt = new FitnessNbDominated(all, pr, true, false);
	final FitnessNbDominated fitPess = new FitnessNbDominated(all, pr, false, false);
	final FitnessNbDominated fitOptW = new FitnessNbDominated(all, pr, true, true);
	final FitnessNbDominated fitPessW = new FitnessNbDominated(all, pr, false, true);
	fitOpt.updateConnexions();
	fitPess.updateConnexions();
	fitOptW.updateConnexions();
	fitPessW.updateConnexions();

	assertEquals(6 + 0, fitOpt.getFitness(rv113, rv222));
	assertEquals(27 + 0, fitOptW.getFitness(rv113, rv222));
	assertEquals(0, fitPess.getFitness(rv113, rv222));
	assertEquals(0, fitPessW.getFitness(rv113, rv222));
	assertEquals(3 + 3, fitOpt.getFitness(rv114, rv222));
	assertEquals(15 + 7, fitOptW.getFitness(rv114, rv222));
	assertEquals(3, fitPess.getFitness(rv114, rv222));
	assertEquals(7, fitPessW.getFitness(rv114, rv222));

	assertEquals(Pair.create(rv113, rv222), fitOpt.getFittest());
	assertEquals(Pair.create(rv114, rv222), fitPess.getFittest());
	assertEquals(Pair.create(rv113, rv222), fitOptW.getFittest());
	assertEquals(Pair.create(rv114, rv222), fitPessW.getFittest());

	// we could also check with m=4, n=3: weighted is supposed to be different than uniform.
    }

    @Test
    public void test11() {
	final AllRankVectors all = new AllRankVectors(1, 1);
        final Preorder<List<Integer>> pr = new Preorder<List<Integer>>();
	final List<Integer> rv1 = ImmutableList.of(1);
    
        final FitnessNbDominated fitOpt = new FitnessNbDominated(all, pr, true, false);
        final FitnessNbDominated fitPess = new FitnessNbDominated(all, pr, false, false);
        fitOpt.updateConnexions();
        fitPess.updateConnexions();
    
	final Pair<List<Integer>, List<Integer>> pair = Pair.create(rv1, rv1);
	assertEquals(pair, fitOpt.getFittest());
	assertEquals(pair, fitPess.getFittest());
    }

}
