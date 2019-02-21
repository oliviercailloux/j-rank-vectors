package io.github.oliviercailloux.y2018.minimax.utils;

public enum AggregationOperator {
	MAX, AVG;

	@Override
	public String toString() {
		switch (this) {
		case MAX:
			return "max";
		case AVG:
			return "average";
		default:
			throw new IllegalStateException();
		}
	}
}
