package io.github.oliviercailloux.j_voting.elicitation;

import static org.junit.Assert.assertEquals;

import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.Test;

import io.github.oliviercailloux.j_voting.elicitation.Answer;
import io.github.oliviercailloux.j_voting.elicitation.PSRWeights;
import io.github.oliviercailloux.j_voting.elicitation.QuestionCommittee;
import io.github.oliviercailloux.minimax.XPRunner;

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
