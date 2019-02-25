package io.github.oliviercailloux.y2018.minimax;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.oliviercailloux.y2018.j_voting.Generator;


public class StrategyTwoPhasesTest {

	@Test
	void testFourAlts() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(4), Generator.getVoters(2));
		final StrategyTwoPhases s = StrategyTwoPhases.build(k);
		assertEquals(QuestionType.COMMITTEE_QUESTION, s.nextQuestion().getType());
		assertEquals(QuestionType.COMMITTEE_QUESTION, s.nextQuestion().getType());
		assertEquals(QuestionType.VOTER_QUESTION, s.nextQuestion().getType());
	}
	
	@Test
	void testTenAlts() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(10), Generator.getVoters(2));
		final StrategyTwoPhases s = StrategyTwoPhases.build(k);
		for(int i =0; i<k.getAlternatives().size()-2;i++) {
			assertEquals(QuestionType.COMMITTEE_QUESTION, s.nextQuestion().getType());
		}
		assertEquals(QuestionType.VOTER_QUESTION, s.nextQuestion().getType());
	}
	
}
