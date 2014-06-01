package Search;

public class StateBornedValue {
	/*StateValue alpha;
	StateValue beta;
	
	/**
	 * Renvoie true si la valeur représentée est fixée
	 */
	/*public boolean IsFixed (){
		return alpha.equals(beta);
	}

	public StateBornedValue(StateValue alpha, StateValue beta) {
		super();
		this.alpha = alpha;
		this.beta = beta;
	}
	
	public StateBornedValue Intersection (StateBornedValue value){
		if (this.IsFixed()) return this;
		if (value.IsFixed()) return value;
		else {
			StateValue min = alpha.max(value.alpha);
			StateValue max = beta.min(value.beta);
			return new StateBornedValue (min, max);
		}
	}*/
}
