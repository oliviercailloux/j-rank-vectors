package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Generator;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class StrategyMiniMaxTest {

	@Test
	void testOneAlt() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(1), Generator.getVoters(1));
		final StrategyMiniMax s = StrategyMiniMax.build(k);
		assertThrows(IllegalArgumentException.class, () -> s.nextQuestion());
	}

	@Test
	void testTwoAltsOneVKnown() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(2), Generator.getVoters(1));
		final StrategyMiniMax s = StrategyMiniMax.build(k);
		k.getProfile().get(new Voter(1)).asGraph().putEdge(new Alternative(1), new Alternative(2));
		assertThrows(IllegalArgumentException.class, () -> s.nextQuestion());
	}

	@Test
	void testTwoAltsOneV() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(2), Generator.getVoters(1));
		final StrategyMiniMax s = StrategyMiniMax.build(k);
		final Question q1 = new Question(new QuestionVoter(new Voter(1), new Alternative(1), new Alternative(2)));
		final Question q2 = new Question(new QuestionVoter(new Voter(1), new Alternative(2), new Alternative(1)));
		assertTrue(s.nextQuestion().equals(q2));
	}

	@Test
	void testTwoAltsTwoVsOneKnown() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(2), Generator.getVoters(2));
		final StrategyMiniMax s = StrategyMiniMax.build(k);
		k.getProfile().get(new Voter(1)).asGraph().putEdge(new Alternative(1), new Alternative(2));
		assertEquals(new Question(new QuestionVoter(new Voter(2), new Alternative(1), new Alternative(2))),
				s.nextQuestion());
	}
	
}
