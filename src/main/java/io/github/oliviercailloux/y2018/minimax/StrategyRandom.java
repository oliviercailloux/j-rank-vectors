package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

/**
 * Uses a random approach to get the next question: builds a set containing all
 * the questions (both to the voters and to the committee), randomly picks one.
 *
 * Problems: 1) After having asked a question, the preference knowledge changes,
 * so some question in the set might not have sense anymore. Solution: recompute
 * the set every time. Expensive. 2) Because of the rational nature of λ, there
 * exists an infinite number of questions for the committee. Solution: divide
 * the admissible interval of λ in t finite slots and update this range after
 * every answer. Note: Is there an upper bound for lambda? Should be n(m-2)
 * //TOCHECK This makes the initial admissible interval [1, n(m-2)]. Then divide
 * this, for example, in 10 slots, ask a question and depending from the answer
 * reduce the interval for the next question.
 **/

public class StrategyRandom implements Strategy {

	PrefKnowledge knowledge;

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(StrategyRandom.class);

	public static StrategyRandom build(PrefKnowledge knowledge) {
		return new StrategyRandom(knowledge);
	}

	private Random random;

	private StrategyRandom(PrefKnowledge knowledge) {
		final long seed = ThreadLocalRandom.current().nextLong();
		LOGGER.info("Using seed: {}.", seed);
		random = new Random(seed);
		this.knowledge = knowledge;
	}

	void setRandom(Random random) {
		this.random = requireNonNull(random);
	}

	@Override
	public Question nextQuestion() {
		final int m = knowledge.getAlternatives().size();

		checkArgument(m >= 2, "Questions can be asked only if there are at least two alternatives.");

		final ImmutableSet.Builder<Voter> questionableVotersBuilder = ImmutableSet.builder();
		for (Voter voter : knowledge.getVoters()) {
			/** TODO this should be the transitive closure. */
			final Graph<Alternative> graph = knowledge.getProfile().get(voter).asGraph();
			if (graph.edges().size() != m * (m - 1) / 2) {
				questionableVotersBuilder.add(voter);
			}
		}
		final ImmutableSet<Voter> questionableVoters = questionableVotersBuilder.build();

		final boolean aboutWeight;
		if (m == 2) {
			checkArgument(!questionableVoters.isEmpty(),
					"No question to ask about weights, and everything is known about the voters.");
			aboutWeight = false;
		} else {
			if (questionableVoters.isEmpty()) {
				aboutWeight = true;
			} else {
				aboutWeight = random.nextBoolean();
			}
		}

		final Question q;

		if (aboutWeight) {
			assert m >= 3;
			final int rank = random.nextInt(m - 2) + 1;
			assert rank >= 1;
			assert rank <= m - 2;

			final Range<Aprational> lambdaRange = knowledge.getLambdaRange(rank);
			final Aprational avg = AprationalMath.sum(lambdaRange.lowerEndpoint(), lambdaRange.upperEndpoint())
					.divide(new Apint(2));
			q = new Question(new QuestionCommittee(avg, rank));
		} else {
			assert !questionableVoters.isEmpty();
			final int idx = random.nextInt(questionableVoters.size());
			final Voter voter = questionableVoters.asList().get(idx);
			final ArrayList<Alternative> altsRandomOrder = new ArrayList<>(knowledge.getAlternatives());
			Collections.shuffle(altsRandomOrder, random);
			/** TODO this should be the transitive closure. */
			final Graph<Alternative> graph = knowledge.getProfile().get(voter).asGraph();
			final Optional<Alternative> withIncomparabilities = altsRandomOrder.stream()
					.filter((a1) -> graph.adjacentNodes(a1).size() != m - 1).findAny();
			assert withIncomparabilities.isPresent();
			final Alternative a1 = withIncomparabilities.get();
			final Optional<Alternative> incomparable = altsRandomOrder.stream()
					.filter((a2) -> !a1.equals(a2) && !graph.adjacentNodes(a1).contains(a2)).findAny();
			assert incomparable.isPresent();
			final Alternative a2 = incomparable.get();
			q = new Question(new QuestionVoter(voter, a1, a2));
		}

		return q;
	}
}
