package io.github.oliviercailloux.y2018.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.y2018.j_voting.Alternative;

public class RegretTest {

	
	
	
	@Test
	void testRanksCase1() throws Exception {
		final MutableGraph<Alternative> pref= GraphBuilder.directed().build();
		Alternative x=new Alternative(1);
		Alternative y=new Alternative(2);
		
		Alternative a=new Alternative(3);
		Alternative u=new Alternative(4);
		Alternative c=new Alternative(5);
		Alternative b=new Alternative(6);
		Alternative d=new Alternative(7);
		Alternative f=new Alternative(8);
		
		pref.putEdge(a,x);
		pref.putEdge(x,b);
		pref.putEdge(x,d);
		pref.putEdge(b,y);
		pref.putEdge(c,y);
		pref.putEdge(y,f);
		pref.addNode(u);
		
		/** changed the visibility of isolatedNodes(pref) in class Regret**/
		assertEquals(4,Regret.getWorstRanks(x, y, pref)[0]);
		assertEquals(6,Regret.getWorstRanks(x, y, pref)[1]);
	}
	
	@Test
	void testRanksCase2() throws Exception {
		final MutableGraph<Alternative> pref= GraphBuilder.directed().build();
		Alternative x=new Alternative(1);
		Alternative y=new Alternative(2);
		
		Alternative a=new Alternative(3);
		Alternative u=new Alternative(4);
		Alternative c=new Alternative(5);
		Alternative b=new Alternative(6);
		Alternative d=new Alternative(7);
		Alternative f=new Alternative(8);
		
		pref.putEdge(a,y);
		pref.putEdge(y,b);
		pref.putEdge(y,d);
		pref.putEdge(b,x);
		pref.putEdge(c,x);
		pref.putEdge(x,f);
		pref.addNode(u);
		
		/** changed the visibility of isolatedNodes(pref) in class Regret**/
		assertEquals(7,Regret.getWorstRanks(x, y, pref)[0]);
		assertEquals(2,Regret.getWorstRanks(x, y, pref)[1]);
	}
	
	
	@Test
	void testRanksCase3() throws Exception {
		final MutableGraph<Alternative> pref= GraphBuilder.directed().build();
		Alternative x=new Alternative(1);
		Alternative y=new Alternative(2);
		
		Alternative a=new Alternative(3);
		Alternative u=new Alternative(4);
		Alternative c=new Alternative(5);
		Alternative b=new Alternative(6);
		Alternative d=new Alternative(7);
		Alternative f=new Alternative(8);
		
		pref.addNode(x);
		pref.addNode(y);
		pref.addNode(a);
		pref.addNode(c);
		pref.addNode(u);
		pref.addNode(d);
		pref.addNode(f);
		pref.addNode(b);
		pref.putEdge(a, x);
		
		/** changed the visibility of isolatedNodes(pref) in class Regret**/
		assertEquals(8,Regret.getWorstRanks(x, y, pref)[0]);
		assertEquals(1,Regret.getWorstRanks(x, y, pref)[1]);
	}
	
	
	@Test
	void testNodes() throws Exception {
		final MutableGraph<Alternative> pref= GraphBuilder.directed().build();
		Alternative a=new Alternative(1);
		pref.addNode(a);
		pref.putEdge(new Alternative(2), new Alternative(3));
		Set<Alternative> notRanked = new HashSet<Alternative>();
		notRanked.add(a);
		/** changed the visibility of isolatedNodes(pref) in class Regret**/
		//assertEquals(notRanked,Regret.isolatedNodes(pref));
	}
	
}
