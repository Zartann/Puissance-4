package Search;

import java.util.List;

import Game.PlateauCourant;

public class AlphaBeta {

	/**
	 * Contient le nombre total de positions qui ont �t� �valu�es.
	 */
	public int totalPositions = 0;
	
	//Au d�but, on va � la profondeur maximale.
	public StateValue alphaBeta(PlateauCourant state, StateValue alpha, StateValue beta){
		totalPositions++;
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

		StateValue score;
		for(int shot : shots){
			state.playNext(shot);

			//On r�cup�re l'oppos� du coup suivant et on garde le max avec la valeur courante.
			score = alphaBeta(state, beta.opposite(), alpha.opposite()).opposite();
			value = value.max(score);
			alpha = alpha.max(score);

			state.undoLast();

			if(score.betterOrEquals(beta))
				break;

		}

		return value;
	}

}
