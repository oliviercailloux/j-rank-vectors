package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

class VoterPartialPreferenceTest {

	@Test
	void test() {
		final VoterPartialPreference p = VoterPartialPreference.about(new Voter(1),
				ImmutableSet.of(new Alternative(1), new Alternative(2), new Alternative(3)));
		assertEquals(0, p.asGraph().edges().size());
		assertEquals(0, p.asTransitiveGraph().edges().size());
		p.asGraph().putEdge(new Alternative(1), new Alternative(2));
		assertEquals(1, p.asGraph().edges().size());
		assertEquals(1, p.asTransitiveGraph().edges().size());
		p.asGraph().putEdge(new Alternative(2), new Alternative(3));
		assertEquals(2, p.asGraph().edges().size());
		assertEquals(3, p.asTransitiveGraph().edges().size());
	}

}
