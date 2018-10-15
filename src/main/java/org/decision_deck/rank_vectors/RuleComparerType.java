package org.decision_deck.rank_vectors;

public enum RuleComparerType {
	SUPPL_WINNERS, WO_SCORE, WO_SCORE_SUPPL, WO_SCORE_APPROX_WINNER;

	@Override
	public String toString() {
		switch (this) {
		case SUPPL_WINNERS:
			return "avg suppl winners, ratio";
		case WO_SCORE:
			return "wo score";
		case WO_SCORE_SUPPL:
			return "wo score suppl";
		case WO_SCORE_APPROX_WINNER:
			return "wo score on approx";
		default:
			throw new IllegalStateException();
		}
	}
}