package io.github.oliviercailloux.y2018.minimax;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Preconditions;

/**
 * TODO consider protecting the sub-objects; not using types; forbidding a > a.
 *
 * @author Olivier Cailloux
 *
 */
public class Question {

	private QuestionVoter qv;
	private QuestionCommittee qc;

	public Question(QuestionVoter qv) {
		this.qv = Objects.requireNonNull(qv);
		this.qc = null;
	}

	public Question(QuestionCommittee qc) {
		this.qc = Objects.requireNonNull(qc);
		this.qv = null;
	}

	public QuestionVoter getQuestionVoter() {
		Preconditions.checkState(qc == null);
		return qv;
	}

	public QuestionCommittee getQuestionCommittee() {
		Preconditions.checkState(qv == null);
		return qc;
	}

	public QuestionType getType() {
		if (qv == null) {
			return QuestionType.COMMITTEE_QUESTION;
		}
		return QuestionType.VOTER_QUESTION;
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Question)) {
			return false;
		}
		final Question q2 = (Question) o2;
		return Objects.equals(qv, q2.qv) && Objects.equals(qc, q2.qc);
	}

	@Override
	public int hashCode() {
		return Objects.hash(qv, qc);
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		switch (getType()) {
		case COMMITTEE_QUESTION:
			helper.addValue(qc);
			break;
		case VOTER_QUESTION:
			helper.addValue(qv);
			break;
		default:
			throw new AssertionError();
		}
		return helper.toString();
	}
}
