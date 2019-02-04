package org.decision_deck.rank_vectors;

public class IncompleteProfileException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IncompleteProfileException() {
	    super("Profile Incomplete");
	}
	
	public IncompleteProfileException(String errorMessage) {
	    super(errorMessage);
	}
	
	public IncompleteProfileException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}

	public String getMessage()
    {
        return super.getMessage();
    }
}
