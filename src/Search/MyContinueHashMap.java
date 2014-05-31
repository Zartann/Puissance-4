package Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Game.PositionGris;

public class MyContinueHashMap implements Map<PositionGris, ContinueStateValueWithBound> {
	
	private class MyElement{
		public PositionGris pos;
		public ContinueStateValueWithBound val;
		public int cout = 0;
		
		public MyElement(PositionGris key, ContinueStateValueWithBound value) {
			pos = key;
			val = value;
		}
		
		public MyElement(PositionGris key, ContinueStateValueWithBound value, int cout) {
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
	public MyContinueHashMap(int taille, boolean keepRecent){
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
	public Set<java.util.Map.Entry<PositionGris, ContinueStateValueWithBound>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContinueStateValueWithBound get(Object key) {
		if(!(key instanceof PositionGris))
			return null;
		
		MyElement elem = map[key.hashCode()];
		
		if(elem == null)
			return null;
		
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
	public ContinueStateValueWithBound put(PositionGris key, ContinueStateValueWithBound value){
		throw new RuntimeException("Mauvaise méthode put !!! Spécifier le cout !");
	}
	
	public ContinueStateValueWithBound put(PositionGris key, ContinueStateValueWithBound value, int cout) {
		
		if(keepRecent)
			return putRecent(key, value);
		else
			return putMostComplex(key, value, cout);

	}
	
	public ContinueStateValueWithBound putRecent(PositionGris key, ContinueStateValueWithBound value){
		int hash = key.hashCode();
		MyElement elem = map[hash];
		map[hash] = new MyElement(key, value);
		
		if(elem == null)
			return null;
		
		return elem.val;
	}
	
	public ContinueStateValueWithBound putMostComplex(PositionGris key, ContinueStateValueWithBound value, int cout){
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
	public void putAll(Map<? extends PositionGris, ? extends ContinueStateValueWithBound> m) {
		throw new RuntimeException("Unimplemented : putAll");
		// TODO Auto-generated method stub
		
	}

	@Override
	public ContinueStateValueWithBound remove(Object key) {
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
	public Collection<ContinueStateValueWithBound> values() {
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
			if(map[i] != null){
				if(map[i].val.value.isNotWinNorLoss())
					map[i] = null;
				else
					map[i].val.setBound(0);
			}
		}
	}

}