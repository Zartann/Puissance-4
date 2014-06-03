package Search;

import java.util.HashMap;
import java.util.List;

import Game.PlateauCourant;
import Game.Position;

//M�thode Alpha-Beta en m�morisant certaines positions via une table de hachage
public class AlphaBetaHash {
	/**
	 * table de hachage m�morisant les �tats jusqu'� profondeur limit�e : <=profondeurMax
	 */
	
	final HashMap<Position, StateValueWithBound> hashTable = new HashMap<Position, StateValueWithBound>();

	int profondeurMax;
	
	/**
	 * Contient le nombre total de positions qui ont �t� �valu�es
	 */
	public int totalPositions = 0;

	public AlphaBetaHash(int profondeurMax) {
		super();
		this.profondeurMax = profondeurMax;
	}

	
	public StateValue alphaBetaHache(PlateauCourant state, StateValue alpha, StateValue beta, int profondeur){
		totalPositions++;
		//On d�finit la position atteinte
		Position pos = state.cle();
		
		//On cherche cette position dans la table
		StateValueWithBound boundValue = hashTable.get(pos);
		//On va devoir actualiser les bornes
		StateValue newAlpha = alpha, newBeta = beta;
		
		if(boundValue != null){//La position est dans la table
			if(boundValue.isLowerBound())
				newAlpha = newAlpha.max(boundValue.value);
			
			else if(boundValue.isUpperBound())
				newBeta = newBeta.min(boundValue.value);
			
			else{//La position est accurate
				newAlpha = boundValue.value;
				newBeta = boundValue.value;
			}
			
			if(newAlpha.betterOrEquals(newBeta))
				//On coupe ici si les bornes se croisent
				return boundValue.value;
		}
		
		//On ne consid�re que les coups jouables
		List<Integer> shots = state.validShots();
		
		//On regarde si le jeu est d�j� termin�
		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le r�sultat calcul�
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arr�te si aucun coup n'est possible ou si l'issue est d�cid�e
		if(shots.isEmpty() || !value.isDraw()){
			//L'�valuation est accurate
			if(profondeur <= profondeurMax)
				hashTable.put(pos, new StateValueWithBound(value, 0));
			
			return value;
		}
		
		//On initialise l'�tat � LOSS
		value = StateValue.LOSS;

		StateValue score;
		for(int shot : shots){
			//On teste chaque coup suivant jouable
			state.playNext(shot);
			
			//On r�cup�re l'oppos� du r�sultat du coup suivant et on garde le max avec la valeur courante.
			score = alphaBetaHache(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1).opposite();
			value = value.max(score);
			newAlpha = newAlpha.max(score);
			
			//On d�fait le coup jou�
			state.undoLast();
			
			//On cherche une �ventuelle coupure
			if(score.isWin())
				break;

			if(score.betterOrEquals(newBeta))
				break;
			
		}

		if(profondeur <= profondeurMax){
			//Si value vaut WIN ou LOSS, le principe de prudence impose que cette valeur est accurate
			StateValueWithBound v = !value.isDraw() ? new  StateValueWithBound(value, 0)
													: new StateValueWithBound(value, newAlpha, newBeta);
			hashTable.put(pos, v);
		}
			
		return value;
		
	}

}
