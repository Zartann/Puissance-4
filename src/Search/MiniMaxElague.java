package Search;

import java.util.List;

import Game.PlateauCourant;

public class MiniMaxElague {

	//Au d�but, on va � la profondeur maximale.
	public static StateValue miniMax(PlateauCourant state, boolean isMax){
		
		List<Integer> shots = state.validShots();
		
		StateValue value = state.result();

		//Code d'affichage
		//System.out.println(state.result() + " :");
		//System.out.println(state);
		
		//On s'arr�te si aucun coup n'est possible ou si l'issue est d�cid�e
		if(shots.isEmpty() || !value.isDraw())
			return value;
		
		if(isMax){
			
			//On initialise l'�tat � LOSS
			value = StateValue.LOSS;
			
			for(int shot : shots){
				state.playCurrent(shot);
				//On n'oublie pas d'inverser la valeur de isMax en passant la ain � l'adversaire (dont le but est donc de minimiser le r�sultat)
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
				//Comme pr�c�demment : en passant la main au joueur, celui-ci va vouloir maximiser la valeur du r�sultat
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
