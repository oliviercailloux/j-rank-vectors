package io.github.oliviercailloux.y2018.minimax;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class Question {
	
	private QuestionVoter qv;
	private QuestionCommittee qc;
	
	public Question(QuestionVoter qv) {
		this.qv=Objects.requireNonNull(qv);
		this.qc=null;
	}
	
	public Question(QuestionCommittee qc) {
		this.qc=Objects.requireNonNull(qc);
		this.qv=null;
	}
	
	public QuestionVoter getQuestionVoter() {
		Preconditions.checkState(qv==null);
		return qv;
	}
	
	public QuestionCommittee getQuestionCommittee() {
		Preconditions.checkState(qc==null);
		return qc;
	}
	
	public QuestionType getType() {
		if(qv==null) {
			return QuestionType.COMMITTEE_QUESTION;
		}
		return QuestionType.VOTER_QUESTION;
	}
}
