package io.github.oliviercailloux.y2018.minimax;

public enum Answer {

	EQUAL, GREATER, LOWER;

	@Override
	public String toString() {
		switch (this) {
		case EQUAL:
			return "is equal to";
		case GREATER:
			return "is greater than";
		case LOWER:
			return "is lower than";
		default:
			throw new IllegalStateException();
		}
	}

}
