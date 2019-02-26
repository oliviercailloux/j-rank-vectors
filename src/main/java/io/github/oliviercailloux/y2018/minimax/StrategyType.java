package io.github.oliviercailloux.y2018.minimax;

public enum StrategyType {

	MINIMAX_MIN, MINIMAX_AVG, MINIMAX_WEIGHTED_AVG, RANDOM, TWO_PHASES, CURRENT_SOLUTION;

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
		case TWO_PHASES:
			return "Two Phases Strategy";
		case CURRENT_SOLUTION:
			return "Current Solution Strategy";
		default:
			throw new IllegalStateException();
		}
	}
	
}
