package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.AprationalMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.graph.Graph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;
import io.github.oliviercailloux.y2018.minimax.utils.AggregationOperator;
import io.github.oliviercailloux.y2018.minimax.utils.AggregationOperator.AggOps;

/** Uses the Regret to get the next question. **/

public class StrategyMiniMax implements Strategy {

	private PrefKnowledge knowledge;
	public boolean profileCompleted;
	private static AggOps op;
	private static double w1;
	private static double w2;

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(StrategyMiniMax.class);

	public static StrategyMiniMax build(PrefKnowledge knowledge) {
		return new StrategyMiniMax(knowledge);
	}

	public static StrategyMiniMax build(PrefKnowledge knowledge, AggOps operator) {
		op = operator;
		return new StrategyMiniMax(knowledge);
	}

	public static StrategyMiniMax build(PrefKnowledge knowledge, AggOps operator, double w_1, double w_2) {
		checkArgument(operator.equals(AggOps.WEIGHTED_AVERAGE));
		checkArgument(w_1 > 0);
		checkArgument(w_2 > 0);
		op = operator;
		w1 = w_1;
		w2 = w_2;
		return new StrategyMiniMax(knowledge);
	}

	private StrategyMiniMax(PrefKnowledge knowledge) {
		this.knowledge = knowledge;
		profileCompleted = false;
	}

	@Override
	public Question nextQuestion() {
		final int m = knowledge.getAlternatives().size();

		checkArgument(m >= 2, "Questions can be asked only if there are at least two alternatives.");

		final HashMap<Question, Double> questions = new HashMap<>();

		for (Voter voter : knowledge.getVoters()) {
			final Graph<Alternative> graph = knowledge.getProfile().get(voter).asTransitiveGraph();

			for (Alternative a1 : knowledge.getAlternatives()) {
				if (graph.adjacentNodes(a1).size() != m - 1) {
					for (Alternative a2 : knowledge.getAlternatives()) {
						if (!a1.equals(a2) && !graph.adjacentNodes(a1).contains(a2)) {
							Question q = new Question(new QuestionVoter(voter, a1, a2));
							double score = getScore(q);
							questions.put(q, score);
						}
					}
				}
			}
		}

		final ArrayList<Integer> candidateRanks = IntStream.rangeClosed(1, m - 2).boxed()
				.collect(Collectors.toCollection(ArrayList::new));
		for (int rank : candidateRanks) {
			final Range<Aprational> lambdaRange = knowledge.getLambdaRange(rank);
			double diff = (lambdaRange.upperEndpoint().subtract(lambdaRange.lowerEndpoint())).doubleValue();
			if (diff > 0.1) {
				final Aprational avg = AprationalMath.sum(lambdaRange.lowerEndpoint(), lambdaRange.upperEndpoint())
						.divide(new Apint(2));
				Question q = new Question(new QuestionCommittee(avg, rank));
				double score = getScore(q);
				questions.put(q, score);
			}
		}

		checkArgument(!questions.isEmpty(), "No question to ask about weights or voters.");

		Question nextQ = questions.keySet().iterator().next();
		double minScore = questions.get(nextQ);

		for (Question q : questions.keySet()) {
			double score = questions.get(q);
			if (score < minScore) {
				nextQ = q;
				minScore = score;
			}
		}

		return nextQ;
	}

	private double getScore(Question q) {
		PrefKnowledge yesKnowledge = PrefKnowledge.copyOf(knowledge);
		PrefKnowledge noKnowledge = PrefKnowledge.copyOf(knowledge);
		double yesMMR = 0;
		double noMMR = 0;
		if (q.getType().equals(QuestionType.VOTER_QUESTION)) {
			QuestionVoter qv = q.getQuestionVoter();
			Alternative a = qv.getFirstAlternative();
			Alternative b = qv.getSecondAlternative();

			MutableGraph<Alternative> yesGraph = yesKnowledge.getProfile().get(qv.getVoter()).asGraph();
			yesGraph.putEdge(a, b);
			yesKnowledge.getProfile().get(qv.getVoter()).setGraphChanged();

			MutableGraph<Alternative> noGraph = noKnowledge.getProfile().get(qv.getVoter()).asGraph();
			noGraph.putEdge(b, a);
			noKnowledge.getProfile().get(qv.getVoter()).setGraphChanged();

			Regret.getMMRAlternatives(yesKnowledge);
			yesMMR = Regret.getMMR();

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
