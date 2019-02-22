package io.github.oliviercailloux.y2018.minimax;

public enum StrategyType {

	MINIMAX_MIN, MINIMAX_AVG, MINIMAX_WEIGHTED_AVG, RANDOM;

	@Override
	public String toString() {
		switch (this) {
		case MINIMAX_MIN:
			return "Minimax Strategy with min aggregation operator";
		case MINIMAX_AVG:
			return "Minimax Strategy with avg aggregation operator";
		case MINIMAX_WEIGHTED_AVG:
			return "Minimax Strategy with weighted average aggregation operator";
		case RANDOM:
			return "Random Strategy";
		default:
			throw new IllegalStateException();
		}
	}
	
}
