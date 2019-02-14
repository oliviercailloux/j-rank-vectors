package io.github.oliviercailloux.y2018.minimax;

import java.util.Objects;

import org.apfloat.Aprational;

import com.google.common.base.MoreObjects;

public class QuestionCommittee {

	private Aprational lambda;
	private int rank;

	public QuestionCommittee(Aprational lambda, int rank) {
		this.lambda = lambda;
		this.rank = rank;
	}

	/**
	 * Given a query of the type: (w_i − w_{i+1}) >= λ (w_{i+1} − w_{i+2}).
	 *
	 * @return λ
	 */
	public Aprational getLambda() {
		return lambda;
	}

	/**
	 * Given a query of the type: (w_i − w_{i+1}) >= λ (w_{i+1} − w_{i+2}).
	 *
	 * @return i
	 */
	public int getRank() {
		return rank;
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof QuestionCommittee)) {
			return false;
		}
		final QuestionCommittee q2 = (QuestionCommittee) o2;
		return Objects.equals(lambda, q2.lambda) && Objects.equals(rank, q2.rank);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lambda, rank);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("lambda", lambda).add("rank", rank).toString();
	}

}
