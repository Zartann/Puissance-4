package Search;

import java.util.List;

import Game.PlateauCourant;
import Game.PositionGris;

//M�thode it�rative utilis�e avec nos propres tables de hachage
public class IteratifHash {
	
	//Repr�sente le coup de l'information associ�e
	private class Cout{
		public int cout = 0;
	}
	
	public static int tailleTable = 20000003;

	/**
	 * Contient le nombre total de positions qui ont �t� �valu�es.
	 */
	public int totalPositions = 0;

	//On utilise deux tables de hachage avec les deux techniques de remplacement diff�rentes : positions les plus r�cemment calcul�es,
	//et positions n�cessitant le plus de traitements pour obtenir le r�sultat stock�
	MyHashMap grisRecentHashTable = new MyHashMap(tailleTable, true);
	MyHashMap grisComplexHashTable = new MyHashMap(tailleTable, false);

	public StateValue iteratifHash(PlateauCourant state){

		StateValue value = StateValue.DRAW;
		for(int i = 0; i <= state.nombreCoupsRestants(); i += 2){
			//Parcours en largeur d'abord
			value = iterationHash(state, StateValue.LOSS, StateValue.WIN, 0, i, new Cout());
			//Coupure li�e au caract�re prudent
			if(!value.isDraw())
				break;

			//On �limine les entr�es qui ne serviront pas � l'it�ration suivante
			grisRecentHashTable.clearNonFinalPos();
			grisComplexHashTable.clearNonFinalPos();
		}

		return value;
	}

	//Fonction r�cursive � appliquer successivement dans iteratifHash
	public StateValue iterationHash(PlateauCourant state, StateValue alpha, StateValue beta,
			int profondeur, int profondeurMaxIteration, Cout cout){
		totalPositions++;

		//Evaluation rapide de l'�tat si possible

		List<Integer> shots = state.orderedValidShots();

		StateValue value = state.result();

		//Si c'est au tour de l'adversaire, on inverse le r�sultat calcul�
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arr�te si aucun coup n'est possible ou si l'issue est d�cid�e
		if(shots.isEmpty() || !value.isDraw()){
			return value;
		}


		//Evaluation de l'�tat si profondeur de recherche atteinte
		if(profondeur == profondeurMaxIteration)
			return state.eval();

		//Comparaison avec les tables de hachage ; on met en priorit� la valeur contenur dans grisRecentHashTable

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

		//On initialise l'�tat � LOSS
		value = StateValue.LOSS;

		Cout c;
		StateValue score;
		for(int shot : shots){
			state.playNext(shot);
			//c.cout=0
			c = new Cout();

			//On r�cup�re l'oppos� du coup suivant et on garde le max avec la valeur courante.
			score = iterationHash(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1, profondeurMaxIteration, c).opposite();
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			//On augmente le co�t avec le co�t de l'information des appels r�cursifs
			cout.cout += c.cout;

			state.undoLast();
			
			//On ne fera pas mieux que WIN
			if(score.isWin())
				break;

			//Cas de coupure
			if(score.betterOrEquals(newBeta))
				break;

		}
		//On incr�mente le co�t car on a trait� l'�tat courant
		cout.cout++;
		
		//Si value vaut WIN ou LOSS, le principe de prudence impose que cette valeur soit accurate
		StateValueWithBound v = !value.isDraw() ? new  StateValueWithBound(value, 0)
												: new StateValueWithBound(value, newAlpha, newBeta);
		grisRecentHashTable.put(pos, v, cout.cout);
		grisComplexHashTable.put(pos, v, cout.cout);

		return value;

	}

}
