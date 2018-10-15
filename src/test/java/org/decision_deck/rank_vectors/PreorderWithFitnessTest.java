package org.decision_deck.rank_vectors;


/**
 * TODO reuse these tests for other classes.
 * 
 * @author Olivier Cailloux
 * 
 */
public class PreorderWithFitnessTest {

    // @Test
    // public void testTr1() {
    // final PreorderWithFitness pr = new PreorderWithFitness(new AllRankVectors(1, 1));
    // assertFalse(pr.asPairs().isEmpty());
    // final ImmutableList<Integer> rv1 = ImmutableList.of(1);
    // pr.addEqTransitive(rv1, rv1);
    // assertEquals(1, pr.asPairs().size());
    // assertEquals(ImmutableSet.of(Pair.create(rv1, rv1)), pr.asPairs());
    // }
    //
    // @Test
    // public void testTr1N() {
    // final int n = 16;
    // final PreorderWithFitness pr = new PreorderWithFitness(new AllRankVectors(1, n));
    // final List<Integer> rv1 = Collections.nCopies(n, 1);
    // assertEquals(ImmutableSet.of(Pair.create(rv1, rv1)), pr.asPairs());
    // pr.addEqTransitive(rv1, rv1);
    // assertEquals(ImmutableSet.of(Pair.create(rv1, rv1)), pr.asPairs());
    // }
    //
    // @Test
    // public void testTrM1() {
    // final int m = 5;
    // final AllRankVectors all = new AllRankVectors(m, 1);
    // final PreorderWithFitness pr = new PreorderWithFitness(all);
    // final List<Integer> rv1 = ImmutableList.of(1);
    // final List<Integer> rv2 = ImmutableList.of(2);
    // final List<Integer> rv3 = ImmutableList.of(3);
    // final List<Integer> rv4 = ImmutableList.of(4);
    // final List<Integer> rv5 = ImmutableList.of(5);
    // assertTrue(pr.asPairs().contains(Pair.create(rv1, rv1)));
    // assertTrue(pr.asPairs().contains(Pair.create(rv3, rv3)));
    // assertTrue(pr.asPairs().contains(Pair.create(rv5, rv5)));
    // assertEquals(5, pr.asPairs().size());
    // pr.addEqTransitive(rv1, rv1);
    // assertEquals(5, pr.asPairs().size());
    // /** 2 = 3, 3 ≥ 4, 4 ≥ 5. */
    // pr.addEqTransitive(rv2, rv3);
    // assertEquals(7, pr.asPairs().size());
    // pr.addTransitive(rv3, rv4);
    // pr.addTransitive(rv4, rv5);
    // assertEquals(12, pr.asPairs().size());
    // assertTrue(pr.asPairs().contains(Pair.create(rv2, rv4)));
    // assertTrue(pr.asPairs().contains(Pair.create(rv2, rv5)));
    // assertFalse(pr.asPairs().contains(Pair.create(rv4, rv2)));
    // /** 5 ≥ 2, creates a big equivalence class {2, 3, 4, 5}. */
    // pr.addTransitive(rv5, rv2);
    // assertTrue(pr.asPairs().contains(Pair.create(rv4, rv2)));
    // assertFalse(pr.asPairs().contains(Pair.create(rv4, rv1)));
    // assertEquals(1 + 4 * 4, pr.asPairs().size());
    //
    // /** 1 = 3, creates a unique equivalence class. */
    // pr.addEqTransitive(rv1, rv3);
    // assertEquals(5 * 5, pr.asPairs().size());
    // assertTrue(pr.asPairs().contains(Pair.create(rv4, rv1)));
    //
    // }
    //
    // @Test
    // public void testFitness() {
    // final int m = 5;
    // final AllRankVectors all = new AllRankVectors(m, 1);
    // final PreorderWithFitness pr = new PreorderWithFitness(all);
    // final List<Integer> rv1 = ImmutableList.of(1);
    // final List<Integer> rv2 = ImmutableList.of(2);
    // final List<Integer> rv3 = ImmutableList.of(3);
    // final List<Integer> rv4 = ImmutableList.of(4);
    // final List<Integer> rv5 = ImmutableList.of(5);
    //
    // pr.addTransitive(rv4, rv5);
    // assertEquals(Pair.create(rv1, rv4), pr.getFittest());
    //
    // pr.addEqTransitive(rv2, rv3);
    // assertEquals(Pair.create(rv2, rv4), pr.getFittest());
    //
    // /** (2 = 3) > 4 > 5. */
    // pr.addTransitive(rv3, rv4);
    // assertEquals(5, pr.getFitness(rv1, rv2));
    // assertEquals(5, pr.getFitness(rv1, rv3));
    // assertEquals(3, pr.getFitness(rv1, rv4));
    // assertEquals(2, pr.getFitness(rv1, rv5));
    // assertEquals(0, pr.getFitness(rv2, rv2));
    // assertEquals(0, pr.getFitness(rv2, rv3));
    // assertEquals(0, pr.getFitness(rv2, rv5));
    // assertEquals(0, pr.getFitness(rv4, rv5));
    // assertEquals(Pair.create(rv1, rv2), pr.getFittest());
    //
    // /** 5 ≥ 2, creates a big equivalence class {2, 3, 4, 5}. */
    // pr.addTransitive(rv5, rv2);
    // assertEquals(5, pr.getFitness(rv1, rv2));
    // assertEquals(5, pr.getFitness(rv1, rv3));
    // assertEquals(5, pr.getFitness(rv1, rv4));
    // assertEquals(5, pr.getFitness(rv1, rv5));
    // assertEquals(0, pr.getFitness(rv2, rv2));
    // assertEquals(0, pr.getFitness(rv2, rv3));
    // assertEquals(0, pr.getFitness(rv2, rv5));
    // assertEquals(0, pr.getFitness(rv4, rv5));
    // assertEquals(Pair.create(rv1, rv2), pr.getFittest());
    //
    // /** 1 = 3, creates a unique equivalence class. */
    // pr.addEqTransitive(rv1, rv3);
    // assertEquals(0, pr.getFitness(rv1, rv2));
    // assertEquals(0, pr.getFitness(rv1, rv3));
    // assertEquals(0, pr.getFitness(rv1, rv4));
    // assertEquals(0, pr.getFitness(rv1, rv5));
    // assertEquals(0, pr.getFitness(rv2, rv2));
    // assertEquals(0, pr.getFitness(rv2, rv3));
    // assertEquals(0, pr.getFitness(rv2, rv5));
    // assertEquals(0, pr.getFitness(rv4, rv5));
    // assertEquals(Pair.create(rv1, rv2), pr.getFittest());
    // }
    //
    // @Test
    // public void testFitness43() throws Exception {
    // final int m = 4;
    // final int n = 3;
    // final AllRankVectors all = new AllRankVectors(m, n);
    // final PreorderWithFitness pr = new PreorderWithFitness(all);
    // final Set<Pair<List<Integer>, List<Integer>>> paretoPairs = all.getParetoDominanceNonTransitive().asPairs();
    // for (Pair<List<Integer>, List<Integer>> paretoPair : paretoPairs) {
    // pr.addTransitive(paretoPair.getElt1(), paretoPair.getElt2());
    // }
    //
    // final List<Integer> rv114 = ImmutableList.of(1, 1, 4);
    // final List<Integer> rv222 = ImmutableList.of(2, 2, 2);
    // final List<Integer> rv333 = ImmutableList.of(3, 3, 3);
    // assertEquals(8, pr.getFitness(rv114, rv333));
    // assertEquals(8, pr.getFitness(rv222, rv114));
    // pr.addTransitive(rv114, rv333);
    // assertEquals(0, pr.getFitness(rv114, rv333));
    // }

}
