package Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Game.PositionGris;


public class MyHashMap implements Map<PositionGris, StateValueWithBound> {
	
	private class MyElement{
		/**
		 * Position avec jetons isol�s et valeur associ�e
		 */
		public PositionGris pos;
		public StateValueWithBound val;
		/**
		 * Co�t en terme d'�l�ments visit�s pour obtenir cette information de valeur sur cet �tat
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
	 * Type de la table, en fonction de la strat�gie de remplacement
	 * keepRecent = true -> on conserve les derniers �l�ments calcul�s
	 * keeprecent = false -> on conserve les informations les plus co�teuses � obtenir
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
			//On cherche l'�quivalence via equals de key et de la position dans map � l'indice le hashCode de key
			return (map[key.hashCode()].pos.equals(key));
		//Les cl�s doivent toutes �tre de type PositionGris
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
		
		//Un �l�ment non nul aura toujours un champ pos non nul vu le constructeur
		if(elem.pos.equals(key)){
			//les valeurs sont les valeurs (pr�cises ou pas) associ�es aux positions
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
				//Un �l�ment non nul aura toujours un champ pos non nul vu le constructeur
				set.add(map[i].pos);
		}
		return set;
	}

	@Override
	public StateValueWithBound put(PositionGris key, StateValueWithBound value){
		throw new RuntimeException("Mauvaise m�thode put !!! Sp�cifier le cout !");
	}
	
	public StateValueWithBound put(PositionGris key, StateValueWithBound value, int cout) {
		//Si on doit remplacer, on le fait selon la r�gle sp�cifi�e par keepRecent
		if(keepRecent)
			return putRecent(key, value);
		else
			return putMostComplex(key, value, cout);

	}
	
	public StateValueWithBound putRecent(PositionGris key, StateValueWithBound value){
		int hash = key.hashCode();
		MyElement elem = map[hash];
		//On �crase automatiquement l'ancienne valeur
		map[hash] = new MyElement(key, value);
		
		//On renvoie la valeur qui vient d'�tre supprim�e
		if(elem == null)
			return null;
		
		return elem.val;
	}
	
	public StateValueWithBound putMostComplex(PositionGris key, StateValueWithBound value, int cout){
		int hash = key.hashCode();
		
		MyElement elem = map[hash];
		//Si la table ne contient aucun enregistrement li� au code de hachage de cette position, on �crit le nouveau et on renvoie null 
		if(elem == null){
			map[hash] = new MyElement(key, value, cout);
			return null;
		}
		//Sinon on n'effectue un remplacement que si le coup du nouvel �l�ment est sup�rieur � celui de l'ancien
		if(elem.cout < cout)
			map[hash] = new MyElement(key, value, cout);
		
		//On renvoie la valeur ancienne (qu'elle ait �t� remplac�e ou non)
		return elem.val;
	}

	@Override
	public void putAll(Map<? extends PositionGris, ? extends StateValueWithBound> m) {
		throw new RuntimeException("Unimplemented : putAll");
	}

	@Override
	public StateValueWithBound remove(Object key) {
		if(!(key instanceof PositionGris))
			//On n'a rien supprim�, donc on ne renvoie rien
			return null;
		
		MyElement elem = map[key.hashCode()];
		if(elem.pos.equals(key)){
			//On supprime l'�l�ment de hashCode celui de key seulement si la position associ�e est aussi la m�me (ou du moins �quivalente)
			map[key.hashCode()] = null;
			//On renvoie la valeur qui vient d'�tre supprim�e
			return elem.val;
		}
		//On n'a rien supprim�, donc on ne renvoie rien
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
	 * Supprime de la table tous les �l�ments qui ne sont pas � Win ou Loss
	 * et ne servent donc pas � l'it�ration suivante
	 */
	public void clearNonFinalPos(){
		for(int i = 0; i < map.length; i++){
			//On elimine tous les �l�ments qui ne sont pas Win ou Loss
			if(map[i] != null){
				//Un �l�ment non nul a un champ val non nul dont le champ value est non nul (cf constructeurs)
				if(map[i].val.value.isDraw())
					map[i] = null;
				else
					//Tout �l�ment �valu� � Win ou Loss est accurate (par propri�t� de prudence)
					map[i].val.setBound(0);
			}
		}
	}
	
}
