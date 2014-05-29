package Search;

import java.util.HashMap;
import java.util.List;

import Game.PlateauCourant;
import Game.Position;

public class Iteratif {

	int profondeurStockageMax;

	//On utilise deux tables de hachage, une pour l'it�ration pr�c�dente, une pour l'actuelle
	HashMap<Position, StateValueWithBound> formerHashTable = 
			new HashMap<Position, StateValueWithBound>();
	
	HashMap<Position, StateValueWithBound> currentHashTable = 
			new HashMap<Position, StateValueWithBound>();
	
	public Iteratif(int prof){
		profondeurStockageMax = prof;
	}
	
	public StateValue iteratif(PlateauCourant state){
		
		StateValue value = StateValue.DRAW;
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			value = iteration(state, StateValue.LOSS, StateValue.WIN, 0, i);
			if(!value.isDraw())
				break;
			
			//On change d'it�ration, donc on change de table de hachage.
			formerHashTable = currentHashTable;
			currentHashTable = new HashMap<Position, StateValueWithBound>();
		}
		
		return value;
	}
	
	public StateValue iteration(PlateauCourant state, StateValue alpha, StateValue beta,
			int profondeur, int profondeurMaxIteration){
		
		Position pos = state.cle();
		
		//Evaluation rapide de l'�tat si possible
		
		List<Integer> shots = state.validShots();

		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le r�sultat calcul�
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arr�te si aucun coup n'est possible ou si l'issue est d�cid�e
		if(shots.isEmpty() || !value.isDraw()){
			//L'�valuation est accurate
			if(profondeur <= profondeurStockageMax)
				currentHashTable.put(pos, new StateValueWithBound(value, 0));
			
			return value;
		}
		
		
		//Evaluation de l'�tat si profondeur de recherche atteinte
		if(profondeur == profondeurMaxIteration)
			return state.eval();
		
		//Verification si l'�tat a �t� compl�tement �valu� � l'it�ration pr�c�dente
		StateValueWithBound formerValue = formerHashTable.get(pos);
		if(formerValue != null && !formerValue.value.isDraw()){
			if(profondeur <= profondeurStockageMax)
				currentHashTable.put(pos, new StateValueWithBound(formerValue.value, 0));
		}
		
		//Comparaison avec la table de hachage
		
		StateValueWithBound boundValue = currentHashTable.get(pos);
		StateValue newAlpha = alpha, newBeta = beta;
		
		if(boundValue != null){
			if(boundValue.isLowerBound())
				newAlpha = newAlpha.max(boundValue.value);
			
			else if(boundValue.isUpperBound())
				newBeta = newBeta.min(boundValue.value);
			
			else{
				newAlpha = boundValue.value;
				newBeta = boundValue.value;
			}
			
			if(newAlpha.betterOrEquals(newBeta))
				return boundValue.value;
		}
		
		//On initialise l'�tat � LOSS
		value = StateValue.LOSS;

		StateValue score;
		for(int shot : shots){
			state.playNext(shot);
			
			//On r�cup�re l'oppos� du coup suivant et on garde le max avec la valeur courante.
			score = iteration(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1, profondeurMaxIteration).opposite();
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			
			state.undoLast();

			if(score.betterOrEquals(beta))
				break;
			
		}

		if(profondeur <= profondeurStockageMax)
			currentHashTable.put(pos, new StateValueWithBound(value, newAlpha, newBeta));
			
		return value;
		
	}
	

	public StateValue iteratif2(PlateauCourant state){
		
		StateValue value = StateValue.DRAW;
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			value = iteration(state, StateValue.LOSS, StateValue.WIN, 0, i);
			if(!value.isDraw())
				break;
			
			//On change d'it�ration, donc on change de table de hachage.
			formerHashTable = currentHashTable;
			currentHashTable = new HashMap<Position, StateValueWithBound>();
			
			StateValueWithBound v;
			for(Position pos : formerHashTable.keySet()){
				v = formerHashTable.get(pos);
				if(!v.value.isDraw()){
					v.setBound(0);
					currentHashTable.put(pos, v);
				}
			}
		}
		
		return value;
	}
	
	public StateValue iteration2(PlateauCourant state, StateValue alpha, StateValue beta,
			int profondeur, int profondeurMaxIteration){
		
		Position pos = state.cle();
		
		//Evaluation rapide de l'�tat si possible
		
		List<Integer> shots = state.validShots();

		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le r�sultat calcul�
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arr�te si aucun coup n'est possible ou si l'issue est d�cid�e
		if(shots.isEmpty() || !value.isDraw()){
			//L'�valuation est accurate
			if(profondeur <= profondeurStockageMax)
				currentHashTable.put(pos, new StateValueWithBound(value, 0));
			
			return value;
		}
		
		
		//Evaluation de l'�tat si profondeur de recherche atteinte
		if(profondeur == profondeurMaxIteration)
			return state.eval();
		
		//Comparaison avec la table de hachage
		
		StateValueWithBound boundValue = currentHashTable.get(pos);
		StateValue newAlpha = alpha, newBeta = beta;
		
		if(boundValue != null){
			if(boundValue.isLowerBound())
				newAlpha = newAlpha.max(boundValue.value);
			
			else if(boundValue.isUpperBound())
				newBeta = newBeta.min(boundValue.value);
			
			else{
				newAlpha = boundValue.value;
				newBeta = boundValue.value;
			}
			
			if(newAlpha.betterOrEquals(newBeta))
				return boundValue.value;
		}
		
		//On initialise l'�tat � LOSS
		value = StateValue.LOSS;

		StateValue score;
		for(int shot : shots){
			state.playNext(shot);
			
			//On r�cup�re l'oppos� du coup suivant et on garde le max avec la valeur courante.
			score = iteration2(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1, profondeurMaxIteration).opposite();
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			
			state.undoLast();

			if(score.betterOrEquals(beta))
				break;
			
		}

		if(profondeur <= profondeurStockageMax)
			currentHashTable.put(pos, new StateValueWithBound(value, newAlpha, newBeta));
			
		return value;
		
	}

}
