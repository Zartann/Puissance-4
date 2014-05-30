package Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Game.PositionGris;


public class MyHashMap implements Map<PositionGris, StateValueWithBound> {
	
	private class MyElement{
		public PositionGris pos;
		public StateValueWithBound val;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(Object key) {
		if(key instanceof PositionGris)
			return (map[key.hashCode()].pos.equals(key));
		
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new RuntimeException("Unimplemented : containsValue");
		// TODO Auto-generated method stub
	}

	@Override
	public Set<java.util.Map.Entry<PositionGris, StateValueWithBound>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StateValueWithBound get(Object key) {
		if(!(key instanceof PositionGris))
			return null;
		
		MyElement elem = map[key.hashCode()];
		if(elem.pos.equals(key)){
			return elem.val;
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		throw new RuntimeException("Unimplemented : isEmpty");
		// TODO Auto-generated method stub
	}

	@Override
	public Set<PositionGris> keySet() {
		Set<PositionGris> set = new HashSet<PositionGris>();
		for(int i = 0; i < map.length; i++){
			if(map[i] != null)
				set.add(map[i].pos);
		}
		return set;
	}

	@Override
	public StateValueWithBound put(PositionGris key, StateValueWithBound value){
		throw new RuntimeException("Mauvaise méthode put !!! Spécifier le cout !");
	}
	
	public StateValueWithBound put(PositionGris key, StateValueWithBound value, int cout) {
		
		if(keepRecent)
			return putRecent(key, value);
		else
			return putMostComplex(key, value, cout);

	}
	
	public StateValueWithBound putRecent(PositionGris key, StateValueWithBound value){
		int hash = key.hashCode();
		MyElement elem = map[hash];
		map[hash] = new MyElement(key, value);
		
		if(elem == null)
			return null;
		
		return elem.val;
	}
	
	public StateValueWithBound putMostComplex(PositionGris key, StateValueWithBound value, int cout){
		int hash = key.hashCode();
		
		MyElement elem = map[hash];
		if(elem == null){
			map[hash] = new MyElement(key, value, cout);
			return null;
		}
		
		if(elem.cout < cout)
			map[hash] = new MyElement(key, value, cout);
		
		return elem.val;
	}

	@Override
	public void putAll(Map<? extends PositionGris, ? extends StateValueWithBound> m) {
		throw new RuntimeException("Unimplemented : putAll");
		// TODO Auto-generated method stub
		
	}

	@Override
	public StateValueWithBound remove(Object key) {
		if(!(key instanceof PositionGris))
			return null;
		
		MyElement elem = map[key.hashCode()];
		if(elem.pos.equals(key)){
			map[key.hashCode()] = null;
			return elem.val;
		}
		return null;
	}

	@Override
	public int size() {
		throw new RuntimeException("Unimplemented : putAll");
		// TODO Auto-generated method stub
	}

	@Override
	public Collection<StateValueWithBound> values() {
		throw new RuntimeException("Unimplemented : putAll");
		// TODO Auto-generated method stub
	}
	
	/**
	 * Supprime de la table tous les éléments qui ne sont pas à Win ou Loss
	 * et ne servent donc pas à l'itération suivante
	 */
	public void clearNonFinalPos(){
		for(int i = 0; i < map.length; i++){
			//On elimine tous les éléments qui ne sont pas Win ou Loss
			if(map[i] != null && !map[i].val.value.isDraw())
				map[i] = null;
		}
	}
	
}
