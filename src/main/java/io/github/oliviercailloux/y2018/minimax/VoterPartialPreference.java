package io.github.oliviercailloux.y2018.minimax;

import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class VoterPartialPreference {

	public static VoterPartialPreference about(Voter voter, Set<Alternative> alternatives) {
		final MutableGraph<Alternative> graph = GraphBuilder.directed().build();
		for (Alternative alternative : alternatives) {
			graph.addNode(alternative);
		}
		final PrefGraph watcher = new PrefGraph(graph);
		final VoterPartialPreference v = new VoterPartialPreference(voter, watcher);
		watcher.setCallback(v);
		return v;
	}

	private Voter voter;
	private MutableGraph<Alternative> pref;
	private ImmutableGraph<Alternative> transitiveEquivalent;

	private VoterPartialPreference(Voter voter, MutableGraph<Alternative> pref) {
		this.voter = voter;
		this.pref = pref;
		transitiveEquivalent = null;
	}

	public MutableGraph<Alternative> asGraph() {
		return this.pref;
	}

	public ImmutableGraph<Alternative> asTransitiveGraph() {
		if (transitiveEquivalent == null) {
			final MutableGraph<Alternative> trans = Graphs.copyOf(Graphs.transitiveClosure(pref));
			for (Alternative a : trans.nodes()) {
				trans.removeEdge(a, a);
			}
			transitiveEquivalent = ImmutableGraph.copyOf(trans);
		}
		return transitiveEquivalent;
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof VoterPartialPreference)) {
			return false;
		}
		VoterPartialPreference p2 = (VoterPartialPreference) o2;
		return voter.equals(p2.voter) && pref.equals(p2.pref);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.voter, this.pref);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("voter", voter).add("pref", pref).toString();
	}

	void setGraphChanged() {
		transitiveEquivalent = null;
	}
}
