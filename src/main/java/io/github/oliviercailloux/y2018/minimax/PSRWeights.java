package io.github.oliviercailloux.y2018.minimax;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apfloat.Aprational;

import com.google.common.base.Preconditions;

public class PSRWeights {

	private final List<Double> weights;
	private final double upperBound = 1;
	private final double lowerBound = 0;

	public static PSRWeights given(List<Double> weights) {
		return new PSRWeights(weights);
	}

	private PSRWeights(List<Double> weights) {
		Preconditions.checkNotNull(weights);
		if (weights.size() > 1) {
			if (weights.get(0) != upperBound || weights.get(weights.size() - 1) != lowerBound
					|| !checkConvexity(weights)) {
				throw new IllegalArgumentException("Sequence not valid");
			}
		}
		this.weights = new LinkedList<>();
		this.weights.addAll(weights);
	}

	@SuppressWarnings("unused")
	private boolean checkMonotonicity(List<Double> weight) {
		Iterator<Double> ls = weight.iterator();
		double curr;
		double prev = ls.next();
		while (ls.hasNext()) {
			curr = ls.next();
			if (prev < curr) {
				return false;
			}
			prev = curr;
		}
		return true;
	}

	private boolean checkConvexity(List<Double> weight) {
		double wi1, wi2, wi3;
		for (int i = 0; i < weight.size() - 2; i++) {
			wi1 = weight.get(i);
			wi2 = weight.get(i + 1);
			wi3 = weight.get(i + 2);
			if ((wi1 - wi2) < (wi2 - wi3)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieves the weight associated to the given rank. The first position of the
	 * ranking is 1.
	 *
	 * @return the weight of the given rank
	 */
	public double getWeightAtRank(int rank) {
		return this.weights.get(rank - 1);
	}

	public List<Double> getWeights() {
		return this.weights;
	}

	/**
	 * Given a query d * (w_i − w_{i+1}) >= n * (w_{i+1} − w_{i+2}) where n/d = λ
	 *
	 * @return if the term on the left is GREATER, EQUAL or LOWER than the right one
	 */
	public Answer askQuestion(QuestionCommittee qc) {
		int i = qc.getRank();
		Aprational lambda = qc.getLambda();
		double left = lambda.denominator().intValue() * (weights.get(i - 1) - weights.get(i));
		double right = lambda.numerator().intValue() * (weights.get(i) - weights.get(i + 1));
		if (left > right) {
			return Answer.GREATER;
		} else if (left == right) {
			return Answer.EQUAL;
		}
		return Answer.LOWER;
	}

	@Override
	public String toString() {
		return this.weights.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof PSRWeights)) {
			return false;
		}
		PSRWeights w = (PSRWeights) o;
		return w.weights.equals(this.weights);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.weights);
	}

}