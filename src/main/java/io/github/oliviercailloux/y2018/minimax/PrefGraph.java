package io.github.oliviercailloux.y2018.minimax;

import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.minimax.utils.ForwardingMutableGraph;

class PrefGraph extends ForwardingMutableGraph<Alternative> implements MutableGraph<Alternative> {

	private VoterPartialPreference v;

	PrefGraph(MutableGraph<Alternative> delegate) {
		super(delegate);
	}

	public void setCallback(VoterPartialPreference v) {
		this.v = v;
	}

	@Override
	public boolean addNode(Alternative node) {
		v.setGraphChanged();
		return super.addNode(node);
	}

	@Override
	public boolean putEdge(Alternative nodeU, Alternative nodeV) {
		v.setGraphChanged();
		return super.putEdge(nodeU, nodeV);
	}

	@Override
	public boolean removeEdge(Alternative nodeU, Alternative nodeV) {
		v.setGraphChanged();
		return super.removeEdge(nodeU, nodeV);
	}

	@Override
	public boolean removeNode(Alternative node) {
		v.setGraphChanged();
		return super.removeNode(node);
	}
}
