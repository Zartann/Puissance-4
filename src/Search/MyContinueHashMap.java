package Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Game.PositionGris;

public class MyContinueHashMap implements Map<PositionGris, ContinueStateValueWithBound> {
	
	private class MyValue{
		public PositionGris pos;
		public ContinueStateValueWithBound val;
		public int cout = 0;
		
		public MyValue(PositionGris key, ContinueStateValueWithBound value) {
			pos = key;
			val = value;
		}
		
		public MyValue(PositionGris key, ContinueStateValueWithBound value, int cout) {
			pos = key;
			val = value;
			this.cout = cout;
		}
	}
	
	private class MyBestShot{
		public PositionGris pos;
		public int bestShot;
		
		public MyBestShot(PositionGris key, int best) {
			pos = key;
			bestShot = best;
		}
	}
	
	/**
	 * Contient les valeurs de la table
	 */
	MyValue[] map;
	
	/**
	 * Contient les meilleurs coups associés à chaque position
	 * Si la position est remplacée dans la table des valeurs, elle est remplacée ici aussi.
	 */
	MyBestShot[] bestCoups;
	
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
		map = new MyValue[taille];
		bestCoups = new MyBestShot[taille];
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
		throw new RuntimeException("Unimplemented : keySet");
		// TODO Auto-generated method stub
	}

	@Override
	public ContinueStateValueWithBound get(Object key) {
		throw new RuntimeException("Mauvaise méthode get !!! Utiliser getValue ou getBestCoup !");
		/*if(!(key instanceof PositionGris))
			return null;
		
		MyValue elem = map[key.hashCode()];
		
		if(elem == null)
			return null;
		
		if(elem.pos.equals(key)){
			return elem.val;
		}
		return null;*/
	}
	
	/**
	 * Renvoie la valeur associée à la position si elle est dans la table
	 * Renvoie null sinon
	 * @param pos
	 * @return
	 */
	public ContinueStateValueWithBound getValue(PositionGris pos){
		MyValue elem = map[pos.hashCode()];
		
		if(elem == null)
			return null;
		
		if(elem.pos.equals(pos)){
			return elem.val;
		}
		return null;
	}
	
	/**
	 * Renvoie le meilleur coup associé à la position si elle est dans la table
	 * Renvoie le coup symétrique si le symétrique de la position est dans la table
	 * Renvoie -1 sinon
	 * @param pos
	 * @return
	 */
	public int getBestCoup(PositionGris pos){
		MyBestShot shot = bestCoups[pos.hashCode()];
		
		if(shot == null)
			return -1;
		
		if(shot.pos.equals(pos)){
			if(shot.pos.isSymetrical(pos))
				return pos.symetricalShot(shot.bestShot);
			else
				return shot.bestShot;
		}
		return -1;
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
	
	public ContinueStateValueWithBound put(PositionGris key, 
										ContinueStateValueWithBound value, int bestShot, int cout) {
		
		if(keepRecent)
			return putRecent(key, value, bestShot);
		else
			return putMostComplex(key, value, bestShot, cout);

	}
	
	/**
	 * On remplace dans les deux tableaux l'ancienne position par la nouvelle.
	 * @param key
	 * @param value
	 * @param bestShot
	 * @return
	 */
	public ContinueStateValueWithBound putRecent(PositionGris key, 
									ContinueStateValueWithBound value, int bestShot){
		int hash = key.hashCode();
		MyValue elem = map[hash];
		map[hash] = new MyValue(key, value);
		bestCoups[hash] = new MyBestShot(key, bestShot);
		
		if(elem == null)
			return null;
		
		return elem.val;
	}
	
	/**
	 * On garde dans les deux tableaux la position la plus coûteuse.
	 * @param key
	 * @param value
	 * @param bestShot
	 * @param cout
	 * @return
	 */
	public ContinueStateValueWithBound putMostComplex(PositionGris key, 
			ContinueStateValueWithBound value, int bestShot, int cout){
		int hash = key.hashCode();
		
		MyValue elem = map[hash];
		if(elem == null){
			map[hash] = new MyValue(key, value, cout);
			bestCoups[hash] = new MyBestShot(key, bestShot);
			return null;
		}
		
		if(elem.cout < cout){
			map[hash] = new MyValue(key, value, cout);
			bestCoups[hash] = new MyBestShot(key, bestShot);
		}
		
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
		
		MyValue elem = map[key.hashCode()];
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