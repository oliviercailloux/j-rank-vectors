package io.github.oliviercailloux.y2018.minimax;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apfloat.Apint;
import org.apfloat.Aprational;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class XPRunner {

	static Set<Alternative> alternatives;
	static Set<Voter> voters;
	static Oracle context;
	static PrefKnowledge knowledge;
	static StrategyRandom strategy;
	static double[] sumOfRanks;
	private static BufferedWriter bw;
	static int k; // number of questions
	static int k_vot;
	static int exp;
	static int n, m;
	static Alternative winner;

	public static void main(String[] args) throws IOException {
		exp = 0;
		initFile();
		for (n = 3; n < 8; n++) {
			for (m = 3; m < 8; m++) {
				exp++;
				bw.write("Experiment number " + exp + " : " + n + " voters, " + m + " alternatives \n");
				bw.flush();
				
				alternatives = new HashSet<>();
				for (int i = 1; i <= m; i++) {
					alternatives.add(new Alternative(i));
				}
				voters = new HashSet<>();
				for (int i = 1; i <= n; i++) {
					voters.add(new Voter(i));
				}
				sumOfRanks = new double[m];
				context = Oracle.build(ImmutableMap.copyOf(genProfile(n, m)), genWeights(m));
				writeContext();
				
				for (k = 15; k <= 40; k += 5) {
					k_vot = 0;
					knowledge = PrefKnowledge.given(alternatives, voters);
					strategy = StrategyRandom.build(knowledge);
					winner=null;
					for (int i = 0; i < k; i++) {
						Question q = strategy.nextQuestion();
						Answer a = context.getAnswer(q);
						updateKnowledge(q, a);
						if (q.getType().equals(QuestionType.VOTER_QUESTION))
							k_vot++;
					}
					winner = Regret.getMMRAlternative(knowledge);
					writeShortStats();
				}
				bw.write("\n");
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void initFile() {
		try {
			File file = new File("./stats.txt");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			bw = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeContext() {
		try {
			bw.write("True preferences: \n");
			bw.write("Profile: " + context.getProfile().values().toString() + "\n");
			bw.write("Weights: " + context.getWeights().toString() + " \n");
			Alternative win = computeTrueWinner();
			bw.write("Scores: \n");
			for (Alternative a : alternatives) {
				bw.write(a.getId() + ": " + sumOfRanks[a.getId() - 1] + "\n");
			}
			bw.write("True winner: " + win + "\n \n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeShortStats() {
		try {
			if (strategy.profileCompleted) {
				bw.write("Profile completed after " + k_vot + " questions: \n");
			}
			bw.write(k_vot + " questions to the voters, " + (k - k_vot) + " questions to the committee \n");
			bw.write("Approximate winner: " + winner + "\n \n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeStats() {
		try {
			bw.write("Deducted preferences after " + k + " questions: \n");
			bw.write(k_vot + " questions to the voters, " + (k - k_vot) + " questions to the committee \n");
			bw.write("Partial Profile: " + knowledge.getProfile().values().toString() + "\n");
			bw.write("Weights Ranges: \n" + knowledge.getConstraintsOnWeights().rangesAsString() + "\n \n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateKnowledge(Question qt, Answer answ) {
		switch (qt.getType()) {
		case VOTER_QUESTION:
			QuestionVoter qv = qt.getQuestionVoter();
			Alternative a = qv.getFirstAlternative();
			Alternative b = qv.getSecondAlternative();
			MutableGraph<Alternative> graph = knowledge.getProfile().get(qv.getVoter()).asGraph();
			switch (answ) {
			case GREATER:
				graph.putEdge(a, b);
				knowledge.getProfile().get(qv.getVoter()).setGraphChanged();
				break;
			case LOWER:
				graph.putEdge(b, a);
				knowledge.getProfile().get(qv.getVoter()).setGraphChanged();
				break;
			// $CASES-OMITTED$
			default:
				throw new IllegalStateException();
			}
			break;
		case COMMITTEE_QUESTION:
			QuestionCommittee qc = qt.getQuestionCommittee();
			Aprational lambda = qc.getLambda();
			int rank = qc.getRank();
			switch (answ) {
			case EQUAL:
				knowledge.addConstraint(rank, ComparisonOperator.EQ, lambda);
				break;
			case GREATER:
				knowledge.addConstraint(rank, ComparisonOperator.GE, lambda);
				break;
			case LOWER:
				knowledge.addConstraint(rank, ComparisonOperator.LE, lambda);
				break;
			default:
				throw new IllegalStateException();
			}
			break;
		default:
			throw new IllegalStateException();
		}
	}

	private static Alternative computeTrueWinner() {
		for (int i = 0; i < alternatives.size(); i++) {
			Alternative a = new Alternative(i + 1);
			double sum = 0;
			for (Voter v : voters) {
				int rank = context.getProfile().get(v).getPref().getAlternativeRank(a);
				sum += context.getWeights().getWeightAtRank(rank);
			}
			sumOfRanks[i] = sum;
		}
		int indmax = 0;
		double max = sumOfRanks[0];
		for (int i = 1; i < sumOfRanks.length; i++) {
			if (sumOfRanks[i] > max) {
				max = sumOfRanks[i];
				indmax = i;
			}
		}
		return new Alternative(indmax + 1);
	}

	public static PSRWeights genWeights(int nbAlternatives) {
		List<Double> weights = new LinkedList<>();
		weights.add(1d);
		double previous = 1d;
		Random r = new Random();
		double[] differences = new double[nbAlternatives - 1];
		double sum = 0;
		for (int i = 0; i < nbAlternatives - 1; i++) {
			differences[i] = r.nextDouble();
			sum += differences[i];
		}
		for (int i = 0; i < nbAlternatives - 1; i++) {
			differences[i] = differences[i] / sum;
		}
		Arrays.sort(differences);
		for (int i = nbAlternatives - 2; i > 0; i--) {
			double curr = previous - differences[i];
			weights.add(curr);
			previous = curr;
		}
		weights.add(0d);
		return PSRWeights.given(weights);
	}

	public static Map<Voter, VoterStrictPreference> genProfile(int nbVoters, int nbAlternatives) {
		checkArgument(nbVoters >= 1);
		checkArgument(nbAlternatives >= 1);
		Map<Voter, VoterStrictPreference> profile = new HashMap<>();

		List<Alternative> availableRanks = new LinkedList<>();
		for (int i = 1; i <= nbAlternatives; i++) {
			availableRanks.add(new Alternative(i));
		}

		for (int i = 1; i <= nbVoters; ++i) {
			Voter v = new Voter(i);
			List<Alternative> linearOrder = Lists.newArrayList(availableRanks);
			Collections.shuffle(linearOrder);
			VoterStrictPreference pref = new VoterStrictPreference(v, linearOrder);
			profile.put(v, pref);
		}

		return profile;
	}

	@SuppressWarnings("unused")
	private static void firstTest() {
		int m1 = 3;
		int n1 = 7;
		alternatives = new HashSet<>();
		for (int i = 1; i <= m1; i++) {
			alternatives.add(new Alternative(i));
		}
		voters = new HashSet<>();
		for (int i = 1; i <= n1; i++) {
			voters.add(new Voter(i));
		}
		sumOfRanks = new double[m1];
		context = Oracle.build(ImmutableMap.copyOf(genProfile(n1, m1)), genWeights(m1));

		writeContext();
		for (k = 20; k <= 100; k += 10) {
			k_vot = 0;
			knowledge = PrefKnowledge.given(alternatives, voters);
			strategy = StrategyRandom.build(knowledge);
			for (int i = 0; i < k; i++) {
				Question q = strategy.nextQuestion();
				Answer a = context.getAnswer(q);
				updateKnowledge(q, a);
				if (q.getType().equals(QuestionType.VOTER_QUESTION))
					k_vot++;
			}
			writeStats();
		}

		try {
			bw.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
