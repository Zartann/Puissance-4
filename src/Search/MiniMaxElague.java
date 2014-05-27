package Search;

import java.util.List;

import Game.PlateauCourant;

public class MiniMaxElague {

	//Au début, on va à la profondeur maximale.
	public static StateValue miniMax(PlateauCourant state, boolean isMax){
		
		List<Integer> shots = state.validShots();
		
		StateValue value = state.result();

		//System.out.println(state.result() + " :");
		//System.out.println(state);
		
		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw())
			return value;
		
		if(isMax){
			
			//On initialise l'état à LOSS
			value = StateValue.LOSS;
			
			for(int shot : shots){
				state.playCurrent(shot);
				value = value.max(miniMax(state, !isMax));
				state.undoLast();
				
				//On peut déjà couper comme ceci :
				if(value.isWin())
					break;
			}
		}
		else{
			//On initialise l'état à WIN
			value = StateValue.WIN;
			
			for(int shot : shots){
				state.playAdverse(shot);
				value = value.min(miniMax(state, !isMax));
				state.undoLast();
				
				//On peut déjà couper comme ceci :
				if(value.isLoss())
					break;
			}
		}
		
		
		return value;
	}

}
