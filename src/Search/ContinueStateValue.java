package Search;

public class ContinueStateValue {
	StateValue valDiscrete;
	int valContinue;
	
	//Constructeurs
	public ContinueStateValue (int val){
		valDiscrete = StateValue.DRAW;
		valContinue = val;
	}
	
	public ContinueStateValue (StateValue valDiscrete){
		this.valDiscrete = valDiscrete;
	}

}
