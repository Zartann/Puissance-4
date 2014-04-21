package Search;

import java.util.List;

import Game.PlateauCourant;

public class Minimax {

	//Au d�but, on va � la profondeur maximale.
	public static StateValue miniMax(PlateauCourant state, boolean isMax){
		
		List<Integer> shots = state.validShots();
		
		StateValue value = state.result();
		
		if(shots.isEmpty() || !value.isDraw())
			return value;
		
		if(isMax){
			
			//On initialise l'�tat � LOSS
			value = StateValue.LOSS;
			
			for(int shot : shots){
				state.playCurrent(shot);
				value = value.max(miniMax(state, !isMax));
				state.undoLast();
				
				//On peut d�j� couper comme ceci :
				if(value.isWin())
					break;
			}
		}
		else{
			//On initialise l'�tat � WIN
			value = StateValue.WIN;
			
			for(int shot : shots){
				state.playAdverse(shot);
				value = value.min(miniMax(state, !isMax));
				state.undoLast();
				
				//On peut d�j� couper comme ceci :
				if(value.isLoss())
					break;
			}
		}
		
		
		return value;
	}

}