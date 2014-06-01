package Search;

public enum StateValue {
	/**
	 * Etats possibles d'un plateau de jeu
	 */
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
	 * Indique si la valeur est sup�rieure ou �gale � s
	 * @param s
	 * @return boolean
	 */
	public boolean betterOrEquals(StateValue s){

		//Beaucoup plus rapide qu'avec ne serait-ce qu'un seul if !!!
		return (s == LOSS || 
				s == DRAW && this != LOSS ||
				s == WIN && this == WIN);
	}
	
	/**
	 * Indique si la valeur est inf�rieure ou �gale � s
	 * @param s
	 * @return boolean
	 */
	public boolean lessOrEquals(StateValue s){
		
		return (this == LOSS || 
				this == DRAW && s != LOSS ||
				s == WIN);
	}

	/**
	 * 
	 * @param s
	 * @return Etat maximal entre this et s
	 */
	public StateValue max(StateValue s){
		if(this.betterOrEquals(s))
			return this;

		return s;
	}

	/**
	 * 
	 * @param s
	 * @return Etat minimal entre this et s
	 */
	public StateValue min(StateValue s){
		if(this.lessOrEquals(s))
			return this;

		return s;
	}
	
	public StateValue opposite(){
		if(this == LOSS)
			return WIN;
		
		if(this == WIN)
			return LOSS;
		
		return DRAW;
	}

	public String toString(){
		if(this == LOSS)
			return "LOSS";

		if(this == DRAW)
			return "DRAW";

		return "WIN";
	}
}
