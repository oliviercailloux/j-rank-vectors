package io.github.oliviercailloux.y2018.minimax;

import java.util.HashSet;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsBuilder;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class Regret {

	public static double getMMR(PrefKnowledge knowledge, ConstraintsOnWeights weights) {
		double minMR = Double.MAX_VALUE;
		double MR;
		for (Alternative x : knowledge.getAlternatives()) {
			MR = getMR(x, knowledge, weights);
			if (MR < minMR) {
				minMR = MR;
			}
		}
		return minMR;
	}

	public static double getMR(Alternative x, PrefKnowledge knowledge, ConstraintsOnWeights weights) {
		double maxPMR = Double.MIN_VALUE;
		double PMR;
		for (Alternative y : knowledge.getAlternatives()) {
			PMR = getPMR(x, y, knowledge, weights);
			if (PMR > maxPMR) {
				maxPMR = PMR;
			}
		}
		return maxPMR;
	}

	public static double getPMR(Alternative x, Alternative y, PrefKnowledge knowledge, ConstraintsOnWeights weights) {
		MutableGraph<Alternative> pref;
		int nbAlt = knowledge.getAlternatives().size();
		int[] xrank = new int[nbAlt + 1];
		int[] yrank = new int[nbAlt + 1];
		int[] r;
		for (Voter v : knowledge.getProfile().keySet()) {
			pref = knowledge.getProfile().get(v).asGraph();
			r = getWorstRanks(x, y, pref);
			xrank[r[0]]++;
			yrank[r[1]]++;
		}
		ConstraintsOnWeights cow = ConstraintsOnWeights.withRankNumber(nbAlt);
		SumTermsBuilder sb = SumTerms.builder();
		for (int i = 1; i <= nbAlt; i++) {
			sb.add(cow.getTerm(yrank[i] - xrank[i], i));
		}
		SumTerms objective = sb.build();
		System.out.println(objective.toString());
		return cow.maximize(objective);
	}

	private static int[] getWorstRanks(Alternative x, Alternative y, MutableGraph<Alternative> pref) {
		int rankx = 0;
		int ranky = 0;
		MutableGraph<Alternative> trans = Graphs.copyOf(Graphs.transitiveClosure(pref));
		trans.nodes().forEach(n -> trans.removeEdge(n, n));
		HashSet<Alternative> A = new HashSet<Alternative>(pref.nodes());
		A.remove(x);
		A.remove(y);
		/**
		 * Case1 x >^p y : place as much alternatives as possible above x W1: worst than
		 * x (in >^p). W2: worst than y. W3: better than y. A: the whole set of
		 * alternatives. Then the better ones are B: A \ W1 \ W2. The middle ones are M:
		 * W1 intersection W3.
		 * 
		 * NOTE: W2 ⊆ W1 always => B: A \ W1
		 **/
		if (trans.hasEdgeConnecting(x, y)) {
			HashSet<Alternative> W1 = new HashSet<Alternative>(trans.successors(x));
			W1.remove(y);
			HashSet<Alternative> W3 = new HashSet<Alternative>(trans.predecessors(y));
			W3.remove(x);
			HashSet<Alternative> B = new HashSet<Alternative>(A);
			B.removeAll(W1);
			rankx = B.size() + 1;

			HashSet<Alternative> M = new HashSet<Alternative>(W1);
			M.retainAll(W3);
			ranky = rankx + M.size() + 1;
		} else {
			/**
			 * Case2 y >^p x: place as much alternatives as possible between x and y Case3 x
			 * ?^p y: consider y >^p x W1: better than y. W2: worst than x. A: the whole set
			 * of alternatives. Then the better ones are W1. The middle ones are M: A \ W1 \
			 * W2. So the rank of x is |A \ W2|+1.
			 **/
			HashSet<Alternative> W1 = new HashSet<Alternative>(trans.predecessors(y));
			ranky = W1.size() + 1;

			HashSet<Alternative> W2 = new HashSet<Alternative>(trans.successors(x));
			A.removeAll(W2);
			rankx = A.size() + 2;
		}
		int[] r = { rankx, ranky };
		return r;
	}

}
