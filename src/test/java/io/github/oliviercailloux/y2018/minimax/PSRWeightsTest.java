package io.github.oliviercailloux.y2018.minimax;

import static org.junit.Assert.assertEquals;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.Test;

public class PSRWeightsTest {

	@Test
	public void testLambda() {
		Aprational a = new Aprational(new Apint(2), new Apint(3));
		QuestionCommittee qc = QuestionCommittee.given(a, 1);
		Answer answ;
		PSRWeights weights = XPRunner.genWeights(7);
		double left = (weights.getWeightAtRank(1) - weights.getWeightAtRank(2));
		double right = a.doubleValue() * (weights.getWeightAtRank(2) - weights.getWeightAtRank(3));
		if (left > right) {
			answ = Answer.GREATER;
		} else if (left == right) {
			answ = Answer.EQUAL;
		} else {
			answ = Answer.LOWER;
		}
		System.out.println(weights);
		System.out.println(a.numerator() + " / " + a.denominator() + " = " + a.doubleValue());
		System.out.println(left + " " + answ + " " + right);
		assertEquals(answ, weights.askQuestion(qc));

	}
}
