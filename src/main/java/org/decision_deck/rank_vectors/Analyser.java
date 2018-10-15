package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.decision_deck.rank_vectors.Elicit.TargetRuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.io.CharSink;
import com.google.common.io.Closeables;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.primitives.Doubles;

public class Analyser {

	private String m_prefix;
	private ContiguousSet<Integer> m_restrictedColumns;

	public Analyser() {
		m_prefix = "";
		m_restrictedColumns = null;
		m_quality = RuleComparerType.SUPPL_WINNERS;
		m_target = TargetRuleType.BORDA;
		m_fitness = FitnessType.RANDOM;
		m_inputDir = new File("/home/olivier/Recherche/Choix social (et AMCD)/Constrain voting rules/exp results");
		m_qNumber = 99;
		m_outputFile = new File(
				"/home/olivier/Recherche/Choix social (et AMCD)/Constrain voting rules/latex/src/Dump.tex");
		m_intervalSummaries = true;
		m_writeCsv = true;
	}

	public static void main(String[] args) throws Exception {
		final Analyser analyser = new Analyser();
		analyser.proceed();
		// analyser.writeAll();
	}

	private void proceed() throws IOException {
		m_intervalSummaries = true;
		m_writeCsv = false;
		if (m_writeCsv) {
			m_writer = Files.newWriter(new File("out.csv"), Charsets.UTF_8);
			m_csvW = new CsvListWriter(m_writer, CsvPreference.STANDARD_PREFERENCE);
			m_csvW.writeHeader("n", "m", "q", "fit", "nb w.", "wo su.", "nb inc", "nb inc 2", "nb w. R", "wo su. R",
					"nb inc R", "nb inc 2 R");
		} else {
			m_writer = new OutputStreamWriter(System.out);
		}
//	showStats(4, 10);
//	showStats(6, 6);
		showStats(10, 4);
		if (m_writeCsv) {
			m_csvW.close();
		}
	}

	public void showStats(final int m, final int n) throws IOException {
		final NumberFormat intFormat = Elicit.getIntFormat();
		final ImmutableList<Integer> qNumbers = ImmutableList.of(0, 25, 99);
		for (Integer qNumber : qNumbers) {
			m_qNumber = qNumber;
			// m_fitnesses = Arrays.asList(FitnessType.values());
			m_fitnesses = ImmutableList.of(FitnessType.OPTIMISTIC_WEIGHTED, FitnessType.RANDOM,
					FitnessType.PESSIMISTIC_WEIGHTED, FitnessType.LIKELIHOOD, FitnessType.LIKELIHOOD_PLUS);
			for (FitnessType fitness : m_fitnesses) {
				m_fitness = fitness;
				m_target = TargetRuleType.RANDOM;

				m_quality = RuleComparerType.SUPPL_WINNERS;
				readData();
				final String nbWRandom = getSummary(m_allValues.get(m, n), false) + getSizeStr(m_allValues.get(m, n));
				final String nbIRandom = getSummaryInt(m_nbIncomps.get(m, n)) + getSizeStr(m_nbIncomps.get(m, n));

				m_quality = RuleComparerType.WO_SCORE_SUPPL;
				readData();
				final String woRandom = getSummary(m_allValues.get(m, n), true) + getSizeStr(m_allValues.get(m, n));
				final String nbIRandom2 = getSummaryInt(m_nbIncomps.get(m, n)) + getSizeStr(m_nbIncomps.get(m, n));

				m_target = TargetRuleType.BORDA;

				m_quality = RuleComparerType.SUPPL_WINNERS;
				readData();
				final String nbWBorda = getSummary(m_allValues.get(m, n), false) + getSizeStr(m_allValues.get(m, n));
				final String nbIBorda = getSummaryInt(m_nbIncomps.get(m, n)) + getSizeStr(m_nbIncomps.get(m, n));

				m_quality = RuleComparerType.WO_SCORE_SUPPL;
				readData();
				final String woBorda = getSummary(m_allValues.get(m, n), true) + getSizeStr(m_allValues.get(m, n));
				final String nbIBorda2 = getSummaryInt(m_nbIncomps.get(m, n)) + getSizeStr(m_nbIncomps.get(m, n));

				if (m_writeCsv) {
					m_csvW.write(ImmutableList.of(String.valueOf(n), String.valueOf(m), String.valueOf(m_qNumber),
							toShortString(m_fitness), nbWBorda, woBorda, nbIBorda, nbIBorda2, nbWRandom, woRandom,
							nbIRandom, nbIRandom2));
				} else {
					if (m_qNumber != 0 || m_fitness == m_fitnesses.get(0)) {
						final String size = m_qNumber == 0 ? n + " & " + m + " & " : "  &   & ";
						final String qStr = m_fitness == m_fitnesses.get(0) ? intFormat.format(m_qNumber) + " & "
								: "   & ";
						final String fitStr = m_qNumber == 0 ? "  & " : toShortString(m_fitness) + " & ";
						final String tableLine = size + qStr + fitStr + nbWBorda + " & " + woBorda + " & " + nbWRandom
								+ " & " + woRandom + "\\\\";
						m_writer.write(tableLine);
						m_writer.write(Elicit.EOL);
						m_writer.flush();
					}
				}
			}
		}
	}

	private String getSizeStr(List<?> list) {
		return " (" + list.size() + ")";
	}

	void writeAll() throws IOException {
		m_outputFile.delete();

		m_quality = RuleComparerType.SUPPL_WINNERS;
		askTargFit();
		m_quality = RuleComparerType.WO_SCORE;
		askTargFit();
		m_quality = RuleComparerType.WO_SCORE_APPROX_WINNER;
		askTargFit();
	}

	public void askTargFit() throws IOException {
		m_target = TargetRuleType.BORDA;

		m_fitness = FitnessType.RANDOM;
		m_qNumber = 0;
		readData();
		writeToOutput(summarize(m_allValues));

		m_fitness = FitnessType.PESSIMISTIC;
		m_qNumber = 0;
		m_fitness = FitnessType.LIKELIHOOD;
		m_qNumber = 0;
		m_fitness = FitnessType.RANDOM;
		m_qNumber = 25;
		m_fitness = FitnessType.PESSIMISTIC;
		m_qNumber = 25;
		m_fitness = FitnessType.LIKELIHOOD;
		m_qNumber = 25;
		m_fitness = FitnessType.RANDOM;
		m_qNumber = 99;
		m_fitness = FitnessType.PESSIMISTIC;
		m_qNumber = 99;
		m_fitness = FitnessType.LIKELIHOOD;
		m_qNumber = 99;

		m_target = TargetRuleType.RANDOM;

		m_fitness = FitnessType.RANDOM;
		m_qNumber = 0;
		m_fitness = FitnessType.PESSIMISTIC;
		m_qNumber = 0;
		m_fitness = FitnessType.LIKELIHOOD;
		m_qNumber = 0;
		m_fitness = FitnessType.RANDOM;
		m_qNumber = 25;
		m_fitness = FitnessType.PESSIMISTIC;
		m_qNumber = 25;
		m_fitness = FitnessType.LIKELIHOOD;
		m_qNumber = 25;
		m_fitness = FitnessType.RANDOM;
		m_qNumber = 99;
		m_fitness = FitnessType.PESSIMISTIC;
		m_qNumber = 99;
		m_fitness = FitnessType.LIKELIHOOD;
		m_qNumber = 99;
	}

	/**
	 * Sets all values and avg values and nb incomp tables.
	 * 
	 * @throws IOException if necessary.
	 */
	private void readData() throws IOException {
		final Pattern allValuesPattern;
		final String searchString = m_quality.toString();
		allValuesPattern = Pattern
				.compile("\\b" + m_qNumber + "Q '" + searchString + "': ([0-9\\.; ]+); avg = [0-9\\.]+.");

		final File[] inputFiles = m_inputDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				final boolean soloMatch = name.contains("Target=" + m_target + ", fit=" + m_fitness);
				if (soloMatch) {
					s_logger.debug("Name {} OK.", name);
					return true;
				}
				final Matcher matcher = Pattern.compile("Target=" + m_target + ", fit=\\[([^\\]]*)\\]").matcher(name);
				if (!matcher.find()) {
					s_logger.debug("Name {} NOK.", name);
					return false;
				}
				final String[] fitnessesStr = matcher.group(1).split(", ");
				final List<FitnessType> fitnesses = Lists.transform(Arrays.asList(fitnessesStr),
						new Function<String, FitnessType>() {
							@Override
							public FitnessType apply(String input) {
								return FitnessType.fromString(input);
							}
						});
				final boolean contains = fitnesses.contains(m_fitness);
				s_logger.debug("Name {} contains: {}, search: {}.", name, fitnesses, m_fitness);
				return contains;
			}
		});
		final List<String> allLines = Lists.newArrayList();
		for (File inputFile : inputFiles) {
			final List<String> lines = Files.readLines(inputFile, Charsets.UTF_8);
			allLines.addAll(lines);
		}
		if (allLines.size() < 10) {
			throw new IllegalStateException();
		}
		int lastM = -1;
		int lastN = -1;
		String lastFitness = null;
		final Pattern mPattern = Pattern.compile("^m = ([0-9]+)$");
		final Pattern nPattern = Pattern.compile("^n = ([0-9]+)$");
		final Pattern fitnessPattern = Pattern.compile("^Fitness = '([^\\.]+)'\\.$");
		final Pattern incompPattern = Pattern.compile("Nb incomp \\(unordered pairs\\): ([0-9]+).$");
		m_allValues = HashBasedTable.create();
		m_avgValues = HashBasedTable.create();
		m_nbIncomps = HashBasedTable.create();
		for (String line : allLines) {
			{
				final Matcher matcher = mPattern.matcher(line);
				if (matcher.find()) {
					lastM = Integer.valueOf(matcher.group(1));
				}
			}
			{
				final Matcher matcher = nPattern.matcher(line);
				if (matcher.find()) {
					lastN = Integer.valueOf(matcher.group(1));
				}
			}
			{
				final Matcher matcher = fitnessPattern.matcher(line);
				if (matcher.find()) {
					lastFitness = matcher.group(1);
				}
			}
			{
				final Matcher matcher = allValuesPattern.matcher(line);
				if (matcher.find()) {
					if (lastM == -1 || lastN == -1 || lastFitness == null) {
						throw new IllegalStateException();
					}
					if (!lastFitness.equals(m_fitness.toString())) {
						s_logger.debug("Does not match: {} VS {}.", lastFitness, m_fitness);
						continue;
					}
					final String valuesString = matcher.group(1);
					s_logger.debug("Values string: {}.", valuesString);
					final String[] valuesStrings = valuesString.split("[,;] ");
					final ImmutableList<Double> values = ImmutableList
							.copyOf(Doubles.stringConverter().convertAll(Arrays.asList(valuesStrings)));
					addTo(m_allValues, lastM, lastN, values);
					addTo(m_avgValues, lastM, lastN, RuleUtils.getAverage(values));

					final Matcher matcher2 = incompPattern.matcher(line);
					if (!matcher2.find()) {
						throw new IllegalStateException(line);
					}
					final String nbIncompStr = matcher2.group(1);
					s_logger.debug("Nb incomp str: {}.", nbIncompStr);
					final Integer nbIncomp = Integer.valueOf(nbIncompStr);
					addTo(m_nbIncomps, lastM, lastN, nbIncomp);
				}
			}
		}
		if (m_allValues.isEmpty()) {
			throw new IllegalStateException();
		}
	}

	public Table<Integer, Integer, String> summarize(final Table<Integer, Integer, List<Double>> table) {
		return Tables.transformValues(table, new Function<List<Double>, String>() {
			@Override
			public String apply(List<Double> input) {
				return getSummary(input, false);
			}
		});
	}

	private <R, C, V> void addTo(Table<R, C, List<V>> table, R row, C column, V value) {
		if (!table.contains(row, column)) {
			table.put(row, column, Lists.<V>newLinkedList());
		}
		final List<V> list = table.get(row, column);
		list.add(value);
	}

	private <R, C, V> void addTo(Table<R, C, List<V>> table, R row, C column, ImmutableList<V> values) {
		if (!table.contains(row, column)) {
			table.put(row, column, Lists.<V>newLinkedList());
		}
		final List<V> list = table.get(row, column);
		list.addAll(values);
	}

	public void writeToOutput(final Table<Integer, Integer, String> table) throws IOException {
		final String title = "Quality=" + m_quality + ", Q" + m_qNumber + ", Target=" + m_target + ", " + "Fit="
				+ m_fitness + ".";
		m_prefix = Elicit.EOL + title;
		final ContiguousSet<Integer> allCols = ContiguousSet.create(Range.closed(2, 14), DiscreteDomain.integers());
		if (table.columnKeySet().containsAll(allCols)) {
			m_restrictedColumns = ContiguousSet.create(Range.closed(2, 7), DiscreteDomain.integers());
			// export(table, Files.asCharSink(outputFile, Charsets.UTF_8));
			export(table, Files.asCharSink(m_outputFile, Charsets.UTF_8, FileWriteMode.APPEND));
			m_prefix = "";
			m_restrictedColumns = ContiguousSet.create(Range.closed(8, 14), DiscreteDomain.integers());
			export(table, Files.asCharSink(m_outputFile, Charsets.UTF_8, FileWriteMode.APPEND));
		} else {
			m_restrictedColumns = null;
			// export(table, Files.asCharSink(outputFile, Charsets.UTF_8));
			export(table, Files.asCharSink(m_outputFile, Charsets.UTF_8, FileWriteMode.APPEND));
		}
	}

	@SuppressWarnings({ "unused", "all" })
	private static final Logger s_logger = LoggerFactory.getLogger(Analyser.class);
	private FitnessType m_fitness;
	private final File m_inputDir;
	private int m_qNumber;
	private final File m_outputFile;
	private TargetRuleType m_target;
	private RuleComparerType m_quality;
	private Table<Integer, Integer, List<Double>> m_allValues;
	private Table<Integer, Integer, List<Double>> m_avgValues;
	private HashBasedTable<Integer, Integer, List<Integer>> m_nbIncomps;
	private List<FitnessType> m_fitnesses;
	private boolean m_intervalSummaries;
	private CsvListWriter m_csvW;
	private Writer m_writer;
	private boolean m_writeCsv;

	private String getSummaryInt(List<Integer> values) {
		final NumberFormat doubleFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
		doubleFormat.setMaximumFractionDigits(0);
		doubleFormat.setMinimumFractionDigits(0);
		doubleFormat.setGroupingUsed(false);
		doubleFormat.setRoundingMode(RoundingMode.HALF_UP);
		final NumberFormat fDbl = doubleFormat;
		final int min = Collections.min(values);
		final double avg = RuleUtils.getAverageInt(values);
		final int max = Collections.max(values);
		final boolean skipMin = Math.abs(min - avg) < 0.1;
		final boolean skipMax = Math.abs(max - avg) < 0.1;
		if (skipMin && skipMax) {
			assert (min == max);
		}
		return (skipMin && skipMax) ? "" + min : (fDbl.format(avg) + " 〈" + min + ", " + max + "〉");
	}

	public void export(final Table<Integer, Integer, String> table, CharSink sink) throws IOException {
		final Writer writer = sink.openBufferedStream();
		final String tableType = "tabular";
		final Set<Integer> columns = m_restrictedColumns == null ? table.columnKeySet() : m_restrictedColumns;
		checkArgument(table.columnKeySet().containsAll(columns));
		try {
			writer.write(m_prefix);
			writer.write(Elicit.EOL);
			writer.write(Elicit.EOL);
			writer.write("\\begin{" + tableType + "}{l");
			writer.write(Strings.repeat("c", columns.size()));
			writer.write("}");
			writer.write(Elicit.EOL);
			for (Integer n : columns) {
				writer.write("\t&" + n.toString());
			}
			writer.write("\\\\");
			writer.write(Elicit.EOL);
			writer.write("\\hline");
			writer.write(Elicit.EOL);
			final Set<Integer> remainingRows = Sets.filter(table.rowKeySet(), new Predicate<Integer>() {
				@Override
				public boolean apply(Integer mCheck) {
					final Map<Integer, String> row = table.row(mCheck);
					return !Sets.intersection(row.keySet(), columns).isEmpty();
				}
			});
			for (Integer m : remainingRows) {
				writer.write(m.toString());
				for (Integer n : columns) {
					final String entry = table.get(m, n);
					writer.write("\t&");
					if (entry != null) {
						writer.write(entry);
					}
				}
				writer.write("\\\\");
				writer.write(Elicit.EOL);
			}

			writer.write("\\end{" + tableType + "}");
			writer.write(Elicit.EOL);
			writer.close();
		} finally {
			Closeables.close(writer, true);
		}
	}

	private String getSummary(List<Double> values, boolean avoidZero) {
		final NumberFormat format = Elicit.getDoubleFormat();
		if (m_intervalSummaries) {
			final double min = Collections.min(values);
			final double average = RuleUtils.getAverage(values);
			final double max = Collections.max(values);
			final boolean skipMin = Math.abs(min - average) < 0.1;
			final boolean skipMax = Math.abs(max - average) < 0.1;
			return skipMin && skipMax ? format.format(average)
					: format.format(average) + " 〈" + format.format(min) + ", " + format.format(max) + "〉";
			// return (skipMin ? "" : format.format(min)) + " | " + format.format(average) +
			// " | "
			// + (skipMax ? "" : format.format(max));
		}
		final double avg = RuleUtils.getAverage(values);
		if (avoidZero && Math.abs(avg) < 0.01) {
			return "";
		}
		return format.format(avg);
	}

	public String toShortString(FitnessType fitness) {
		switch (fitness) {
		case OPTIMISTIC:
			return "onw";
		case OPTIMISTIC_WEIGHTED:
			return "o";
		case RANDOM:
			return "r";
		case PESSIMISTIC:
			return "pnw";
		case PESSIMISTIC_WEIGHTED:
			return "p";
		case LIKELIHOOD:
			return "l";
		case LIKELIHOOD_PLUS:
			return "l+";
		default:
			throw new IllegalStateException();
		}
	}

}
