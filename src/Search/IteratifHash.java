package Search;

import java.util.List;

import Game.PlateauCourant;
import Game.PositionGris;

public class IteratifHash {
	
	private class Cout{
		public int cout = 0;
	}
	
	public static int tailleTable = 10000000;

	/**
	 * Contient le nombre total de positions qui ont été évaluées.
	 */
	public int totalPositions = 0;

	//On utilise deux tables de hachage, une pour l'itération précédente, une pour l'actuelle
	MyHashMap grisRecentHashTable = new MyHashMap(tailleTable, true);
	MyHashMap grisComplexHashTable = new MyHashMap(tailleTable, false);

	public StateValue iteratifHash(PlateauCourant state){

		StateValue value = StateValue.DRAW;
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			value = iterationHash(state, StateValue.LOSS, StateValue.WIN, 0, i, new Cout());
			if(!value.isDraw())
				break;

			//On élimine les entrées qui ne serviront pas à l'itération suivante
			grisRecentHashTable.clearNonFinalPos();
			grisComplexHashTable.clearNonFinalPos();
		}

		return value;
	}

	public StateValue iterationHash(PlateauCourant state, StateValue alpha, StateValue beta,
			int profondeur, int profondeurMaxIteration, Cout cout){
		totalPositions++;

		//Evaluation rapide de l'état si possible

		List<Integer> shots = state.validShots();

		StateValue value = state.result();

		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw()){
			return value;
		}


		//Evaluation de l'état si profondeur de recherche atteinte
		if(profondeur == profondeurMaxIteration)
			return state.eval();

		//Comparaison avec les tables de hachage

		PositionGris pos = state.cleGris();
		
		StateValueWithBound boundValue = grisRecentHashTable.get(pos);
		if(boundValue == null)
			boundValue = grisComplexHashTable.get(pos);
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

		Cout c;
		StateValue score;
		for(int shot : shots){
			state.playNext(shot);
			c = new Cout();

			//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
			score = iterationHash(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1, profondeurMaxIteration, c).opposite();
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			cout.cout += c.cout;

			state.undoLast();

			if(score.betterOrEquals(beta))
				break;

		}
		cout.cout++;

		StateValueWithBound v = new StateValueWithBound(value, newAlpha, newBeta);
		grisRecentHashTable.put(pos, v, cout.cout);
		grisComplexHashTable.put(pos, v, cout.cout);

		return value;

	}

}
