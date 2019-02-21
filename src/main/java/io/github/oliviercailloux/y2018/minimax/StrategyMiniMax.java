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

import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

/** Uses the Regret to get the next question. **/

public class StrategyMiniMax implements Strategy {

	private PrefKnowledge knowledge;
	public boolean profileCompleted;

	public static StrategyMiniMax build(PrefKnowledge knowledge) {
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

		final HashMap <Question, Double> questions = new HashMap<>();
				
		final ArrayList<QuestionVoter> votersQuestions = new ArrayList<>();
		for (Voter voter : knowledge.getVoters()) {
			final Graph<Alternative> graph = knowledge.getProfile().get(voter).asTransitiveGraph();

			for (Alternative a1 : knowledge.getAlternatives()) {
				if (graph.adjacentNodes(a1).size() != m - 1) {
					for (Alternative a2 : knowledge.getAlternatives()) {
						if (!a1.equals(a2) && !graph.adjacentNodes(a1).contains(a2)) {
							votersQuestions.add(new QuestionVoter(voter, a1, a2));
							
						}
					}
				}
			}
		}

		final ArrayList<QuestionCommittee> committeeQuestions = new ArrayList<>();
		final ArrayList<Integer> candidateRanks = IntStream.rangeClosed(1, m - 2).boxed()
				.collect(Collectors.toCollection(ArrayList::new));
		for (int rank : candidateRanks) {
			final Range<Aprational> lambdaRange = knowledge.getLambdaRange(rank);
			double diff = (lambdaRange.lowerEndpoint().subtract(lambdaRange.upperEndpoint())).doubleValue();
			if (diff > 0.1) {
				final Aprational avg = AprationalMath.sum(lambdaRange.lowerEndpoint(), lambdaRange.upperEndpoint())
						.divide(new Apint(2));
				committeeQuestions.add(new QuestionCommittee(avg, rank));
			}
		}

		final boolean existsQuestionWeight = !committeeQuestions.isEmpty();
		final boolean existsQuestionVoters = !votersQuestions.isEmpty();

		checkArgument(existsQuestionWeight || existsQuestionVoters, "No question to ask about weights or voters.");

		final Question q = null;

		if (!existsQuestionVoters) {
			profileCompleted = true;
		}

		return q;
	}

}
