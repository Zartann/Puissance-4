package Search;

import java.util.List;

import Game.PlateauCourant;

public class NegaMaxElague {

	//Au d�but, on va � la profondeur maximale.
	public static StateValue negaMax(PlateauCourant state){

		List<Integer> shots = state.validShots();

		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le r�sultat calcul�
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arr�te si aucun coup n'est possible ou si l'issue est d�cid�e
		if(shots.isEmpty() || !value.isDraw())
			return value;

		//On initialise l'�tat � LOSS
		value = StateValue.LOSS;

		for(int shot : shots){
			state.playNext(shot);
			
			//On r�cup�re l'oppos� du coup suivant et on garde le max avec la valeur courante
			value = value.max(negaMax(state).opposite());
			state.undoLast();

			//On peut d�j� couper comme ceci :
			if(value.isWin())
				break;
		}

		return value;
	}

}
