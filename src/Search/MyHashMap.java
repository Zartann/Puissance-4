package Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Game.PositionGris;


public class MyHashMap implements Map<PositionGris, Integer> {
	
	private class MyElement{
		public PositionGris pos;
		public int val;
		public int cout = 0;
		
		public MyElement(PositionGris key, Integer value) {
			pos = key;
			val = value;
		}
		
		public MyElement(PositionGris key, Integer value, int cout) {
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
	public Set<java.util.Map.Entry<PositionGris, Integer>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer get(Object key) {
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
	public Integer put(PositionGris key, Integer value){
		throw new RuntimeException("Mauvaise méthode put !!! Spécifier le cout !");
	}
	
	public Integer put(PositionGris key, Integer value, int cout) {
		
		if(keepRecent)
			return putRecent(key, value);
		else
			return putMostComplex(key, value, cout);

	}
	
	public Integer putRecent(PositionGris key, Integer value){
		int hash = key.hashCode();
		MyElement elem = map[hash];
		map[hash] = new MyElement(key, value);
		
		if(elem == null)
			return null;
		
		return elem.val;
	}
	
	public Integer putMostComplex(PositionGris key, Integer value, int cout){
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
	public void putAll(Map<? extends PositionGris, ? extends Integer> m) {
		throw new RuntimeException("Unimplemented : putAll");
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer remove(Object key) {
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
	public Collection<Integer> values() {
		throw new RuntimeException("Unimplemented : putAll");
		// TODO Auto-generated method stub
	}
	
}
