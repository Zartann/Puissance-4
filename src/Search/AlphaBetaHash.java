package Search;

import java.util.HashMap;
import java.util.List;

import Game.PlateauCourant;
import Game.Position;

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


	/*public StateValue alphaBetaHache(PlateauCourant state, StateValue alpha, StateValue beta, int profondeur){

		List<Integer> shots = state.validShots();

		//On cherche si on a d�j� rencontr� l'�tat courant auparavant
		if (tableHachage.containsKey (state.hachage())){
			
			StateBornedValue ancienneBorne = tableHachage.get(state.hachage());
			StateBornedValue borneCourante = new StateBornedValue (alpha, beta);
			
			//Si c'est au tour de l'adversaire on inverse la borne courante
			if (!state.playerIsNext()) 
				borneCourante = new StateBornedValue (beta.opposite(), alpha.opposite());
			
			//On calcule l'intersection des deux intervalles trouv�s
			StateBornedValue nouvelleBorne = ancienneBorne.Intersection(borneCourante);
			
			tableHachage.put(state.hachage(), nouvelleBorne);
		}

		else {}

		//On cherche si l'�tat courant a d�j� �t� r�solu ou non
		if ((tableHachage.containsKey(state.hachage())) && tableHachage.get(state.hachage ()).IsFixed()){
			
			if (!state.playerIsNext()) 
				return tableHachage.get(state.hachage ()).alpha.opposite();
			else 
				return tableHachage.get(state.hachage ()).alpha;
		}

		//Sinon on proc�de comme alpha-beta
		StateValue value = state.result();

		//On s'arr�te si aucun coup n'est possible ou si l'issue est d�cid�e
		if(shots.isEmpty() || !value.isDraw()) {
			if (profondeur <= profondeurMax) 
				tableHachage.put(state.hachage(), new StateBornedValue (value, value));
			
			//Si c'est au tour de l'adversaire, on inverse le r�sultat calcul�
			if(!state.playerIsNext())
				value = value.opposite();
			
			return value;
		}
		
		else {

			//On initialise l'�tat � LOSS
			value = StateValue.LOSS;

			StateValue score;

			StateValue newAlpha = alpha;
			StateValue newBeta = beta;

			//Si l'�tat courant a d�j� une entr�e on change les bornes alpha-beta
			if (tableHachage.containsKey(state.hachage())){
				newAlpha = tableHachage.get(state.hachage()).alpha;
				newBeta = tableHachage.get(state.hachage()).beta;
			}
			
			for(int shot : shots){
				state.playNext(shot);

				//On r�cup�re l'oppos� du coup suivant et on garde le max avec la valeur courante.
				score = alphaBetaHache(state, newBeta.opposite(), newAlpha.opposite(), profondeur+1).opposite();
				value = value.max(score);
				newAlpha = newAlpha.max(score);

				state.undoLast();

				if(score.betterOrEquals(newBeta))
					break;

			}
			if (profondeur <= profondeurMax) 
				tableHachage.put(state.hachage(), new StateBornedValue (value, value));

			return value;
		}

	}*/
	
	
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
