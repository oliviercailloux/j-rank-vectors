package io.github.oliviercailloux.y2018.minimax;

import java.util.HashSet;
import java.util.Set;

import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class Regret {

	public static double getMMR(PrefKnowledge knowledge, ConstraintsOnWeights weights) {
		return 1;
	}

	public static double getMR(Alternative a, PrefKnowledge knowledge, ConstraintsOnWeights weights) {
		return 1;
	}

	public static double getPMR(Alternative a, Alternative b, PrefKnowledge knowledge, ConstraintsOnWeights weights) {
		Voter v = new Voter(1);
		MutableGraph<Alternative> pref = knowledge.getProfile().get(v).asGraph();

		return 1;
	}

	public static int[] getWorstRanks(Alternative x, Alternative y, MutableGraph<Alternative> pref) {
		int rankx = 0;
		int ranky = 0;
		/**
		 * In case x >^p y. W1: worst than x (in >^p). W2: worst than y. W3:better than
		 * y. A:the whole set of alternatives. Then the better ones are A \ W1 \ W2. The
		 * middle ones are W1 intersection W3.
		 **/
		Graph<Alternative> trans = Graphs.transitiveClosure(pref);
		if (trans.hasEdgeConnecting(x, y)) {
			/** case1 x>y: place as much alternatives as possible above x **/
			rankx += pref.inDegree(x); // A
			HashSet<Alternative> C = new HashSet<Alternative>(pref.predecessors(y));
			C.removeAll(pref.successors(x));
			rankx += C.size(); // C
			rankx += isolatedNodes(pref).size(); // U
			rankx++;

			ranky = rankx + 1;
			HashSet<Alternative> B = new HashSet<Alternative>(pref.predecessors(y));
			B.retainAll(pref.successors(x));
			ranky += B.size(); // B
		} else {
			/**
			 * case2 y>x: place as much alternatives as possible between x and y case3 x?y:
			 * consider y>x
			 **/
			ranky += pref.inDegree(y) + 1; // A

			rankx = ranky + 1;
			Set<Alternative> isNodes = isolatedNodes(pref);
			isNodes.remove(x);
			isNodes.remove(y);
			rankx += isNodes.size(); // U
			System.out.println(rankx);
			HashSet<Alternative> CBD = new HashSet<Alternative>(trans.predecessors(x));
			System.out.println(trans);
			CBD.removeAll(pref.predecessors(y));
			System.out.println(CBD);
			CBD.remove(y);
			System.out.println(CBD);
			rankx += CBD.size(); // C U B U D
			System.out.println(CBD);
		}
		int[] r = { rankx, ranky };
		return r;
	}

	private static Set<Alternative> isolatedNodes(MutableGraph<Alternative> pref) {
		Set<Alternative> notRanked = new HashSet<Alternative>();
		for (Alternative x : pref.nodes()) {
			if (pref.predecessors(x).size() == 0 && pref.successors(x).size() == 0) {
				notRanked.add(x);
			}
		}
		return notRanked;
	}

}
