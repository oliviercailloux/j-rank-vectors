package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Generator;
import io.github.oliviercailloux.y2018.j_voting.Voter;

class StrategyRandomTest {

	@Test
	void testOneAlt() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(1), Generator.getVoters(1));
		final StrategyRandom s = StrategyRandom.build(k);
		final Random notRandom = new Random(0);
		s.setRandom(notRandom);
		assertThrows(IllegalArgumentException.class, () -> s.nextQuestion());
	}

	@Test
	void testTwoAltsOneVKnown() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(2), Generator.getVoters(1));
		final StrategyRandom s = StrategyRandom.build(k);
		final Random notRandom = new Random(0);
		s.setRandom(notRandom);
		k.getProfile().get(new Voter(1)).asGraph().putEdge(new Alternative(1), new Alternative(2));
		assertThrows(IllegalArgumentException.class, () -> s.nextQuestion());
	}

	@Test
	void testTwoAltsOneV() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(2), Generator.getVoters(1));
		final StrategyRandom s = StrategyRandom.build(k);
		final Random notRandom = new Random(0);
		s.setRandom(notRandom);
		assertEquals(new Question(new QuestionVoter(new Voter(1), new Alternative(1), new Alternative(2))),
				s.nextQuestion());
	}

	@Test
	void testTwoAltsTwoVsOneKnown() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(2), Generator.getVoters(2));
		final StrategyRandom s = StrategyRandom.build(k);
		final Random notRandom = new Random(0);
		s.setRandom(notRandom);
		k.getProfile().get(new Voter(1)).asGraph().putEdge(new Alternative(1), new Alternative(2));
		assertEquals(new Question(new QuestionVoter(new Voter(2), new Alternative(1), new Alternative(2))),
				s.nextQuestion());
	}

	@Test
	void testThreeAltsOneVKnown() {
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(3), Generator.getVoters(1));
		final StrategyRandom s = StrategyRandom.build(k);
		final Random notRandom = new Random(0);
		s.setRandom(notRandom);
		final MutableGraph<Alternative> g = k.getProfile().get(new Voter(1)).asGraph();
		g.putEdge(new Alternative(1), new Alternative(2));
		g.putEdge(new Alternative(2), new Alternative(3));
		assertThrows(IllegalArgumentException.class, () -> s.nextQuestion());
	}

}
