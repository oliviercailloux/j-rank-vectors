package io.github.oliviercailloux.y2018.minimax;

import static org.junit.Assert.assertTrue;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Generator;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class StrategyCurrentSolutionTest {

	@Test
	void testTau() throws Exception {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(3), Generator.getVoters(3));
		final StrategyCurrentSolution s = StrategyCurrentSolution.build(k);
		k.getProfile().get(new Voter(1)).asGraph().putEdge(new Alternative(1), new Alternative(2));
		k.getProfile().get(new Voter(1)).asGraph().putEdge(new Alternative(2), new Alternative(3));
		k.getProfile().get(new Voter(2)).asGraph().putEdge(new Alternative(1), new Alternative(2));
		k.getProfile().get(new Voter(3)).asGraph().putEdge(new Alternative(3), new Alternative(1));
//		k.getProfile().get(new Voter(3)).asGraph().putEdge(new Alternative(4), new Alternative(1));
	//	System.out.println(s.nextQuestion());
		assertTrue(s.nextQuestion().getType().equals(QuestionType.COMMITTEE_QUESTION));
		k.addConstraint(1, ComparisonOperator.LE, new Apint(2));
		assertTrue(s.nextQuestion().getType().equals(QuestionType.COMMITTEE_QUESTION));
		k.addConstraint(1, ComparisonOperator.LE, new Aprational (new Apint(3), new Apint(2)));
		assertTrue(s.nextQuestion().getType().equals(QuestionType.COMMITTEE_QUESTION));
		k.addConstraint(1, ComparisonOperator.LE, new Aprational (new Apint(5), new Apint(4)));
		assertTrue(s.nextQuestion().getType().equals(QuestionType.COMMITTEE_QUESTION));
	}
	
}
