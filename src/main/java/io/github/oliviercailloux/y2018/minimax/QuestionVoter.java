package io.github.oliviercailloux.y2018.minimax;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class QuestionVoter {

	private QuestionType qt;
	private Voter voter;
	private Alternative a, b;

	public QuestionVoter(Voter voter, Alternative a, Alternative b) {
		this.qt = QuestionType.VOTER_QUESTION;
		this.voter = voter;
		this.a = a;
		this.b = b;
	}

	public QuestionType type() {
		return qt;
	}

	public Voter getVoter() {
		return this.voter;
	}

	/**
	 * A Question to the voter has the form a > b
	 *
	 * @return a
	 */
	public Alternative getFirstAlternative() {
		return a;
	}

	/**
	 * A Question to the voter has the form a > b
	 *
	 * @return a
	 */
	public Alternative getSecondAlternative() {
		return b;
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof QuestionVoter)) {
			return false;
		}
		final QuestionVoter q2 = (QuestionVoter) o2;
		return Objects.equals(voter, q2.voter) && Objects.equals(a, q2.a) && Objects.equals(b, q2.b);
	}

	@Override
	public int hashCode() {
		return Objects.hash(voter, a, b);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("voter", voter).add("a", a).add("b", b).toString();
	}

}
