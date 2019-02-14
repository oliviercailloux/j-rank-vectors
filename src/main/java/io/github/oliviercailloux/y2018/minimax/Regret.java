package io.github.oliviercailloux.y2018.minimax;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsBuilder;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class Regret {

	public static Alternative getMMRAlternative(PrefKnowledge knowledge) {
		List<Alternative> alt = knowledge.getAlternatives().asList();
		ListIterator<Alternative> i = alt.listIterator();
		Alternative minAlt = i.next();
		double minMR = getMR(minAlt, knowledge);
		double MR;
		while (i.hasNext()) {
			Alternative x = i.next();
			MR = getMR(x, knowledge);
			if (MR < minMR) {
				minMR = MR;
				minAlt = x;
			}
		}
		return minAlt;
	}

	private static double getMR(Alternative x, PrefKnowledge knowledge) {
		List<Alternative> alt = knowledge.getAlternatives().asList();
		ListIterator<Alternative> i = alt.listIterator();
		double maxPMR = Double.MIN_VALUE;
		double PMR;
		while (i.hasNext()) {
			Alternative y = i.next();
			if (!x.equals(y)) {
				PMR = getPMR(x, y, knowledge);
				if (PMR > maxPMR) {
					maxPMR = PMR;
				}
			}
		}
		return maxPMR;
	}

	private static double getPMR(Alternative x, Alternative y, PrefKnowledge knowledge) {
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
		ConstraintsOnWeights cow = knowledge.getConstraintsOnWeights();
		SumTermsBuilder sb = SumTerms.builder();
		for (int i = 1; i <= nbAlt; i++) {
			sb.add(cow.getTerm(yrank[i] - xrank[i], i));
		}
		SumTerms objective = sb.build();
		return cow.maximize(objective);
	}

	private static int[] getWorstRanks(Alternative x, Alternative y, MutableGraph<Alternative> pref) {
		int rankx = 0;
		int ranky = 0;
		MutableGraph<Alternative> trans = Graphs.copyOf(Graphs.transitiveClosure(pref));
		trans.nodes().forEach(n -> trans.removeEdge(n, n));
		HashSet<Alternative> A = new HashSet<>(pref.nodes());
		A.remove(x);
		A.remove(y);
		/**
		 * Case1 x >^p y : place as much alternatives as possible above x W1: worst than
		 * x (in >^p). W3: better than y. A: the whole set of alternatives. Then the
		 * better ones are B: A \ W1. The middle ones are M: W1 intersection W3.
		 **/
		if (trans.hasEdgeConnecting(x, y)) {
			HashSet<Alternative> W1 = new HashSet<>(trans.successors(x));
			W1.remove(y);
			HashSet<Alternative> W3 = new HashSet<>(trans.predecessors(y));
			W3.remove(x);
			HashSet<Alternative> B = new HashSet<>(A);
			B.removeAll(W1);
			rankx = B.size() + 1;

			HashSet<Alternative> M = new HashSet<>(W1);
			M.retainAll(W3);
			ranky = rankx + M.size() + 1;
		} else {
			/**
			 * Case2 y >^p x: place as much alternatives as possible between x and y Case3 x
			 * ?^p y: consider y >^p x W1: better than y. W2: worst than x. A: the whole set
			 * of alternatives. Then the better ones are W1. The middle ones are M: A \ W1 \
			 * W2. So the rank of x is |A \ W2|+1.
			 **/
			HashSet<Alternative> W1 = new HashSet<>(trans.predecessors(y));
			ranky = W1.size() + 1;

			HashSet<Alternative> W2 = new HashSet<>(trans.successors(x));
			A.removeAll(W2);
			rankx = A.size() + 2;
		}
		int[] r = { rankx, ranky };
		return r;
	}

}
