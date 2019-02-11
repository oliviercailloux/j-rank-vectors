package io.github.oliviercailloux.y2018.minimax;

import java.util.Set;

import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;

import io.github.oliviercailloux.y2018.j_voting.Alternative;

public class Regret {

	
	
	public static double getPMR(Alternative a, Alternative b, PrefKnowledge knowledge, ConstraintsOnWeights weights) {
		
		
		
		return 1;
	}
	
	
//	private int getWorstRanks(Alternative x, Alternative y) {
//		int rankx=0;
//		int ranky=0;
//		if (!pref.nodes().contains(x)) {
//		}
//		Graph <Alternative> trans=Graphs.transitiveClosure(pref);
//		if (trans.hasEdgeConnecting(x, y)) { 			// case 1 x>y
//			rankx+=pref.inDegree(x); // A
//			Set <Alternative> C= pref.predecessors(y);
//			C.removeAll(pref.successors(x));
//			rankx+=C.size(); // C
//			
//		}else if(trans.hasEdgeConnecting(y, x)) {
//			
//		}else {
//			
//		}
//		return 0;
//	}
	
	
}
