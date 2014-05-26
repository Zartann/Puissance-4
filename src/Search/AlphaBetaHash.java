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



	//Au début, on va à la profondeur maximale.
			public StateValue alphaBetaHache(PlateauCourant state, StateValue alpha, StateValue beta, int profondeur){

				List<Integer> shots = state.validShots();
				
				//On cherche si on a déjà rencontré l'état courant auparavant
				if (tableHachage.containsKey (state.hachage())){
					StateBornedValue ancienneBorne = tableHachage.get(state.hachage());
					StateBornedValue borneCourante = new StateBornedValue (alpha, beta);
					//Si c'est au tour de l'adversaire on inverse la borne courante
					if (!state.playerIsNext()) borneCourante = new StateBornedValue (beta.opposite(), alpha.opposite());
					//On calcule l'intersection des deux intervalles trouvés
					StateBornedValue nouvelleBorne = ancienneBorne.Intersection(borneCourante);
					tableHachage.put(state.hachage(), nouvelleBorne);
				}
				
				else {}
					StateValue value = state.result();
					
					//On s'arrête si aucun coup n'est possible ou si l'issue est décidée
					if(shots.isEmpty() ||!value.isDraw()) {
						if (profondeur <= profondeurMax) tableHachage.put(state.hachage(), new StateBornedValue (value, value));
						//Si c'est au tour de l'adversaire, on inverse le résultat calculé
						if(!state.playerIsNext())
							value = value.opposite();
						return value;
					}
					else {
						
						//On initialise l'état à LOSS
						value = StateValue.LOSS;

						StateValue score;
						for(int shot : shots){
							state.playNext(shot);
							
							//On récupère l'opposé du coup suivant et on garde le max avec la valeur courante.
							score = alphaBetaHache(state, beta.opposite(), alpha.opposite(), profondeur+1).opposite();
							value = value.max(score);
							alpha = alpha.max(score);
							
							state.undoLast();

							if(score.betterOrEquals(beta))
								break;
							
						}
						if (profondeur<= profondeurMax) tableHachage.put(state.hachage(), new StateBornedValue (value, value));

						return value;
					}

					}
					
				}
					
					
					

					

					
				


