package Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Game.PositionGris;


public class MyHashMap implements Map<PositionGris, StateValueWithBound> {
	
	private class MyElement{
		/**
		 * Position avec jetons isolés et valeur associée
		 */
		public PositionGris pos;
		public StateValueWithBound val;
		/**
		 * Coût en terme d'éléments visités pour obtenir cette information de valeur sur cet état
		 */
		public int cout = 0;
		
		public MyElement(PositionGris key, StateValueWithBound value) {
			pos = key;
			val = value;
		}
		
		public MyElement(PositionGris key, StateValueWithBound value, int cout) {
			pos = key;
			val = value;
			this.cout = cout;
		}
	}
	
	/**
	 * Contient les valeurs de la table
	 */
	MyElement[] map;
	
	/**
	 * Type de la table, en fonction de la stratégie de remplacement
	 * keepRecent = true -> on conserve les derniers éléments calculés
	 * keeprecent = false -> on conserve les informations les plus coûteuses à obtenir
	 */
	boolean keepRecent;
	
	/**
	 * 
	 * @param taille
	 * @param keepRecent 
	 */
	public MyHashMap(int taille, boolean keepRecent){
		map = new MyElement[taille];
		this.keepRecent = keepRecent;
	}
	
	@Override
	public void clear() {
		throw new RuntimeException("Unimplemented : clear");
		
	}

	@Override
	public boolean containsKey(Object key) {
		if(key instanceof PositionGris)
			//On cherche l'équivalence via equals de key et de la position dans map à l'indice le hashCode de key
			return (map[key.hashCode()].pos.equals(key));
		//Les clés doivent toutes être de type PositionGris
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new RuntimeException("Unimplemented : containsValue");
	}

	@Override
	public Set<java.util.Map.Entry<PositionGris, StateValueWithBound>> entrySet() {
		throw new RuntimeException("Unimplemented : entrySet");
	}

	@Override
	public StateValueWithBound get(Object key) {
		if(!(key instanceof PositionGris))
			return null;
		
		MyElement elem = map[key.hashCode()];
		
		if(elem == null)
			return null;
		
		//Un élément non nul aura toujours un champ pos non nul vu le constructeur
		if(elem.pos.equals(key)){
			//les valeurs sont les valeurs (précises ou pas) associées aux positions
			return elem.val;
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		throw new RuntimeException("Unimplemented : isEmpty");
	}

	@Override
	public Set<PositionGris> keySet() {
		Set<PositionGris> set = new HashSet<PositionGris>();
		for(int i = 0; i < map.length; i++){
			if(map[i] != null)
				//Un élément non nul aura toujours un champ pos non nul vu le constructeur
				set.add(map[i].pos);
		}
		return set;
	}

	@Override
	public StateValueWithBound put(PositionGris key, StateValueWithBound value){
		throw new RuntimeException("Mauvaise méthode put !!! Spécifier le cout !");
	}
	
	public StateValueWithBound put(PositionGris key, StateValueWithBound value, int cout) {
		//Si on doit remplacer, on le fait selon la règle spécifiée par keepRecent
		if(keepRecent)
			return putRecent(key, value);
		else
			return putMostComplex(key, value, cout);

	}
	
	public StateValueWithBound putRecent(PositionGris key, StateValueWithBound value){
		int hash = key.hashCode();
		MyElement elem = map[hash];
		//On écrase automatiquement l'ancienne valeur
		map[hash] = new MyElement(key, value);
		
		//On renvoie la valeur qui vient d'être supprimée
		if(elem == null)
			return null;
		
		return elem.val;
	}
	
	public StateValueWithBound putMostComplex(PositionGris key, StateValueWithBound value, int cout){
		int hash = key.hashCode();
		
		MyElement elem = map[hash];
		//Si la table ne contient aucun enregistrement lié au code de hachage de cette position, on écrit le nouveau et on renvoie null 
		if(elem == null){
			map[hash] = new MyElement(key, value, cout);
			return null;
		}
		//Sinon on n'effectue un remplacement que si le coup du nouvel élément est supérieur à celui de l'ancien
		if(elem.cout < cout)
			map[hash] = new MyElement(key, value, cout);
		
		//On renvoie la valeur ancienne (qu'elle ait été remplacée ou non)
		return elem.val;
	}

	@Override
	public void putAll(Map<? extends PositionGris, ? extends StateValueWithBound> m) {
		throw new RuntimeException("Unimplemented : putAll");
	}

	@Override
	public StateValueWithBound remove(Object key) {
		if(!(key instanceof PositionGris))
			//On n'a rien supprimé, donc on ne renvoie rien
			return null;
		
		MyElement elem = map[key.hashCode()];
		if(elem.pos.equals(key)){
			//On supprime l'élément de hashCode celui de key seulement si la position associée est aussi la même (ou du moins équivalente)
			map[key.hashCode()] = null;
			//On renvoie la valeur qui vient d'être supprimée
			return elem.val;
		}
		//On n'a rien supprimé, donc on ne renvoie rien
		return null;
	}

	@Override
	public int size() {
		throw new RuntimeException("Unimplemented : size");
	}

	@Override
	public Collection<StateValueWithBound> values() {
		throw new RuntimeException("Unimplemented : values");
	}
	
	/**
	 * Supprime de la table tous les éléments qui ne sont pas à Win ou Loss
	 * et ne servent donc pas à l'itération suivante
	 */
	public void clearNonFinalPos(){
		for(int i = 0; i < map.length; i++){
			//On elimine tous les éléments qui ne sont pas Win ou Loss
			if(map[i] != null){
				//Un élément non nul a un champ val non nul dont le champ value est non nul (cf constructeurs)
				if(map[i].val.value.isDraw())
					map[i] = null;
				else
					//Tout élément évalué à Win ou Loss est accurate (par propriété de prudence)
					map[i].val.setBound(0);
			}
		}
	}
	
}
