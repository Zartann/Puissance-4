package Search;

import java.util.List;

import Game.PlateauCourant;

public class AlphaBeta {

	/**
	 * Contient le nombre total de positions qui ont été évaluées.
	 */
	public int totalPositions = 0;
	
	//Au début, on va à la profondeur maximale.
	public StateValue alphaBeta(PlateauCourant state, StateValue alpha, StateValue beta){
		totalPositions++;
		//On considère les coups réalisables
		List<Integer> shots = state.validShots();

		//On regarde si la partie a déjà un gagnant
		StateValue value = state.result();

		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw())
			return value;

		//On initialise l'état à LOSS
		value = StateValue.LOSS;

		StateValue score;
		for(int shot : shots){
			state.playNext(shot);

			//Pour chaque coup suivant valide, on récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
			score = alphaBeta(state, beta.opposite(), alpha.opposite()).opposite();
			value = value.max(score);
			alpha = alpha.max(score);
			
			//On défait le coup que l'on vient de jouer
			state.undoLast();

			//Si on dépasse la borne supérieure, on fait une coupure
			if(score.betterOrEquals(beta))
				break;

		}

		return value;
	}

}
