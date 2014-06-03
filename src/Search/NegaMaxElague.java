package Search;

import java.util.List;

import Game.PlateauCourant;

public class NegaMaxElague {

	//Au début, on va à la profondeur maximale.
	public static StateValue negaMax(PlateauCourant state){

		List<Integer> shots = state.validShots();

		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw())
			return value;

		//On initialise l'état à LOSS
		value = StateValue.LOSS;

		for(int shot : shots){
			state.playNext(shot);
			
			//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante
			value = value.max(negaMax(state).opposite());
			state.undoLast();

			//On peut déjà couper comme ceci :
			if(value.isWin())
				break;
		}

		return value;
	}

}
