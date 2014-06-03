package Search;

import java.util.List;

import Game.PlateauCourant;
import Game.PositionGris;

//Méthode itérative utilisée avec nos propres tables de hachage et un ordonnancement dynamique des coups
public class DynIteratifHash {

	//Représente le coup de l'information associée
	private class Cout{
		public int cout = 0;
	}

	public static int tailleTable = 20000003;

	/**
	 * Contient le nombre total de positions qui ont été évaluées.
	 */
	public int totalPositions = 0;

	//On utilise deux tables de hachage avec les deux techniques de remplacement différentes : positions les plus récemment calculées,
	//et positions nécessitant le plus de traitements pour obtenir le résultat stocké
	MyContinueHashMap grisRecentHashTable = new MyContinueHashMap(tailleTable, true);
	MyContinueHashMap grisComplexHashTable = new MyContinueHashMap(tailleTable, false);

	public StateValue dynIteratifHash(PlateauCourant state){

		ContinueStateValue value = new ContinueStateValue(StateValue.DRAW);
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			//Parcours en largeur d'abord
			value = dynIterationHash(state, new ContinueStateValue(StateValue.LOSS),
					new ContinueStateValue(StateValue.WIN), 0, i, new Cout());
			//Coupure liée au caractère prudent
			if(value.isLoss() || value.isWin())
				break;

			//On élimine les entrées qui ne serviront pas à l'itération suivante
			grisRecentHashTable.clearNonFinalPos();
			grisComplexHashTable.clearNonFinalPos();
		}

		if(value.isNotWinNorLoss())
			return StateValue.DRAW;

		//Dans ce cas on a une valeur WIN ou LOSS qui est accurate (principe de prudence)
		return value.valDiscrete;
	}

	//Fonction récursive à appliquer successivement dans dynIteratifHash
	public ContinueStateValue dynIterationHash(PlateauCourant state, ContinueStateValue alpha, ContinueStateValue beta,
			int profondeur, int profondeurMaxIteration, Cout cout){
		totalPositions++;

		PositionGris pos = state.cleGris();

		//Evaluation rapide de l'état si possible

		//On ajoute le meilleur coup en premier dans la liste ; priorité à l'information stockée dans grisRecentHashTable
		int bestCoup = grisRecentHashTable.getBestCoup(pos);
		if(bestCoup == -1)
			bestCoup = grisComplexHashTable.getBestCoup(pos);

		//On ajoute les autres coups par ordonnancement statique
		List<Integer> shots = (bestCoup != -1) ? state.dynOrderedValidShots(bestCoup) 
				: state.orderedValidShots();

		if(bestCoup != -1){
			//Code d'affichage
			//System.out.println("BestShot Trouvé : " + bestCoup);
			//On place bestCoup en tête
			shots.add(0, bestCoup);
		}

		ContinueStateValue value = new ContinueStateValue(state.result());

		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || value.isLoss() || value.isWin()){
			//Code d'affichage
			//System.out.println("Calcul direct : Joueur = " + state.playerIsNext() + "; Valeur = " + value);
			//System.out.println();
			return value;
		}


		//Evaluation de l'état si profondeur de recherche atteinte
		if(profondeur == profondeurMaxIteration){
			//System.out.println("Evaluation : Joueur = " + state.playerIsNext() + "; Valeur = " + (state.playerIsNext() ? state.evalContinue() : state.evalContinue().opposite()));
			//System.out.println();
			return state.playerIsNext() ? new ContinueStateValue(state.eval()) : new ContinueStateValue(state.eval().opposite());
		}

		//Comparaison avec les tables de hachage

		//Priorité à l'information contenue dans grisRecentHashTable
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

			//Coupure immédiate
			if(newAlpha.betterOrEquals(newBeta)){
				//Code d'affichage
				//System.out.println("Table de hachage : Joueur = " + state.playerIsNext() + "; Valeur = " + boundValue.value);
				//System.out.println();
				return boundValue.value;
			}
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
			//Code d'affichage
			//System.out.println("BestShot : " + bestShot + "; Shot : " + shot);
			//System.out.println(state);
			
			//Si score vaut mieux strictement que value, on change de meilleur coup pour l'itération suivante
			//avant d'actualiser les autres données comme dans les autres algorithmes
			if(!score.lessOrEquals(value))
				bestShot = shot;
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			//On augmente le coût avec le coût de l'information des appels récursifs
			cout.cout += c.cout;

			state.undoLast();

			//On ne fera pas mieux que WIN
			if(score.isWin())
				break;

			//Coupure
			if(score.betterOrEquals(newBeta))
				break;

		}
		//On a traité l'état courant donc on incrémente le coût associé à l'information obtenue
		cout.cout++;

		//Si la valeur est Win ou Loss, elle est accurate (principe de prudence) ; sinon elle dépend des bornes newAlpha et newBeta
		ContinueStateValueWithBound v = !value.isNotWinNorLoss() ? new  ContinueStateValueWithBound(value, 0)
		: new ContinueStateValueWithBound(value, newAlpha, newBeta);
		grisRecentHashTable.put(pos, v, bestShot, cout.cout);
		grisComplexHashTable.put(pos, v, bestShot, cout.cout);

		//Code d'affichage
		//System.out.println("Fin d'étude : Joueur = " + state.playerIsNext() + "; Valeur = " + value + "; bestCoup = " + bestShot);
		//System.out.println();
		return value;

	}

}
