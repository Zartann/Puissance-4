package Search;

import java.util.HashMap;
import java.util.List;

import Game.PlateauCourant;
import Game.Position;

public class AlphaBetaHash {
	//On ajoute une table de hachage mémorisant les états jusqu'à profondeur limitée : <=profondeurMax
	final HashMap<Position, StateBornedValue> tableHachage = new HashMap<Position, StateBornedValue> ();

	int profondeurMax;


	public AlphaBetaHash(int profondeurMax) {
		super();
		this.profondeurMax = profondeurMax;
	}


	public StateValue alphaBetaHache(PlateauCourant state, StateValue alpha, StateValue beta, int profondeur){

		List<Integer> shots = state.validShots();

		//On cherche si on a déjà rencontré l'état courant auparavant
		if (tableHachage.containsKey (state.hachage())){
			
			StateBornedValue ancienneBorne = tableHachage.get(state.hachage());
			StateBornedValue borneCourante = new StateBornedValue (alpha, beta);
			
			//Si c'est au tour de l'adversaire on inverse la borne courante
			if (!state.playerIsNext()) 
				borneCourante = new StateBornedValue (beta.opposite(), alpha.opposite());
			
			//On calcule l'intersection des deux intervalles trouvés
			StateBornedValue nouvelleBorne = ancienneBorne.Intersection(borneCourante);
			
			tableHachage.put(state.hachage(), nouvelleBorne);
		}

		else {}

		//On cherche si l'état courant a déjà été résolu ou non
		if ((tableHachage.containsKey(state.hachage())) && tableHachage.get(state.hachage ()).IsFixed()){
			
			if (!state.playerIsNext()) 
				return tableHachage.get(state.hachage ()).alpha.opposite();
			else 
				return tableHachage.get(state.hachage ()).alpha;
		}

		//Sinon on procède comme alpha-beta
		StateValue value = state.result();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw()) {
			if (profondeur <= profondeurMax) 
				tableHachage.put(state.hachage(), new StateBornedValue (value, value));
			
			//Si c'est au tour de l'adversaire, on inverse le résultat calculé
			if(!state.playerIsNext())
				value = value.opposite();
			
			return value;
		}
		
		else {

			//On initialise l'état à LOSS
			value = StateValue.LOSS;

			StateValue score;

			StateValue newAlpha = alpha;
			StateValue newBeta = beta;

			//Si l'état courant a déjà une entrée on change les bornes alpha-beta
			if (tableHachage.containsKey(state.hachage())){
				newAlpha = tableHachage.get(state.hachage()).alpha;
				newBeta = tableHachage.get(state.hachage()).beta;
			}
			
			for(int shot : shots){
				state.playNext(shot);

				//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
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

	}
	
	final HashMap<Position, StateValueWithBound> hashTable = new HashMap<Position, StateValueWithBound>();
	
	public StateValue alphaBetaHache2(PlateauCourant state, StateValue alpha, StateValue beta, int profondeur){
		
		Position pos = state.hachage();
		
		StateValueWithBound boundValue = hashTable.get(pos);
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
		
		List<Integer> shots = state.validShots();

		StateValue value = state.result();
		
		//Si c'est au tour de l'adversaire, on inverse le résultat calculé
		if(!state.playerIsNext())
			value = value.opposite();

		//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
		if(shots.isEmpty() || !value.isDraw()){
			//L'évaluation est accurate
			if(profondeur <= profondeurMax)
				hashTable.put(pos, new StateValueWithBound(value, 0));
			
			return value;
		}
		
		//On initialise l'état à LOSS
		value = StateValue.LOSS;

		StateValue score;
		for(int shot : shots){
			state.playNext(shot);
			
			//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
			score = alphaBetaHache2(state, beta.opposite(), alpha.opposite(), profondeur+1).opposite();
			value = value.max(score);
			alpha = alpha.max(score);
			
			state.undoLast();

			if(score.betterOrEquals(beta))
				break;
			
		}

		if(profondeur <= profondeurMax)
			hashTable.put(pos, new StateValueWithBound(value, newAlpha, newBeta));
			
		return value;
		
	}

}
