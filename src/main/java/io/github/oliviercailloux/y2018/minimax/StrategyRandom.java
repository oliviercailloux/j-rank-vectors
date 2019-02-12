package io.github.oliviercailloux.y2018.minimax;

/** Uses a random approach to get the next question:
 *  builds a set containing all the questions (both to the voters 
 *  and to the committee), randomly picks one.
 *  
 *  Problems:
 *  1) After having asked a question, the preference knowledge changes,
 *     so some question in the set might not have sense anymore.
 *     	Solution: recompute the set every time. Expensive. 
 *  2) Because of the rational nature of λ, there exists an infinite 
 *     number of questions for the committee.
 *     	Solution: divide the admissible interval of λ in t finite
 *     	slots and update this range after every answer. 
 *     		Note: Is there an upper bound for lambda? Should be n(m-2) //TOCHECK
 *     			  This makes the initial admissible interval [1, n(m-2)].
 *     			  Then divide this, for example, in 10 slots, ask a question and
 *     			  depending from the answer reduce the interval for the next question.
 **/

public class StrategyRandom implements Strategy{

	@Override
	public Question getQuestion(PrefKnowledge knowledge) {
		// TODO Auto-generated method stub
		return null;
	}
}
