package org.decision_deck.rank_vectors;

public enum QuestionType {

	VOTER_QUESTION, COMMITTEE_QUESTION;

	@Override
	public String toString() {
		switch (this) {
		case VOTER_QUESTION:
			return "Question to a voter";
		case COMMITTEE_QUESTION:
			return "Question to the committee";
		default:
			throw new IllegalStateException();
		}
	}
	
}
