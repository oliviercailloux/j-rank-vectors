package io.github.oliviercailloux.y2018.minimax;

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
