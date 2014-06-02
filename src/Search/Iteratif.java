package Search;

import java.util.HashMap;
import java.util.List;

import Game.PlateauCourant;
import Game.Position;
import Game.PositionGris;

public class Iteratif {
	/**
	 * On ne stockera aucune entrée correspondant à une profondeur supérieure
	 */
	int profondeurStockageMax;
	
	/**
	 * Contient le nombre total de positions qui ont été évaluées
	 */
	public int totalPositions = 0;

	//On utilise deux tables de hachage, une pour l'itération précédente, une pour l'actuelle
	HashMap<Position, StateValueWithBound> formerHashTable = 
			new HashMap<Position, StateValueWithBound>();
	
	HashMap<Position, StateValueWithBound> currentHashTable = 
			new HashMap<Position, StateValueWithBound>();
	
	public Iteratif(int prof){
		profondeurStockageMax = prof;
	}
	
	public StateValue iteratif2(PlateauCourant state){
		
		StateValue value = StateValue.DRAW;
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			//On effectue un parcours en largeur d'abord jusqu'à la profondeur i, incrémentée de 2 à chaque étape
			value = iteration2(state, StateValue.LOSS, StateValue.WIN, 0, i);
			if(!value.isDraw())
				break;
			
			//On change d'itération, donc on change de table de hachage.
			formerHashTable = currentHashTable;
			currentHashTable = new HashMap<Position, StateValueWithBound>();
			
			StateValueWithBound v;
			for(Position pos : formerHashTable.keySet()){
				v = formerHashTable.get(pos);
				//Les valeurs v (donc v.value vu les constructeurs) ne sont jamais nulles avec ce type de remplacement
				if(!v.value.isDraw()){
					//Toute position non DRAW est accurate
					v.setBound(0);
					currentHashTable.put(pos, v);
				}
			}
		}
		//On retrouve la valeur trouvée après exploration jusqu'à la fin de la partie
		return value;
	}
	
	//Fonction récursive appelée par Iteratif2
	public StateValue iteration2(PlateauCourant state, StateValue alpha, StateValue beta,
			int profondeur, int profondeurMaxIteration){
		totalPositions++;
		Position pos = state.cle();
		
		//Evaluation rapide de l'état si possible
		
		List<Integer> shots = state.validShots();

		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw()){
			//L'évaluation est accurate
			if(profondeur <= profondeurStockageMax)
				currentHashTable.put(pos, new StateValueWithBound(value, 0));
			
			return value;
		}
		
		
		//Evaluation de l'état si profondeur de recherche atteinte
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
			
			//Coupure éventuelle
			if(newAlpha.betterOrEquals(newBeta))
				return boundValue.value;
		}
		
		//On initialise l'état à LOSS
		value = StateValue.LOSS;

		StateValue score;
		for(int shot : shots){
			state.playNext(shot);
			
			//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
			score = iteration2(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1, profondeurMaxIteration).opposite();
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			
			state.undoLast();
			
			//On ne fera pas mieux que WIN -> coupures
			if (score.isWin())
				break;
			
			//Coupure car l'adversaire n'acceptera pas un résultat supérieur à newBeta
			if(score.betterOrEquals(newBeta))
				break;
			
		}
		
		if(profondeur <= profondeurStockageMax){
			//Si value vaut WIN ou LOSS, le principe de prudence impose que cette valeur est accurate
			StateValueWithBound v = !value.isDraw() ? new  StateValueWithBound(value, 0)
													: new StateValueWithBound(value, newAlpha, newBeta);
			currentHashTable.put(pos, v);
		}
			
		return value;
		
	}
	
	
	//On ajoute maintenant la notion de jeton isolé (gris)
	
	//On utilise deux tables de hachage, une pour l'itération précédente, une pour l'actuelle
		HashMap<PositionGris, StateValueWithBound> formerGrisHashTable = 
				new HashMap<PositionGris, StateValueWithBound>();
		
		HashMap<PositionGris, StateValueWithBound> currentGrisHashTable = 
				new HashMap<PositionGris, StateValueWithBound>();
	
	public StateValue iteratifGris(PlateauCourant state){
		
		StateValue value = StateValue.DRAW;
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			//Parcours en largeur d'abord
			value = iterationGris(state, StateValue.LOSS, StateValue.WIN, 0, i);
			//Coupure possible grâce au principe de prudence
			if(!value.isDraw())
				break;
			
			//On change d'itération, donc on change de table de hachage.
			formerGrisHashTable = currentGrisHashTable;
			currentGrisHashTable = new HashMap<PositionGris, StateValueWithBound>();
			
			StateValueWithBound v;
			for(PositionGris pos : formerGrisHashTable.keySet()){
				v = formerGrisHashTable.get(pos);
				//Les valeurs v (donc v.value vu les constructeurs) ne sont jamais nulles avec ce type de remplacement
				if(!v.value.isDraw()){
					//Valeur accurate dans ce cas
					v.setBound(0);
					currentGrisHashTable.put(pos, v);
				}
			}
		}
		//On renvoie la valeur obtenue après parcours de toutes les parties possibles (avec élagage)
		return value;
	}
	
	public StateValue iterationGris(PlateauCourant state, StateValue alpha, StateValue beta,
			int profondeur, int profondeurMaxIteration){
		totalPositions++;
		PositionGris pos = state.cleGris();
		//Evaluation rapide de l'état si possible
		
		List<Integer> shots = state.orderedValidShots();

		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw()){
			//L'évaluation est accurate
			if(profondeur <= profondeurStockageMax)
				currentGrisHashTable.put(pos, new StateValueWithBound(value, 0));
			
			return value;
		}
		
		
		//Evaluation de l'état si profondeur de recherche atteinte
		if(profondeur == profondeurMaxIteration)
			return state.eval();
		
		//Comparaison avec la table de hachage
		
		StateValueWithBound boundValue = currentGrisHashTable.get(pos);
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
		
		//On initialise l'état à LOSS
		value = StateValue.LOSS;

		StateValue score;
		for(int shot : shots){
			state.playNext(shot);
			
			//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
			score = iterationGris(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1, profondeurMaxIteration).opposite();
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			
			state.undoLast();
			
			//On ne fera pas mieux que WIN
			if(score.isWin())
				break;

			//Coupure éventuelle
			if(score.betterOrEquals(newBeta))
				break;
			
		}

		if(profondeur <= profondeurStockageMax){
			//Si value vaut WIN ou LOSS, le principe de prudence impose que cette valeur est accurate
				StateValueWithBound v = !value.isDraw() ? new  StateValueWithBound(value, 0)
														: new StateValueWithBound(value, newAlpha, newBeta);
				currentGrisHashTable.put(pos, v);
		}
			
		return value;
		
	}

}
