package Search;

public class ContinueStateValueWithBound {
	//Version continue de StateValueWithBound
	
	ContinueStateValue value;
	
	/**
	 * Vaut -1 si lower bound (ie value >= beta), 0 si accurate (alpha < value < beta), 1 sinon
	 */
	private int bound;
	
	//int bestCoup;
	
	public ContinueStateValueWithBound(ContinueStateValue v, ContinueStateValue alpha, ContinueStateValue beta/*, int i*/){
		value = v;
		
		if(v.lessOrEquals(alpha)){
			bound = 1;
			//bestCoup = i;
			return;
		}
		
		if(v.betterOrEquals(beta)){
			bound = -1;
			//bestCoup = i;
			return;
		}
		//valeur accurate
		bound = 0;
		//bestCoup = i;
	}
	

	public ContinueStateValueWithBound(ContinueStateValue v, int b/*, int i*/){
		value = v;
		bound = b;
		//bestCoup = i;
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
	
	public void setBound(int i){
		if(i < -1 || i > 1)
			throw new RuntimeException("La borne doit être -1, 0 ou 1.");
		
		bound = i;
	}
	
}
