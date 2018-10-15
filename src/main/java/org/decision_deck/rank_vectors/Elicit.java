package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.decision_deck.rank_vectors.PreorderRule.ComparisonStatus;
import org.decision_deck.rank_vectors.RuleComparerWoScore.ScoreType;
import org.decision_deck.utils.Pair;
import org.decision_deck.utils.relation.BinaryRelation;
import org.decision_deck.utils.relation.BinaryRelationImpl;
import org.decision_deck.utils.relation.Preorder;
import org.decision_deck.utils.relation.RelationUtils;
import org.decision_deck.utils.relation.graph.Edge;
import org.decision_deck.utils.relation.graph.mess.GraphUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.math.IntMath;

public class Elicit {
    static public class RvWeightedNamer implements VertexNameProvider<List<Integer>>, Function<List<Integer>, String> {
	static private final RvWeightedNamer s_instance = new RvWeightedNamer();

	@Override
	public String getVertexName(List<Integer> rv) {
	    return Joiner.on("").join(rv) + " (" + Collections2.orderedPermutations(rv).size() + ")";
	}

	@Override
	public String apply(List<Integer> input) {
	    return getVertexName(input);
	}

	static public RvWeightedNamer getInstance() {
	    return s_instance;
	}
    }

    static public class VertexIdProviderToDelete<E> implements VertexNameProvider<E> {
	private final Map<E, Integer> m_ids;

	@SuppressWarnings("boxing")
	public VertexIdProviderToDelete(Iterable<E> source) {
	    int id = 0;
	    m_ids = Maps.newLinkedHashMap();
	    for (E e : source) {
		m_ids.put(e, id++);
	    }
	}

	@Override
	public String getVertexName(E vertex) {
	    final Integer id = m_ids.get(vertex);
	    checkArgument(id != null);
	    return String.valueOf(id);
	}
    }

    private static final Logger s_logger = LoggerFactory.getLogger(Elicit.class);
    private final NumberFormat m_format;
    static public final String EOL = System.getProperty("line.separator");
    private final Table<RuleComparerType, Integer, List<Double>> m_avgErrors = HashBasedTable.create();
    private int m_nbQuestions;
    private final Function<Double, String> m_formatFunction;
    private final List<List<Integer>> m_nbIncomparables = Lists.newLinkedList();
    private TargetRuleType m_targetRuleType;
    private FitnessType m_currentFitnessType;
    private Set<Integer> m_mInterval;
    private ContiguousSet<Integer> m_nbRvsInterval;
    private int m_nbRepeatTryRandomFitness;
    private Comparator<List<Integer>> m_targetWo;
    private RankBasedVotingRule m_targetRule;
    private final List<FitnessType> m_fitnessTypes = Lists.newLinkedList();
    private int m_nbRepeatChooseRandomTarget;
    private VertexNameProvider<List<Integer>> m_rvNamer;
    private boolean m_checkHot;
    private Set<Integer> m_nInterval;

    public Elicit() {
	m_format = getDoubleFormat();
	m_formatFunction = new Function<Double, String>() {
	    @Override
	    public String apply(Double input) {
		if (Double.isNaN(input) || Double.isInfinite(input)) {
		    throw new IllegalStateException();
		}
		return m_format.format(input);
	    }
	};
	m_nbQuestions = 99;
	m_targetRuleType = TargetRuleType.BORDA;
	m_currentFitnessType = FitnessType.PESSIMISTIC;
	m_mInterval = ContiguousSet.create(Range.closed(3, 14), DiscreteDomain.integers());
	m_nInterval = ContiguousSet.create(Range.closed(2, 14), DiscreteDomain.integers());
	m_nbRvsInterval = ContiguousSet.create(Range.closed(1, 600), DiscreteDomain.integers());
	m_nbRepeatChooseRandomTarget = 10;
	m_nbRepeatTryRandomFitness = 1;
	m_rvNamer = RvNamer.getInstance();
	m_checkHot = true;
    }

    static public NumberFormat getIntFormat() {
	final NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
	format.setMinimumIntegerDigits(2);
	return new NumberFormat() {

	    @Override
	    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		return format.format(number, toAppendTo, pos);
	    }

	    @Override
	    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		return new StringBuffer(format.format(number, toAppendTo, pos).toString().replaceFirst("\\G0", " "));
	    }

	    @Override
	    public Number parse(String source, ParsePosition parsePosition) {
		return format.parse(source, parsePosition);
	    }
	};
    }

    public static void main(String[] args) throws Exception {
	new Elicit().proceed();
	// new Elicit().viewParetoGraph();
	// new Elicit().viewGraph();
    }

    public void viewGraph() throws Exception {
	final int m = 4;
	final int n = 3;
	final AllRankVectors all = new AllRankVectors(m, n);
	final Preorder<List<Integer>> paretoPreorder = new Preorder<List<Integer>>();
	final Set<Pair<List<Integer>, List<Integer>>> paretoPairs = all.getParetoDominanceNonTransitive().asPairs();
	for (Pair<List<Integer>, List<Integer>> paretoPair : paretoPairs) {
	    paretoPreorder.addTransitive(paretoPair.getElt1(), paretoPair.getElt2());
	}
	RelationUtils.completeRandomly(paretoPreorder, new Random().nextLong());
	final BinaryRelationImpl<List<Integer>, List<Integer>> completed = new BinaryRelationImpl<List<Integer>, List<Integer>>();
	for (Pair<List<Integer>, List<Integer>> pair : paretoPreorder.asPairs()) {
	    completed.asPairs().add(pair);
	}
	GraphUtils.removeLoops(completed);
	GraphUtils.computeTransitiveReduct(completed);
	export(completed);
    }

    public void export(BinaryRelation<List<Integer>, List<Integer>> relation) throws FileNotFoundException,
	    IOException, InterruptedException {
	final DirectedGraph<List<Integer>, Edge<List<Integer>>> g = GraphUtils.getDefaultDiGraph(relation);
	// Rather use the GraphExporter from jmcda-utils!
	final DOTExporter<List<Integer>, Edge<List<Integer>>> exporter = new DOTExporter<List<Integer>, Edge<List<Integer>>>(
		new VertexIdProviderToDelete<List<Integer>>(Sets.union(relation.getFrom(), relation.getTo())),
		m_rvNamer, null);
	final BufferedWriter w = Files.newWriter(new File("output.dot"), Charsets.UTF_8);
	exporter.export(w, g);
	w.close();
	convertFile("output.dot", "output");
    }

    public void proceed() throws Exception {
	parseProps();
	m_nbQuestions = 99;
	m_nbRepeatChooseRandomTarget = 1;
	m_nbRepeatTryRandomFitness = 1;

	// m_mInterval = ContiguousSet.create(Range.closed(3, 14), DiscreteDomain.integers());
	// m_nInterval = ContiguousSet.create(Range.closed(2, 14), DiscreteDomain.integers());

	m_nbRvsInterval = ContiguousSet.create(Range.closed(1, 800), DiscreteDomain.integers());
	m_fitnessTypes.clear();
	m_fitnessTypes.addAll(ImmutableSet.of(FitnessType.OPTIMISTIC_WEIGHTED, FitnessType.RANDOM,
		FitnessType.PESSIMISTIC_WEIGHTED, FitnessType.LIKELIHOOD, FitnessType.LIKELIHOOD_PLUS));

	m_mInterval = ImmutableSet.of(6);
	m_nInterval = ImmutableSet.of(6);
	m_targetRuleType = TargetRuleType.BORDA;
	writeQuestionFile();

	m_mInterval = ImmutableSet.of(10);
	m_nInterval = ImmutableSet.of(4);
	m_targetRuleType = TargetRuleType.BORDA;
	writeQuestionFile();

	while (true) {
	    m_fitnessTypes.clear();
	    m_fitnessTypes.addAll(ImmutableSet.of(FitnessType.OPTIMISTIC_WEIGHTED, FitnessType.RANDOM,
		    FitnessType.PESSIMISTIC_WEIGHTED, FitnessType.LIKELIHOOD, FitnessType.LIKELIHOOD_PLUS));

	    m_mInterval = ImmutableSet.of(4);
	    m_nInterval = ImmutableSet.of(10);
	    m_targetRuleType = TargetRuleType.RANDOM;
	    writeQuestionFile();

	    m_mInterval = ImmutableSet.of(6);
	    m_nInterval = ImmutableSet.of(6);
	    m_targetRuleType = TargetRuleType.RANDOM;
	    writeQuestionFile();

	    m_mInterval = ImmutableSet.of(10);
	    m_nInterval = ImmutableSet.of(4);
	    m_targetRuleType = TargetRuleType.RANDOM;
	    writeQuestionFile();

	    m_fitnessTypes.clear();
	    m_fitnessTypes.add(FitnessType.RANDOM);

	    m_mInterval = ImmutableSet.of(4);
	    m_nInterval = ImmutableSet.of(10);
	    m_targetRuleType = TargetRuleType.BORDA;
	    writeQuestionFile();

	    m_mInterval = ImmutableSet.of(6);
	    m_nInterval = ImmutableSet.of(6);
	    m_targetRuleType = TargetRuleType.BORDA;
	    writeQuestionFile();

	    m_mInterval = ImmutableSet.of(10);
	    m_nInterval = ImmutableSet.of(4);
	    m_targetRuleType = TargetRuleType.BORDA;
	    writeQuestionFile();
	}
    }

    private void parseProps() throws IOException {
	Properties properties = new Properties();
	final URL url = Resources.getResource("props.prop");
	final CharSource source = Resources.asCharSource(url, Charsets.UTF_8);
	final Reader stream = source.openStream();
	properties.load(stream);
	stream.close();
	final String disable = properties.getProperty("DONT_KEEP_COOL");
	if ("true".equalsIgnoreCase(disable)) {
	    m_checkHot = false;
	    s_logger.warn("Disabled temp checks.");
	}
    }

    public void writeQuestionFile() throws Exception {
	for (int i = 0; i <= m_nbQuestions; ++i) {
	    m_nbIncomparables.add(Lists.<Integer> newLinkedList());
	}
	final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	final CharSink sink = Files.asCharSink(new File("Target=" + m_targetRuleType + ", fit=" + m_fitnessTypes
		+ ", m=" + m_mInterval + ", nbrvs=" + m_nbRvsInterval + " — " + df.format(new Date()) + ".txt"),
		Charsets.UTF_8);
	// final CharSink sink = Files.asCharSink(new File("output.txt"), Charsets.UTF_8);
	final Writer w = sink.openBufferedStream();

	if (m_targetRuleType == TargetRuleType.BORDA) {
	    m_targetWo = new Borda();
	    m_targetRule = new WeakOrderRule(m_targetWo);
	}

	for (int m : m_mInterval) {
	    for (int n : m_nInterval) {
		final int nbIncrRvs = IntMath.binomial(n + m - 1, m - 1);
		if (!m_nbRvsInterval.contains(nbIncrRvs)) {
		    s_logger.info("Test with m = " + m + ", n = " + n + " involving {} rvs skipped.", nbIncrRvs);
		    continue;
		}

		keepCoolMan();
		final AllRankVectors all = new AllRankVectors(m, n);
		keepCoolMan();

		setUpErrorsTable();
		final int nbRepeatChooseRandomTarget;
		if (m_targetRuleType == TargetRuleType.RANDOM) {
		    nbRepeatChooseRandomTarget = m_nbRepeatChooseRandomTarget;
		} else {
		    nbRepeatChooseRandomTarget = 1;
		}
		for (int repeat = 0; repeat < nbRepeatChooseRandomTarget; ++repeat) {
		    final Long seed;
		    if (m_targetRuleType == TargetRuleType.RANDOM) {
			seed = new Random().nextLong();
			final PreorderRule targetRulePr = getRandomTargetRule(all, seed);
			m_targetRule = targetRulePr;
			m_targetWo = getWoRule(targetRulePr);
		    } else {
			seed = null;
		    }

		    for (FitnessType fitnessType : m_fitnessTypes) {
			m_currentFitnessType = fitnessType;
			final int nbRepeatTryRandomFitness;
			if (m_currentFitnessType == FitnessType.RANDOM) {
			    nbRepeatTryRandomFitness = m_nbRepeatTryRandomFitness;
			} else {
			    nbRepeatTryRandomFitness = 1;
			}
			for (int repeatF = 0; repeatF < nbRepeatTryRandomFitness; ++repeatF) {
			    switch (m_targetRuleType) {
			    case BORDA:
				w.write("Target = Borda" + EOL);
				break;
			    case RANDOM:
				w.write("Target = Random, seed = " + seed + EOL);
				break;
			    default:
				throw new IllegalStateException();
			    }
			    question(w, all);
			}
		    }
		}
		w.write("Nb incomparables Q0: " + m_nbIncomparables.get(0) + "." + EOL);
		if (m_nbQuestions >= 25) {
		    w.write("Nb incomparables Q25: " + m_nbIncomparables.get(25) + "." + EOL);
		}
		if (m_nbQuestions >= 99) {
		    w.write("Nb incomparables Q99: " + m_nbIncomparables.get(99) + "." + EOL);
		}
		for (int i = 0; i <= m_nbQuestions; ++i) {
		    m_nbIncomparables.get(i).clear();
		}
	    }
	}
	// question(w, 5, 5);
	w.close();
    }

    private void setUpErrorsTable() {
	final RuleComparerType[] comparers = RuleComparerType.values();

	for (RuleComparerType comparer : comparers) {
	    for (int i = 0; i <= m_nbQuestions; ++i) {
		m_avgErrors.put(comparer, i, Lists.<Double> newArrayList());
	    }
	}
    }

    public void question(Writer w, AllRankVectors all) throws IOException, InterruptedException {
	final int m = all.getM();
	final int n = all.getN();
	final int nbIncrRvs = IntMath.binomial(n + m - 1, m - 1);
	s_logger.info("Test with m = " + m + ", n = " + n + " involving {} rvs.", nbIncrRvs);

	final Preorder<List<Integer>> approxPreorder = new Preorder<List<Integer>>();
	for (Pair<List<Integer>, List<Integer>> paretoPair : all.getParetoDominanceNonTransitive().asPairs()) {
	    approxPreorder.addTransitive(paretoPair.getElt1(), paretoPair.getElt2());
	}
	final PreorderRule approxRule = new PreorderRule(approxPreorder);
	final Fitness fitter;
	// final FitnessNbDominated fitterP = new FitnessNbDominated(all, approxPreorder, false, false);
	// final FitnessNbDominated fitterW = new FitnessNbDominated(all, approxPreorder, false, true);
	switch (m_currentFitnessType) {
	case OPTIMISTIC:
	    fitter = new FitnessNbDominated(all, approxPreorder, true, false);
	    break;
	case OPTIMISTIC_WEIGHTED:
	    fitter = new FitnessNbDominated(all, approxPreorder, true, true);
	    break;
	case PESSIMISTIC:
	    fitter = new FitnessNbDominated(all, approxPreorder, false, false);
	    break;
	case PESSIMISTIC_WEIGHTED:
	    fitter = new FitnessNbDominated(all, approxPreorder, false, true);
	    break;
	case RANDOM:
	    fitter = new FitnessRandom(all, approxPreorder);
	    break;
	case LIKELIHOOD:
	    fitter = new FitnessLikelihood(all, approxPreorder, approxRule, false);
	    break;
	case LIKELIHOOD_PLUS:
	    fitter = new FitnessLikelihood(all, approxPreorder, approxRule, true);
	    break;
	default:
	    throw new IllegalStateException();
	}

	w.write("m = " + m + EOL);
	w.write("n = " + n + EOL);
	final int sampleSize = 1000;
	w.write("sample size for quality measures = " + sampleSize + EOL);
	w.write("Average winners, target: "
		+ m_format.format(RuleUtils.getAverageNbWinners(m, n, m_targetRule, sampleSize)) + "." + EOL);
	w.write("Fitness = '" + m_currentFitnessType + "'." + EOL);

	final List<RuleComparer> ruleComparers = Lists.newLinkedList();
	ruleComparers.add(new RuleComparerNbWinners(all.getM(), all.getN(), m_targetRule, approxRule));
	ruleComparers.add(new RuleComparerWoScore(all, m_targetRule, m_targetWo, approxRule, ScoreType.BY_PROFILE));
	ruleComparers.add(new RuleComparerWoScore(all, m_targetRule, m_targetWo, approxRule,
		ScoreType.SUM_SUPPL_WINNERS));

	for (int i = 0; i <= m_nbQuestions; ++i) {
	    for (RuleComparer comparer : ruleComparers) {
		final List<Double> errors = compareRules(comparer, sampleSize);
		w.write(i + "Q '" + comparer + "': " + getAsStr(errors));
		final double avgError = RuleUtils.getAverage(errors);
		w.write("; avg = " + m_format.format(avgError) + ". ");
	    }

	    final int nbIncomparable = RelationUtils.getNbIncomparable(approxPreorder);
	    w.write("Nb incomp (unordered pairs): " + nbIncomparable + ".");
	    m_nbIncomparables.get(i).add(nbIncomparable);
	    w.write(EOL);

	    final Pair<List<Integer>, List<Integer>> fittest = fitter.getFittest();
	    // s_logger.info("FittestP: {}, asking.", fitterP.getFittest());
	    // s_logger.info("FittestW: {}, asking.", fitterW.getFittest());
	    final List<Integer> x = fittest.getElt1();
	    final List<Integer> y = fittest.getElt2();
	    final boolean geq = m_targetWo.compare(x, y) >= 0;
	    final boolean leq = m_targetWo.compare(y, x) >= 0;
	    if (leq && geq) {
		approxPreorder.addEqTransitive(x, y);
		w.write("Obtained: " + RvNamer.getInstance().apply(x) + " = " + RvNamer.getInstance().apply(y) + "."
			+ EOL);
	    } else if (leq) {
		approxPreorder.addTransitive(y, x);
		w.write("Obtained: " + RvNamer.getInstance().apply(y) + " > " + RvNamer.getInstance().apply(x) + "."
			+ EOL);
	    } else if (geq) {
		approxPreorder.addTransitive(x, y);
		w.write("Obtained: " + RvNamer.getInstance().apply(x) + " > " + RvNamer.getInstance().apply(y) + "."
			+ EOL);
	    } else {
		throw new IllegalStateException();
	    }
	    keepCoolMan();
	}
	// export(GraphUtils.getTransitiveReduct(fitter));
    }

    public void keepCoolMan() throws IOException, InterruptedException {
	while (isHot()) {
	    s_logger.info("Too hot, sleeping.");
	    Thread.sleep(1000);
	}
    }

    static public enum TargetRuleType {
	BORDA, RANDOM;
	@Override
	public String toString() {
	    switch (this) {
	    case BORDA:
		return "Borda";
	    case RANDOM:
		return "Random";
	    default:
		throw new IllegalStateException();
	    }
	}
    }

    static public class RvNamer implements VertexNameProvider<List<Integer>>, Function<List<Integer>, String> {
	static private final RvNamer s_instance = new RvNamer();

	@Override
	public String getVertexName(List<Integer> rv) {
	    return Joiner.on("").join(rv);
	}

	@Override
	public String apply(List<Integer> input) {
	    return getVertexName(input);
	}

	static public RvNamer getInstance() {
	    return s_instance;
	}
    }

    private PreorderRule getRandomTargetRule(AllRankVectors all, long seed) {
	final Preorder<List<Integer>> targetPreorder = new Preorder<List<Integer>>();
	for (Pair<List<Integer>, List<Integer>> paretoPair : all.getParetoDominanceNonTransitive().asPairs()) {
	    targetPreorder.addTransitive(paretoPair.getElt1(), paretoPair.getElt2());
	}
	RelationUtils.completeRandomly(targetPreorder, seed);
	final PreorderRule targetRule = new PreorderRule(targetPreorder);
	return targetRule;
    }

    private Comparator<List<Integer>> getWoRule(final PreorderRule rule) {
	final Comparator<List<Integer>> targetWo = new Comparator<List<Integer>>() {
	    @Override
	    public int compare(List<Integer> o1, List<Integer> o2) {
		final ComparisonStatus status = rule.compare(o1, o2);
		// assert (status != ComparisonStatus.INCOMPARABLE);
		switch (status) {
		case BETTER:
		    return 1;
		case EQUIVALENT:
		    return 0;
		case WORST:
		    return -1;
		default:
		    throw new IllegalStateException("Should be comparable.");
		}
	    }
	};
	return targetWo;
    }

    private String getAsStr(List<Double> errors) {
	final Collection<String> errorsStrings = Collections2.transform(errors, m_formatFunction);
	final String errorString = Joiner.on("; ").join(errorsStrings);
	return errorString;
    }

    private List<Double> compareRules(RuleComparer comparer, int sampleSize) {
	final List<Double> errors = Lists.newLinkedList();
	for (int j = 0; j < 3; ++j) {
	    errors.add(comparer.sample(sampleSize));
	}
	return errors;
    }

    public boolean isHot() throws IOException, InterruptedException {
	if (!m_checkHot) {
	    return false;
	}
	Runtime rt = Runtime.getRuntime();
	Process pr = rt.exec("sensors");
	// Process pr = rt.exec("ls");
	final int exitCode = pr.waitFor();
	final InputStream outputStream = pr.getInputStream();
	final InputStream errorStream = pr.getErrorStream();
	final InputStreamReader outputReader = new InputStreamReader(outputStream, Charsets.UTF_8);
	final String output = CharStreams.toString(outputReader);
	final InputStreamReader errorReader = new InputStreamReader(errorStream, Charsets.UTF_8);
	final String error = CharStreams.toString(errorReader);
	s_logger.debug("Sensors output: {}", output);
	if (!error.isEmpty()) {
	    s_logger.warn("Error: {}", error);
	}
	assert exitCode == 0;

	final Pattern pattern = Pattern.compile("temp1: +\\+([0-9\\.]+)°C");
	final Matcher matcher = pattern.matcher(output);
	final List<Double> temps = Lists.newArrayList();
	while (matcher.find()) {
	    final String tempStr = matcher.group(1);
	    s_logger.debug("Group: {}.", tempStr);
	    final Double temp = Double.valueOf(tempStr);
	    temps.add(temp);
	}
	// final Double maxTemp = Collections.max(temps);
	// return maxTemp >= 80;
	Collections.sort(temps);
	if (temps.size() != 2) {
	    throw new IllegalStateException();
	}
	final Double lowest = temps.get(0);
	final Double highest = temps.get(1);
	return lowest >= 70 || highest >= 80;
    }

    public void viewParetoGraph() throws Exception {
	final int m = 4;
	final int n = 3;
	final AllRankVectors all = new AllRankVectors(m, n);
	final BinaryRelation<List<Integer>, List<Integer>> paretoNT = all.getParetoDominanceNonTransitive();
	// final BinaryRelation<List<Integer>, List<Integer>> pareto = GraphUtils.getTransitiveClosure(paretoNT);
	// GraphUtils.computeTransitiveReduct(pareto);
	// export(pareto);
	m_rvNamer = RvWeightedNamer.getInstance();
	export(paretoNT);
    }

    public void convertFile(String sourceFilename, String filenameBase) throws IOException, InterruptedException {
	Runtime rt = Runtime.getRuntime();
	Process pr = rt.exec("dot -Tsvg " + sourceFilename + " -o " + filenameBase + ".svg");
	// Process pr = rt.exec("ls");
	final int exitCode = pr.waitFor();
	final InputStream outputStream = pr.getInputStream();
	final InputStream errorStream = pr.getErrorStream();
	final InputStreamReader outputReader = new InputStreamReader(outputStream, Charsets.UTF_8);
	final String output = CharStreams.toString(outputReader);
	final InputStreamReader errorReader = new InputStreamReader(errorStream, Charsets.UTF_8);
	final String error = CharStreams.toString(errorReader);
	if (!output.isEmpty()) {
	    s_logger.info("Output: {}", output);
	}
	if (!error.isEmpty()) {
	    s_logger.warn("Error: {}", error);
	}

	assert exitCode == 0;
    }

    static public NumberFormat getDoubleFormat() {
	final NumberFormat doubleFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
	doubleFormat.setMaximumFractionDigits(1);
	doubleFormat.setMinimumFractionDigits(1);
	doubleFormat.setRoundingMode(RoundingMode.HALF_UP);
	return doubleFormat;
    }

}
