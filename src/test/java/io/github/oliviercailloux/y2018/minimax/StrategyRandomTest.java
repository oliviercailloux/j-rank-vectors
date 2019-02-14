package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Generator;
import io.github.oliviercailloux.y2018.j_voting.Voter;

class StrategyRandomTest {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(StrategyRandomTest.class);

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

	@Test
	void testThreeAltsTwoVKnown() {
		final StrategyRandom s = StrategyRandom.build();
		final Random notRandom = new Random(0);
		s.setRandom(notRandom);
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(3), Generator.getVoters(2));
		final MutableGraph<Alternative> g1 = k.getProfile().get(new Voter(1)).asGraph();
		g1.putEdge(new Alternative(1), new Alternative(2));
		g1.putEdge(new Alternative(2), new Alternative(3));
		final MutableGraph<Alternative> g2 = k.getProfile().get(new Voter(2)).asGraph();
		g2.putEdge(new Alternative(1), new Alternative(2));
		g2.putEdge(new Alternative(2), new Alternative(3));
		assertEquals(new Question(new QuestionCommittee(new Aprational(new Apint(3), new Apint(2)), 1)),
				s.getQuestion(k));
	}

	@Test
	void testThreeAltsTwoVAllKnown() {
		final StrategyRandom s = StrategyRandom.build();
		final Random notRandom = new Random(0);
		s.setRandom(notRandom);
		final PrefKnowledge k = PrefKnowledge.given(Generator.getAlternatives(3), Generator.getVoters(2));
		final MutableGraph<Alternative> g1 = k.getProfile().get(new Voter(1)).asGraph();
		g1.putEdge(new Alternative(1), new Alternative(2));
		g1.putEdge(new Alternative(2), new Alternative(3));
		final MutableGraph<Alternative> g2 = k.getProfile().get(new Voter(2)).asGraph();
		g2.putEdge(new Alternative(1), new Alternative(2));
		g2.putEdge(new Alternative(2), new Alternative(3));
		k.addConstraint(1, ComparisonOperator.EQ, new Apint(1));
		assertThrows(IllegalArgumentException.class, () -> s.getQuestion(k));
	}

}
