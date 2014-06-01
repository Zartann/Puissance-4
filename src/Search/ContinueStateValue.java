package Search;

public class ContinueStateValue {
	/**
	 * valDiscrete est utilis� pour une issue s�re (apr�s exploration de tous les chemins � partir de la position consid�r�e)
	 * valContinue est utilis� pour les issues non encore enti�rement d�finies
	 */
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
	 * Indique si la valeur est sup�rieure ou �gale � s
	 * @param s
	 * @return boolean
	 */
	public boolean betterOrEquals(ContinueStateValue s){

		//Beaucoup plus rapide qu'avec ne serait-ce qu'un seul if !!!
		return //s est perdante
				(s.valDiscrete==StateValue.LOSS)||
				//this est gagnante
				(this.valDiscrete==StateValue.WIN)||
				//valDiscrete est DRAW ainsi que s.valDiscrete
				((this.valDiscrete!=null) &&(this.valDiscrete==s.valDiscrete))||
				//this n'est pas bien d�finie, et soit s n'est pas bien d�finie mais inf�rieure � this,
				//soit elle vaut DRAW, et alors on consid�re que this vaut mieux qu'elle si elle pr�sente un avantage (valContinue >=0) 
				(this.valDiscrete ==null)&&
				(((s.valDiscrete==null)&&(this.valContinue>=s.valContinue))||
						((s.valDiscrete==StateValue.DRAW)&&(this.valContinue>=0)));
	}
	
	/**
	 * Indique si la valeur est inf�rieure ou �gale � s
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
			//this est pr�cis�ment d�finie
			return new ContinueStateValue (this.valDiscrete.opposite());
		//this est impr�cise
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
		else return ""+this.valContinue;
	}
}
