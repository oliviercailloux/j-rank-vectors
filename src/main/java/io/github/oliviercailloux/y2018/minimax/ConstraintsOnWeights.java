package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.jlp.elements.Constraint;
import io.github.oliviercailloux.jlp.elements.Objective;
import io.github.oliviercailloux.jlp.elements.RangeOfDouble;
import io.github.oliviercailloux.jlp.elements.SumTerms;
import io.github.oliviercailloux.jlp.elements.SumTermsBuilder;
import io.github.oliviercailloux.jlp.elements.Term;
import io.github.oliviercailloux.jlp.elements.Variable;
import io.github.oliviercailloux.jlp.elements.VariableDomain;
import io.github.oliviercailloux.jlp.mp.MP;
import io.github.oliviercailloux.jlp.mp.MPBuilder;
import io.github.oliviercailloux.jlp.or_tools.OrToolsSolver;
import io.github.oliviercailloux.jlp.result.Result;

/**
 *
 * The weight of rank 1 is 1. If there is more than one rank, the weight of
 * lowest rank is 0.
 *
 * @author Olivier Cailloux
 *
 */
public class ConstraintsOnWeights {
	private final MPBuilder builder;
	private final OrToolsSolver solver;

	/**
	 * @param m at least one: the number of ranks, or equivalently, the number of
	 *          alternatives
	 */
	public static ConstraintsOnWeights withRankNumber(int m) {
		return new ConstraintsOnWeights(m);
	}

	private ConstraintsOnWeights(int m) {
		checkArgument(m >= 1);
		builder = MP.builder();
		builder.addVariable(
				Variable.of("w", VariableDomain.REAL_DOMAIN, RangeOfDouble.closed(1d, 1d), ImmutableSet.of(1)));
		for (int rank = 2; rank < m; ++rank) {
			builder.addVariable(
					Variable.of("w", VariableDomain.REAL_DOMAIN, RangeOfDouble.ZERO_ONE_RANGE, ImmutableSet.of(rank)));
		}
		if (m >= 2) {
			builder.addVariable(
					Variable.of("w", VariableDomain.REAL_DOMAIN, RangeOfDouble.closed(0d, 0d), ImmutableSet.of(1)));
		}
		solver = new OrToolsSolver();
	}

	/**
	 * Adds the constraint: (w_i − w_{i+1}) OP λ (w_{i+1} − w_{i+2}).
	 *
	 * @param i      1 ≤ i ≤ m-2
	 * @param op     the operator
	 * @param lambda a finite double
	 */
	public void addConstraint(int i, ComparisonOperator op, double lambda) {
		checkArgument(i >= 1);
		checkArgument(i <= getM() - 2);
		checkArgument(Double.isFinite(lambda));
		final SumTermsBuilder sumBuilder = SumTerms.builder();
		sumBuilder.addTerm(1, getVariable(i));
		sumBuilder.addTerm(-lambda - 1d, getVariable(i + 1));
		sumBuilder.addTerm(lambda, getVariable(i + 2));
		final Constraint cst = Constraint.of(sumBuilder.build(), op, 0d);
		builder.addConstraint(cst);
	}

	public Range<Double> getRange(int rank) {
		checkArgument(rank >= 1);
		checkArgument(rank <= getM());

		final double lBound;
		{
			builder.setObjective(Objective.min(SumTerms.of(1d, getVariable(rank))));
			final Result result = solver.solve(builder);
			switch (result.getResultStatus()) {
			case INFEASIBLE:
			case MEMORY_LIMIT_REACHED:
			case TIME_LIMIT_REACHED:
				throw new IllegalStateException();
			case UNBOUNDED:
				lBound = Double.NEGATIVE_INFINITY;
				break;
			case OPTIMAL:
				lBound = result.getSolution().get().getObjectiveValue();
				break;
			default:
				throw new AssertionError();
			}
		}

		final double uBound;
		{
			builder.setObjective(Objective.max(SumTerms.of(1d, getVariable(rank))));
			final Result result = solver.solve(builder);
			switch (result.getResultStatus()) {
			case INFEASIBLE:
			case MEMORY_LIMIT_REACHED:
			case TIME_LIMIT_REACHED:
				throw new IllegalStateException();
			case UNBOUNDED:
				uBound = Double.POSITIVE_INFINITY;
				break;
			case OPTIMAL:
				uBound = result.getSolution().get().getObjectiveValue();
				break;
			default:
				throw new AssertionError();
			}
		}

		return RangeOfDouble.using(lBound, uBound);
	}

	public int getM() {
		return builder.getVariables().size();
	}

	public Term getTerm(double coefficient, int rank) {
		return Term.of(coefficient, getVariable(rank));
	}

	public double maximize(SumTerms sum) {
		builder.setObjective(Objective.max(sum));
		return solver.solve(builder).getSolution().get().getObjectiveValue();
	}

	private Variable getVariable(int rank) {
		checkArgument(rank >= 1);
		checkArgument(rank <= getM());
		return builder.getVariable(getVariableDescription(rank));
	}

	private String getVariableDescription(int rank) {
		return Variable.getDefaultDescription("w", ImmutableList.of(rank));
	}
}
