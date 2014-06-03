package Search;

import java.util.List;

import Game.PlateauCourant;

public class MiniMaxElague {

	//Au début, on va à la profondeur maximale.
	public static StateValue miniMax(PlateauCourant state, boolean isMax){
		
		List<Integer> shots = state.validShots();
		
		StateValue value = state.result();

		//Code d'affichage
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
				//On n'oublie pas d'inverser la valeur de isMax en passant la ain à l'adversaire (dont le but est donc de minimiser le résultat)
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
				//Comme précédemment : en passant la main au joueur, celui-ci va vouloir maximiser la valeur du résultat
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
