package io.github.oliviercailloux.y2018.minimax;

public interface Strategy {
	/**
	 * Returns the next question that this strategy thinks is best asking.
	 *
	 * @return a question.
	 * @throws IllegalStateException if everything is known.
	 */
	public Question nextQuestion() throws IllegalStateException;
}
