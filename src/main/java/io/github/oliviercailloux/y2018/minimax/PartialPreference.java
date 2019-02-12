package io.github.oliviercailloux.y2018.minimax;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class PartialPreference {

	public static PartialPreference about(Voter voter, Set<Alternative> alternatives) {
		final MutableGraph<Alternative> graph = GraphBuilder.directed().build();
		graph.nodes().addAll(alternatives);
		return new PartialPreference(voter, graph);
	}

	private Voter voter;
	private MutableGraph<Alternative> pref;

	public PartialPreference(Voter voter, Graph<Alternative> pref) {
		this.voter = voter;
		Preconditions.checkNotNull(pref);
		if (Graphs.hasCycle(pref)) {
			throw new IllegalArgumentException();
		}
		this.pref = Graphs.copyOf(pref);
	}

	public MutableGraph<Alternative> asGraph() {
		return this.pref;
	}

	public int hashCode() {
		return Objects.hashCode(this.voter, this.pref);
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof PartialPreference)) {
			return false;
		}
		PartialPreference pp = (PartialPreference) o;
		if (this.voter != pp.voter || !this.pref.equals(pp.pref)) {
			return false;
		}
		return true;
	}

	public String toString() {
		return this.voter + " : " + this.pref.toString();
	}

}
