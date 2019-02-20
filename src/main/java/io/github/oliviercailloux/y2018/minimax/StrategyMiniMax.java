package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
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
		return null;
	}

}
