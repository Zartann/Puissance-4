package Search;

public enum StateValue {
	LOSS, DRAW, WIN;
	
	public boolean isLoss(){
		return (this == LOSS);
	}
	
	public boolean isDraw(){
		return (this == DRAW);
	}
	
	public boolean isWin(){
		return (this == WIN);
	}
	
	/**
	 * 
	 * @param s
	 * @return Etat maximal entre this et s
	 */
	public StateValue max(StateValue s){
		if(this == LOSS)
			return s;
		
		if(this == WIN)
			return this;
		
		if(s == LOSS)
			return this;
		
		return s;
	}
	
	/**
	 * 
	 * @param s
	 * @return Etat minimal entre this et s
	 */
	public StateValue min(StateValue s){
		if(this == LOSS)
			return this;
		
		if(this == WIN)
			return s;
		
		if(s == WIN)
			return this;
		
		return s;
	}
}
