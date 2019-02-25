package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.AprationalMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;
import com.google.common.graph.Graph;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;
import io.github.oliviercailloux.y2018.minimax.utils.AggregationOperator;
import io.github.oliviercailloux.y2018.minimax.utils.AggregationOperator.AggOps;

public class StrategyCurrentSolution implements Strategy {
	private PrefKnowledge knowledge;
	private static AggOps op;
	private static double w1;
	private static double w2;

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(StrategyMiniMax.class);

	public static StrategyCurrentSolution build(PrefKnowledge knowledge) {
		op = AggOps.MAX;
		return new StrategyCurrentSolution(knowledge);
	}

	public static StrategyCurrentSolution build(PrefKnowledge knowledge, AggOps operator) {
		checkArgument(!operator.equals(AggOps.WEIGHTED_AVERAGE));
		op = operator;
		return new StrategyCurrentSolution(knowledge);
	}

	public static StrategyCurrentSolution build(PrefKnowledge knowledge, AggOps operator, double w_1, double w_2) {
		checkArgument(operator.equals(AggOps.WEIGHTED_AVERAGE));
		checkArgument(w_1 > 0);
		checkArgument(w_2 > 0);
		op = operator;
		w1 = w_1;
		w2 = w_2;
		return new StrategyCurrentSolution(knowledge);
	}

	private StrategyCurrentSolution(PrefKnowledge knowledge) {
		this.knowledge = knowledge;
	}

	@Override
	public Question nextQuestion() {
		Question nextQ;
		final int m = knowledge.getAlternatives().size();

		checkArgument(m >= 2, "Questions can be asked only if there are at least two alternatives.");

		if (Regret.tau1SmallerThanTau2(knowledge)) {
			/** Ask a question to the committee about the most valuable rank */
			PSRWeights wTau = Regret.getWTau();
			PSRWeights wBar = Regret.getWBar();
			double maxDiff = wBar.getWeightAtRank(1) - wTau.getWeightAtRank(1);
			int maxRank = 1;
			for (int i = 2; i <= m; i++) {
				double diff = Math.abs(wBar.getWeightAtRank(i) - wTau.getWeightAtRank(i));
				if (diff > maxDiff) {
					maxDiff = diff;
					maxRank = i;
				}
			}
			final Range<Aprational> lambdaRange = knowledge.getLambdaRange(maxRank);
			final Aprational avg = AprationalMath.sum(lambdaRange.lowerEndpoint(), lambdaRange.upperEndpoint())
					.divide(new Apint(2));
			nextQ = Question.toCommittee(QuestionCommittee.given(avg, maxRank));
		} else {
			Random random = new Random();
			Voter voter = Regret.getCandidateVoter(random.nextBoolean());
			assert voter != null;
			final ArrayList<Alternative> altsRandomOrder = new ArrayList<>(knowledge.getAlternatives());
			Collections.shuffle(altsRandomOrder, random);
			final Graph<Alternative> graph = knowledge.getProfile().get(voter).asTransitiveGraph();
			final Optional<Alternative> withIncomparabilities = altsRandomOrder.stream()
					.filter((a1) -> graph.adjacentNodes(a1).size() != m - 1).findAny();
			assert withIncomparabilities.isPresent();
			final Alternative a1 = withIncomparabilities.get();
			final Optional<Alternative> incomparable = altsRandomOrder.stream()
					.filter((a2) -> !a1.equals(a2) && !graph.adjacentNodes(a1).contains(a2)).findAny();
			assert incomparable.isPresent();
			final Alternative a2 = incomparable.get();
			nextQ = Question.toVoter(voter, a1, a2);

		}

		return nextQ;
	}

	public double getScore(Question q) {
		PrefKnowledge yesKnowledge = PrefKnowledge.copyOf(knowledge);
		PrefKnowledge noKnowledge = PrefKnowledge.copyOf(knowledge);
		double yesMMR = 0;
		double noMMR = 0;
		if (q.getType().equals(QuestionType.VOTER_QUESTION)) {
			QuestionVoter qv = q.getQuestionVoter();
			Alternative a = qv.getFirstAlternative();
			Alternative b = qv.getSecondAlternative();

			yesKnowledge.getProfile().get(qv.getVoter()).asGraph().putEdge(a, b);
			Regret.getMMRAlternatives(yesKnowledge);
			yesMMR = Regret.getMMR();

			noKnowledge.getProfile().get(qv.getVoter()).asGraph().putEdge(b, a);
			Regret.getMMRAlternatives(noKnowledge);
			noMMR = Regret.getMMR();
		} else if (q.getType().equals(QuestionType.COMMITTEE_QUESTION)) {

			QuestionCommittee qc = q.getQuestionCommittee();
			Aprational lambda = qc.getLambda();
			int rank = qc.getRank();

			yesKnowledge.addConstraint(rank, ComparisonOperator.GE, lambda);
			noKnowledge.addConstraint(rank, ComparisonOperator.LE, lambda);

			Regret.getMMRAlternatives(yesKnowledge);
			yesMMR = Regret.getMMR();

			Regret.getMMRAlternatives(noKnowledge);
			noMMR = Regret.getMMR();
		}

		switch (op) {
		case MAX:
			return AggregationOperator.getMax(yesMMR, noMMR);
		case MIN:
			return AggregationOperator.getMin(yesMMR, noMMR);
		case WEIGHTED_AVERAGE:
			return AggregationOperator.weightedAvg(yesMMR, noMMR, w1, w2);
		case AVG:
			return AggregationOperator.getAvg(yesMMR, noMMR);
		default:
			throw new IllegalStateException();
		}
	}
}
