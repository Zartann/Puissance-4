package Search;

import java.util.List;

import Game.PlateauCourant;
import Game.PositionGris;

public class DynIteratifHash {

	private class Cout{
		public int cout = 0;
	}

	public static int tailleTable = 20000000;

	/**
	 * Contient le nombre total de positions qui ont été évaluées.
	 */
	public int totalPositions = 0;

	//On utilise deux tables de hachage, une pour l'itération précédente, une pour l'actuelle
	MyContinueHashMap grisRecentHashTable = new MyContinueHashMap(tailleTable, true);
	MyContinueHashMap grisComplexHashTable = new MyContinueHashMap(tailleTable, false);

	public StateValue dynIteratifHash(PlateauCourant state){

		ContinueStateValue value = new ContinueStateValue(StateValue.DRAW);
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			value = dynIterationHash(state, new ContinueStateValue(StateValue.LOSS),
					new ContinueStateValue(StateValue.WIN), 0, i, new Cout());
			if(value.isLoss() || value.isWin())
				break;

			//On élimine les entrées qui ne serviront pas à l'itération suivante
			grisRecentHashTable.clearNonFinalPos();
			grisComplexHashTable.clearNonFinalPos();
		}

		if(value.isNotWinNorLoss())
			return StateValue.DRAW;

		return value.valDiscrete;
	}

	public ContinueStateValue dynIterationHash(PlateauCourant state, ContinueStateValue alpha, ContinueStateValue beta,
			int profondeur, int profondeurMaxIteration, Cout cout){
		totalPositions++;
		//System.out.println(state);

		PositionGris pos = state.cleGris();

		//Evaluation rapide de l'état si possible

		//On ajoute le meilleur coup en premier dans la liste
		int bestCoup = grisRecentHashTable.getBestCoup(pos);
		if(bestCoup == -1)
			bestCoup = grisComplexHashTable.getBestCoup(pos);

		List<Integer> shots = (bestCoup != -1) ? state.dynOrderedValidShots(bestCoup) 
				: state.orderedValidShots();

		if(bestCoup != -1)
			shots.add(0, bestCoup);

		ContinueStateValue value = new ContinueStateValue(state.result());

		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || value.isLoss() || value.isWin()){
			return value;
		}


		//Evaluation de l'état si profondeur de recherche atteinte
		if(profondeur == profondeurMaxIteration){
			return state.playerIsNext() ? state.evalContinue() : state.evalContinue().opposite();
		}

		//Comparaison avec les tables de hachage

		ContinueStateValueWithBound boundValue = grisRecentHashTable.getValue(pos);
		if(boundValue == null)
			boundValue = grisComplexHashTable.getValue(pos);
		ContinueStateValue newAlpha = alpha, newBeta = beta;

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



		//On initialise l'état au pire non décidé
		value = new ContinueStateValue(StateValue.LOSS);

		Cout c;
		ContinueStateValue score;
		int bestShot = shots.get(0);
		for(int shot : shots){
			state.playNext(shot);
			c = new Cout();

			//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
			score = dynIterationHash(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1, profondeurMaxIteration, c).opposite();
			if(!score.lessOrEquals(value))
				bestShot = shot;
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			cout.cout += c.cout;

			state.undoLast();

			if(score.isWin())
				break;

			if(score.betterOrEquals(beta))
				break;

		}
		cout.cout++;

		ContinueStateValueWithBound v = value.isWin() ? new  ContinueStateValueWithBound(value, 0)
		: new ContinueStateValueWithBound(value, newAlpha, newBeta);
		grisRecentHashTable.put(pos, v, bestShot, cout.cout);
		grisComplexHashTable.put(pos, v, bestShot, cout.cout);

		return value;

	}

}
