package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class RegretTest {

	@Test
	void testPMR() throws Exception {
		Voter v1 = new Voter(1);
		Voter v2 = new Voter(2);
		Voter v3 = new Voter(3);
		Set<Voter> voters = new HashSet<Voter>();
		voters.add(v1);
		voters.add(v2);
		voters.add(v3);
		
		Alternative a = new Alternative(1);
		Alternative b = new Alternative(2);
		Alternative c = new Alternative(3);
		Alternative d = new Alternative(4);
		Set<Alternative> alt = new HashSet<Alternative>();
		alt.add(a);
		alt.add(b);
		alt.add(c);
		alt.add(d);
		
		final MutableGraph<Alternative> pref1 = GraphBuilder.directed().build();
		pref1.putEdge(a, b);
		pref1.putEdge(b, c);
		pref1.putEdge(c, d);

		final MutableGraph<Alternative> pref2 = GraphBuilder.directed().build();
		pref2.putEdge(d, a);
		pref2.putEdge(a, b);
		pref2.putEdge(b, c);

		final MutableGraph<Alternative> pref3 = GraphBuilder.directed().build();
		pref3.putEdge(c, a);
		pref3.putEdge(a, b);
		pref3.putEdge(b, d);

		VoterPartialPreference vpp1 = new VoterPartialPreference (v1,pref1);
		VoterPartialPreference vpp2 = new VoterPartialPreference (v2,pref2);
		VoterPartialPreference vpp3 = new VoterPartialPreference (v3,pref3);
		
		PrefKnowledge knowledge = PrefKnowledge.given(alt, voters);
		
	}

	@Test
	void testRanksCase1() throws Exception {
		final MutableGraph<Alternative> pref = GraphBuilder.directed().build();
		Alternative x = new Alternative(1);
		Alternative y = new Alternative(2);

		Alternative a = new Alternative(3);
		Alternative u = new Alternative(4);
		Alternative c = new Alternative(5);
		Alternative b = new Alternative(6);
		Alternative d = new Alternative(7);
		Alternative f = new Alternative(8);
		Alternative a1 = new Alternative(9);
		Alternative b1 = new Alternative(10);
		Alternative c1 = new Alternative(11);
		Alternative d1 = new Alternative(12);
		Alternative u1 = new Alternative(13);

		pref.putEdge(a, x);
		pref.putEdge(x, b);
		pref.putEdge(x, d);
		pref.putEdge(b, y);
		pref.putEdge(c, y);
		pref.putEdge(y, f);
		pref.addNode(u);
		pref.putEdge(a1, a);
		pref.putEdge(c1, c);
		pref.putEdge(x, d1);
		pref.putEdge(b1, y);
		pref.putEdge(b, b1);
		pref.putEdge(a1, u1);

		/** changed the visibility of isolatedNodes(pref) in class Regret **/
		// assertEquals(7,Regret.getWorstRanks(x, y, pref)[0]);
		// assertEquals(10,Regret.getWorstRanks(x, y, pref)[1]);
	}

	@Test
	void testRanksCase2() throws Exception {
		final MutableGraph<Alternative> pref = GraphBuilder.directed().build();
		Alternative x = new Alternative(1);
		Alternative y = new Alternative(2);

		Alternative a = new Alternative(3);
		Alternative u = new Alternative(4);
		Alternative c = new Alternative(5);
		Alternative b = new Alternative(6);
		Alternative d = new Alternative(7);
		Alternative f = new Alternative(8);

		pref.putEdge(a, y);
		pref.putEdge(y, b);
		pref.putEdge(y, d);
		pref.putEdge(b, x);
		pref.putEdge(c, x);
		pref.putEdge(x, f);
		pref.addNode(u);

		/** changed the visibility of isolatedNodes(pref) in class Regret **/
		// assertEquals(7,Regret.getWorstRanks(x, y, pref)[0]);
		// assertEquals(2,Regret.getWorstRanks(x, y, pref)[1]);
	}

	@Test
	void newTestRanksCase2() throws Exception {
		final MutableGraph<Alternative> pref = GraphBuilder.directed().build();
		Alternative x = new Alternative(1);
		Alternative y = new Alternative(2);

		Alternative a = new Alternative(3);
		Alternative u = new Alternative(4);
		Alternative c = new Alternative(5);
		Alternative b = new Alternative(6);
		Alternative d = new Alternative(7);
		Alternative f = new Alternative(8);
		Alternative a1 = new Alternative(9);
		Alternative b1 = new Alternative(10);
		Alternative c1 = new Alternative(11);
		Alternative d1 = new Alternative(12);
		Alternative u1 = new Alternative(13);

		pref.putEdge(a, y);
		pref.putEdge(y, b);
		pref.putEdge(y, d);
		pref.putEdge(b, x);
		pref.putEdge(c, x);
		pref.putEdge(x, f);
		pref.addNode(u);
		pref.putEdge(a1, a);
		pref.putEdge(c1, c);
		pref.putEdge(x, d1);
		pref.putEdge(b1, x);
		pref.putEdge(b, b1);
		pref.putEdge(a1, u1);

		/** changed the visibility of isolatedNodes(pref) in class Regret **/
		// assertEquals(11,Regret.getWorstRanks(x, y, pref)[0]);
		// assertEquals(3,Regret.getWorstRanks(x, y, pref)[1]);
	}

	@Test
	void testRanksCase3() throws Exception {
		final MutableGraph<Alternative> pref = GraphBuilder.directed().build();
		Alternative x = new Alternative(1);
		Alternative y = new Alternative(2);

		Alternative a = new Alternative(3);
		Alternative u = new Alternative(4);
		Alternative c = new Alternative(5);
		Alternative b = new Alternative(6);
		Alternative d = new Alternative(7);
		Alternative f = new Alternative(8);

		pref.addNode(x);
		pref.addNode(y);
		pref.addNode(a);
		pref.addNode(c);
		pref.addNode(u);
		pref.addNode(d);
		pref.addNode(f);
		pref.addNode(b);

		/** changed the visibility of isolatedNodes(pref) in class Regret **/
		// assertEquals(8,Regret.getWorstRanks(x, y, pref)[0]);
		// assertEquals(1,Regret.getWorstRanks(x, y, pref)[1]);
	}

}
