package Search;

public class StateValueWithBound {
	
	StateValue value;
	
	/**
	 * Vaut -1 si lower bound (ie value >= beta), 0 si accurate (alpha < value < beta), 1 sinon
	 */
	private int bound;

	public StateValueWithBound(StateValue v, StateValue alpha, StateValue beta){
		value = v;
		
		if(v.lessOrEquals(alpha)){
			bound = 1;
			return;
		}
		
		if(v.betterOrEquals(beta)){
			bound = -1;
			return;
		}
		
		bound = 0;
	}
	
	public StateValueWithBound(StateValue v, int b){
		value = v;
		bound = b;
	}
	
	public boolean isUpperBound(){
		return bound == 1;
	}
	
	public boolean isLowerBound(){
		return bound == -1;
	}
	
	public boolean isAccurate(){
		return bound == 0;
	}
}
