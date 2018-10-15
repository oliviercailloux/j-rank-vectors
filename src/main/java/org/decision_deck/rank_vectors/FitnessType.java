package org.decision_deck.rank_vectors;

import static com.google.common.base.Preconditions.checkNotNull;

public enum FitnessType {
    OPTIMISTIC, OPTIMISTIC_WEIGHTED, RANDOM, PESSIMISTIC, PESSIMISTIC_WEIGHTED, LIKELIHOOD, LIKELIHOOD_PLUS;
    static public FitnessType fromString(String s) {
	checkNotNull(s);
	for (FitnessType type : FitnessType.values()) {
	    if (s.equals(type.toString())) {
		return type;
	    }
	}
	throw new IllegalArgumentException();
    }

    static public int LIKELIHOOD_PLUS_SAMPLE = 10000;

    @Override
    public String toString() {
	switch (this) {
	case OPTIMISTIC:
	    return "optimistic";
	case OPTIMISTIC_WEIGHTED:
	    return "optimistic-weighted";
	case RANDOM:
	    return "random";
	case PESSIMISTIC:
	    return "pessimistic";
	case PESSIMISTIC_WEIGHTED:
	    return "pessimistic-weighted";
	case LIKELIHOOD:
	    return "likelihood";
	case LIKELIHOOD_PLUS:
	    return "likelihood" + LIKELIHOOD_PLUS_SAMPLE;
	default:
	    throw new IllegalStateException();
	}
    }

    public String toShortString() {
        switch (this) {
        case OPTIMISTIC:
	    return "o";
        case OPTIMISTIC_WEIGHTED:
	    return "ow";
        case RANDOM:
	    return "r";
        case PESSIMISTIC:
	    return "p";
        case PESSIMISTIC_WEIGHTED:
	    return "pw";
        case LIKELIHOOD:
	    return "l";
        case LIKELIHOOD_PLUS:
	    return "l+";
        default:
            throw new IllegalStateException();
        }
    }
}