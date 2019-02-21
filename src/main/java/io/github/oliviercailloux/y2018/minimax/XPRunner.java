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

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.graph.MutableGraph;
import com.google.common.math.Stats;

import io.github.oliviercailloux.jlp.elements.ComparisonOperator;
import io.github.oliviercailloux.y2018.j_voting.Alternative;
import io.github.oliviercailloux.y2018.j_voting.Voter;

public class XPRunner {

	static Set<Alternative> alternatives;
	static Set<Voter> voters;
	static Oracle context;
	static PrefKnowledge knowledge;
	static double[] sumOfRanks;
	private static BufferedWriter bw;
	static int k; // number of questions
	static List<Alternative> winners;
	static List<Alternative> trueWinners;
	static double trueWinScore;
	static double avgloss;
	static double regret;
	static List<Double> avglosses;
	static List<Double> regrets;

	public static void main(String[] args) throws IOException {
		BufferedWriter b = initFile("./experiments.txt");
		for (int n = 3; n < 8; n++) {
			for (int m = 3; m < 8; m++) {
				run(m, n);
				Stats regretStats = Stats.of(regrets);
				double regretMean = regretStats.mean();
				double regretSD = regretStats.populationStandardDeviation();
				Stats lossStats = Stats.of(avglosses);
				double lossMean = lossStats.mean();
				double lossSD = lossStats.populationStandardDeviation();

				b.write(n + " Voters, " + m + " Alternatives \n");
				b.write("Mean of the Regrets: " + regretMean + " Standard Deviation of the Regrets: " + regretSD
						+ "\n");
				b.write("Mean of the Average Losses: " + lossMean + " Standard Deviation of the Average Losses: "
						+ lossSD + "\n\n");
				b.flush();

			}
		}
		b.close();
	//	bw.close();
	}

	private static void run(int m, int n) {
		//bw = initFile("./stats.txt");
		avglosses = new LinkedList<>();
		regrets = new LinkedList<>();
		for (int j = 0; j < 10; j++) {
			XYSeries regretSeries = new XYSeries("Regret");
			XYSeries avgLossSeries = new XYSeries("Average Loss");
			alternatives = new HashSet<>();
			for (int i = 1; i <= m; i++) {
				alternatives.add(new Alternative(i));
			}
			voters = new HashSet<>();
			for (int i = 1; i <= n; i++) {
				voters.add(new Voter(i));
			}
			context = Oracle.build(ImmutableMap.copyOf(genProfile(n, m)), genWeights(m));
			knowledge = PrefKnowledge.given(alternatives, voters);
			Strategy strategy = StrategyRandom.build(knowledge);
			sumOfRanks = new double[m];
			trueWinners = computeTrueWinners();
		//	writeContext();

			int maxQuestions = 40;
			for (k = 1; k <= maxQuestions; k++) {
				Question q = strategy.nextQuestion();
				Answer a = context.getAnswer(q);
				updateKnowledge(q, a);
				winners = Regret.getMMRAlternatives(knowledge);
				regret = Regret.getMMR();
				regrets.add(regret);
				regretSeries.add(k, regret);
				List<Double> losses = new LinkedList<>();
				for (Alternative alt : winners) {
					double approxTrueScore = 0;
					for (VoterStrictPreference vsp : context.getProfile().values()) {
						int rank = vsp.getPref().getAlternativeRank(alt);
						approxTrueScore += context.getWeights().getWeightAtRank(rank);
					}
					losses.add(trueWinScore - approxTrueScore);
				}
				avgloss = 0;
				for (double loss : losses) {
					avgloss += loss;
				}
				avgloss = avgloss / losses.size();
				avglosses.add(avgloss);
				avgLossSeries.add(k, avgloss);
			//	writeShortStats();
			}
			if(j<1) {
				plot(regretSeries, avgLossSeries,m,n);
			}
		}
	}

	private static void plot(XYSeries regretSeries, XYSeries avgLossSeries,int m, int n) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(regretSeries);
		dataset.addSeries(avgLossSeries);
		JFreeChart chart = ChartFactory.createXYLineChart(n+" Voters "+m+" Alternatives", "k", "", dataset,
				PlotOrientation.VERTICAL, true, true, false);
//		NumberAxis xAxis = new NumberAxis();
//		xAxis.setTickUnit(new NumberTickUnit(1));
//		XYPlot plot = (XYPlot) chart.getPlot();
//		plot.setDomainAxis(xAxis);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(700, 470));
		JFrame jf = new JFrame("Plot");
		jf.setContentPane(chartPanel);
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jf.pack();
		RefineryUtilities.centerFrameOnScreen(jf);
		jf.setVisible(true);
	}
	
	private static BufferedWriter initFile(String tfile) {
		BufferedWriter b = null;
		try {
			File file = new File(tfile);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			b = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	private static void writeContext() {
		try {
			bw.write("True preferences: " + alternatives.size() + " alternatives " + voters.size() + " voters \n");
			bw.write("Profile: " + context.getProfile().values().toString() + "\n");
			bw.write("Weights: " + context.getWeights().toString() + " \n");
			bw.write("Scores: \n");
			for (Alternative a : alternatives) {
				bw.write(a.getId() + ": " + sumOfRanks[a.getId() - 1] + "\n");
			}
			bw.write("True winners: " + trueWinners + " True score: " + trueWinScore + "\n \n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeShortStats() {
		try {
//			if (profileCompleted) {
//				bw.write("Profile completed after " + k_vot + " questions: \n");
//			}
//			bw.write(k_vot + " questions to the voters, " + (k - k_vot) + " questions to the committee \n");
			bw.write("After " + k + " questions ");
			bw.write("Approximate winners: " + winners + " Regret: " + regret + " Average Loss: " + avgloss + "\n \n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void writeStats() {
		try {
			bw.write("Deducted preferences after " + k + " questions: \n");
			// bw.write(k_vot + " questions to the voters, " + (k - k_vot) + " questions to
			// the committee \n");
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

	private static List<Alternative> computeTrueWinners() {
		List<Alternative> trueWin = new LinkedList<>();
		for (int i = 0; i < alternatives.size(); i++) {
			Alternative a = new Alternative(i + 1);
			double sum = 0;
			for (Voter v : voters) {
				int rank = context.getProfile().get(v).getPref().getAlternativeRank(a);
				sum += context.getWeights().getWeightAtRank(rank);
			}
			sumOfRanks[i] = sum;
		}

		double max = sumOfRanks[0];
		trueWin.add(new Alternative(1));
		for (int i = 1; i < sumOfRanks.length; i++) {
			if (sumOfRanks[i] == max) {
				trueWin.add(new Alternative(i + 1));
			}
			if (sumOfRanks[i] > max) {
				trueWin.clear();
				max = sumOfRanks[i];
				trueWin.add(new Alternative(i + 1));
			}
		}
		trueWinScore = max;
		return trueWin;
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

}
