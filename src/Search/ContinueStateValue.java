package Search;

public class ContinueStateValue {
	StateValue valDiscrete;
	int valContinue;
	
	//Constructeurs
	public ContinueStateValue (int val){
		valContinue = val;
	}
	
	public ContinueStateValue (StateValue valDiscrete){
		this.valDiscrete = valDiscrete;
	}
	
	public boolean isLoss(){
		return (valDiscrete == StateValue.LOSS);
	}

	public boolean isNotWinNorLoss(){
		return ((valDiscrete != StateValue.LOSS)&&(valDiscrete != StateValue.WIN));
	}

	public boolean isWin(){
		return (valDiscrete == StateValue.WIN);
	}
	
	/**
	 * Indique si la valeur est supérieure ou égale à s
	 * @param s
	 * @return boolean
	 */
	public boolean betterOrEquals(ContinueStateValue s){

		//Beaucoup plus rapide qu'avec ne serait-ce qu'un seul if !!!
		return (s.valDiscrete==StateValue.LOSS)||
				(this.valDiscrete==StateValue.WIN)||
				((this.valDiscrete!=null) &&(this.valDiscrete==s.valDiscrete))||
				(this.valDiscrete ==null)&&
				(((s.valDiscrete==null)&&(this.valContinue>=s.valContinue))||
						((s.valDiscrete==StateValue.DRAW)&&(this.valContinue>=0)));
	}
	
	/**
	 * Indique si la valeur est inférieure ou égale à s
	 * @param s
	 * @return boolean
	 */
	public boolean lessOrEquals(ContinueStateValue s){
		
		return s.betterOrEquals(this);
	}

	/**
	 * 
	 * @param s
	 * @return Etat maximal entre this et s
	 */
	public ContinueStateValue max(ContinueStateValue s){
		if(this.betterOrEquals(s))
			return this;

		return s;
	}

	/**
	 * 
	 * @param s
	 * @return Etat minimal entre this et s
	 */
	public ContinueStateValue min(ContinueStateValue s){
		if (this.lessOrEquals(s))
			return this;
		return s;
	}
	
	public ContinueStateValue opposite(){
		if (this.valDiscrete!=null)
			return new ContinueStateValue (this.valDiscrete.opposite());
		else return new ContinueStateValue (-this.valContinue);
	}

	public String toString(){
		if(this.valDiscrete!=null){
			switch (this.valDiscrete){
			case DRAW : return "DRAW";
			case WIN : return "WIN";
			default : return "LOSS";
			}
		}
		else return "Pas eu le temps de déterminer";
	}
}
